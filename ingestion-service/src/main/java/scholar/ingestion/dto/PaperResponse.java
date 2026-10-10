// record returns by API

package scholar.ingestion.dto;

import java.time.Instant;
import java.util.UUID;
import scholar.ingestion.entity.Paper;

public record PaperResponse(UUID id, String title, String filename, Instant uploadedAt, PaperStatus status) {

    public static PaperResponse from(Paper p) {
        return new PaperResponse(p.getId(), p.getTitle(), p.getFilename(), p.getUploadedAt(), p.getStatus());
    }
}
