package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.MayorCandidateInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MayorCandidateOutDTO;
import org.example.menaandfeena_finalproject.Model.MayorCandidate;
import org.example.menaandfeena_finalproject.Repository.MayorCandidateRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MayorCandidateService {

    private final MayorCandidateRepository mayorCandidateRepository;

    public List<MayorCandidateOutDTO> getAllMayorCandidates() {
        List<MayorCandidateOutDTO> mayorCandidateOutDTOS = new ArrayList<>();

        for (MayorCandidate mayorCandidate : mayorCandidateRepository.findAll()) {
            mayorCandidateOutDTOS.add(toOutDTO(mayorCandidate));
        }

        return mayorCandidateOutDTOS;
    }

    public void addMayorCandidate(MayorCandidateInDTO mayorCandidateInDTO) {
        MayorCandidate mayorCandidate = new MayorCandidate();
        mayorCandidate.setAppliedAt(mayorCandidateInDTO.getAppliedAt());
        mayorCandidate.setStatus(mayorCandidateInDTO.getStatus());

        mayorCandidateRepository.save(mayorCandidate);
    }

    public void updateMayorCandidate(Integer id, MayorCandidateInDTO mayorCandidateInDTO) {
        MayorCandidate oldMayorCandidate = mayorCandidateRepository.findMayorCandidateById(id);

        if (oldMayorCandidate == null) {
            throw new ApiException("Mayor candidate not found");
        }

        oldMayorCandidate.setAppliedAt(mayorCandidateInDTO.getAppliedAt());
        oldMayorCandidate.setStatus(mayorCandidateInDTO.getStatus());

        mayorCandidateRepository.save(oldMayorCandidate);
    }

    public void deleteMayorCandidate(Integer id) {
        MayorCandidate mayorCandidate = mayorCandidateRepository.findMayorCandidateById(id);

        if (mayorCandidate == null) {
            throw new ApiException("Mayor candidate not found");
        }

        mayorCandidateRepository.delete(mayorCandidate);
    }

    private MayorCandidateOutDTO toOutDTO(MayorCandidate mayorCandidate) {
        return new MayorCandidateOutDTO(mayorCandidate.getId(), mayorCandidate.getAppliedAt(), mayorCandidate.getStatus());
    }
}
