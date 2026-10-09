package scholar.ingestion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import scholar.ingestion.dto.PaperStatus;
import jakarta.persistence.GenerationType;

import java.util.UUID;
import java.time.Instant;

@Entity // represent a rows in table
@Table(name = "papers") // names the table
public class Paper {

    // ---- Fields ----
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @Enumerated(EnumType.STRING)
    @Column (name = "status", nullable = false)
    private PaperStatus status;

    // ---- Constructors ---- 
    protected Paper() {} // for JPA

    public Paper(String title, String filename) { // public constructor
        this.title = title;
        this.filename = filename;
        this.uploadedAt = Instant.now();
        this.status = PaperStatus.PENDING;
    }

    // ---- Getters -----
    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getFilename() {
        return filename;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public PaperStatus getStatus() {
        return status;
    }

}
