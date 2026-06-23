package org.example.menaandfeena_finalproject.Service;

import org.example.menaandfeena_finalproject.DTO.In.FamilyMemberInDTO;
import org.example.menaandfeena_finalproject.Model.FamilyMember;
import org.example.menaandfeena_finalproject.Model.User;
import org.example.menaandfeena_finalproject.Repository.FamilyMemberRepository;
import org.example.menaandfeena_finalproject.Repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FamilyMemberServiceTest {

    @InjectMocks
    FamilyMemberService familyMemberService;

    @Mock
    FamilyMemberRepository familyMemberRepository;

    @Mock
    UserRepository userRepository;

    User user;
    FamilyMember familyMember1, familyMember2;
    FamilyMemberInDTO dto;
    List<FamilyMember> familyMembers;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setFullName("Reenad");

        familyMember1 = new FamilyMember();
        familyMember1.setId(1);
        familyMember1.setName("Sara");
        familyMember1.setAge(20);
        familyMember1.setGender("FEMALE");
        familyMember1.setRelation("SISTER");
        familyMember1.setUser(user);

        familyMember2 = new FamilyMember();
        familyMember2.setId(2);
        familyMember2.setName("Ali");
        familyMember2.setAge(10);
        familyMember2.setGender("MALE");
        familyMember2.setRelation("BROTHER");
        familyMember2.setUser(user);

        dto = new FamilyMemberInDTO();
        dto.setName("Nora");
        dto.setAge(25);
        dto.setGender("FEMALE");
        dto.setRelation("SISTER");

        familyMembers = new ArrayList<>();
        familyMembers.add(familyMember1);
        familyMembers.add(familyMember2);
    }

    @Test
    public void getAllTest() {
        when(familyMemberRepository.findAll()).thenReturn(familyMembers);

        List<FamilyMember> result = familyMemberService.getAll();

        Assertions.assertEquals(2, result.size());

        verify(familyMemberRepository, times(1)).findAll();
    }

    @Test
    public void addTest() {
        when(userRepository.findUserById(user.getId())).thenReturn(user);

        familyMemberService.add(user.getId(), dto);

        verify(userRepository, times(1)).findUserById(user.getId());
        verify(familyMemberRepository, times(1)).save(any(FamilyMember.class));
    }

    @Test
    public void updateTest() {
        when(userRepository.findUserById(user.getId())).thenReturn(user);
        when(familyMemberRepository.findFamilyMemberById(familyMember1.getId())).thenReturn(familyMember1);

        familyMemberService.update(user.getId(), familyMember1.getId(), dto);

        Assertions.assertEquals("Nora", familyMember1.getName());
        Assertions.assertEquals(25, familyMember1.getAge());

        verify(userRepository, times(1)).findUserById(user.getId());
        verify(familyMemberRepository, times(1)).findFamilyMemberById(familyMember1.getId());
        verify(familyMemberRepository, times(1)).save(familyMember1);
    }

    @Test
    public void deleteTest() {
        when(userRepository.findUserById(user.getId())).thenReturn(user);
        when(familyMemberRepository.findFamilyMemberById(familyMember1.getId())).thenReturn(familyMember1);

        familyMemberService.delete(user.getId(), familyMember1.getId());

        verify(userRepository, times(1)).findUserById(user.getId());
        verify(familyMemberRepository, times(1)).findFamilyMemberById(familyMember1.getId());
        verify(familyMemberRepository, times(1)).delete(familyMember1);
    }

    @Test
    public void getMyFamilyTest() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(familyMemberRepository.findByUserId(user.getId())).thenReturn(familyMembers);

        List<FamilyMember> result = familyMemberService.getMyFamily(user.getId());

        Assertions.assertEquals(2, result.size());

        verify(userRepository, times(1)).existsById(user.getId());
        verify(familyMemberRepository, times(1)).findByUserId(user.getId());
    }
}