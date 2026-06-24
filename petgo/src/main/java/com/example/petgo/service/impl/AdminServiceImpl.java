package com.example.petgo.service.impl;

import com.example.petgo.dto.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import com.example.petgo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ServiceCategoryRepository serviceCategoryRepository;
    private final CatalogServiceRepository catalogServiceRepository;
    private final HomeSliderRepository homeSliderRepository;

    @Override
    @Transactional
    public List<ServiceCategoryResponse> getAllCategories() {
        List<ServiceCategory> categories = serviceCategoryRepository.findAllByOrderByNameAscIdAsc();
        syncActiveAncestors(categories);
        return buildCategoryTree(categories, null);
    }

    @Override
    @Transactional
    public ServiceCategoryResponse createCategory(ServiceCategoryRequest request) {
        ServiceCategory category = new ServiceCategory();
        mapCategoryRequestToEntity(request, category);
        category = serviceCategoryRepository.save(category);
        return mapToCategoryResponse(category);
    }

    @Override
    @Transactional
    public ServiceCategoryResponse updateCategory(Long id, ServiceCategoryRequest request) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục."));
        mapCategoryRequestToEntity(request, category);
        category = serviceCategoryRepository.saveAndFlush(category);
        return mapToCategoryResponse(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id, ServiceCategoryDeleteRequest request) {
        ServiceCategory category = serviceCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục."));

        if (request != null && Boolean.TRUE.equals(request.hardDelete())) {
            hardDeleteCategoryTree(category, request.moveServicesToCategoryId());
        } else {
            deactivateCategoryTree(category);
            serviceCategoryRepository.saveAndFlush(category);
        }
    }

    private void hardDeleteCategoryTree(ServiceCategory category, Long moveToCategoryId) {
        List<Long> allIds = collectCategoryTreeIds(category);
        allIds.add(category.getId());
        List<ServiceCategory> categoriesToDelete = serviceCategoryRepository.findAllById(allIds);

        for (ServiceCategory cat : categoriesToDelete) {
            List<CatalogService> services = catalogServiceRepository.findByCategories_Id(cat.getId());
            if (!services.isEmpty()) {
                if (moveToCategoryId != null) {
                    ServiceCategory target = serviceCategoryRepository.findById(moveToCategoryId)
                            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục đích để di chuyển dịch vụ."));
                    for (CatalogService svc : services) {
                        List<ServiceCategory> cats = new ArrayList<>(svc.getCategories());
                        cats.remove(cat);
                        cats.add(target);
                        svc.setCategories(cats);
                    }
                    catalogServiceRepository.saveAll(services);
                } else {
                    throw new BadRequestException(
                            "Danh mục \"" + cat.getName() + "\" có " + services.size()
                            + " dịch vụ. Vui lòng chọn danh mục khác để di chuyển hoặc xoá các dịch vụ trước.");
                }
            }
        }

        for (ServiceCategory cat : categoriesToDelete) {
            cat.setParent(null);
        }
        serviceCategoryRepository.saveAll(categoriesToDelete);
        serviceCategoryRepository.flush();

        serviceCategoryRepository.deleteAll(categoriesToDelete);
    }

    private List<Long> collectCategoryTreeIds(ServiceCategory category) {
        List<Long> ids = new ArrayList<>();
        for (ServiceCategory child : category.getChildren()) {
            ids.addAll(collectCategoryTreeIds(child));
            ids.add(child.getId());
        }
        return ids;
    }

    private void mapCategoryRequestToEntity(ServiceCategoryRequest request, ServiceCategory category) {
        String normalizedName = normalizeRequired(request.getName(), "Tên danh mục không được để trống");
        category.setName(normalizedName);
        category.setDescription(normalizeNullable(request.getDescription()));
        category.setParent(resolveParent(request.getParentId(), category.getId()));
        Boolean active = request.getActive() != null ? request.getActive() : true;
        category.setActive(active);
        if (Boolean.TRUE.equals(active)) {
            activateCategoryAncestors(category);
        } else {
            deactivateCategoryTree(category);
        }
    }

    private String normalizeRequired(String value, String message) {
        String normalized = normalizeNullable(value);
        if (normalized == null) {
            throw new BadRequestException(message);
        }
        return normalized;
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private ServiceCategoryResponse mapToCategoryResponse(ServiceCategory category) {
        return mapToCategoryResponse(category, List.of());
    }

    private ServiceCategoryResponse mapToCategoryResponse(ServiceCategory category,
            List<ServiceCategoryResponse> children) {
        return ServiceCategoryResponse.builder()
                .id(category.getId())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .name(category.getName())
                .description(category.getDescription())
                .active(category.getActive())
                .children(children)
                .build();
    }

    private List<ServiceCategoryResponse> buildCategoryTree(List<ServiceCategory> categories, Long parentId) {
        return categories.stream()
                .filter(category -> Objects.equals(parentIdOf(category), parentId))
                .map(category -> mapToCategoryResponse(category, buildCategoryTree(categories, category.getId())))
                .toList();
    }

    private Long parentIdOf(ServiceCategory category) {
        return category.getParent() != null ? category.getParent().getId() : null;
    }

    private ServiceCategory resolveParent(Long parentId, Long currentCategoryId) {
        if (parentId == null) {
            return null;
        }
        if (Objects.equals(parentId, currentCategoryId)) {
            throw new BadRequestException("Danh mục cha không được trùng với danh mục hiện tại.");
        }
        ServiceCategory parent = serviceCategoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục cha."));
        if (currentCategoryId != null && createsCycle(parent, currentCategoryId)) {
            throw new BadRequestException("Danh mục cha không hợp lệ vì tạo vòng lặp phân cấp.");
        }
        return parent;
    }

    private boolean createsCycle(ServiceCategory parent, Long currentCategoryId) {
        ServiceCategory cursor = parent;
        while (cursor != null) {
            if (Objects.equals(cursor.getId(), currentCategoryId)) {
                return true;
            }
            cursor = cursor.getParent();
        }
        return false;
    }

    private void deactivateCategoryTree(ServiceCategory category) {
        category.setActive(false);
        category.getChildren().forEach(this::deactivateCategoryTree);
    }

    private void activateCategoryAncestors(ServiceCategory category) {
        ServiceCategory parent = category.getParent();
        Set<Long> visitedIds = new HashSet<>();
        while (parent != null && visitedIds.add(parent.getId())) {
            parent.setActive(true);
            parent = parent.getParent();
        }
    }

    private void syncActiveAncestors(List<ServiceCategory> categories) {
        boolean hasChanged = false;
        for (ServiceCategory category : categories) {
            if (!Boolean.TRUE.equals(category.getActive())) {
                continue;
            }

            ServiceCategory parent = category.getParent();
            Set<Long> visitedIds = new HashSet<>();
            while (parent != null && visitedIds.add(parent.getId())) {
                if (!Boolean.TRUE.equals(parent.getActive())) {
                    parent.setActive(true);
                    hasChanged = true;
                }
                parent = parent.getParent();
            }
        }

        if (hasChanged) {
            serviceCategoryRepository.saveAllAndFlush(categories);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomeSliderResponse> getAllHomeSliders() {
        return homeSliderRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(this::mapToHomeSliderResponse)
                .toList();
    }

    @Override
    @Transactional
    public HomeSliderResponse createHomeSlider(HomeSliderRequest request) {
        HomeSlider slider = new HomeSlider();
        mapHomeSliderRequestToEntity(request, slider);
        return mapToHomeSliderResponse(homeSliderRepository.save(slider));
    }

    @Override
    @Transactional
    public HomeSliderResponse updateHomeSlider(Long id, HomeSliderRequest request) {
        HomeSlider slider = homeSliderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy slider trang chủ."));
        mapHomeSliderRequestToEntity(request, slider);
        return mapToHomeSliderResponse(homeSliderRepository.saveAndFlush(slider));
    }

    @Override
    @Transactional
    public void deleteHomeSlider(Long id) {
        if (!homeSliderRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy slider trang chủ.");
        }
        homeSliderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public HomeSliderResponse updateHomeSliderVisibility(Long id, Boolean active) {
        if (active == null) {
            throw new BadRequestException("Trạng thái hiển thị không được để trống.");
        }
        HomeSlider slider = homeSliderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy slider trang chủ."));
        slider.setActive(active);
        return mapToHomeSliderResponse(homeSliderRepository.saveAndFlush(slider));
    }

    private void mapHomeSliderRequestToEntity(HomeSliderRequest request, HomeSlider slider) {
        slider.setTitle(normalizeRequired(request.title(), "Tiêu đề slider không được để trống"));
        slider.setSubtitle(normalizeNullable(request.subtitle()));
        slider.setImageUrl(normalizeRequired(request.imageUrl(), "Ảnh slider không được để trống"));
        slider.setCtaLabel(normalizeNullable(request.ctaLabel()));
        slider.setCtaUrl(normalizeNullable(request.ctaUrl()));
        slider.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        slider.setActive(request.active() != null ? request.active() : true);
    }

    private HomeSliderResponse mapToHomeSliderResponse(HomeSlider slider) {
        return HomeSliderResponse.builder()
                .id(slider.getId())
                .title(slider.getTitle())
                .subtitle(slider.getSubtitle())
                .imageUrl(slider.getImageUrl())
                .ctaLabel(slider.getCtaLabel())
                .ctaUrl(slider.getCtaUrl())
                .sortOrder(slider.getSortOrder())
                .active(slider.getActive())
                .build();
    }

}