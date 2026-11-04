package com.gstinvoice.business.service.impl;

import com.gstinvoice.business.dto.BusinessDto;
import com.gstinvoice.business.entity.Business;
import com.gstinvoice.business.entity.BusinessMember;
import com.gstinvoice.business.exception.BadRequestException;
import com.gstinvoice.business.exception.ResourceNotFoundException;
import com.gstinvoice.business.repository.BusinessMemberRepository;
import com.gstinvoice.business.repository.BusinessRepository;
import com.gstinvoice.business.service.BusinessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;
    private final BusinessMemberRepository memberRepository;

    public BusinessServiceImpl(BusinessRepository businessRepository, BusinessMemberRepository memberRepository) {
        this.businessRepository = businessRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public BusinessDto.BusinessResponse createBusiness(Long ownerUserId, BusinessDto.CreateBusinessRequest request) {
        Business business = Business.builder()
                .ownerUserId(ownerUserId)
                .businessName(request.getBusinessName().trim())
                .gstin(request.getGstin() != null ? request.getGstin().trim().toUpperCase() : null)
                .pan(request.getPan() != null ? request.getPan().trim().toUpperCase() : null)
                .email(request.getEmail())
                .phone(request.getPhone())
                .website(request.getWebsite())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .city(request.getCity())
                .state(request.getState().trim())
                .stateCode(request.getStateCode().trim())
                .pincode(request.getPincode())
                .logoUrl(request.getLogoUrl())
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .ifscCode(request.getIfscCode())
                .branchName(request.getBranchName())
                .upiId(request.getUpiId())
                .termsAndConditions(request.getTermsAndConditions())
                .invoicePrefix(request.getInvoicePrefix() != null ? request.getInvoicePrefix() : "INV-")
                .build();

        return mapToResponse(businessRepository.save(business));
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessDto.BusinessResponse getBusinessById(Long id) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + id));
        return mapToResponse(business);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessDto.BusinessResponse> getBusinessesByOwner(Long ownerUserId) {
        return businessRepository.findByOwnerUserId(ownerUserId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BusinessDto.BusinessResponse updateBusiness(Long id, Long ownerUserId, BusinessDto.CreateBusinessRequest request) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + id));

        business.setBusinessName(request.getBusinessName().trim());
        business.setGstin(request.getGstin() != null ? request.getGstin().trim().toUpperCase() : null);
        business.setPan(request.getPan() != null ? request.getPan().trim().toUpperCase() : null);
        business.setEmail(request.getEmail());
        business.setPhone(request.getPhone());
        business.setWebsite(request.getWebsite());
        business.setAddressLine1(request.getAddressLine1());
        business.setAddressLine2(request.getAddressLine2());
        business.setCity(request.getCity());
        business.setState(request.getState().trim());
        business.setStateCode(request.getStateCode().trim());
        business.setPincode(request.getPincode());
        business.setLogoUrl(request.getLogoUrl());
        business.setBankName(request.getBankName());
        business.setAccountNumber(request.getAccountNumber());
        business.setIfscCode(request.getIfscCode());
        business.setBranchName(request.getBranchName());
        business.setUpiId(request.getUpiId());
        business.setTermsAndConditions(request.getTermsAndConditions());
        if (request.getInvoicePrefix() != null) {
            business.setInvoicePrefix(request.getInvoicePrefix());
        }

        return mapToResponse(businessRepository.save(business));
    }

    @Override
    public void deleteBusiness(Long id, Long ownerUserId) {
        Business business = businessRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + id));
        businessRepository.delete(business);
    }

    @Override
    public BusinessDto.MemberResponse addMember(Long businessId, BusinessDto.MemberRequest request) {
        if (!businessRepository.existsById(businessId)) {
            throw new ResourceNotFoundException("Business not found with id: " + businessId);
        }

        if (memberRepository.findByBusinessIdAndEmail(businessId, request.getEmail()).isPresent()) {
            throw new BadRequestException("Member already belongs to this business: " + request.getEmail());
        }

        BusinessMember member = BusinessMember.builder()
                .businessId(businessId)
                .name(request.getName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .role(request.getRole().toUpperCase())
                .build();

        member = memberRepository.save(member);
        return mapToMemberResponse(member);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessDto.MemberResponse> getMembers(Long businessId) {
        return memberRepository.findByBusinessId(businessId)
                .stream()
                .map(this::mapToMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void removeMember(Long businessId, Long memberId) {
        BusinessMember member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + memberId));
        if (!member.getBusinessId().equals(businessId)) {
            throw new BadRequestException("Member does not belong to specified business");
        }
        memberRepository.delete(member);
    }

    private BusinessDto.BusinessResponse mapToResponse(Business b) {
        return BusinessDto.BusinessResponse.builder()
                .id(b.getId())
                .ownerUserId(b.getOwnerUserId())
                .businessName(b.getBusinessName())
                .gstin(b.getGstin())
                .pan(b.getPan())
                .email(b.getEmail())
                .phone(b.getPhone())
                .website(b.getWebsite())
                .addressLine1(b.getAddressLine1())
                .addressLine2(b.getAddressLine2())
                .city(b.getCity())
                .state(b.getState())
                .stateCode(b.getStateCode())
                .pincode(b.getPincode())
                .logoUrl(b.getLogoUrl())
                .bankName(b.getBankName())
                .accountNumber(b.getAccountNumber())
                .ifscCode(b.getIfscCode())
                .branchName(b.getBranchName())
                .upiId(b.getUpiId())
                .termsAndConditions(b.getTermsAndConditions())
                .invoicePrefix(b.getInvoicePrefix())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }

    private BusinessDto.MemberResponse mapToMemberResponse(BusinessMember m) {
        return BusinessDto.MemberResponse.builder()
                .id(m.getId())
                .businessId(m.getBusinessId())
                .userId(m.getUserId())
                .name(m.getName())
                .email(m.getEmail())
                .role(m.getRole())
                .joinedAt(m.getJoinedAt())
                .build();
    }
}

