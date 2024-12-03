package com.zosh.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zosh.modal.Seller;
import com.zosh.modal.VerificationCode;
import com.zosh.repository.VerificationCodeRepository;
import com.zosh.request.LoginRequest;
import com.zosh.response.ApiResponse;
import com.zosh.response.AuthResponse;
import com.zosh.service.AuthService;
import com.zosh.service.SellerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sellers")
public class SellerController {
    private final SellerService sellerService;
    private final VerificationCodeRepository verificationCodeRepository;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginSeller(
            @RequestBody LoginRequest req

    ) throws Exception {
        String otp = req.getOtp();
        String email = req.getEmail();

        req.setEmail("seller_" + email);
        AuthResponse authResponse = authService.siging(req);
        return ResponseEntity.ok(authResponse);
    }

    @PatchMapping("/verify/{otp}")
    public ResponseEntity<Seller> verifySellerEmail(@PathVariable String otp) throws Exception {

        VerificationCode verificationCode = verificationCodeRepository.findByOtp(otp);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new Exception("wrong otp");
        }
        Seller seller = sellerService.verifyEmail(verificationCode.getEmail(), otp);
        return new ResponseEntity<>(seller, HttpStatus.OK);

    }

}
