package com.example.petgo.repository;

import com.example.petgo.entity.ProviderPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProviderPhotoRepository extends JpaRepository<ProviderPhoto, Long> {

  @Query("""
      select pp
      from ProviderPhoto pp
      where pp.provider.id = :providerId
        and pp.mediaType = 'IMAGE'
      order by pp.primary desc, pp.sortOrder asc, pp.id asc
      """)
  List<ProviderPhoto> findImagesByProviderId(@Param("providerId") Long providerId);

  void deleteByProvider_Id(Long providerId);
}
