package scholar.ingestion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.jayway.jsonpath.JsonPath;

import scholar.ingestion.repository.PaperRepository;

@SpringBootTest
@AutoConfigureMockMvc()
@Import(TestcontainersConfiguration.class)
public class PaperIntegrationTest {
    @Autowired
    MockMvcTester mvc;
    @Autowired
    PaperRepository paperRepository;
    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void cleanDatabase() {
        paperRepository.deleteAll(); // empty papers table before every test
        jdbc.update("DELETE FROM vector_store");
    }

    @Test
    void uploadedPaperIsSavedAndListed() throws Exception { // getContentAsString() throws a checked exception
        var postResult = mvc.post().uri("/api/papers").multipart()
                .file(new MockMultipartFile("file", "a.txt", "text/plain", "some paper text".getBytes())).exchange();
        assertThat(postResult).hasStatus(HttpStatus.CREATED);
        assertThat(postResult).bodyJson().extractingPath("$.status").isEqualTo("EMBEDDED");
        assertThat(paperRepository.count()).isEqualTo(1);

        var getAllResult = mvc.get().uri("/api/papers").exchange();
        assertThat(getAllResult).bodyJson().extractingPath("$.length()").isEqualTo(1);
        assertThat(getAllResult).bodyJson().extractingPath("$[0].filename").isEqualTo("a.txt");

        String body = postResult.getResponse().getContentAsString();
        String id = JsonPath.read(body, "$.id");
        var getResult = mvc.get().uri("/api/papers/{id}", id).exchange();
        assertThat(getResult).bodyJson().extractingPath("$.filename").isEqualTo("a.txt");
    }

    @Test
    void savedPaperHasGeneratedFields() {
        var postResult = mvc.post().uri("/api/papers").multipart()
                .file(new MockMultipartFile("file", "a.txt", "text/plain", "some paper text".getBytes())).exchange();
        assertThat(postResult).hasStatus(HttpStatus.CREATED);

        String location = postResult.getResponse().getHeader("Location");
        var getResult = mvc.get().uri(location).exchange(); // return an HTTP response
        assertThat(getResult).bodyJson().extractingPath("$.id").isNotNull();
        assertThat(getResult).bodyJson().extractingPath("$.uploadedAt").isNotNull();
        assertThat(getResult).bodyJson().extractingPath("$.status").isEqualTo("EMBEDDED");
    }

    @Test
    void getByIdReturnsSavedPaper() {
        var postResult = mvc.post().uri("/api/papers").multipart()
                .file(new MockMultipartFile("file", "a.txt", "text/plain", "some paper text".getBytes())).exchange();
        assertThat(postResult).hasStatus(HttpStatus.CREATED);

        String location = postResult.getResponse().getHeader("Location");
        var result = mvc.get().uri(location).exchange();
        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.filename").isEqualTo("a.txt");
    }

    @Test
    void unknownIdReturns404() {
        var id = UUID.randomUUID();
        var result = mvc.get().uri("/api/papers/{id}", id).exchange();
        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void rejectedFileIsNotSaved() {
        var result = mvc.post().uri("/api/papers").multipart()
                .file(new MockMultipartFile("file", "image.png", "image/png", "x".getBytes())).exchange();
        assertThat(result).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
        assertThat(paperRepository.count()).isEqualTo(0);
    }

    @Test
    void uploadedPaperIsChunkedAndEmbedded() throws Exception { // getContentAsString() throws a checked exception
        // arrange
        var text = "The quick brown fox jumps over the grass. ".repeat(200);

        // act
        var postResult = mvc.post().uri("/api/papers").multipart()
                .file(new MockMultipartFile("file", "a.txt", "text/plain", text.getBytes())).exchange();

        // assert
        assertThat(postResult).hasStatus(HttpStatus.CREATED);
        assertThat(postResult).bodyJson().extractingPath("$.status").isEqualTo("EMBEDDED");

        String body = postResult.getResponse().getContentAsString();
        String id = JsonPath.read(body, "$.id");

        // (a) rows for this paper exist and larger than one
        Integer rows = jdbc.queryForObject(
                "SELECT count(*) FROM vector_store WHERE metadata->>'paperId' = ?",
                Integer.class, id);
        assertThat(rows).isGreaterThan(1);

        // (b) every stored embedding has 384 dimensions
        // vector_dims() is a pgvector function returning a vector's length
        List<Integer> dims = jdbc.queryForList(
                "SELECT vector_dims(embedding) FROM vector_store WHERE metadata->>'paperId' = ?",
                Integer.class, id);
        assertThat(dims).containsOnly(384);
    }

}