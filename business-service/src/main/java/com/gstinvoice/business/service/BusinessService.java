package com.gstinvoice.business.service;

import com.gstinvoice.business.dto.BusinessDto;

import java.util.List;

public interface BusinessService {
    BusinessDto.BusinessResponse createBusiness(Long ownerUserId, BusinessDto.CreateBusinessRequest request);
    BusinessDto.BusinessResponse getBusinessById(Long id);
    List<BusinessDto.BusinessResponse> getBusinessesByOwner(Long ownerUserId);
    BusinessDto.BusinessResponse updateBusiness(Long id, Long ownerUserId, BusinessDto.CreateBusinessRequest request);
    void deleteBusiness(Long id, Long ownerUserId);

    BusinessDto.MemberResponse addMember(Long businessId, BusinessDto.MemberRequest request);
    List<BusinessDto.MemberResponse> getMembers(Long businessId);
    void removeMember(Long businessId, Long memberId);
}

