package org.example.menaandfeena_finalproject.Service;


import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.Model.MayorProfile;
import org.example.menaandfeena_finalproject.Repository.MayorProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MayorProfileService {
    private final MayorProfileRepository mayorProfileRepository;

    public List<MayorProfile> getAllMayorProfiles() {
        return mayorProfileRepository.findAll();
    }

    public void addMayorProfile(MayorProfile mayorProfile) {
        mayorProfileRepository.save(mayorProfile);
    }

    public void updateMayorProfile(Integer id, MayorProfile mayorProfile) {

        MayorProfile oldMayorProfile = mayorProfileRepository.findMayorProfileById(id);

        if (oldMayorProfile == null) {
            throw new ApiException("Mayor profile not found");
        }

        oldMayorProfile.setStatus(mayorProfile.getStatus());
        oldMayorProfile.setStartDate(mayorProfile.getStartDate());
        oldMayorProfile.setEndDate(mayorProfile.getEndDate());
       // oldMayorProfile.setUser(mayorProfile.getUser());
     //   oldMayorProfile.setNeighborhood(mayorProfile.getNeighborhood());

        mayorProfileRepository.save(oldMayorProfile);
    }

    public void deleteMayorProfile(Integer id) {

        MayorProfile mayorProfile = mayorProfileRepository.findMayorProfileById(id);

        if (mayorProfile == null) {
            throw new ApiException("Mayor profile not found");
        }
        mayorProfileRepository.delete(mayorProfile);
    }
}
