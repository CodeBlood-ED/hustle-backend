package com.hustle.backend.modules.order.service;

import com.hustle.backend.common.exception.BadRequestException;
import com.hustle.backend.common.exception.ResourceNotFoundException;
import com.hustle.backend.modules.auth.entity.Role;
import com.hustle.backend.modules.auth.entity.User;
import com.hustle.backend.modules.cart.entity.Cart;
import com.hustle.backend.modules.cart.entity.CartItem;
import com.hustle.backend.modules.cart.service.CartService;
import com.hustle.backend.modules.order.dto.*;
import com.hustle.backend.modules.order.entity.*;
import com.hustle.backend.modules.order.repository.OrderRepository;
import com.hustle.backend.modules.product.entity.Product;
import com.hustle.backend.modules.product.repository.ProductRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    @Transactional
    public OrderDto createOrder(User user, String sessionId, CreateOrderRequest request) {
        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(request.getCustomerName().trim());
        order.setCustomerEmail(request.getCustomerEmail().toLowerCase().trim());
        order.setCustomerPhone(request.getCustomerPhone().trim());
        order.setShippingAddress(request.getShippingAddress().trim());
        order.setCity(request.getCity());
        order.setState(request.getState());
        order.setPostalCode(request.getPostalCode());

        // Parse shipping method
        ShippingMethod shippingMethod = ShippingMethod.STANDARD;
        if ("express".equalsIgnoreCase(request.getShippingMethod())) {
            shippingMethod = ShippingMethod.EXPRESS;
        }
        order.setShippingMethod(shippingMethod);

        // Parse payment method
        PaymentMethod paymentMethod = PaymentMethod.CARD;
        if (request.getPaymentMethod() != null) {
            String pm = request.getPaymentMethod().toLowerCase();
            if (pm.contains("paypal")) {
                paymentMethod = PaymentMethod.PAYPAL;
            } else if (pm.contains("apple")) {
                paymentMethod = PaymentMethod.APPLE_PAY;
            } else if (pm.contains("cod")) {
                paymentMethod = PaymentMethod.COD;
            }
        }
        order.setPaymentMethod(paymentMethod);

        double subtotal = 0.0;

        if (request.isFromCart()) {
            Cart cart = cartService.getOrCreateCart(user, sessionId);
            if (cart.getItems() == null || cart.getItems().isEmpty()) {
                throw new BadRequestException("Cannot create order from an empty cart");
            }
            for (CartItem cartItem : cart.getItems()) {
                OrderItem item = new OrderItem(
                        order,
                        cartItem.getProduct(),
                        cartItem.getProduct().getTitle(),
                        cartItem.getModel(),
                        cartItem.getUnitPrice(),
                        cartItem.getQuantity()
                );
                order.addItem(item);
                subtotal += item.getSubtotal();
            }
            cartService.clearCart(user, sessionId);
        } else if (request.getItems() != null && !request.getItems().isEmpty()) {
            for (OrderItemRequest itemReq : request.getItems()) {
                Product product = productRepository.findById(itemReq.getProductId())
                        .filter(Product::isActive)
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemReq.getProductId()));

                Double price = product.getNumericPrice() != null ? product.getNumericPrice() : 0.0;
                String model = itemReq.getModel() != null ? itemReq.getModel().toLowerCase().trim() : product.getCategory();

                OrderItem item = new OrderItem(
                        order,
                        product,
                        product.getTitle(),
                        model,
                        price,
                        itemReq.getQuantity()
                );
                order.addItem(item);
                subtotal += item.getSubtotal();
            }
        } else {
            throw new BadRequestException("Order must have at least one item or fromCart set to true");
        }

        double shippingCost = (shippingMethod == ShippingMethod.EXPRESS) ? 99.0 : (subtotal >= 999.0 ? 0.0 : 49.0);
        double taxAmount = Math.round(subtotal * 0.18);
        double totalAmount = subtotal + shippingCost + taxAmount;

        order.setSubtotal(subtotal);
        order.setShippingCost(shippingCost);
        order.setTaxAmount(taxAmount);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setOrderNumber(generateOrderNumber());

        Order saved = orderRepository.save(order);
        return mapToDto(saved);
    }

    public OrderDto getOrderById(Long id, User currentUser) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (currentUser != null && currentUser.getRole() != Role.ROLE_ADMIN) {
            if (order.getUser() == null || !order.getUser().getId().equals(currentUser.getId())) {
                throw new AccessDeniedException("You are not authorized to view this order");
            }
        }

        return mapToDto(order);
    }

    public OrderDto getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with order number: " + orderNumber));
        return mapToDto(order);
    }

    public List<OrderDto> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        order.setStatus(status);
        Order updated = orderRepository.save(order);
        return mapToDto(updated);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = 1000 + new Random().nextInt(9000);
        return "HST-" + timestamp + "-" + rand;
    }

    public OrderDto mapToDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCity(order.getCity());
        dto.setState(order.getState());
        dto.setPostalCode(order.getPostalCode());
        dto.setShippingMethod(order.getShippingMethod());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setShippingCost(order.getShippingCost());
        dto.setTaxAmount(order.getTaxAmount());
        dto.setSubtotal(order.getSubtotal());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getItems() != null) {
            dto.setItems(order.getItems().stream().map(item -> {
                OrderItemDto itemDto = new OrderItemDto();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
                itemDto.setProductTitle(item.getProductTitle());
                itemDto.setModel(item.getModel());
                itemDto.setUnitPrice(item.getUnitPrice());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setSubtotal(item.getSubtotal());
                itemDto.setImageUrl(item.getProduct() != null ? item.getProduct().getImageUrl() : null);
                return itemDto;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}
