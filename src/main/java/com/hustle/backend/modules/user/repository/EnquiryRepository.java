package com.hustle.backend.modules.user.repository;

import com.hustle.backend.modules.auth.entity.User;
import com.hustle.backend.modules.user.entity.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, Long> {
    List<Enquiry> findByUserOrderByCreatedAtDesc(User user);
    long countByUser(User user);
}
