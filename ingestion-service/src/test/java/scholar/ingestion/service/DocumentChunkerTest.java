package scholar.ingestion.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.mock.web.MockMultipartFile;

class DocumentChunkerTest {

    @Test
    void splitsTextFileIntoChunksTaggedWithPaperId() {
        // arrange
        var text = "The quick brown fox jumps over the lazy dog. ".repeat(200);
        var file = new MockMultipartFile("file", "a.txt", "text/plain", text.getBytes());
        var id = UUID.randomUUID();
        var chunker = new DocumentChunker();

        // act
        List<Document> chunks = chunker.chunk(file, id);

        // assert
        assertThat(chunks).hasSizeGreaterThan(1);
        assertThat(chunks).allSatisfy(chunk ->
                assertThat(chunk.getMetadata()).containsEntry("paperId", id.toString()));
    }
}
