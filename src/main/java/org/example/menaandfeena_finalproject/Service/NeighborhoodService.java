package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Repository.NeighborhoodRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NeighborhoodService {
    private final NeighborhoodRepository neighborhoodRepository;

    public List<Neighborhood> getAll() {
        return neighborhoodRepository.findAll();
    }

    public void add(Neighborhood neighborhood) {
        neighborhoodRepository.save(neighborhood);
    }

    public void update(Integer id, Neighborhood neighborhood) {
        Neighborhood old = neighborhoodRepository.findNeighborhoodById(id);
        if (old == null) throw new ApiException("Neighborhood not found");
        old.setName(neighborhood.getName());
        old.setCity(neighborhood.getCity());
        old.setEstimatedPopulation(neighborhood.getEstimatedPopulation());
        old.setRegisteredPopulation(neighborhood.getRegisteredPopulation());
        neighborhoodRepository.save(old);
    }

    public void delete(Integer id) {
        Neighborhood neighborhood = neighborhoodRepository.findNeighborhoodById(id);
        if (neighborhood == null) throw new ApiException("Neighborhood not found");
        neighborhoodRepository.delete(neighborhood);
    }
}
