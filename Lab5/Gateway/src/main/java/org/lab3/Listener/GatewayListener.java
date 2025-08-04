package org.lab3.Listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lab3.controllers.OwnerController;
import org.lab3.controllers.PetController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GatewayListener {
    private static final Logger log = LoggerFactory.getLogger(GatewayListener.class);

    private final OwnerController ownerController;
    private final PetController petController;
    private final ObjectMapper objectMapper;

    public GatewayListener(OwnerController ownerController, PetController petController, ObjectMapper objectMapper) {
        this.ownerController = ownerController;
        this.petController = petController;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "owner.response")
    public void handleOwnerResponse(String ownerResponseJson) {
        log.info("Received owner response: {}", ownerResponseJson);
        if (ownerResponseJson == null) {
            log.error("Received null owner response");
            ownerController.handleOwnerResponse(Map.of("error", "Received null response"));
            return;
        }
        try {
            Map<String, Object> ownerResponse = objectMapper.readValue(ownerResponseJson, new TypeReference<Map<String, Object>>(){});
            if (ownerResponse.containsKey("content")) {
                List<Map<String, Object>> owners = (List<Map<String, Object>>) ownerResponse.get("content");
                ownerController.handleOwnerListResponse(owners);
            } else {
                ownerController.handleOwnerResponse(ownerResponse);
            }
            log.debug("Processed owner response");
        } catch (Exception e) {
            log.error("Error processing owner response: {}", e.getMessage(), e);
            ownerController.handleOwnerResponse(Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error"));
        }
    }

    // @KafkaListener(topics = "pet.response", groupId = "gateway-group")
    @KafkaListener(topics = "pet.response")
    public void handlePetResponse(PetDTO petResponse) {
        log.info("Received pet response: {}", petResponse);
        try {
//            Map<String, Object> tmp = objectMapper.convertValue(petResponse, new TypeReference<Map<String, Object>>(){});
//            log.info("Converted to map pet response: {}", tmp);
//            String tmp2 = objectMapper.writeValueAsString(tmp);
//            log.info("Converted to String pet response: {}", tmp2);
            petController.handlePetResponse(petResponse);
            log.debug("Processed pet response");
        } catch (Exception e) {
            log.error("Error processing pet response: {}", e.getMessage(), e);
        }

    }

    @KafkaListener(topics = "pet.page.response")
    public void handlePetPageResponse(String pageResponseJson) {
        log.info("Received pet page response: {}", pageResponseJson);
        try {
            Map<String, Object> pageResponse = objectMapper.readValue(pageResponseJson, new TypeReference<Map<String, Object>>(){});
            petController.handlePetPageResponse(pageResponse);
            log.debug("Processed pet page response");
        } catch (Exception e) {
            log.error("Error processing pet page response: {}", e.getMessage(), e);
            petController.handlePetPageResponse(Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error"));
        }
    }
}