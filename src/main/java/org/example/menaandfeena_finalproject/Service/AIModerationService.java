package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.AIModeration;
import org.example.menaandfeena_finalproject.Repository.AIModerationRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AIModerationService {
    private final AIModerationRepository aiModerationRepository;

    public List<AIModeration> getAll() {
        return aiModerationRepository.findAll();
    }

    public void add(AIModeration aiModeration) {
        aiModerationRepository.save(aiModeration);
    }

    public void update(Integer id, AIModeration aiModeration) {
        AIModeration old = aiModerationRepository.findAIModerationById(id);
        if (old == null) throw new ApiException("AI moderation entry not found");
        old.setStatus(aiModeration.getStatus());
        old.setReason(aiModeration.getReason());
        old.setConfidenceScore(aiModeration.getConfidenceScore());
        aiModerationRepository.save(old);
    }

    public void delete(Integer id) {
        AIModeration aiModeration = aiModerationRepository.findAIModerationById(id);
        if (aiModeration == null) throw new ApiException("AI moderation entry not found");
        aiModerationRepository.delete(aiModeration);
    }
}
