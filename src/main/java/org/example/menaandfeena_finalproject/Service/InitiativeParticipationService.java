package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.InitiativeParticipationInDTO;
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

    public void addInitiativeParticipation(InitiativeParticipationInDTO dto) {
        User user = userRepository.findUserById(dto.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }

        Initiative initiative = initiativeRepository.findInitiativeById(dto.getInitiativeId());
        if (initiative == null) {
            throw new ApiException("Initiative not found");
        }

        InitiativeParticipation initiativeParticipation = new InitiativeParticipation();
        initiativeParticipation.setStatus("JOINED");
        initiativeParticipation.setJoinedAt(LocalDate.now());
        initiativeParticipation.setUser(user);
        initiativeParticipation.setInitiative(initiative);

        initiativeParticipationRepository.save(initiativeParticipation);
    }

    public void updateInitiativeParticipation(Integer id, InitiativeParticipationInDTO dto) {

        InitiativeParticipation oldParticipation = initiativeParticipationRepository.findInitiativeParticipationById(id);

        if (oldParticipation == null) {
            throw new ApiException("Initiative participation not found");
        }

        User user = userRepository.findUserById(dto.getUserId());
        if (user == null) {
            throw new ApiException("User not found");
        }

        Initiative initiative = initiativeRepository.findInitiativeById(dto.getInitiativeId());
        if (initiative == null) {
            throw new ApiException("Initiative not found");
        }

        oldParticipation.setUser(user);
        oldParticipation.setInitiative(initiative);

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
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        if (initiative.getUser() == null || initiative.getUser().getNeighborhood() == null) {
            throw new ApiException("Initiative owner neighborhood is required");
        }
        if (!initiative.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Initiative is outside your neighborhood");
        }
        if (initiative.getUser().getId().equals(userId)) {
            throw new ApiException("Initiative owner cannot join own initiative");
        }
        if (!"ACTIVE".equals(initiative.getStatus())) {
            throw new ApiException("Initiative is not active");
        }
        if (initiativeParticipationRepository.existsByUserIdAndInitiativeId(userId, initiativeId)) {
            throw new ApiException("User already joined this initiative");
        }
        if (initiative.getMaxParticipants() != null && initiativeParticipationRepository.countByInitiativeId(initiativeId) >= initiative.getMaxParticipants()) {
            throw new ApiException("Initiative is full");
        }

        InitiativeParticipation participation = new InitiativeParticipation();

        participation.setUser(user);
        participation.setInitiative(initiative);
        participation.setStatus("JOINED");
        participation.setJoinedAt(LocalDate.now());

        initiativeParticipationRepository.save(participation);
    }

    // Walaa
    public void joinFamilyMember(Integer userId, Integer familyMemberId, Integer initiativeId) {
        FamilyMember familyMember = familyMemberRepository.findFamilyMemberById(familyMemberId);

        if (familyMember == null) {
            throw new ApiException("Family member not found");
        }

        if (!familyMember.getUser().getId().equals(userId)) {
            throw new ApiException("You can only join your own family members");
        }

        Initiative initiative = initiativeRepository.findInitiativeById(initiativeId);

        if (initiative == null) {
            throw new ApiException("Initiative not found");
        }
        User user = familyMember.getUser();
        if (user == null) {
            throw new ApiException("Family member owner not found");
        }
        if (user.getNeighborhood() == null) {
            throw new ApiException("User neighborhood is required");
        }
        if (initiative.getUser() == null || initiative.getUser().getNeighborhood() == null) {
            throw new ApiException("Initiative owner neighborhood is required");
        }
        if (!initiative.getUser().getNeighborhood().getId().equals(user.getNeighborhood().getId())) {
            throw new ApiException("Initiative is outside your neighborhood");
        }
        if (initiative.getUser().getId().equals(user.getId())) {
            throw new ApiException("Initiative owner family cannot join own initiative");
        }
        if (!"ACTIVE".equals(initiative.getStatus())) {
            throw new ApiException("Initiative is not active");
        }
        if (initiativeParticipationRepository.existsByUserIdAndInitiativeId(user.getId(), initiativeId)) {
            throw new ApiException("User already joined this initiative");
        }
        if (initiative.getMaxParticipants() != null && initiativeParticipationRepository.countByInitiativeId(initiativeId) >= initiative.getMaxParticipants()) {
            throw new ApiException("Initiative is full");
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
