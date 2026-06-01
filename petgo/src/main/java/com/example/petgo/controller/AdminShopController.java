package com.example.petgo.controller;

import com.example.petgo.dto.shop.ShopDtos.OrderResponse;
import com.example.petgo.dto.shop.ShopDtos.OrderStatusUpdateRequest;
import com.example.petgo.dto.shop.ShopDtos.ProductResponse;
import com.example.petgo.dto.shop.ShopDtos.ProductUpsertRequest;
import com.example.petgo.service.ShopStoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class AdminShopController {
    private final ShopStoreService shopStoreService;

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> products(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(shopStoreService.getAdminProducts(keyword));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductUpsertRequest request) {
        return ResponseEntity.ok(shopStoreService.createProduct(request));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductUpsertRequest request) {
        return ResponseEntity.ok(shopStoreService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        shopStoreService.hideProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/shop-orders")
    public ResponseEntity<List<OrderResponse>> orders(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(shopStoreService.getAdminOrders(status));
    }

    @PutMapping("/shop-orders/{id}/status")
    public ResponseEntity<OrderResponse> status(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(shopStoreService.updateOrderStatus(id, request));
    }
}
