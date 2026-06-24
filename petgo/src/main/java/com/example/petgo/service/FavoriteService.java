package com.example.petgo.service;

import com.example.petgo.dto.CatalogServiceResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Set;

public interface FavoriteService {

    boolean toggleFavorite(HttpServletRequest request, Long serviceId);

    List<CatalogServiceResponse> getUserFavorites(HttpServletRequest request);

    Set<Long> getUserFavoriteIds(HttpServletRequest request);
}
