package scholar.ingestion.service;

import java.util.List;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component 
public class DocumentChunker {

    List<Document> chunk(MultipartFile file, UUID paperId) {
        
        Resource resource = file.getResource();
        DocumentReader reader;

        // extract pages from files
        if ("application/pdf".equals(file.getContentType()))  // read pdf
            reader = new PagePdfDocumentReader(resource);
        else // read text
            reader = new TextReader(resource);
        List<Document> pages = reader.get();

        // add paperId to read pages
        for (Document d: pages) 
            d.getMetadata().put("paperId", paperId.toString());

        // split and tokenize pages into chunks
        var splitter = TokenTextSplitter.builder().withChunkSize(200).build();
        List<Document> docs = splitter.apply(pages);
        return docs;
    }
}