package com.example.petgo.service;

import com.example.petgo.dto.RegistrationAdditionalInfoRequest;
import com.example.petgo.dto.RegistrationResponse;
import com.example.petgo.dto.RegistrationSubmitRequest;
import com.example.petgo.dto.RegistrationUpsertRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface RegistrationService {
    RegistrationResponse getMyPartnerRegistration(HttpServletRequest request);
    RegistrationResponse savePartnerDraft(HttpServletRequest request, RegistrationUpsertRequest requestBody);
    RegistrationResponse submitPartnerRegistration(HttpServletRequest request, RegistrationSubmitRequest requestBody);
    RegistrationResponse submitPartnerAdditionalInformation(HttpServletRequest request, RegistrationAdditionalInfoRequest requestBody);
    List<RegistrationResponse> getPartnerRegistrationHistory(HttpServletRequest request);
}