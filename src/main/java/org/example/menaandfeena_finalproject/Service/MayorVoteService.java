package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.MayorVoteInDTO;
import org.example.menaandfeena_finalproject.DTO.Out.MayorVoteOutDTO;
import org.example.menaandfeena_finalproject.Model.MayorVote;
import org.example.menaandfeena_finalproject.Repository.MayorVoteRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MayorVoteService {

    private final MayorVoteRepository mayorVoteRepository;

    public List<MayorVoteOutDTO> getAllMayorVotes() {
        List<MayorVoteOutDTO> mayorVoteOutDTOS = new ArrayList<>();

        for (MayorVote mayorVote : mayorVoteRepository.findAll()) {
            mayorVoteOutDTOS.add(toOutDTO(mayorVote));
        }

        return mayorVoteOutDTOS;
    }

    public void addMayorVote(MayorVoteInDTO mayorVoteInDTO) {
        MayorVote mayorVote = new MayorVote();
        mayorVote.setCreatedAt(mayorVoteInDTO.getCreatedAt());

        mayorVoteRepository.save(mayorVote);
    }

    public void updateMayorVote(Integer id, MayorVoteInDTO mayorVoteInDTO) {
        MayorVote oldMayorVote = mayorVoteRepository.findMayorVoteById(id);

        if (oldMayorVote == null) {
            throw new ApiException("Mayor vote not found");
        }

        oldMayorVote.setCreatedAt(mayorVoteInDTO.getCreatedAt());

        mayorVoteRepository.save(oldMayorVote);
    }

    public void deleteMayorVote(Integer id) {
        MayorVote mayorVote = mayorVoteRepository.findMayorVoteById(id);

        if (mayorVote == null) {
            throw new ApiException("Mayor vote not found");
        }

        mayorVoteRepository.delete(mayorVote);
    }

    private MayorVoteOutDTO toOutDTO(MayorVote mayorVote) {
        return new MayorVoteOutDTO(mayorVote.getId(), mayorVote.getCreatedAt());
    }
}
