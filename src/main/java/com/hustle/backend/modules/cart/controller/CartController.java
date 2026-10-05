package com.hustle.backend.modules.cart.controller;

import com.hustle.backend.common.ApiResponse;
import com.hustle.backend.modules.auth.entity.User;
import com.hustle.backend.modules.auth.repository.UserRepository;
import com.hustle.backend.modules.cart.dto.AddToCartRequest;
import com.hustle.backend.modules.cart.dto.CartDto;
import com.hustle.backend.modules.cart.dto.UpdateCartItemRequest;
import com.hustle.backend.modules.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart", description = "Endpoints for shopping cart operations")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    private User resolveUser(UserDetails userDetails) {
        if (userDetails != null) {
            return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }
        return null;
    }

    @GetMapping
    @Operation(summary = "Get current cart contents")
    public ResponseEntity<ApiResponse<CartDto>> getCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        User user = resolveUser(userDetails);
        CartDto cart = cartService.getCartDto(user, sessionId);
        return ResponseEntity.ok(ApiResponse.ok(cart));
    }

    @PostMapping("/items")
    @Operation(summary = "Add an item to the cart")
    public ResponseEntity<ApiResponse<CartDto>> addItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            @Valid @RequestBody AddToCartRequest request) {
        User user = resolveUser(userDetails);
        CartDto cart = cartService.addToCart(user, sessionId, request);
        return ResponseEntity.ok(ApiResponse.ok("Item added to cart", cart));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update quantity of a cart item")
    public ResponseEntity<ApiResponse<CartDto>> updateItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        User user = resolveUser(userDetails);
        CartDto cart = cartService.updateCartItem(user, sessionId, itemId, request.getQuantity());
        return ResponseEntity.ok(ApiResponse.ok("Cart item updated", cart));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove an item from the cart")
    public ResponseEntity<ApiResponse<CartDto>> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId,
            @PathVariable Long itemId) {
        User user = resolveUser(userDetails);
        CartDto cart = cartService.removeCartItem(user, sessionId, itemId);
        return ResponseEntity.ok(ApiResponse.ok("Item removed from cart", cart));
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Clear all items from the cart")
    public ResponseEntity<ApiResponse<CartDto>> clearCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {
        User user = resolveUser(userDetails);
        CartDto cart = cartService.clearCart(user, sessionId);
        return ResponseEntity.ok(ApiResponse.ok("Cart cleared", cart));
    }
}
