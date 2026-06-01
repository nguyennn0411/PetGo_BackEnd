package com.example.petgo.service;

import com.example.petgo.dto.shop.ShopDtos.*;
import com.example.petgo.entity.*;
import com.example.petgo.exception.BadRequestException;
import com.example.petgo.exception.ResourceNotFoundException;
import com.example.petgo.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import com.example.petgo.service.PayOsService;
import com.example.petgo.dto.PaymentRequestDTO;
import com.example.petgo.dto.PaymentResponseDTO;

@Service
@RequiredArgsConstructor
public class ShopStoreService {
    private final ProductCategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final ShopOrderStatusHistoryRepository statusHistoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final PaymentRepository paymentRepository;
    private final PayOsService payOsService;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAscIdAsc().stream().map(this::toCategoryResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts(String keyword, Long categoryId, String categorySlug, String species, Boolean hot, Boolean featured, Boolean includeInactive) {
        Specification<Product> spec = (root, query, cb) -> {
            if (query != null) root.fetch("category", jakarta.persistence.criteria.JoinType.LEFT);
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isNull(root.get("deletedAt")));
            if (!Boolean.TRUE.equals(includeInactive)) {
                predicates.add(cb.isTrue(root.get("active")));
            }
            if (StringUtils.hasText(keyword)) {
                String pattern = "%" + keyword.toLowerCase(Locale.ROOT).trim() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("brand")), pattern),
                        cb.like(cb.lower(root.get("sku")), pattern)
                ));
            }
            if (categoryId != null) predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            if (StringUtils.hasText(categorySlug)) predicates.add(cb.equal(root.get("category").get("slug"), categorySlug));
            if (StringUtils.hasText(species)) {
                predicates.add(cb.or(cb.equal(root.get("targetSpecies"), species), cb.equal(root.get("targetSpecies"), "ALL")));
            }
            if (hot != null) predicates.add(hot ? cb.isTrue(root.get("hot")) : cb.isFalse(root.get("hot")));
            if (featured != null) predicates.add(featured ? cb.isTrue(root.get("featured")) : cb.isFalse(root.get("featured")));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return productRepository.findAll(spec).stream().map(this::toProductResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        return productRepository.findBySlugAndDeletedAtIsNull(slug).map(this::toProductResponse).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm: " + slug));
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        List<CartItemResponse> items = cartItemRepository.findByUser_IdOrderByIdDesc(userId).stream().map(this::toCartItemResponse).toList();
        return buildCartResponse(userId, items);
    }

    @Transactional
    public CartResponse addCartItem(CartItemRequest request) {
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        int quantity = request.quantity() == null || request.quantity() < 1 ? 1 : request.quantity();
        CartItem item = cartItemRepository.findByUser_IdAndProduct_Id(user.getId(), product.getId()).orElseGet(CartItem::new);
        if (item.getId() == null) {
            item.setUser(user);
            item.setProduct(product);
            item.setQuantity(quantity);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
        cartItemRepository.save(item);
        return getCart(user.getId());
    }

    @Transactional
    public CartResponse updateCartItem(Long cartItemId, CartItemUpdateRequest request) {
        CartItem item = cartItemRepository.findById(cartItemId).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm trong giỏ"));
        int qty = request.quantity() == null || request.quantity() < 1 ? 1 : request.quantity();
        item.setQuantity(qty);
        cartItemRepository.save(item);
        return getCart(item.getUser().getId());
    }

    @Transactional
    public void removeCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUser_Id(userId);
    }

    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        List<CartItem> cartItems = cartItemRepository.findByUser_IdOrderByIdDesc(user.getId());
        if (cartItems.isEmpty()) throw new BadRequestException("Giỏ hàng đang trống");

        ShopOrder order = new ShopOrder();
        order.setOrderCode("SO" + System.currentTimeMillis());
        order.setCustomerUser(user);
        order.setReceiverName(request.receiverName());
        order.setReceiverPhone(request.receiverPhone());
        order.setReceiverEmail(request.receiverEmail());
        order.setShippingAddress(request.shippingAddress());
        order.setWard(request.ward());
        order.setDistrict(request.district());
        order.setCity(request.city());
        order.setProvince(request.province());
        order.setPaymentMethod(StringUtils.hasText(request.paymentMethod()) ? request.paymentMethod() : "COD");
        order.setCustomerNote(request.customerNote());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            BigDecimal unitPrice = effectivePrice(product);
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            subtotal = subtotal.add(lineTotal);

            ShopOrderItem item = new ShopOrderItem();
            item.setShopOrder(order);
            item.setProduct(product);
            item.setProductNameSnapshot(product.getName());
            item.setProductSkuSnapshot(product.getSku());
            item.setProductImageSnapshot(product.getMainImageUrl());
            item.setQuantity(cartItem.getQuantity());
            item.setUnitPrice(unitPrice);
            item.setLineTotal(lineTotal);
            order.getItems().add(item);

            int nextStock = Math.max(0, (product.getStockQuantity() == null ? 0 : product.getStockQuantity()) - cartItem.getQuantity());
            product.setStockQuantity(nextStock);
            product.setSoldQuantity((product.getSoldQuantity() == null ? 0 : product.getSoldQuantity()) + cartItem.getQuantity());
            if (nextStock <= 0) product.setStatus("OUT_OF_STOCK");
        }
        BigDecimal shipping = subtotal.compareTo(BigDecimal.valueOf(300000)) >= 0 ? BigDecimal.ZERO : BigDecimal.valueOf(30000);
        order.setSubtotalAmount(subtotal);
        order.setShippingFeeAmount(shipping);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTaxAmount(BigDecimal.ZERO);
        order.setTotalAmount(subtotal.add(shipping));

        ShopOrder savedOrder = shopOrderRepository.save(order);
        Invoice invoice = createInvoiceAndPayment(user, savedOrder);
        PaymentResponseDTO payResp = null;
        if ("PAYOS".equalsIgnoreCase(order.getPaymentMethod())) {
            PaymentRequestDTO req = new PaymentRequestDTO(invoice.getId(), null, null, "PAYOS", null, null);
            payResp = payOsService.createPayment(req);
        }
        cartItemRepository.deleteByUser_Id(user.getId());
        return toOrderResponse(savedOrder, payResp == null ? null : payResp.checkoutUrl(), payResp == null ? null : payResp.paymentLinkId());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(Long userId) {
        return shopOrderRepository.findByCustomerUser_IdOrderByIdDesc(userId).stream().map(this::toOrderResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByCode(String orderCode) {
        return shopOrderRepository.findByOrderCode(orderCode).map(this::toOrderResponse).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAdminProducts(String keyword) {
        return getProducts(keyword, null, null, null, null, null, true);
    }

    @Transactional
    public ProductResponse createProduct(ProductUpsertRequest request) {
        Product product = new Product();
        applyProductRequest(product, request);
        if (!StringUtils.hasText(product.getProductCode())) product.setProductCode("PRD" + System.currentTimeMillis());
        return toProductResponse(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpsertRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        applyProductRequest(product, request);
        return toProductResponse(productRepository.save(product));
    }

    @Transactional
    public void hideProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm"));
        product.setActive(false);
        product.setStatus("INACTIVE");
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAdminOrders(String status) {
        List<ShopOrder> orders = StringUtils.hasText(status) ? shopOrderRepository.findByStatusOrderByIdDesc(status) : shopOrderRepository.findAllByOrderByIdDesc();
        return orders.stream().map(this::toOrderResponse).toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, OrderStatusUpdateRequest request) {
        ShopOrder order = shopOrderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng"));
        String from = order.getStatus();
        order.setStatus(request.status());
        ShopOrder saved = shopOrderRepository.save(order);
        ShopOrderStatusHistory history = new ShopOrderStatusHistory();
        history.setShopOrder(saved);
        history.setFromStatus(from);
        history.setToStatus(request.status());
        history.setNote(request.note());
        if (request.changedByUserId() != null) userRepository.findById(request.changedByUserId()).ifPresent(history::setChangedByUser);
        statusHistoryRepository.save(history);
        return toOrderResponse(saved);
    }

    private Invoice createInvoiceAndPayment(User user, ShopOrder order) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-SHOP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        invoice.setUser(user);
        invoice.setShopOrder(order);
        invoice.setInvoiceType("SHOP_ORDER");
        invoice.setStatus("ISSUED");
        invoice.setBillingName(order.getReceiverName());
        invoice.setBillingEmail(order.getReceiverEmail());
        invoice.setBillingPhone(order.getReceiverPhone());
        invoice.setBillingAddress(order.getShippingAddress());
        invoice.setSubtotalAmount(order.getSubtotalAmount());
        invoice.setDiscountAmount(order.getDiscountAmount());
        invoice.setTaxAmount(order.getTaxAmount());
        invoice.setTotalAmount(order.getTotalAmount());
        invoice.setCurrencyCode("VND");
        invoice.setIssuedAt(LocalDateTime.now());
        Invoice savedInvoice = invoiceRepository.save(invoice);

        int sort = 1;
        for (ShopOrderItem item : order.getItems()) {
            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setInvoice(savedInvoice);
            invoiceItem.setItemType("SHOP_PRODUCT");
            invoiceItem.setItemName(item.getProductNameSnapshot());
            invoiceItem.setDescription("Sản phẩm PetGo Store");
            invoiceItem.setQuantity(item.getQuantity());
            invoiceItem.setUnitPrice(item.getUnitPrice());
            invoiceItem.setLineTotal(item.getLineTotal());
            invoiceItem.setSortOrder(sort++);
            invoiceItemRepository.save(invoiceItem);
        }
        if (order.getShippingFeeAmount().compareTo(BigDecimal.ZERO) > 0) {
            InvoiceItem shipping = new InvoiceItem();
            shipping.setInvoice(savedInvoice);
            shipping.setItemType("SHIPPING_FEE");
            shipping.setItemName("Phí giao hàng");
            shipping.setQuantity(1);
            shipping.setUnitPrice(order.getShippingFeeAmount());
            shipping.setLineTotal(order.getShippingFeeAmount());
            shipping.setSortOrder(sort);
            invoiceItemRepository.save(shipping);
        }

        Payment payment = new Payment();
        payment.setPaymentCode("PAY-SHOP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT));
        payment.setInvoice(savedInvoice);
        payment.setPayerUser(user);
        payment.setAmount(order.getTotalAmount());
        payment.setCurrencyCode("VND");
        payment.setPaymentMethod(order.getPaymentMethod());
        payment.setStatus("COD".equals(order.getPaymentMethod()) ? "PENDING" : "PENDING");
        paymentRepository.save(payment);
        return savedInvoice;
    }

    private void applyProductRequest(Product product, ProductUpsertRequest request) {
        ProductCategory category = categoryRepository.findById(request.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục sản phẩm"));
        product.setCategory(category);
        if (StringUtils.hasText(request.productCode())) product.setProductCode(request.productCode());
        product.setName(request.name());
        product.setSlug(StringUtils.hasText(request.slug()) ? request.slug() : slugify(request.name()));
        product.setBrand(request.brand());
        product.setShortDescription(request.shortDescription());
        product.setDescription(request.description());
        product.setTargetSpecies(StringUtils.hasText(request.targetSpecies()) ? request.targetSpecies() : "ALL");
        product.setPriceAmount(request.priceAmount());
        product.setSalePriceAmount(request.salePriceAmount());
        product.setStockQuantity(request.stockQuantity() == null ? 0 : request.stockQuantity());
        product.setSku(request.sku());
        product.setBarcode(request.barcode());
        product.setWeightGram(request.weightGram());
        product.setMainImageUrl(request.mainImageUrl());
        product.setFeatured(Boolean.TRUE.equals(request.featured()));
        product.setHot(Boolean.TRUE.equals(request.hot()));
        product.setActive(request.active() == null || request.active());
        product.setStatus(StringUtils.hasText(request.status()) ? request.status() : "ACTIVE");
    }

    private String slugify(String input) {
        return input.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
    }

    private CartResponse buildCartResponse(Long userId, List<CartItemResponse> items) {
        BigDecimal subtotal = items.stream().map(CartItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shipping = subtotal.compareTo(BigDecimal.valueOf(300000)) >= 0 || subtotal.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(30000);
        return new CartResponse(userId, items, subtotal, shipping, BigDecimal.ZERO, BigDecimal.ZERO, subtotal.add(shipping));
    }

    private BigDecimal effectivePrice(Product product) {
        return product.getSalePriceAmount() != null ? product.getSalePriceAmount() : product.getPriceAmount();
    }

    private CategoryResponse toCategoryResponse(ProductCategory c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getIconKey(), c.getDescription(), c.getSortOrder(), c.getActive());
    }

    private ProductResponse toProductResponse(Product p) {
        ProductCategory c = p.getCategory();
        return new ProductResponse(p.getId(), p.getProductCode(), c == null ? null : c.getId(), c == null ? null : c.getName(), p.getName(), p.getSlug(), p.getBrand(), p.getShortDescription(), p.getDescription(), p.getTargetSpecies(), p.getPriceAmount(), p.getSalePriceAmount(), p.getCurrencyCode(), p.getStockQuantity(), p.getSoldQuantity(), p.getSku(), p.getBarcode(), p.getWeightGram(), p.getMainImageUrl(), p.getAverageRating(), p.getTotalReviews(), p.getFeatured(), p.getHot(), p.getActive(), p.getStatus());
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        Product p = item.getProduct();
        BigDecimal unit = effectivePrice(p);
        BigDecimal total = unit.multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(item.getId(), p.getId(), p.getName(), p.getSlug(), p.getMainImageUrl(), item.getQuantity(), unit, total);
    }

    private OrderResponse toOrderResponse(ShopOrder order) {
        return toOrderResponse(order, null, null);
    }

    private OrderResponse toOrderResponse(ShopOrder order, String checkoutUrl, String paymentLinkId) {
        List<OrderItemResponse> items = order.getItems().stream().map(i -> new OrderItemResponse(i.getId(), i.getProduct() == null ? null : i.getProduct().getId(), i.getProductNameSnapshot(), i.getProductSkuSnapshot(), i.getProductImageSnapshot(), i.getQuantity(), i.getUnitPrice(), i.getLineTotal())).toList();
        return new OrderResponse(order.getId(), order.getOrderCode(), order.getCustomerUser() == null ? null : order.getCustomerUser().getId(), order.getReceiverName(), order.getReceiverPhone(), order.getReceiverEmail(), order.getShippingAddress(), order.getWard(), order.getDistrict(), order.getCity(), order.getProvince(), order.getStatus(), order.getPaymentMethod(), order.getSubtotalAmount(), order.getShippingFeeAmount(), order.getDiscountAmount(), order.getTaxAmount(), order.getTotalAmount(), order.getCurrencyCode(), order.getCreatedAt(), items, checkoutUrl, paymentLinkId);
    }
}
