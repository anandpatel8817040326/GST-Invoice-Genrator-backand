package com.gstinvoice.business.repository;

import com.gstinvoice.business.entity.BusinessMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusinessMemberRepository extends JpaRepository<BusinessMember, Long> {
    List<BusinessMember> findByBusinessId(Long businessId);
    Optional<BusinessMember> findByBusinessIdAndEmail(Long businessId, String email);
}

