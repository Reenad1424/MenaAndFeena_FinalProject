package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, Integer> {
    Insurance findInsuranceById(Integer id);
}
