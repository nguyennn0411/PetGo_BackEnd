package com.example.petgo.repository;

import com.example.petgo.entity.PetPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PetPhotoRepository extends JpaRepository<PetPhoto, Long> {

    List<PetPhoto> findByPet_IdOrderByPrimaryDescSortOrderAscIdAsc(Long petId);

    void deleteByPet_Id(Long petId);
}
