package scholar.ingestion;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import scholar.ingestion.controller.PaperController;
import scholar.ingestion.dto.PaperResponse;
import scholar.ingestion.dto.PaperStatus;
import scholar.ingestion.service.PaperService;

// get should return 200 ok
// post should return 201 Created

@WebMvcTest(PaperController.class)
class PaperControllerTest {
        @Autowired
        MockMvcTester mvc;
        @MockitoBean
        PaperService paperService;

        @Test
        void getPaperById() {
                var id = UUID.randomUUID();
                given(paperService.findById(id))
                                .willReturn(new PaperResponse(id,
                                                "title", "a.pdf", Instant.now(),
                                                PaperStatus.PENDING));
                var result = mvc.get().uri("/api/papers/{id}", id);
                assertThat(result).hasStatusOk().bodyJson().extractingPath("$.title").isEqualTo("title");
                assertThat(result).bodyJson().extractingPath("$.filename").isEqualTo("a.pdf");
        }

        @Test
        void getPaperList() {
                var id = UUID.randomUUID();
                given(paperService.findAll())
                                .willReturn(List.of(
                                                new PaperResponse(id, "title", "a.pdf", Instant.now(),
                                                                PaperStatus.PENDING)));
                var result = mvc.get().uri("/api/papers");
                assertThat(result).hasStatusOk().bodyJson().extractingPath("$[0].title").isEqualTo("title"); 
                assertThat(result).bodyJson().extractingPath("$[0].filename").isEqualTo("a.pdf");
        }

        @Test
        void uploadPaper() {
                var id = UUID.randomUUID();
                given(paperService.create(any())).willReturn(
                                new PaperResponse(id, "title", "a.pdf", Instant.now(), PaperStatus.PENDING));
                assertThat(mvc.post().uri("/api/papers").multipart()
                                .file(new MockMultipartFile("file", "a.pdf", "application/pdf", "x".getBytes())))
                                .hasStatus(HttpStatus.CREATED); 
        }

}
