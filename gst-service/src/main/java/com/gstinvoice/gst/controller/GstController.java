package com.gstinvoice.gst.controller;

import com.gstinvoice.gst.dto.GstDto;
import com.gstinvoice.gst.entity.GstRateSlab;
import com.gstinvoice.gst.entity.HsnMaster;
import com.gstinvoice.gst.service.GstCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gst")
@Tag(name = "GST Calculation Engine", description = "Statutory Indian GST tax computation, place-of-supply rule, CGST/SGST/IGST split")
public class GstController {

    private final GstCalculationService calculationService;

    public GstController(GstCalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calculate GST split (CGST+SGST vs IGST), taxable totals, round off, and words")
    public ResponseEntity<GstDto.ApiResponse<GstDto.CalculationResponse>> calculate(@Valid @RequestBody GstDto.CalculationRequest request) {
        GstDto.CalculationResponse response = calculationService.calculateTax(request);
        return ResponseEntity.ok(
                GstDto.ApiResponse.<GstDto.CalculationResponse>builder()
                        .success(true)
                        .message("Tax calculated successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping("/slabs")
    @Operation(summary = "Get active GST rate slabs (0%, 5%, 12%, 18%, 28%)")
    public ResponseEntity<GstDto.ApiResponse<List<GstRateSlab>>> getSlabs() {
        List<GstRateSlab> slabs = calculationService.getAllSlabs();
        return ResponseEntity.ok(
                GstDto.ApiResponse.<List<GstRateSlab>>builder()
                        .success(true)
                        .message("GST slabs retrieved")
                        .data(slabs)
                        .build()
        );
    }

    @GetMapping("/hsn")
    @Operation(summary = "Search HSN/SAC master directory")
    public ResponseEntity<GstDto.ApiResponse<List<HsnMaster>>> searchHsn(@RequestParam(value = "query", required = false) String query) {
        List<HsnMaster> results = calculationService.searchHsn(query);
        return ResponseEntity.ok(
                GstDto.ApiResponse.<List<HsnMaster>>builder()
                        .success(true)
                        .message("HSN master retrieved")
                        .data(results)
                        .build()
        );
    }
}

