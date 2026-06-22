package org.example.menaandfeena_finalproject;

import org.assertj.core.api.Assertions;
import org.example.menaandfeena_finalproject.Model.Landmark;
import org.example.menaandfeena_finalproject.Model.Neighborhood;
import org.example.menaandfeena_finalproject.Repository.LandmarkRepository;
import org.example.menaandfeena_finalproject.Repository.NeighborhoodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class LandmarkRepositoryTest {

    @Autowired
    LandmarkRepository landmarkRepository;

    @Autowired
    NeighborhoodRepository neighborhoodRepository;

    Neighborhood neighborhood;
    Landmark landmark1;
    Landmark landmark2;

    @BeforeEach
    void setUp() {
        neighborhood = new Neighborhood();
        neighborhood.setName("النرجس");
        neighborhood.setCity("الرياض");
        neighborhood.setRegisteredPopulation(3);

        landmark1 = new Landmark();
        landmark1.setName("مسجد النرجس");
        landmark1.setType("MOSQUE");
        landmark1.setLatitude(24.85);
        landmark1.setLongitude(46.65);
        landmark1.setNeighborhood(neighborhood);

        landmark2 = new Landmark();
        landmark2.setName("حديقة النرجس");
        landmark2.setType("PARK");
        landmark2.setLatitude(24.86);
        landmark2.setLongitude(46.66);
        landmark2.setNeighborhood(neighborhood);
    }

    @Test
    public void findLandmarkByIdTest() {
        neighborhoodRepository.save(neighborhood);
        landmarkRepository.save(landmark1);

        Landmark found = landmarkRepository.findLandmarkById(landmark1.getId());

        Assertions.assertThat(found).isEqualTo(landmark1);
    }

    @Test
    public void findByNeighborhoodTest() {
        neighborhoodRepository.save(neighborhood);
        landmarkRepository.save(landmark1);
        landmarkRepository.save(landmark2);

        List<Landmark> result = landmarkRepository.findByNeighborhood(neighborhood);

        Assertions.assertThat(result).hasSize(2);
    }

    @Test
    public void findByNeighborhoodAndTypeTest() {
        neighborhoodRepository.save(neighborhood);
        landmarkRepository.save(landmark1);
        landmarkRepository.save(landmark2);

        List<Landmark> result = landmarkRepository.findByNeighborhoodAndType(neighborhood, "MOSQUE");

        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.get(0).getType()).isEqualTo("MOSQUE");
    }

    @Test
    public void existsByNameAndTypeAndNeighborhoodIdTest() {
        neighborhoodRepository.save(neighborhood);
        landmarkRepository.save(landmark1);

        boolean exists = landmarkRepository.existsByNameAndTypeAndNeighborhoodId(
                "مسجد النرجس",
                "MOSQUE",
                neighborhood.getId()
        );

        Assertions.assertThat(exists).isTrue();
    }
}