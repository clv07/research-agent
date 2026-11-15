package scholar.ingestion.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import scholar.ingestion.dto.PaperResponse;
import scholar.ingestion.entity.Paper;
import scholar.ingestion.repository.PaperRepository;

@Service
public class PaperService {
    private final PaperRepository papers;
    private final DocumentChunker chunker;
    private final VectorStore vectorStore;

    public PaperService(PaperRepository papers, DocumentChunker chunker, VectorStore vectorStore) {
        this.papers = papers;
        this.chunker = chunker;
        this.vectorStore = vectorStore;
    }

    private static final Set<String> ALLOWED_TYPES = Set.of("application/pdf", "text/plain");
    private static final Logger log = LoggerFactory.getLogger(PaperService.class);
    
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

        // 3. Build a response object with PENDING and return it
        var paper = new Paper(null, filename);
        var saved = papers.save(paper);

        // 4. Chunk file
        List<Document> docs = chunker.chunk(file, saved.getId());
        
        // 5. log chunk count
        log.info("{} -> {} chunks", filename, docs.size());

        // 6. embedding and set status
        try {
            vectorStore.add(docs);
            saved.setEmbedded();
        } catch (RuntimeException e) {
            log.warn("embedding failed for {}", saved.getId(), e);
            saved.setFailed();
        }
        saved = papers.save(saved); // persist new status

        return PaperResponse.from(saved);
    }
    
    public List<PaperResponse> findAll() {
        return papers.findAll().stream().map(PaperResponse::from).toList();
    }

    public PaperResponse findById(UUID id) {
        return papers.findById(id)
                .map(PaperResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paper " + id + " not found"));
    }

    public String getOriginalFilename(UUID id) {
        PaperResponse paper = findById(id);
        return paper.filename();
    }

}
