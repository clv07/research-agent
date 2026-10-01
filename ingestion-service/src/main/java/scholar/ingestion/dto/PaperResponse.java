// record returns by API

package scholar.ingestion.dto;

import java.time.Instant;
import java.util.UUID;

public record PaperResponse(UUID id, String title, String filename, Instant uploadedAt, PaperStatus status) {

}
