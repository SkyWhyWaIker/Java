package org.lab;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.lab.Authentication.SecurityConfig;
import org.lab.Controller.PetController;
import org.lab.DTO.PetDTO;
import org.lab.Model.Color;
import org.lab.Service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PetController.class)
@Import(SecurityConfig.class)
class PetTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void createPet() throws Exception {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(1L);
        petDTO.setName("Buddy");
        petDTO.setColor(Color.BROWN);

        when(petService.createPet(any(PetDTO.class))).thenReturn(petDTO);

        mockMvc.perform(post("/api/pets")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.color").value("BROWN"));

        verify(petService, times(1)).createPet(any(PetDTO.class));
    }

    @Test
    @WithMockUser
    void getPetById() throws Exception {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(1L);
        petDTO.setName("Buddy");
        petDTO.setColor(Color.BROWN);

        when(petService.getPetById(1L)).thenReturn(petDTO);

        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Buddy"))
                .andExpect(jsonPath("$.color").value("BROWN"));

        verify(petService, times(1)).getPetById(1L);
    }

    @Test
    @WithMockUser
    void getPetByIdNotFound() throws Exception {
        when(petService.getPetById(1L)).thenThrow(new RuntimeException("Pet not found"));

        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isNotFound());

        verify(petService, times(1)).getPetById(1L);
    }

    @Test
    @WithMockUser
    void getAllPets() throws Exception {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(1L);
        petDTO.setName("Buddy");
        petDTO.setColor(Color.BROWN);

        Page<PetDTO> page = new PageImpl<>(List.of(petDTO));
        when(petService.getAllPets(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/pets?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Buddy"))
                .andExpect(jsonPath("$.content[0].color").value("BROWN"));

        verify(petService, times(1)).getAllPets(any(PageRequest.class));
    }

    @Test
    @WithMockUser
    void getPetsByColor() throws Exception {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(1L);
        petDTO.setName("Buddy");
        petDTO.setColor(Color.BROWN);

        Page<PetDTO> page = new PageImpl<>(List.of(petDTO));
        when(petService.getPetsByColor(eq("BROWN"), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/pets/color/BROWN?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("Buddy"))
                .andExpect(jsonPath("$.content[0].color").value("BROWN"));

        verify(petService, times(1)).getPetsByColor(eq("BROWN"), any(PageRequest.class));
    }

    @Test
    @WithMockUser
    void getPetsByColorNotFound() throws Exception {
        Page<PetDTO> emptyPage = new PageImpl<>(List.of());
        when(petService.getPetsByColor(eq("RED"), any(PageRequest.class))).thenReturn(emptyPage);

        mockMvc.perform(get("/api/pets/color/RED?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(petService, times(1)).getPetsByColor(eq("RED"), any(PageRequest.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void updatePet() throws Exception {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(1L);
        petDTO.setName("Buddy Updated");
        petDTO.setColor(Color.BROWN);

        when(petService.updatePet(eq(1L), any(PetDTO.class))).thenReturn(petDTO);

        mockMvc.perform(put("/api/pets/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Buddy Updated"))
                .andExpect(jsonPath("$.color").value("BROWN"));

        verify(petService, times(1)).updatePet(eq(1L), any(PetDTO.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void updatePetNotFound() throws Exception {
        PetDTO petDTO = new PetDTO();
        petDTO.setId(1L);
        petDTO.setName("Buddy Updated");
        petDTO.setColor(Color.BROWN);

        when(petService.updatePet(eq(1L), any(PetDTO.class)))
                .thenThrow(new RuntimeException("Pet not found"));

        mockMvc.perform(put("/api/pets/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(petDTO)))
                .andExpect(status().isNotFound());

        verify(petService, times(1)).updatePet(eq(1L), any(PetDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePet() throws Exception {
        doNothing().when(petService).deletePet(1L);

        mockMvc.perform(delete("/api/pets/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(petService, times(1)).deletePet(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePetNotFound() throws Exception {
        doThrow(new RuntimeException("Pet not found")).when(petService).deletePet(1L);

        mockMvc.perform(delete("/api/pets/1")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(petService, times(1)).deletePet(1L);
    }
}