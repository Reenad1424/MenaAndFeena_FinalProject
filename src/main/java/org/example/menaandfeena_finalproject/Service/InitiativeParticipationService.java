package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.InitiativeParticipation;
import org.example.menaandfeena_finalproject.Repository.InitiativeParticipationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InitiativeParticipationService {
    private final InitiativeParticipationRepository initiativeParticipationRepository;

    public List<InitiativeParticipation> getAllInitiativeParticipations() {
        return initiativeParticipationRepository.findAll();
    }

    public void addInitiativeParticipation(InitiativeParticipation initiativeParticipation) {
        initiativeParticipationRepository.save(initiativeParticipation);
    }

    public void updateInitiativeParticipation(Integer id, InitiativeParticipation initiativeParticipation) {

        InitiativeParticipation oldParticipation = initiativeParticipationRepository.findInitiativeParticipationById(id);

        if (oldParticipation == null) {
            throw new ApiException("Initiative participation not found");
        }

        oldParticipation.setStatus(initiativeParticipation.getStatus());
        oldParticipation.setJoinedAt(initiativeParticipation.getJoinedAt());
        // oldParticipation.setUser(initiativeParticipation.getUser());
// oldParticipation.setInitiative(initiativeParticipation.getInitiative());

        initiativeParticipationRepository.save(oldParticipation);
    }

    public void deleteInitiativeParticipation(Integer id) {

        InitiativeParticipation participation = initiativeParticipationRepository.findInitiativeParticipationById(id);

        if (participation == null) {
            throw new ApiException("Initiative participation not found");
        }

        initiativeParticipationRepository.delete(participation);
    }
}
