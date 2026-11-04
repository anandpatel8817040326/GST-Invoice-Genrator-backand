package com.gstinvoice.customer.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_id", nullable = false)
    private Long businessId;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String mobile;

    @Column(length = 15)
    private String gstin;

    @Column(length = 10)
    private String pan;

    @Column(name = "address_line1", length = 255)
    private String addressLine1;

    @Column(name = "address_line2", length = 255)
    private String addressLine2;

    @Column(length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String state;

    @Column(name = "state_code", nullable = false, length = 2)
    private String stateCode;

    @Column(name = "pin_code", length = 10)
    private String pinCode;

    @Builder.Default
    @Column(name = "customer_type", length = 20)
    private String customerType = "B2B"; // B2B or B2C

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

