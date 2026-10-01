// HTTP endpoints
package scholar.ingestion.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import scholar.ingestion.dto.PaperResponse;
import scholar.ingestion.service.PaperService;

@RestController
@RequestMapping("/api/papers")
public class PaperController {
    private final PaperService paperService;

    // Spring injects bean 
    public PaperController(PaperService paperService) {
        this.paperService = paperService;
    }

    // upload new file
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PaperResponse> upload(@RequestParam("file") MultipartFile file) {
        PaperResponse created = paperService.create(file);
        return ResponseEntity.created(URI.create("/api/papers/" + created.id())).body(created);
    }

    // retrieved uploaded file
    @GetMapping
    public List<PaperResponse> list() {
        return paperService.findAll();
    }

    @GetMapping("/{id}")
    public PaperResponse get(@PathVariable UUID id) {
        return paperService.findById(id);
    }
}
