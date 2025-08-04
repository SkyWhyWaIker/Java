package org.lab3.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.lab3.Listener.PetDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pet Controller", description = "API for managing pets")
public class PetController {

    private static final Logger log = LoggerFactory.getLogger(PetController.class);
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private volatile Map<String, Object> lastResponse;
    static final String REPLY_TOPIC = "pet.response";

    public PetController(KafkaTemplate<String, Map<String, Object>> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.lastResponse = null;
    }

    @PostMapping
    @Operation(summary = "Create a new pet")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void createPet(@RequestBody Map<String, Object> petRequest) throws Exception {
        log.info("Received request to create pet: {}", petRequest);
        petRequest.put("replyTopic", REPLY_TOPIC);

        lastResponse = null;
        kafkaTemplate.send("pet.create", petRequest);
        log.info("Sent request to pet.create with replyTopic: {}", REPLY_TOPIC);

        int attempts = 10;
        while (attempts-- > 0 && lastResponse == null) {
            Thread.sleep(1000);
            log.debug("Waiting for response on replyTopic: {}, attempts left: {}", REPLY_TOPIC, attempts);
        }

        if (lastResponse == null) {
            log.error("Timeout waiting for response on replyTopic: {}", REPLY_TOPIC);
            throw new RuntimeException("Pet creation timed out after 10 seconds");
        }

        if (lastResponse.containsKey("error")) {
            log.error("Error in response: {}", lastResponse.get("error"));
            throw new RuntimeException("Pet creation failed: " + lastResponse.get("error"));
        }

        log.info("Received response: {}", lastResponse);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pet by ID")
    @PreAuthorize("isAuthenticated()")
    public synchronized ResponseEntity<Object> getPetById(@PathVariable Long id) throws Exception {
        Map<String, Object> request = Map.of("id", id, "replyTopic", REPLY_TOPIC);
        lastResponse = null;

        kafkaTemplate.send("pet.get", request);
        log.info("Sent request to pet.get with replyTopic: {}", REPLY_TOPIC);

        int attempts = 15;
        while (attempts-- > 0 && lastResponse == null) {
            Thread.sleep(1000);
        }

        if (lastResponse == null) {
            log.error("Timeout waiting for response on replyTopic: {}", REPLY_TOPIC);
            return ResponseEntity.status(404).build();
        }

        if (lastResponse.containsKey("error")) {
            log.error("Error in response: {}", lastResponse.get("error"));
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(lastResponse);
    }

    @GetMapping
    @Operation(summary = "Get all pets with pagination")
    @PreAuthorize("isAuthenticated()")
    public synchronized Page<Map<String, Object>> getAllPets(Pageable pageable) throws Exception {
        Map<String, Object> request = Map.of(
                "replyTopic", REPLY_TOPIC,
                "pageable", Map.of(
                        "page", pageable.getPageNumber(),
                        "size", pageable.getPageSize()
                )
        );
        lastResponse = null;

        kafkaTemplate.send("pet.getAll", request);
        log.info("Sent request to pet.getAll with replyTopic: {}", REPLY_TOPIC);

        int attempts = 15;
        while (attempts-- > 0 && lastResponse == null) {
            Thread.sleep(1000);
        }

        if (lastResponse == null || lastResponse.containsKey("error")) {
            log.warn("No pets found, response: {}", lastResponse);
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<Map<String, Object>> content = (List<Map<String, Object>>) lastResponse.get("content");
        int totalPages = ((Number) lastResponse.get("totalPages")).intValue();
        long totalElements = ((Number) lastResponse.get("totalElements")).longValue();

        return new PageImpl<>(content, pageable, totalElements);
    }

    @GetMapping("/color/{color}")
    @Operation(summary = "Get pets by color with pagination")
    @PreAuthorize("isAuthenticated()")
    public synchronized ResponseEntity<Page<Map<String, Object>>> getPetsByColor(@PathVariable String color, Pageable pageable) throws Exception {
        Map<String, Object> request = Map.of(
                "color", color,
                "replyTopic", REPLY_TOPIC,
                "pageable", Map.of(
                        "page", pageable.getPageNumber(),
                        "size", pageable.getPageSize()
                )
        );
        lastResponse = null;

        kafkaTemplate.send("pet.getByColor", request);
        log.info("Sent request to pet.getByColor with replyTopic: {}", REPLY_TOPIC);

        int attempts = 15;
        while (attempts-- > 0 && lastResponse == null) {
            Thread.sleep(1000);
        }

        if (lastResponse == null || lastResponse.containsKey("error")) {
            log.warn("No pets found for color {}, response: {}", color, lastResponse);
            return ResponseEntity.ok(new PageImpl<>(List.of(), pageable, 0));
        }

        List<Map<String, Object>> content = (List<Map<String, Object>>) lastResponse.get("content");
        int totalPages = ((Number) lastResponse.get("totalPages")).intValue();
        long totalElements = ((Number) lastResponse.get("totalElements")).longValue();

        return ResponseEntity.ok(new PageImpl<>(content, pageable, totalElements));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a pet")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public synchronized ResponseEntity<Map<String, Object>> updatePet(@PathVariable Long id, @RequestBody Map<String, Object> petRequest) throws Exception {
        petRequest.put("id", id);
        petRequest.put("replyTopic", REPLY_TOPIC);
        lastResponse = null;

        kafkaTemplate.send("pet.update", petRequest);
        log.info("Sent request to pet.update with replyTopic: {}", REPLY_TOPIC);

        int attempts = 15;
        while (attempts-- > 0 && lastResponse == null) {
            Thread.sleep(1000);
        }

        if (lastResponse == null) {
            log.error("Timeout waiting for response on replyTopic: {}", REPLY_TOPIC);
            return ResponseEntity.status(404).build();
        }

        if (lastResponse.containsKey("error")) {
            log.error("Error in response: {}", lastResponse.get("error"));
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.ok(lastResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a pet")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public synchronized ResponseEntity<Void> deletePet(@PathVariable Long id) throws Exception {
        Map<String, Object> request = Map.of("id", id, "replyTopic", REPLY_TOPIC);
        lastResponse = null;

        kafkaTemplate.send("pet.delete", request);
        log.info("Sent request to pet.delete with replyTopic: {}", REPLY_TOPIC);

        int attempts = 15;
        while (attempts-- > 0 && lastResponse == null) {
            Thread.sleep(1000);
        }

        if (lastResponse == null) {
            log.error("Timeout waiting for response on replyTopic: {}", REPLY_TOPIC);
            return ResponseEntity.status(404).build();
        }

        if (lastResponse.containsKey("error")) {
            log.error("Error in response: {}", lastResponse.get("error"));
            return ResponseEntity.status(404).build();
        }

        return ResponseEntity.noContent().build();
    }

//    public void handlePetResponse(String petResponse) {
//        log.debug("Handling pet response: {}", petResponse);
//        try {
//            Map<String, Object> request = objectMapper.readValue(petResponse, Map.class);
//            String tmp = request.toString();
//            Map<String, Object> response = objectMapper.convertValue(tmp, Map.class);
//            log.info("Fourth convert {}", request);
//            request.put("replyTopic", REPLY_TOPIC);
//            log.info("Final convert {}", request);
//            lastResponse = response;
//            log.debug("Successfully converted pet response to map: {}", request);
//        } catch (Exception e) {
//            log.error("Error converting pet response to map: {}", e.getMessage(), e);
//            lastResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
//        }
//    }

public void handlePetResponse(PetDTO petResponse) {
    log.debug("Handling pet response: {}", petResponse);
    try {
        // Use ObjectMapper to deserialize with tolerance for null values
//        Map<String, String> response = objectMapper.readValue(petResponse, new TypeReference<Map<String, String>>() {});
        // Handle potential null values by replacing with empty string if needed
//        Map<String, Object> sanitizedResponse = new HashMap<>();
//        for (Map.Entry<String, String> entry : response.entrySet()) {
//            sanitizedResponse.put(entry.getKey(), entry.getValue() != null ? entry.getValue() : "");
//        }
//        lastResponse = sanitizedResponse;
//        log.debug("Successfully converted pet response to map: {}", sanitizedResponse);
    } catch (Exception e) {
        log.error("Error converting pet response to map: {}", e.getMessage(), e);
        lastResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
    }
}

    public void handlePetPageResponse(Map<String, Object> pageResponse) {
        log.debug("Handling pet page response: {}", pageResponse);
        lastResponse = pageResponse;
    }
}