package org.lab3.Listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.lab3.DTO.OwnerDTO;
import org.lab3.service.OwnerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OwnerListener {
    private final OwnerService ownerService;
    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(OwnerListener.class);
    private static final String REPLY_TOPIC = "owner.response";

    public OwnerListener(OwnerService ownerService, KafkaTemplate<String, Map<String, Object>> kafkaTemplate, ObjectMapper objectMapper) {
        this.ownerService = ownerService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "owner.create", groupId = "owner-group")
    public void createOwner(Map<String, Object> request) {
        log.info("Received message in OwnerListener from owner.create: {}", request);
        try {
            OwnerDTO ownerDTO = objectMapper.convertValue(request, OwnerDTO.class);
            log.debug("Converted Map to OwnerDTO: {}", ownerDTO);

            OwnerDTO savedOwner = ownerService.createOwner(ownerDTO);
            log.debug("Successfully created owner: {}", savedOwner);

            Map<String, Object> response = objectMapper.convertValue(savedOwner, Map.class);
            kafkaTemplate.send(REPLY_TOPIC, response);
            log.info("Sent response to owner.response");
        } catch (Exception e) {
            log.error("Error processing owner.create: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            kafkaTemplate.send(REPLY_TOPIC, errorResponse);
        }
    }

    @KafkaListener(topics = "owner.get", groupId = "owner-group")
    public void getOwner(Map<String, Object> request) {
        log.info("Received message in OwnerListener from owner.get: {}", request);
        try {
            Long id = request.containsKey("id") ? ((Number) request.get("id")).longValue() : null;
            if (id == null) {
                throw new IllegalArgumentException("Missing 'id' in request");
            }
            OwnerDTO ownerDTO = ownerService.getOwnerById(id);
            log.debug("Retrieved owner: {}", ownerDTO);

            Map<String, Object> response = objectMapper.convertValue(ownerDTO, Map.class);
            kafkaTemplate.send(REPLY_TOPIC, response);
            log.info("Sent response to owner.response");
        } catch (Exception e) {
            log.error("Error processing owner.get: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            kafkaTemplate.send(REPLY_TOPIC, errorResponse);
        }
    }

    @KafkaListener(topics = "owner.getAll", groupId = "owner-group")
    public void getAllOwners(Map<String, Object> request) {
        log.info("Received message in OwnerListener from owner.getAll: {}", request);
        try {
            List<OwnerDTO> owners = ownerService.getAllOwners();
            log.debug("Retrieved all owners: {}", owners);

            List<Map> response = owners.stream()
                    .map(dto -> objectMapper.convertValue(dto, Map.class))
                    .collect(Collectors.toList());
            Map<String, Object> wrappedResponse = new HashMap<>();
            wrappedResponse.put("content", response);
            kafkaTemplate.send(REPLY_TOPIC, wrappedResponse);
            log.info("Sent response to owner.response");
        } catch (Exception e) {
            log.error("Error processing owner.getAll: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            kafkaTemplate.send(REPLY_TOPIC, errorResponse);
        }
    }

    @KafkaListener(topics = "owner.update", groupId = "owner-group")
    public void updateOwner(Map<String, Object> request) {
        log.info("Received message in OwnerListener from owner.update: {}", request);
        try {
            OwnerDTO ownerDTO = objectMapper.convertValue(request, OwnerDTO.class);
            Long id = ownerDTO.getId();
            if (id == null) {
                throw new IllegalArgumentException("Missing 'id' in OwnerDTO");
            }
            OwnerDTO updatedOwner = ownerService.updateOwner(id, ownerDTO);
            log.debug("Updated owner: {}", updatedOwner);

            Map<String, Object> response = objectMapper.convertValue(updatedOwner, Map.class);
            kafkaTemplate.send(REPLY_TOPIC, response);
            log.info("Sent response to owner.response");
        } catch (Exception e) {
            log.error("Error processing owner.update: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            kafkaTemplate.send(REPLY_TOPIC, errorResponse);
        }
    }

    @KafkaListener(topics = "owner.delete", groupId = "owner-group")
    public void deleteOwner(Map<String, Object> request) {
        log.info("Received message in OwnerListener from owner.delete: {}", request);
        try {
            Long id = request.containsKey("id") ? ((Number) request.get("id")).longValue() : null;
            if (id == null) {
                throw new IllegalArgumentException("Missing 'id' in request");
            }
            ownerService.deleteOwner(id);
            log.debug("Deleted owner with id: {}", id);

            Map<String, Object> response = Map.of("success", true);
            kafkaTemplate.send(REPLY_TOPIC, response);
            log.info("Sent response to owner.response");
        } catch (Exception e) {
            log.error("Error processing owner.delete: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            kafkaTemplate.send(REPLY_TOPIC, errorResponse);
        }
    }
}