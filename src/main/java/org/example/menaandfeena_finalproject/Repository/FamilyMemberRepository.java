package org.example.menaandfeena_finalproject.Repository;

import org.example.menaandfeena_finalproject.Model.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Integer>
{ FamilyMember findFamilyMemberById(Integer id);
  List<FamilyMember> findByUserId(Integer userId);



}
