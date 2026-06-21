package org.example.menaandfeena_finalproject.Service;

import lombok.RequiredArgsConstructor;
import org.example.menaandfeena_finalproject.Api.ApiException;
import org.example.menaandfeena_finalproject.DTO.In.FamilyMemberInDTO;
import org.example.menaandfeena_finalproject.Model.FamilyMember;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.FamilyMemberRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyMemberService {
    private final FamilyMemberRepository familyMemberRepository;
    private final UserRepository userRepository;


    public List<FamilyMember> getAll() {
        return familyMemberRepository.findAll();
    }


    public void add(Integer userId, FamilyMemberInDTO dto) {
        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("Associated user not found");
        }

        FamilyMember familyMember = new FamilyMember();
        familyMember.setName(dto.getName());
        familyMember.setAge(dto.getAge());
        familyMember.setGender(dto.getGender());
        familyMember.setRelation(dto.getRelation());
        familyMember.setUser(user);
        familyMemberRepository.save(familyMember);
    }



    public void update(Integer userId, Integer id, FamilyMemberInDTO dto) {
        User user = userRepository.findUserById(userId);
        if (user == null) throw new ApiException("User not found");

        FamilyMember old = familyMemberRepository.findFamilyMemberById(id);
        if (old == null) throw new ApiException("Family member not found");
        if (old.getUser() == null || !old.getUser().getId().equals(userId)) {
            throw new ApiException("Family member does not belong to this user");
        }

        old.setName(dto.getName());
        old.setAge(dto.getAge());
        old.setGender(dto.getGender());
        old.setRelation(dto.getRelation());
        familyMemberRepository.save(old);
    }

    public void delete(Integer userId, Integer id) {
        User user = userRepository.findUserById(userId);
        if (user == null) throw new ApiException("User not found");

        FamilyMember familyMember = familyMemberRepository.findFamilyMemberById(id);
        if (familyMember == null) throw new ApiException("Family member not found");
        if (familyMember.getUser() == null || !familyMember.getUser().getId().equals(userId)) {
            throw new ApiException("Family member does not belong to this user");
        }
        familyMemberRepository.delete(familyMember);
    }

    //Reenad
    public List<FamilyMember> getMyFamily(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new ApiException("User not found");
        }
        return familyMemberRepository.findByUserId(userId);
    }

}
