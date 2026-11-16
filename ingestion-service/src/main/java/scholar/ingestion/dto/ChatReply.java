package scholar.ingestion.dto;

import java.util.List;

public record ChatReply(String reply, List<Citation> citations) {

    public record Citation(String paperId, String chunkId) {
    }
}
