package com.gstinvoice.gst.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "hsn_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HsnMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hsn_code", nullable = false, length = 20, unique = true)
    private String hsnCode;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "default_gst_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal defaultGstRate;
}

