package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.Landmark;
import org.example.menaandfeena_finalproject.Repository.LandmarkRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LandmarkService {
    private final LandmarkRepository landmarkRepository;

    public List<Landmark> getAll() {
        return landmarkRepository.findAll();
    }

    public void add(Landmark landmark) {
        landmarkRepository.save(landmark);
    }

    public void update(Integer id, Landmark landmark) {
        Landmark old = landmarkRepository.findLandmarkById(id);
        if (old == null) throw new ApiException("Landmark not found");
        old.setName(landmark.getName());
        old.setType(landmark.getType());
        old.setLatitude(landmark.getLatitude());
        old.setLongitude(landmark.getLongitude());
        landmarkRepository.save(old);
    }

    public void delete(Integer id) {
        Landmark landmark = landmarkRepository.findLandmarkById(id);
        if (landmark == null) throw new ApiException("Landmark not found");
        landmarkRepository.delete(landmark);
    }
}
