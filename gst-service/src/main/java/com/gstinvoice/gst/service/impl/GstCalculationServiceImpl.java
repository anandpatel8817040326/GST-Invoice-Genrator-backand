package com.gstinvoice.gst.service.impl;

import com.gstinvoice.gst.dto.GstDto;
import com.gstinvoice.gst.entity.GstRateSlab;
import com.gstinvoice.gst.entity.HsnMaster;
import com.gstinvoice.gst.repository.GstRateSlabRepository;
import com.gstinvoice.gst.repository.HsnMasterRepository;
import com.gstinvoice.gst.service.GstCalculationService;
import com.gstinvoice.gst.util.IndianCurrencyUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class GstCalculationServiceImpl implements GstCalculationService {

    private final GstRateSlabRepository slabRepository;
    private final HsnMasterRepository hsnMasterRepository;

    public GstCalculationServiceImpl(GstRateSlabRepository slabRepository, HsnMasterRepository hsnMasterRepository) {
        this.slabRepository = slabRepository;
        this.hsnMasterRepository = hsnMasterRepository;
    }

    @PostConstruct
    public void seedDefaultData() {
        if (slabRepository.count() == 0) {
            slabRepository.save(GstRateSlab.builder().ratePercentage(BigDecimal.valueOf(0.00)).slabName("Exempt (0%)").active(true).build());
            slabRepository.save(GstRateSlab.builder().ratePercentage(BigDecimal.valueOf(5.00)).slabName("Essential Goods (5%)").active(true).build());
            slabRepository.save(GstRateSlab.builder().ratePercentage(BigDecimal.valueOf(12.00)).slabName("Standard Low (12%)").active(true).build());
            slabRepository.save(GstRateSlab.builder().ratePercentage(BigDecimal.valueOf(18.00)).slabName("Standard General (18%)").active(true).build());
            slabRepository.save(GstRateSlab.builder().ratePercentage(BigDecimal.valueOf(28.00)).slabName("Luxury/Sin Goods (28%)").active(true).build());
        }

        if (hsnMasterRepository.count() == 0) {
            hsnMasterRepository.save(HsnMaster.builder().hsnCode("998311").description("IT Consulting and Support Services").defaultGstRate(BigDecimal.valueOf(18.00)).build());
            hsnMasterRepository.save(HsnMaster.builder().hsnCode("998314").description("Information Technology Design and Development Services").defaultGstRate(BigDecimal.valueOf(18.00)).build());
            hsnMasterRepository.save(HsnMaster.builder().hsnCode("8471").description("Automatic Data Processing Machines / Laptops").defaultGstRate(BigDecimal.valueOf(18.00)).build());
            hsnMasterRepository.save(HsnMaster.builder().hsnCode("4820").description("Registers, Account Books, Note Books").defaultGstRate(BigDecimal.valueOf(12.00)).build());
            hsnMasterRepository.save(HsnMaster.builder().hsnCode("0401").description("Fresh Milk and Dairy Products").defaultGstRate(BigDecimal.valueOf(0.00)).build());
        }
    }

    @Override
    public GstDto.CalculationResponse calculateTax(GstDto.CalculationRequest request) {
        String sellerState = request.getSellerStateCode() != null ? request.getSellerStateCode().trim() : "";
        String buyerState = request.getBuyerStateCode() != null ? request.getBuyerStateCode().trim() : "";

        // Decision Rule:
        // Intra-State: sellerState == buyerState -> 50% CGST + 50% SGST
        // Inter-State: sellerState != buyerState -> 100% IGST
        boolean isIntraState = sellerState.equalsIgnoreCase(buyerState);

        BigDecimal taxableSubtotal = BigDecimal.ZERO;
        BigDecimal totalCgst = BigDecimal.ZERO;
        BigDecimal totalSgst = BigDecimal.ZERO;
        BigDecimal totalIgst = BigDecimal.ZERO;

        List<GstDto.CalculatedItemResponse> calculatedItems = new ArrayList<>();

        for (GstDto.CalculationItemRequest item : request.getItems()) {
            BigDecimal qty = item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE;
            BigDecimal price = item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO;
            BigDecimal itemDiscount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;
            BigDecimal gstRate = item.getGstRate() != null ? item.getGstRate() : BigDecimal.ZERO;

            // 1. Taxable Amount = (Quantity * Unit Price) - Discount
            BigDecimal grossLine = price.multiply(qty).setScale(2, RoundingMode.HALF_UP);
            BigDecimal taxableAmount = grossLine.subtract(itemDiscount);
            if (taxableAmount.compareTo(BigDecimal.ZERO) < 0) {
                taxableAmount = BigDecimal.ZERO;
            }
            taxableAmount = taxableAmount.setScale(2, RoundingMode.HALF_UP);

            // 2. Line Tax = (Taxable Amount * GST Rate) / 100
            BigDecimal lineTotalTax = taxableAmount.multiply(gstRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal cgstRate = BigDecimal.ZERO;
            BigDecimal cgstAmount = BigDecimal.ZERO;
            BigDecimal sgstRate = BigDecimal.ZERO;
            BigDecimal sgstAmount = BigDecimal.ZERO;
            BigDecimal igstRate = BigDecimal.ZERO;
            BigDecimal igstAmount = BigDecimal.ZERO;

            if (isIntraState) {
                // Split evenly: CGST = 50%, SGST = 50%
                cgstRate = gstRate.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                sgstRate = cgstRate;

                cgstAmount = lineTotalTax.divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
                sgstAmount = lineTotalTax.subtract(cgstAmount); // Avoid penny rounding discrepancies

                totalCgst = totalCgst.add(cgstAmount);
                totalSgst = totalSgst.add(sgstAmount);
            } else {
                // Interstate: 100% IGST
                igstRate = gstRate;
                igstAmount = lineTotalTax;
                totalIgst = totalIgst.add(igstAmount);
            }

            BigDecimal lineTotal = taxableAmount.add(lineTotalTax).setScale(2, RoundingMode.HALF_UP);
            taxableSubtotal = taxableSubtotal.add(taxableAmount);

            calculatedItems.add(GstDto.CalculatedItemResponse.builder()
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .hsnSac(item.getHsnSac())
                    .quantity(qty)
                    .unitPrice(price)
                    .discount(itemDiscount)
                    .taxableAmount(taxableAmount)
                    .gstRate(gstRate)
                    .cgstRate(cgstRate)
                    .cgstAmount(cgstAmount)
                    .sgstRate(sgstRate)
                    .sgstAmount(sgstAmount)
                    .igstRate(igstRate)
                    .igstAmount(igstAmount)
                    .totalTax(lineTotalTax)
                    .totalAmount(lineTotal)
                    .build());
        }

        BigDecimal overallDiscount = request.getOverallDiscount() != null ? request.getOverallDiscount() : BigDecimal.ZERO;
        BigDecimal totalTax = totalCgst.add(totalSgst).add(totalIgst);

        // Raw Total = Taxable Subtotal - Overall Discount + Total Tax
        BigDecimal rawTotal = taxableSubtotal.subtract(overallDiscount).add(totalTax);
        if (rawTotal.compareTo(BigDecimal.ZERO) < 0) {
            rawTotal = BigDecimal.ZERO;
        }

        // Standard Indian accounting round-off to nearest rupee
        BigDecimal roundedGrandTotal = rawTotal.setScale(0, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
        BigDecimal roundOff = roundedGrandTotal.subtract(rawTotal).setScale(2, RoundingMode.HALF_UP);

        String amountInWords = IndianCurrencyUtil.convertToWords(roundedGrandTotal);

        return GstDto.CalculationResponse.builder()
                .isIntraState(isIntraState)
                .sellerStateCode(sellerState)
                .buyerStateCode(buyerState)
                .items(calculatedItems)
                .taxableSubtotal(taxableSubtotal.setScale(2, RoundingMode.HALF_UP))
                .totalDiscount(overallDiscount.setScale(2, RoundingMode.HALF_UP))
                .totalCgst(totalCgst.setScale(2, RoundingMode.HALF_UP))
                .totalSgst(totalSgst.setScale(2, RoundingMode.HALF_UP))
                .totalIgst(totalIgst.setScale(2, RoundingMode.HALF_UP))
                .totalTax(totalTax.setScale(2, RoundingMode.HALF_UP))
                .roundOff(roundOff)
                .grandTotal(roundedGrandTotal)
                .amountInWords(amountInWords)
                .build();
    }

    @Override
    public List<GstRateSlab> getAllSlabs() {
        return slabRepository.findByActiveTrueOrderByRatePercentageAsc();
    }

    @Override
    public List<HsnMaster> searchHsn(String query) {
        if (query == null || query.trim().isEmpty()) {
            return hsnMasterRepository.findAll();
        }
        return hsnMasterRepository.findByHsnCodeContainingOrDescriptionContainingIgnoreCase(query.trim(), query.trim());
    }
}

