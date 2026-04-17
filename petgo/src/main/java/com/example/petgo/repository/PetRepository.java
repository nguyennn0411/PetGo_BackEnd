package com.example.petgo.repository;

import com.example.petgo.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PetRepository extends JpaRepository<Pet, Long> {

    @Query("""
            select p
            from Pet p
            join fetch p.owner o
            where o.id = :ownerUserId
              and p.deletedAt is null
              and upper(coalesce(p.status, 'ACTIVE')) <> 'INACTIVE'
            order by p.createdAt desc, p.id desc
            """)
    List<Pet> findActiveByOwnerUserId(@Param("ownerUserId") Long ownerUserId);

    @Query("""
            select p
            from Pet p
            join fetch p.owner o
            where p.id = :petId
              and o.id = :ownerUserId
              and p.deletedAt is null
            """)
    Optional<Pet> findOwnedActivePet(@Param("ownerUserId") Long ownerUserId, @Param("petId") Long petId);

    @Query("""
            select count(p)
            from Pet p
            where p.owner.id = :ownerUserId
              and p.deletedAt is null
              and upper(coalesce(p.status, 'ACTIVE')) <> 'INACTIVE'
            """)
    long countActiveByOwnerUserId(@Param("ownerUserId") Long ownerUserId);
}
