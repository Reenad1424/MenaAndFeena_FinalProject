package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.InitiativeInDTO;
import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.InitiativeRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InitiativeService {
    private final InitiativeRepository initiativeRepository;
    private final UserRepository userRepository;
    public List<Initiative> getAllInitiatives() {
        return initiativeRepository.findAll();
    }

    public void addInitiative(InitiativeInDTO initiativeInDTO) {
        Initiative initiative = new Initiative();
        initiative.setTitle(initiativeInDTO.getTitle());
        initiative.setDescription(initiativeInDTO.getDescription());
        initiative.setDate(initiativeInDTO.getDate());
        initiative.setMaxParticipants(initiativeInDTO.getMaxParticipants());
        initiative.setCategory(initiativeInDTO.getCategory());
        initiative.setStatus("ACTIVE");

        initiativeRepository.save(initiative);
    }

    public void updateInitiative(Integer id, InitiativeInDTO initiativeInDTO) {
        Initiative oldInitiative = initiativeRepository.findInitiativeById(id);

        if (oldInitiative == null) {
            throw new ApiException("Initiative not found");
        }

        oldInitiative.setTitle(initiativeInDTO.getTitle());
        oldInitiative.setDescription(initiativeInDTO.getDescription());
        oldInitiative.setDate(initiativeInDTO.getDate());
        oldInitiative.setMaxParticipants(initiativeInDTO.getMaxParticipants());
        oldInitiative.setCategory(initiativeInDTO.getCategory());

        initiativeRepository.save(oldInitiative);
    }

    public void deleteInitiative(Integer id) {
        Initiative initiative = initiativeRepository.findInitiativeById(id);

        if (initiative == null) {
            throw new ApiException("Initiative not found");
        }

        initiativeRepository.delete(initiative);
    }


// Walaa
    public List<Initiative> getInitiativesByCategory(String category) {
        return initiativeRepository.findInitiativesByCategory(category);
    }

// Walaa
public List<Initiative> getUpcomingInitiatives() {
    return initiativeRepository.findInitiativesByDateAfter(LocalDate.now());
}

// Walaa
public Initiative getInitiativeById(Integer id) {
    Initiative initiative = initiativeRepository.findInitiativeById(id);
    if (initiative == null) {
        throw new ApiException("Initiative not found");
    }
    return initiative;

}



// Walaa
public void createInitiative(Integer userId, InitiativeInDTO initiativeInDTO) {

    User user = userRepository.findUserById(userId);

    if (user == null) {
        throw new ApiException("User not found");
    }

    Initiative initiative = new Initiative();
    initiative.setTitle(initiativeInDTO.getTitle());
    initiative.setDescription(initiativeInDTO.getDescription());
    initiative.setDate(initiativeInDTO.getDate());
    initiative.setMaxParticipants(initiativeInDTO.getMaxParticipants());
    initiative.setCategory(initiativeInDTO.getCategory());
    initiative.setUser(user);
    initiative.setStatus("ACTIVE");

    initiativeRepository.save(initiative);
}


}
