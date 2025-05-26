package org.lab;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.lab.Controller.OwnerController;
import org.lab.DTO.OwnerDTO;
import org.lab.Service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OwnerController.class)
class OwnerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OwnerService ownerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createOwner() throws Exception {
        OwnerDTO ownerDTO = new OwnerDTO();
        ownerDTO.setId(1L);
        ownerDTO.setName("John");

        when(ownerService.createOwner(any(OwnerDTO.class))).thenReturn(ownerDTO);

        mockMvc.perform(post("/api/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"));

        verify(ownerService, times(1)).createOwner(any(OwnerDTO.class));
    }

    @Test
    void getOwnerById() throws Exception {
        OwnerDTO ownerDTO = new OwnerDTO();
        ownerDTO.setId(1L);
        ownerDTO.setName("John");

        when(ownerService.getOwnerById(1L)).thenReturn(ownerDTO);

        mockMvc.perform(get("/api/owners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"));

        verify(ownerService, times(1)).getOwnerById(1L);
    }

    @Test
    void getAllOwners() throws Exception {
        OwnerDTO owner1 = new OwnerDTO();
        owner1.setId(1L);
        owner1.setName("John");
        OwnerDTO owner2 = new OwnerDTO();
        owner2.setId(2L);
        owner2.setName("Jane");

        when(ownerService.getAllOwners()).thenReturn(List.of(owner1, owner2));

        mockMvc.perform(get("/api/owners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane"));

        verify(ownerService, times(1)).getAllOwners();
    }

    @Test
    void updateOwner() throws Exception {
        OwnerDTO ownerDTO = new OwnerDTO();
        ownerDTO.setId(1L);
        ownerDTO.setName("John Updated");

        when(ownerService.updateOwner(eq(1L), any(OwnerDTO.class))).thenReturn(ownerDTO);

        mockMvc.perform(put("/api/owners/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Updated"));

        verify(ownerService, times(1)).updateOwner(eq(1L), any(OwnerDTO.class));
    }

    @Test
    void deleteOwner() throws Exception {
        doNothing().when(ownerService).deleteOwner(1L);

        mockMvc.perform(delete("/api/owners/1"))
                .andExpect(status().isOk());

        verify(ownerService, times(1)).deleteOwner(1L);
    }
}