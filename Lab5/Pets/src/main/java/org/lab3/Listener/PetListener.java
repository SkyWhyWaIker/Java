package org.lab3.Listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.lab3.DTO.PetDTO;
import org.lab3.service.PetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class PetListener {
    private final PetService petService;
//    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final Logger log = LoggerFactory.getLogger(PetListener.class);
    private static final String REPLY_TOPIC = "pet.response";

    public PetListener(PetService petService, KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.petService = petService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "pet.create")
    public void createPet(Map<String, Object> petRequest) {
        log.info("Received pet.create request: {}", petRequest);
        try {
            log.info("Entering try block");
            String replyTopic = (String) petRequest.get("replyTopic");
            if (replyTopic == null) {
                throw new IllegalArgumentException("replyTopic is missing in request");
            }

            petRequest.remove("replyTopic");
            log.info("Removed replyTopic: {}", petRequest);
            PetDTO petDTO = objectMapper.convertValue(petRequest, PetDTO.class);
            log.info("Converted to PetDTO: {}", petDTO);

            PetDTO savedPet = petService.createPet(petDTO);
            log.info("Created pet: {}", savedPet);

            TypeFactory typeFactory = objectMapper.getTypeFactory();
            JavaType stringType = typeFactory.constructType(String.class);
            JavaType objectType = typeFactory.constructType(Object.class);
            JavaType mapType = typeFactory.constructMapType(Map.class, stringType, objectType);

            Map<String, Object> response = objectMapper.convertValue(savedPet, mapType);
            String tmp2 = objectMapper.writeValueAsString(response);
            log.info("Serialized response: {}", tmp2);
            log.info("Preparing to send response to {}: {}", "pet.response", response);
            kafkaTemplate.send("pet.response", savedPet).get(10, TimeUnit.SECONDS);
            log.info("Successfully sent response to {}: {}", "pet.response", response);
        } catch (Exception e) {
            log.error("Error in pet.create: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            String fallbackReplyTopic = (String) petRequest.getOrDefault("replyTopic", REPLY_TOPIC);
            try {
                String tmp = errorResponse.toString();
                kafkaTemplate.send("pet.response", tmp).get(10, TimeUnit.SECONDS);
                log.info("Successfully sent error response to {}: {}", fallbackReplyTopic, errorResponse);
            } catch (Exception ex) {
                log.error("Failed to send error response to {}: {}", fallbackReplyTopic, ex.getMessage(), ex);
            }
        }
    }

    @KafkaListener(topics = "pet.get")
    public void getPet(Map<String, Object> request) throws Exception {
        log.info("Received message in PetListener from pet.get: {}", request);
        try {
            if (!request.containsKey("id")) {
                throw new IllegalArgumentException("Missing 'id' in request");
            }
            Long id = ((Number) request.get("id")).longValue();
            PetDTO petDTO = petService.getPetById(id);
            log.debug("Retrieved pet: {}", petDTO);

            Map<String, Object> response = objectMapper.convertValue(petDTO, Map.class);
            String tmp = response.toString();
            kafkaTemplate.send(REPLY_TOPIC, tmp).get(10, TimeUnit.SECONDS);
            log.info("Sent response to replyTopic: {}", REPLY_TOPIC);
        } catch (Exception e) {
            log.error("Error processing pet.get: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            String tmp = errorResponse.toString();
            kafkaTemplate.send(REPLY_TOPIC, tmp).get();
        }
    }

    @KafkaListener(topics = "pet.getAll")
    public void getAllPets(Map<String, Object> request) throws Exception {
        log.info("Received message in PetListener from pet.getAll: {}", request);
        try {
            if (!request.containsKey("pageable")) {
                throw new IllegalArgumentException("Missing 'pageable' in request");
            }
            Map<String, Object> pageableMap = (Map<String, Object>) request.get("pageable");
            if (pageableMap == null || !pageableMap.containsKey("page") || !pageableMap.containsKey("size")) {
                throw new IllegalArgumentException("Missing 'page' or 'size' in pageable");
            }
            int page = ((Number) pageableMap.get("page")).intValue();
            int size = ((Number) pageableMap.get("size")).intValue();
            PageRequest pageable = PageRequest.of(page, size);
            Page<PetDTO> pets = petService.getAllPets(pageable);
            Map<String, Object> response = new HashMap<>();
            response.put("content", pets.getContent().stream().map(dto -> objectMapper.convertValue(dto, Map.class)).toList());
            response.put("totalPages", pets.getTotalPages());
            response.put("totalElements", pets.getTotalElements());
            String tmp = response.toString();
            kafkaTemplate.send(REPLY_TOPIC, tmp).get();
            log.info("Sent response to replyTopic: {}", REPLY_TOPIC);
        } catch (Exception e) {
            log.error("Error processing pet.getAll: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            String tmp = errorResponse.toString();
            kafkaTemplate.send(REPLY_TOPIC, tmp).get();
        }
    }

    @KafkaListener(topics = "pet.getByColor")
    public void getPetsByColor(Map<String, Object> request) throws Exception {
        log.info("Received message in PetListener from pet.getByColor: {}", request);
        try {
            if (!request.containsKey("color") || !request.containsKey("pageable")) {
                throw new IllegalArgumentException("Missing 'color' or 'pageable' in request");
            }
            String color = (String) request.get("color");
            Map<String, Object> pageableMap = (Map<String, Object>) request.get("pageable");
            if (pageableMap == null || !pageableMap.containsKey("page") || !pageableMap.containsKey("size")) {
                throw new IllegalArgumentException("Missing 'page' or 'size' in pageable");
            }
            int page = ((Number) pageableMap.get("page")).intValue();
            int size = ((Number) pageableMap.get("size")).intValue();
            PageRequest pageable = PageRequest.of(page, size);
            Page<PetDTO> pets = petService.getPetsByColor(color, pageable);
            Map<String, Object> response = new HashMap<>();
            response.put("content", pets.getContent().stream().map(dto -> objectMapper.convertValue(dto, Map.class)).toList());
            response.put("totalPages", pets.getTotalPages());
            response.put("totalElements", pets.getTotalElements());
            String tmp = response.toString();
            kafkaTemplate.send(REPLY_TOPIC, tmp).get(10, TimeUnit.SECONDS);
            log.info("Sent response to replyTopic: {}", REPLY_TOPIC);
        } catch (Exception e) {
            log.error("Error processing pet.getByColor: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            String tmp = errorResponse.toString();
            kafkaTemplate.send(REPLY_TOPIC, tmp).get(10, TimeUnit.SECONDS);
        }
    }

    @KafkaListener(topics = "pet.update")
    public void updatePet(Map<String, Object> request) throws Exception {
        log.info("Received message in PetListener from pet.update: {}", request);
        try {
            if (!request.containsKey("id")) {
                throw new IllegalArgumentException("Missing 'id' in request");
            }
            PetDTO petDTO = objectMapper.convertValue(request, PetDTO.class);
            Long id = petDTO.getId();
            if (id == null) {
                throw new IllegalArgumentException("Missing 'id' in PetDTO");
            }
            PetDTO updatedPet = petService.updatePet(id, petDTO);
            log.debug("Updated pet: {}", updatedPet);

            Map<String, Object> response = objectMapper.convertValue(updatedPet, Map.class);
            String tmp = response.toString();
            kafkaTemplate.send(REPLY_TOPIC, tmp).get(10, TimeUnit.SECONDS);
            log.info("Sent response to replyTopic: {}", REPLY_TOPIC);
        } catch (Exception e) {
            log.error("Error processing pet.update: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            String tmp = errorResponse.toString();
            kafkaTemplate.send(REPLY_TOPIC, tmp).get(10, TimeUnit.SECONDS);
        }
    }

    @KafkaListener(topics = "pet.delete")
    public void deletePet(Map<String, Object> request) throws Exception {
        log.info("Received message in PetListener from pet.delete: {}", request);
        try {
            if (!request.containsKey("id")) {
                throw new IllegalArgumentException("Missing 'id' in request");
            }
            Long id = ((Number) request.get("id")).longValue();
            petService.deletePet(id);
            log.debug("Deleted pet with id: {}", id);

            Map<String, Object> response = Map.of("success", true);
            String tmp = response.toString();
            kafkaTemplate.send(REPLY_TOPIC, tmp).get(10, TimeUnit.SECONDS);
            log.info("Sent response to replyTopic: {}", REPLY_TOPIC);
        } catch (Exception e) {
            log.error("Error processing pet.delete: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = Map.of("error", e.getMessage() != null ? e.getMessage() : "Unknown error");
            String tmp = errorResponse.toString();
            kafkaTemplate.send(REPLY_TOPIC, tmp).get(10, TimeUnit.SECONDS);
        }
    }
}