package com.gstinvoice.gst.repository;

import com.gstinvoice.gst.entity.GstRateSlab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GstRateSlabRepository extends JpaRepository<GstRateSlab, Long> {
    List<GstRateSlab> findByActiveTrueOrderByRatePercentageAsc();
    Optional<GstRateSlab> findByRatePercentage(BigDecimal ratePercentage);
}

