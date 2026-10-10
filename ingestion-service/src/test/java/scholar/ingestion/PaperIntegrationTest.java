package scholar.ingestion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
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

    @BeforeEach 
    void cleanDatabase() {
        paperRepository.deleteAll(); // empty papers table before every test
    }

    @Test 
    void uploadedPaperIsSavedAndListed() throws Exception { // getContentAsString() throws a checked exception
        var postResult = mvc.post().uri("/api/papers").multipart()
            .file(new MockMultipartFile("file", "a.pdf", "application/pdf", "x".getBytes())).exchange();
        assertThat(postResult).hasStatus(HttpStatus.CREATED);
        assertThat(postResult).bodyJson().extractingPath("$.status").isEqualTo("PENDING");
        assertThat(paperRepository.count()).isEqualTo(1);
        
        var getAllResult = mvc.get().uri("/api/papers").exchange();
        assertThat(getAllResult).bodyJson().extractingPath("$.length()").isEqualTo(1);
        assertThat(getAllResult).bodyJson().extractingPath("$[0].filename").isEqualTo("a.pdf");

        String body = postResult.getResponse().getContentAsString();
        String id = JsonPath.read(body, "$.id");
        var getResult = mvc.get().uri("/api/papers/{id}", id).exchange();
        assertThat(getResult).bodyJson().extractingPath("$.filename").isEqualTo("a.pdf");
    }
    
    @Test
    void savedPaperHasGeneratedFields() {
        var postResult = mvc.post().uri("/api/papers").multipart()
            .file(new MockMultipartFile("file", "a.pdf", "application/pdf", "x".getBytes())).exchange();
        assertThat(postResult).hasStatus(HttpStatus.CREATED);
      
        String location = postResult.getResponse().getHeader("Location");
        var getResult = mvc.get().uri(location).exchange(); // return an HTTP response
        assertThat(getResult).bodyJson().extractingPath("$.id").isNotNull();
        assertThat(getResult).bodyJson().extractingPath("$.uploadedAt").isNotNull();
        assertThat(getResult).bodyJson().extractingPath("$.status").isEqualTo("PENDING");
    }
    
    @Test
    void getByIdReturnsSavedPaper() {
        var postResult = mvc.post().uri("/api/papers").multipart()
            .file(new MockMultipartFile("file", "a.pdf", "application/pdf", "x".getBytes())).exchange();
        assertThat(postResult).hasStatus(HttpStatus.CREATED);

        String location = postResult.getResponse().getHeader("Location");
        var result = mvc.get().uri(location).exchange();
        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.filename").isEqualTo("a.pdf");
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


    
}