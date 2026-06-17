package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.ElectionRoundInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.ElectionRoundOutDTO;
import org.example.menaandfeena_finalproject.Model.ElectionRound;
import org.example.menaandfeena_finalproject.Repository.ElectionRoundRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectionRoundService {

    private final ElectionRoundRepository electionRoundRepository;

    public List<ElectionRoundOutDTO> getAllElectionRounds() {
        List<ElectionRoundOutDTO> electionRoundOutDTOS = new ArrayList<>();

        for (ElectionRound electionRound : electionRoundRepository.findAll()) {
            electionRoundOutDTOS.add(toOutDTO(electionRound));
        }

        return electionRoundOutDTOS;
    }

    public void addElectionRound(ElectionRoundInDTO electionRoundInDTO) {
        ElectionRound electionRound = new ElectionRound();
        electionRound.setStartDate(electionRoundInDTO.getStartDate());
        electionRound.setEndDate(electionRoundInDTO.getEndDate());
        electionRound.setStatus(electionRoundInDTO.getStatus());

        electionRoundRepository.save(electionRound);
    }

    public void updateElectionRound(Integer id, ElectionRoundInDTO electionRoundInDTO) {
        ElectionRound oldElectionRound = electionRoundRepository.findElectionRoundById(id);

        if (oldElectionRound == null) {
            throw new ApiException("Election round not found");
        }

        oldElectionRound.setStartDate(electionRoundInDTO.getStartDate());
        oldElectionRound.setEndDate(electionRoundInDTO.getEndDate());
        oldElectionRound.setStatus(electionRoundInDTO.getStatus());

        electionRoundRepository.save(oldElectionRound);
    }

    public void deleteElectionRound(Integer id) {
        ElectionRound electionRound = electionRoundRepository.findElectionRoundById(id);

        if (electionRound == null) {
            throw new ApiException("Election round not found");
        }

        electionRoundRepository.delete(electionRound);
    }

    private ElectionRoundOutDTO toOutDTO(ElectionRound electionRound) {
        return new ElectionRoundOutDTO(electionRound.getId(), electionRound.getStartDate(), electionRound.getEndDate(), electionRound.getStatus());
    }
}
