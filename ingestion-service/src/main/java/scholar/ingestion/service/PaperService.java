package scholar.ingestion.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import scholar.ingestion.dto.PaperResponse;
import scholar.ingestion.dto.PaperStatus;

@Service
public class PaperService {
    private final Map<UUID, PaperResponse> papers = new ConcurrentHashMap<>();
    private static final Set<String> ALLOWED_TYPES = Set.of("application/pdf", "text/plain");

    public PaperResponse create(MultipartFile file) {
        // 1. File validation
        // 1a. if empty, return 400
        if (file.isEmpty())
            throw new ResponseStatusException(HttpStatusCode.valueOf(400));

        // 1b. read content type. If not in ALLOWED_TYPES, throw 422
        String type = file.getContentType();
        if (!ALLOWED_TYPES.contains(type))
            throw new ResponseStatusException(HttpStatusCode.valueOf(422));

        // 2. Get filename - retain path to the file
        String filename = file.getOriginalFilename();
        filename = StringUtils.hasText(filename) ? filename : "untitled";

        // 3. Build a response object with PENDING, put it in hashmap and return it
        var paper = new PaperResponse(UUID.randomUUID(), null, filename, Instant.now(), PaperStatus.PENDING);
        papers.put(paper.id(), paper);
        return paper;
    }
    
    public List<PaperResponse> findAll() {
        return List.copyOf(papers.values());
    }

    public PaperResponse findById(UUID id) {
        var paper = papers.get(id);
        if (paper == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper " + id + " not found");
        return paper;
    }

    public String getOriginalFilename(UUID id) {
        PaperResponse paper = findById(id);
        return paper.filename();
    }

}
