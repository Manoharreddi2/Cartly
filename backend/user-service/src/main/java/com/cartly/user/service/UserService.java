package com.cartly.user.service;

import com.cartly.user.dto.AddressRequest;
import com.cartly.user.dto.AddressResponse;
import com.cartly.user.dto.ProfileRequest;
import com.cartly.user.entity.Address;
import com.cartly.user.entity.UserProfile;
import com.cartly.user.exception.ResourceNotFoundException;
import com.cartly.user.repository.AddressRepository;
import com.cartly.user.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserProfileRepository profileRepository;
    private final AddressRepository addressRepository;

    public UserProfile getProfile(Long userId) {
        return profileRepository.findByUserId(userId)
                .orElseGet(() -> profileRepository.save(UserProfile.builder()
                        .userId(userId)
                        .name("Customer")
                        .email("user" + userId + "@cartly.com")
                        .build()));
    }

    @Transactional
    public UserProfile updateProfile(Long userId, ProfileRequest request) {
        UserProfile profile = getProfile(userId);
        profile.setName(request.getName());
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl());
        }
        return profileRepository.save(profile);
    }

    public List<AddressResponse> getAddresses(Long userId) {
        return addressRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse addAddress(Long userId, AddressRequest request) {
        if (request.isDefault()) {
            List<Address> existing = addressRepository.findByUserIdOrderByCreatedAtDesc(userId);
            for (Address addr : existing) {
                if (addr.isDefault()) {
                    addr.setDefault(false);
                    addressRepository.save(addr);
                }
            }
        }

        Address address = Address.builder()
                .userId(userId)
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .state(request.getState())
                .zipCode(request.getZipCode())
                .country(request.getCountry() != null ? request.getCountry() : "United States")
                .isDefault(request.isDefault())
                .build();

        Address saved = addressRepository.save(address);
        return toAddressResponse(saved);
    }

    @Transactional
    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (request.isDefault() && !address.isDefault()) {
            List<Address> existing = addressRepository.findByUserIdOrderByCreatedAtDesc(userId);
            for (Address addr : existing) {
                if (addr.isDefault()) {
                    addr.setDefault(false);
                    addressRepository.save(addr);
                }
            }
        }

        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setStreetAddress(request.getStreetAddress());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setZipCode(request.getZipCode());
        if (request.getCountry() != null) {
            address.setCountry(request.getCountry());
        }
        address.setDefault(request.isDefault());

        Address saved = addressRepository.save(address);
        return toAddressResponse(saved);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getUserId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        addressRepository.delete(address);
    }

    public List<UserProfile> getAllUsers() {
        return profileRepository.findAll();
    }

    private AddressResponse toAddressResponse(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .fullName(a.getFullName())
                .phone(a.getPhone())
                .streetAddress(a.getStreetAddress())
                .city(a.getCity())
                .state(a.getState())
                .zipCode(a.getZipCode())
                .country(a.getCountry())
                .isDefault(a.isDefault())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
