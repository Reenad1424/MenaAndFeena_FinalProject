package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Repository.InitiativeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InitiativeService {
    private final InitiativeRepository initiativeRepository;

    public List<Initiative> getAllInitiatives() {
        return initiativeRepository.findAll();
    }

    public void addInitiative(Initiative initiative) {
        initiativeRepository.save(initiative);
    }

    public void updateInitiative(Integer id, Initiative initiative) {
        Initiative oldInitiative = initiativeRepository.findInitiativeById(id);

        if (oldInitiative == null) {
            throw new ApiException("Initiative not found");
        }

        oldInitiative.setTitle(initiative.getTitle());
        oldInitiative.setDescription(initiative.getDescription());
        oldInitiative.setDate(initiative.getDate());
        oldInitiative.setMaxParticipants(initiative.getMaxParticipants());
        oldInitiative.setStatus(initiative.getStatus());

        initiativeRepository.save(oldInitiative);
    }

    public void deleteInitiative(Integer id) {
        Initiative initiative = initiativeRepository.findInitiativeById(id);

        if (initiative == null) {
            throw new ApiException("Initiative not found");
        }

        initiativeRepository.delete(initiative);
    }
}
