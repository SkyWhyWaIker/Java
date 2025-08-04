package org.lab3.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.lab3.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owners")
@Tag(name = "Owner Controller", description = "API for managing owners")
public class OwnerController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);
    private static final int RESPONSE_TIMEOUT_SECONDS = 10;
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private volatile Map<String, Object> lastResponse; // Волатильное поле для потокобезопасности
    private static final String REPLY_TOPIC = "owner.response";

    @Autowired
    public OwnerController(KafkaTemplate<String, Map<String, Object>> kafkaTemplate,
                           ObjectMapper objectMapper,
                           UserService userService) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.lastResponse = null;
    }

    @PostMapping
    @Operation(summary = "Create a new owner")
    public synchronized Map<String, Object> createOwner(@RequestBody Map<String, Object> ownerRequest) throws Exception {
        logger.info("Received request to create owner: {}", ownerRequest);
        Long userId = getCurrentUserId();
        ownerRequest.put("userId", userId);
        ownerRequest.put("replyTopic", REPLY_TOPIC);

        lastResponse = null;
        kafkaTemplate.send("owner.create", ownerRequest);
        logger.info("Sent request to owner.create with replyTopic: {}", REPLY_TOPIC);

        int attempts = RESPONSE_TIMEOUT_SECONDS;
        while (attempts-- > 0 && lastResponse == null) {
            Thread.sleep(1000);
            logger.debug("Waiting for response on replyTopic: {}, attempts left: {}", REPLY_TOPIC, attempts);
        }

        if (lastResponse == null) {
            logger.error("Timeout waiting for response on replyTopic: {}", REPLY_TOPIC);
            throw new RuntimeException("Owner creation timed out after " + RESPONSE_TIMEOUT_SECONDS + " seconds");
        }

        if (lastResponse.containsKey("error")) {
            logger.error("Error in response: {}", lastResponse.get("error"));
            throw new RuntimeException("Owner creation failed: " + lastResponse.get("error"));
        }

        logger.info("Received response: {}", lastResponse);
        return lastResponse;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get owner by ID")
    public synchronized Map<String, Object> getOwnerById(@PathVariable Long id) throws Exception {
        Map<String, Object> request = Map.of("id", id, "replyTopic", REPLY_TOPIC);
        lastResponse = null;

        kafkaTemplate.send("owner.get", request);
        logger.info("Sent request to owner.get with replyTopic: {}", REPLY_TOPIC);

        int attempts = RESPONSE_TIMEOUT_SECONDS;
        while (attempts-- > 0 && lastResponse == null) {
            Thread.sleep(1000);
        }

        if (lastResponse == null) {
            logger.error("Timeout waiting for response on replyTopic: {}", REPLY_TOPIC);
            throw new RuntimeException("Get owner timed out after " + RESPONSE_TIMEOUT_SECONDS + " seconds");
        }

        if (lastResponse.containsKey("error")) {
            logger.error("Error in response: {}", lastResponse.get("error"));
            throw new RuntimeException("Get owner failed: " + lastResponse.get("error"));
        }

        return lastResponse;
    }

    @GetMapping
    @Operation(summary = "Get all owners")
    public synchronized List<Map<String, Object>> getAllOwners() throws Exception {
        Map<String, Object> request = Map.of("replyTopic", REPLY_TOPIC);
        lastResponse = null;

        kafkaTemplate.send("owner.getAll", request);
        logger.info("Sent request to owner.getAll with replyTopic: {}", REPLY_TOPIC);

        int attempts = RESPONSE_TIMEOUT_SECONDS;
        while (attempts-- > 0 && lastResponse == null) {
            Thread.sleep(1000);
        }

        if (lastResponse == null || lastResponse.containsKey("error")) {
            logger.warn("No owners found, response: {}", lastResponse);
            return List.of();
        }

        List<Map<String, Object>> content = (List<Map<String, Object>>) lastResponse.get("content");
        return content != null ? content : List.of();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an owner")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public synchronized Map<String, Object> updateOwner(@PathVariable Long id, @RequestBody Map<String, Object> ownerRequest)
            throws Exception {
        Long userId = getCurrentUserId();
        ownerRequest.put("id", id);
        ownerRequest.put("userId", userId);
        ownerRequest.put("replyTopic", REPLY_TOPIC);
        lastResponse = null;

        kafkaTemplate.send("owner.update", ownerRequest);
        logger.info("Sent request to owner.update with replyTopic: {}", REPLY_TOPIC);

        int attempts = RESPONSE_TIMEOUT_SECONDS;
        while (attempts-- > 0 && lastResponse == null) {
            Thread.sleep(1000);
        }

        if (lastResponse == null) {
            logger.error("Timeout waiting for response on replyTopic: {}", REPLY_TOPIC);
            throw new RuntimeException("Update owner timed out after " + RESPONSE_TIMEOUT_SECONDS + " seconds");
        }

        if (lastResponse.containsKey("error")) {
            logger.error("Error in response: {}", lastResponse.get("error"));
            throw new RuntimeException("Update owner failed: " + lastResponse.get("error"));
        }

        return lastResponse;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an owner")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public synchronized void deleteOwner(@PathVariable Long id) throws Exception {
        Map<String, Object> request = Map.of("id", id, "replyTopic", REPLY_TOPIC);
        lastResponse = null;

        kafkaTemplate.send("owner.delete", request);
        logger.info("Sent request to owner.delete with replyTopic: {}", REPLY_TOPIC);

        int attempts = RESPONSE_TIMEOUT_SECONDS;
        while (attempts-- > 0 && lastResponse == null) {
            Thread.sleep(1000);
        }

        if (lastResponse == null) {
            logger.error("Timeout waiting for response on replyTopic: {}", REPLY_TOPIC);
            throw new RuntimeException("Delete owner timed out after " + RESPONSE_TIMEOUT_SECONDS + " seconds");
        }

        if (lastResponse.containsKey("error")) {
            logger.error("Error in response: {}", lastResponse.get("error"));
            throw new RuntimeException("Delete owner failed: " + lastResponse.get("error"));
        }
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userService.findUserIdByUsername(username);
        } else {
            logger.warn("User not authenticated, using default user ID");
            return 1L;
        }
    }

    public synchronized void handleOwnerResponse(Map<String, Object> ownerResponse) {
        logger.debug("Handling owner response: {}", ownerResponse);
        lastResponse = ownerResponse;
    }

    public synchronized void handleOwnerListResponse(List<Map<String, Object>> owners) {
        logger.debug("Handling owner list response: {}", owners);
        Map<String, Object> response = new HashMap<>();
        response.put("content", owners);
        lastResponse = response;
    }
}