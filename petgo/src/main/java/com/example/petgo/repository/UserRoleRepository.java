package com.example.petgo.repository;

import com.example.petgo.entity.RoleType;
import com.example.petgo.entity.User;
import com.example.petgo.entity.UserRole;
import com.example.petgo.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
  List<UserRole> findByUser_Id(Long userId);

  @Query("""
      select distinct u
      from UserRole ur
      join ur.user u
      join ur.role r
      where r.code in :roleCodes
        and u.deletedAt is null
      order by u.id asc
      """)
  List<User> findActiveUsersByRoleCodes(@Param("roleCodes") List<RoleType> roleCodes);
}
