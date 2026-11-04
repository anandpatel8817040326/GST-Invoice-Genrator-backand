package com.gstinvoice.gst.repository;

import com.gstinvoice.gst.entity.HsnMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HsnMasterRepository extends JpaRepository<HsnMaster, Long> {
    Optional<HsnMaster> findByHsnCode(String hsnCode);
    List<HsnMaster> findByHsnCodeContainingOrDescriptionContainingIgnoreCase(String hsnCode, String description);
}

