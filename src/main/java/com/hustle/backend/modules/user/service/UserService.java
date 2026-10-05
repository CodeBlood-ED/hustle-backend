package com.hustle.backend.modules.user.service;

import com.hustle.backend.common.exception.ResourceNotFoundException;
import com.hustle.backend.modules.auth.entity.User;
import com.hustle.backend.modules.auth.repository.UserRepository;
import com.hustle.backend.modules.order.repository.OrderRepository;
import com.hustle.backend.modules.user.dto.*;
import com.hustle.backend.modules.user.entity.Address;
import com.hustle.backend.modules.user.entity.Enquiry;
import com.hustle.backend.modules.user.repository.AddressRepository;
import com.hustle.backend.modules.user.repository.EnquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final EnquiryRepository enquiryRepository;
    private final OrderRepository orderRepository;

    public UserService(UserRepository userRepository,
                       AddressRepository addressRepository,
                       EnquiryRepository enquiryRepository,
                       OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.enquiryRepository = enquiryRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setContact(user.getContact());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());

        long ordersCount = orderRepository.findByUserOrderByCreatedAtDesc(user).size();
        long addressesCount = addressRepository.countByUser(user);
        long enquiriesCount = enquiryRepository.countByUser(user);

        dto.setTotalOrders(ordersCount);
        dto.setTotalAddresses(addressesCount);
        dto.setTotalEnquiries(enquiriesCount);

        List<AddressDto> addresses = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user)
                .stream()
                .map(this::mapToAddressDto)
                .collect(Collectors.toList());
        dto.setAddresses(addresses);

        return dto;
    }

    @Transactional
    public UserProfileDto updateProfile(User user, UpdateProfileRequest request) {
        user.setName(request.getName().trim());
        user.setContact(request.getContact().trim());
        User updated = userRepository.save(user);
        return getUserProfile(updated);
    }

    @Transactional(readOnly = true)
    public List<AddressDto> getUserAddresses(User user) {
        return addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user)
                .stream()
                .map(this::mapToAddressDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDto addAddress(User user, CreateAddressRequest request) {
        if (request.isDefault()) {
            // Unset previous defaults
            List<Address> existing = addressRepository.findByUserOrderByIsDefaultDescCreatedAtDesc(user);
            for (Address a : existing) {
                if (a.isDefault()) {
                    a.setDefault(false);
                    addressRepository.save(a);
                }
            }
        }

        Address address = new Address(
                user,
                request.getLabel(),
                request.getStreetAddress(),
                request.getCity(),
                request.getState(),
                request.getPostalCode(),
                request.getCountry(),
                request.isDefault()
        );

        Address saved = addressRepository.save(address);
        return mapToAddressDto(saved);
    }

    @Transactional
    public void deleteAddress(User user, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        addressRepository.delete(address);
    }

    @Transactional(readOnly = true)
    public List<EnquiryDto> getUserEnquiries(User user) {
        return enquiryRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToEnquiryDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnquiryDto createEnquiry(User user, CreateEnquiryRequest request) {
        Enquiry enquiry = new Enquiry(
                user,
                user.getName(),
                user.getEmail(),
                request.getSubject(),
                request.getMessage(),
                request.getType()
        );

        Enquiry saved = enquiryRepository.save(enquiry);
        return mapToEnquiryDto(saved);
    }

    private AddressDto mapToAddressDto(Address address) {
        return new AddressDto(
                address.getId(),
                address.getLabel(),
                address.getStreetAddress(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry(),
                address.isDefault(),
                address.getCreatedAt()
        );
    }

    private EnquiryDto mapToEnquiryDto(Enquiry enquiry) {
        return new EnquiryDto(
                enquiry.getId(),
                enquiry.getCustomerName(),
                enquiry.getCustomerEmail(),
                enquiry.getSubject(),
                enquiry.getMessage(),
                enquiry.getType(),
                enquiry.getStatus(),
                enquiry.getCreatedAt()
        );
    }
}
