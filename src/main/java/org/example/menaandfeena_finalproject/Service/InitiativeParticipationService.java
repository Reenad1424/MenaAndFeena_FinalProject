package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.FamilyMember;
import org.example.menaandfeena_finalproject.Model.Initiative;
import org.example.menaandfeena_finalproject.Model.InitiativeParticipation;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.FamilyMemberRepository;
import org.example.menaandfeena_finalproject.Repository.InitiativeParticipationRepository;
import org.example.menaandfeena_finalproject.Repository.InitiativeRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InitiativeParticipationService {
    private final InitiativeParticipationRepository initiativeParticipationRepository;
    private final UserRepository userRepository;
    private final InitiativeRepository initiativeRepository;
    private final FamilyMemberRepository familyMemberRepository;

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

   // Walaa
    public void joinInitiative(Integer userId, Integer initiativeId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new ApiException("User not found");
        }

        Initiative initiative = initiativeRepository.findInitiativeById(initiativeId);
        if (initiative == null) {
            throw new ApiException("Initiative not found");
        }

        InitiativeParticipation participation = new InitiativeParticipation();

        participation.setUser(user);
        participation.setInitiative(initiative);
        participation.setStatus("JOINED");
        participation.setJoinedAt(LocalDate.now());

        initiativeParticipationRepository.save(participation);
    }

    // Walaa
    public void joinFamilyMember(Integer familyMemberId, Integer initiativeId) {
        FamilyMember familyMember = familyMemberRepository.findFamilyMemberById(familyMemberId);

        if (familyMember == null) {
            throw new ApiException("Family member not found");
        }

        Initiative initiative = initiativeRepository.findInitiativeById(initiativeId);

        if (initiative == null) {
            throw new ApiException("Initiative not found");
        }

        InitiativeParticipation participation = new InitiativeParticipation();
        participation.setUser(familyMember.getUser());
        participation.setInitiative(initiative);
        participation.setStatus("JOINED");
        participation.setJoinedAt(LocalDate.now());
        initiativeParticipationRepository.save(participation);
    }

// Walaa
public List<InitiativeParticipation> getParticipants(Integer initiativeId) { //عرض جميع المشاركين في مبادرة معينة

    Initiative initiative = initiativeRepository.findInitiativeById(initiativeId);
    if (initiative == null) {
        throw new ApiException("Initiative not found");
    }

    return initiativeParticipationRepository.findByInitiative_Id(initiativeId);
}












}
