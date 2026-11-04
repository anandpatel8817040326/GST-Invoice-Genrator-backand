package com.gstinvoice.gst.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "gst_rate_slabs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GstRateSlab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rate_percentage", nullable = false, precision = 5, scale = 2, unique = true)
    private BigDecimal ratePercentage;

    @Column(name = "slab_name", nullable = false, length = 100)
    private String slabName;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(length = 255)
    private String description;
}

