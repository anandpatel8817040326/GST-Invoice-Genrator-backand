package com.gstinvoice.business.repository;

import com.gstinvoice.business.entity.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    List<Business> findByOwnerUserId(Long ownerUserId);
    Optional<Business> findByIdAndOwnerUserId(Long id, Long ownerUserId);
    Optional<Business> findByGstin(String gstin);
}

