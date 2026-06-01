package com.example.petgo.controller;

import com.example.petgo.dto.shop.ShopDtos.*;
import com.example.petgo.service.ShopStoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class ShopController {
    private final ShopStoreService shopStoreService;

    @GetMapping("/shop/categories")
    public ResponseEntity<List<CategoryResponse>> categories() {
        return ResponseEntity.ok(shopStoreService.getCategories());
    }

    @GetMapping("/shop/products")
    public ResponseEntity<List<ProductResponse>> products(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Boolean hot,
            @RequestParam(required = false) Boolean featured
    ) {
        return ResponseEntity.ok(shopStoreService.getProducts(keyword, categoryId, categorySlug, species, hot, featured, false));
    }

    @GetMapping("/shop/products/{slug}")
    public ResponseEntity<ProductResponse> product(@PathVariable String slug) {
        return ResponseEntity.ok(shopStoreService.getProductBySlug(slug));
    }

    @GetMapping("/cart")
    public ResponseEntity<CartResponse> cart(@RequestParam Long userId) {
        return ResponseEntity.ok(shopStoreService.getCart(userId));
    }

    @PostMapping("/cart/items")
    public ResponseEntity<CartResponse> addCart(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(shopStoreService.addCartItem(request));
    }

    @PutMapping("/cart/items/{id}")
    public ResponseEntity<CartResponse> updateCart(@PathVariable Long id, @Valid @RequestBody CartItemUpdateRequest request) {
        return ResponseEntity.ok(shopStoreService.updateCartItem(id, request));
    }

    @DeleteMapping("/cart/items/{id}")
    public ResponseEntity<Void> removeCart(@PathVariable Long id) {
        shopStoreService.removeCartItem(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cart")
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        shopStoreService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/shop/orders/checkout")
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(shopStoreService.checkout(request));
    }

    @GetMapping("/shop/orders/my")
    public ResponseEntity<List<OrderResponse>> myOrders(@RequestParam Long userId) {
        return ResponseEntity.ok(shopStoreService.getMyOrders(userId));
    }

    @GetMapping("/shop/orders/{orderCode}")
    public ResponseEntity<OrderResponse> order(@PathVariable String orderCode) {
        return ResponseEntity.ok(shopStoreService.getOrderByCode(orderCode));
    }
}
