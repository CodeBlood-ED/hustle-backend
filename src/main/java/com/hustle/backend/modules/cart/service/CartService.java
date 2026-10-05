package com.hustle.backend.modules.cart.service;

import com.hustle.backend.common.exception.ResourceNotFoundException;
import com.hustle.backend.modules.auth.entity.User;
import com.hustle.backend.modules.cart.dto.*;
import com.hustle.backend.modules.cart.entity.Cart;
import com.hustle.backend.modules.cart.entity.CartItem;
import com.hustle.backend.modules.cart.repository.CartItemRepository;
import com.hustle.backend.modules.cart.repository.CartRepository;
import com.hustle.backend.modules.product.entity.Product;
import com.hustle.backend.modules.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Cart getOrCreateCart(User user, String sessionId) {
        if (user != null) {
            return cartRepository.findByUser(user)
                    .orElseGet(() -> cartRepository.save(new Cart(user, null)));
        } else if (sessionId != null && !sessionId.isBlank()) {
            return cartRepository.findBySessionId(sessionId)
                    .orElseGet(() -> cartRepository.save(new Cart(null, sessionId)));
        } else {
            return cartRepository.save(new Cart(null, null));
        }
    }

    @Transactional(readOnly = true)
    public CartDto getCartDto(User user, String sessionId) {
        Cart cart = getOrCreateCart(user, sessionId);
        return mapToCartDto(cart);
    }

    @Transactional
    public CartDto addToCart(User user, String sessionId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(user, sessionId);
        Product product = productRepository.findById(request.getProductId())
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductIdAndModel(
                cart.getId(), product.getId(), request.getModel().toLowerCase().trim());

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            Double price = product.getNumericPrice() != null ? product.getNumericPrice() : 0.0;
            CartItem newItem = new CartItem(cart, product, request.getModel().toLowerCase().trim(), request.getQuantity(), price);
            cart.addItem(newItem);
            cartItemRepository.save(newItem);
        }

        return mapToCartDto(cart);
    }

    @Transactional
    public CartDto updateCartItem(User user, String sessionId, Long itemId, int quantity) {
        Cart cart = getOrCreateCart(user, sessionId);
        CartItem item = cartItemRepository.findById(itemId)
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        if (quantity <= 0) {
            cart.removeItem(item);
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return mapToCartDto(cart);
    }

    @Transactional
    public CartDto removeCartItem(User user, String sessionId, Long itemId) {
        Cart cart = getOrCreateCart(user, sessionId);
        CartItem item = cartItemRepository.findById(itemId)
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));

        cart.removeItem(item);
        cartItemRepository.delete(item);

        return mapToCartDto(cart);
    }

    @Transactional
    public CartDto clearCart(User user, String sessionId) {
        Cart cart = getOrCreateCart(user, sessionId);
        cart.getItems().clear();
        cartRepository.save(cart);
        return mapToCartDto(cart);
    }

    public CartDto mapToCartDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());

        int totalCount = 0;
        double subtotal = 0.0;

        if (cart.getItems() != null) {
            dto.setItems(cart.getItems().stream().map(item -> {
                CartItemDto itemDto = new CartItemDto();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setName(item.getProduct().getTitle());
                itemDto.setModel(item.getModel());
                itemDto.setModelLabel(formatModelLabel(item.getModel()));
                itemDto.setPrice(item.getProduct().getNetPrice());
                itemDto.setUnitPrice(item.getUnitPrice());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setSubtotal(item.getSubtotal());
                itemDto.setImageUrl(item.getProduct().getImageUrl());
                itemDto.setAccent(item.getProduct().getAccent());
                return itemDto;
            }).collect(Collectors.toList()));

            for (CartItem item : cart.getItems()) {
                totalCount += item.getQuantity();
                subtotal += item.getSubtotal();
            }
        }

        dto.setItemCount(totalCount);
        dto.setSubtotal(subtotal);
        return dto;
    }

    private String formatModelLabel(String model) {
        if (model == null) return "";
        return switch (model.toLowerCase().trim()) {
            case "iphone-13" -> "iPhone 13";
            case "iphone-14" -> "iPhone 14";
            case "iphone-15" -> "iPhone 15";
            case "iphone-16" -> "iPhone 16";
            case "iphone-16-pro" -> "iPhone 16 Pro";
            case "iphone-16-pro-max" -> "iPhone 16 Pro Max";
            case "iphone-17" -> "iPhone 17";
            case "iphone-17-pro" -> "iPhone 17 Pro";
            case "iphone-18" -> "iPhone 18";
            case "iphone-18-pro" -> "iPhone 18 Pro";
            default -> model;
        };
    }
}
