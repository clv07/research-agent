package scholar.ingestion.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import scholar.ingestion.entity.Paper;
import java.util.UUID;

public interface PaperRepository extends JpaRepository<Paper, UUID> {
}

