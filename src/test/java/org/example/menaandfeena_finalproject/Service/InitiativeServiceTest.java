package org.example.menaandfeena_finalproject.Service;

import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.InitiativeInDTO;
import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.InitiativeRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class InitiativeServiceTest {

    @Mock
    private InitiativeRepository initiativeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InitiativeService initiativeService;

    User user;
    Initiative initiative;
    InitiativeInDTO initiativeInDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFullName("Walaa");

        initiative = new Initiative();
        initiative.setId(1);
        initiative.setTitle("Clean Neighborhood");
        initiative.setDescription("Cleaning initiative");
        initiative.setDate(LocalDate.now().plusDays(3));
        initiative.setMaxParticipants(50);
        initiative.setCategory("VOLUNTEERING");
        initiative.setStatus("ACTIVE");
        initiative.setUser(user);

        initiativeInDTO = new InitiativeInDTO();
        initiativeInDTO.setTitle("Clean Neighborhood");
        initiativeInDTO.setDescription("Cleaning initiative");
        initiativeInDTO.setDate(LocalDate.now().plusDays(3));
        initiativeInDTO.setMaxParticipants(50);
        initiativeInDTO.setCategory("VOLUNTEERING");
    }

    @Test
    void createInitiative_ShouldCreateSuccessfully() {
        when(userRepository.findUserById(1)).thenReturn(user);

        initiativeService.createInitiative(1, initiativeInDTO);

        verify(initiativeRepository, times(1)).save(any(Initiative.class));
    }

    @Test
    void createInitiative_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findUserById(1)).thenReturn(null);

        assertThrows(ApiException.class, () ->
                initiativeService.createInitiative(1, initiativeInDTO)
        );
    }

    @Test
    void getInitiativeById_ShouldReturnInitiative() {
        when(initiativeRepository.findInitiativeById(1)).thenReturn(initiative);

        Initiative result = initiativeService.getInitiativeById(1);

        assertEquals(initiative, result);
    }

    @Test
    void getInitiativeById_ShouldThrowException_WhenNotFound() {
        when(initiativeRepository.findInitiativeById(1)).thenReturn(null);

        assertThrows(ApiException.class, () ->
                initiativeService.getInitiativeById(1)
        );
    }

    @Test
    void updateInitiative_ShouldUpdateSuccessfully() {
        when(initiativeRepository.findInitiativeById(1)).thenReturn(initiative);

        initiativeService.updateInitiative(1, initiativeInDTO);

        verify(initiativeRepository, times(1)).save(initiative);
        assertEquals("Clean Neighborhood", initiative.getTitle());
    }

    @Test
    void deleteInitiative_ShouldDeleteSuccessfully() {
        when(initiativeRepository.findInitiativeById(1)).thenReturn(initiative);

        initiativeService.deleteInitiative(1);

        verify(initiativeRepository, times(1)).delete(initiative);
    }
}