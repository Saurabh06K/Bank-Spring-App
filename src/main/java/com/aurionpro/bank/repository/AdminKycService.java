//package com.aurionpro.bank.repository;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.aurionpro.bank.dto.KycDto;
//import com.aurionpro.bank.entity.Kyc;
//
//@Service
//public class AdminKycService {
//	 @Autowired
//	    private KycRepository kycRepo;
//
//	    public List<KycDto> getAllPendingKyc() {
//	        return kycRepo.findByKycStatus("Pending")
//	                      .stream()
//	                      .map(this::toKycDto)
//	                      .collect(Collectors.toList());
//	    }
//
//	    public void approveKyc(int kycId) {
//	        Kyc kyc = kycRepo.findById(kycId).orElseThrow(() -> new KycNotFoundException("KYC not found"));
//	        kyc.setKycStatus("Approved");
//	        kycRepo.save(kyc);
//	    }
//}
