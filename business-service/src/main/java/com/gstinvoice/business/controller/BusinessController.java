package com.gstinvoice.business.controller;

import com.gstinvoice.business.dto.BusinessDto;
import com.gstinvoice.business.service.BusinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businesses")
@Tag(name = "Business Management", description = "Business CRUD, statutory GST profile, bank accounts, and team members")
public class BusinessController {

    private final BusinessService businessService;

    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @PostMapping
    @Operation(summary = "Create business profile")
    public ResponseEntity<BusinessDto.ApiResponse<BusinessDto.BusinessResponse>> createBusiness(
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long userId,
            @Valid @RequestBody BusinessDto.CreateBusinessRequest request) {
        BusinessDto.BusinessResponse response = businessService.createBusiness(userId, request);
        return new ResponseEntity<>(
                BusinessDto.ApiResponse.<BusinessDto.BusinessResponse>builder()
                        .success(true)
                        .message("Business profile created successfully")
                        .data(response)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get business by ID")
    public ResponseEntity<BusinessDto.ApiResponse<BusinessDto.BusinessResponse>> getBusinessById(@PathVariable Long id) {
        BusinessDto.BusinessResponse response = businessService.getBusinessById(id);
        return ResponseEntity.ok(
                BusinessDto.ApiResponse.<BusinessDto.BusinessResponse>builder()
                        .success(true)
                        .message("Business retrieved successfully")
                        .data(response)
                        .build()
        );
    }

    @GetMapping
    @Operation(summary = "Get businesses owned by current user")
    public ResponseEntity<BusinessDto.ApiResponse<List<BusinessDto.BusinessResponse>>> getMyBusinesses(
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long userId) {
        List<BusinessDto.BusinessResponse> list = businessService.getBusinessesByOwner(userId);
        return ResponseEntity.ok(
                BusinessDto.ApiResponse.<List<BusinessDto.BusinessResponse>>builder()
                        .success(true)
                        .message("User businesses retrieved")
                        .data(list)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update business profile")
    public ResponseEntity<BusinessDto.ApiResponse<BusinessDto.BusinessResponse>> updateBusiness(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long userId,
            @Valid @RequestBody BusinessDto.CreateBusinessRequest request) {
        BusinessDto.BusinessResponse response = businessService.updateBusiness(id, userId, request);
        return ResponseEntity.ok(
                BusinessDto.ApiResponse.<BusinessDto.BusinessResponse>builder()
                        .success(true)
                        .message("Business updated successfully")
                        .data(response)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete business profile")
    public ResponseEntity<BusinessDto.ApiResponse<Void>> deleteBusiness(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false, defaultValue = "1") Long userId) {
        businessService.deleteBusiness(id, userId);
        return ResponseEntity.ok(
                BusinessDto.ApiResponse.<Void>builder()
                        .success(true)
                        .message("Business deleted successfully")
                        .build()
        );
    }

    @PostMapping("/{businessId}/members")
    @Operation(summary = "Add employee/member to business")
    public ResponseEntity<BusinessDto.ApiResponse<BusinessDto.MemberResponse>> addMember(
            @PathVariable Long businessId,
            @Valid @RequestBody BusinessDto.MemberRequest request) {
        BusinessDto.MemberResponse member = businessService.addMember(businessId, request);
        return new ResponseEntity<>(
                BusinessDto.ApiResponse.<BusinessDto.MemberResponse>builder()
                        .success(true)
                        .message("Member added successfully")
                        .data(member)
                        .build(),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{businessId}/members")
    @Operation(summary = "Get all members of a business")
    public ResponseEntity<BusinessDto.ApiResponse<List<BusinessDto.MemberResponse>>> getMembers(@PathVariable Long businessId) {
        List<BusinessDto.MemberResponse> list = businessService.getMembers(businessId);
        return ResponseEntity.ok(
                BusinessDto.ApiResponse.<List<BusinessDto.MemberResponse>>builder()
                        .success(true)
                        .message("Members retrieved successfully")
                        .data(list)
                        .build()
        );
    }

    @DeleteMapping("/{businessId}/members/{memberId}")
    @Operation(summary = "Remove member from business")
    public ResponseEntity<BusinessDto.ApiResponse<Void>> removeMember(
            @PathVariable Long businessId,
            @PathVariable Long memberId) {
        businessService.removeMember(businessId, memberId);
        return ResponseEntity.ok(
                BusinessDto.ApiResponse.<Void>builder()
                        .success(true)
                        .message("Member removed successfully")
                        .build()
        );
    }
}

