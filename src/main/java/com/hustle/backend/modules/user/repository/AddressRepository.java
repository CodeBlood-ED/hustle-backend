package com.hustle.backend.modules.user.repository;

import com.hustle.backend.modules.auth.entity.User;
import com.hustle.backend.modules.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);
    long countByUser(User user);
}
