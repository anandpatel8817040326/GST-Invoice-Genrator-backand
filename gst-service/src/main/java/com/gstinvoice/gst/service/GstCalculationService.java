package com.gstinvoice.gst.service;

import com.gstinvoice.gst.dto.GstDto;
import com.gstinvoice.gst.entity.GstRateSlab;
import com.gstinvoice.gst.entity.HsnMaster;

import java.util.List;

public interface GstCalculationService {
    GstDto.CalculationResponse calculateTax(GstDto.CalculationRequest request);
    List<GstRateSlab> getAllSlabs();
    List<HsnMaster> searchHsn(String query);
}

