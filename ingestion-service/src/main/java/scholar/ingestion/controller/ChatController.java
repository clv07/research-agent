package scholar.ingestion.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import scholar.ingestion.dto.ChatReply;
import scholar.ingestion.dto.ChatRequest;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final VectorStore vectorStore;

    public ChatController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostMapping
    public ChatReply chat(@RequestBody ChatRequest req) {
        // embed the question with the same model as the chunks, then find the 5 nearest chunks
        List<Document> hits = vectorStore.similaritySearch(
                SearchRequest.builder().query(req.message()).topK(5).build());

        // log scores to pick a similarityThreshold later
        hits.forEach(d -> log.info("score {} chunk {}", d.getScore(), d.getId()));

        // numbered passages as the reply text
        String reply = hits.isEmpty()
                ? "No relevant passages found."
                : IntStream.range(0, hits.size())
                        .mapToObj(i -> "[" + (i + 1) + "] " + hits.get(i).getText())
                        .collect(Collectors.joining("\n\n"));

        List<ChatReply.Citation> citations = hits.stream()
                .map(d -> new ChatReply.Citation((String) d.getMetadata().get("paperId"), d.getId()))
                .toList();

        return new ChatReply(reply, citations);
    }
}
