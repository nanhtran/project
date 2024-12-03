package com.zosh.service.impl;

import java.net.Authenticator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.zosh.config.JwtProvider;
import com.zosh.domain.USER_ROLE;
import com.zosh.modal.Cart;
import com.zosh.modal.Seller;
import com.zosh.modal.User;
import com.zosh.modal.VerificationCode;
import com.zosh.repository.CartRepository;
import com.zosh.repository.UserRepository;
import com.zosh.repository.SellerRepository;
import com.zosh.repository.VerificationCodeRepository;
import com.zosh.request.LoginRequest;
import com.zosh.response.AuthResponse;
import com.zosh.response.SignupRequest;
import com.zosh.service.AuthService;
import com.zosh.service.EmailService;
import com.zosh.utils.OtpUtil;

import jakarta.validation.constraints.Email;

import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties.AssertingParty.Verification;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartRepository cartRepository;
    private final JwtProvider jwtProvider;
    private final VerificationCodeRepository verificationCodeRepository;
    private final EmailService emailService;
    private final CustomUserServiceImpl customUserService;
    private final SellerRepository sellerRepository;

    @Override
    public String createUser(SignupRequest req) throws Exception {

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(req.getEmail());

        if (verificationCode == null || !verificationCode.getOtp().equals(req.getOtp())) {
            throw new Exception("Wrong otp...");
        }

        User user = userRepository.findByEmail(req.getEmail());

        if (user == null) {
            User createdUser = new User();
            createdUser.setEmail(req.getEmail());
            createdUser.setFullName(req.getFullName());
            createdUser.setRole(USER_ROLE.ROLE_CUSTOMER);
            createdUser.setMobile("8964765432");
            createdUser.setPassword(passwordEncoder.encode(req.getOtp()));

            user = userRepository.save(createdUser);

            Cart cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(USER_ROLE.ROLE_CUSTOMER.toString()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(req.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtProvider.generateToken(authentication);
    }

    @Override
    public void sentLoginOtp(String email, USER_ROLE role) throws Exception {
        String SIGNING_PREFIX = "signing_";

        if (email.startsWith(SIGNING_PREFIX)) {
            email = email.substring(SIGNING_PREFIX.length());

            if (role.equals(USER_ROLE.ROLE_SELLER)) {
                Seller seller = sellerRepository.findByEmail(email);
                if (seller == null) {
                    throw new Exception("seller not found");

                }

            } else {
                System.out.println("email  " + email);
                User user = userRepository.findByEmail(email);
                if (user == null) {
                    throw new Exception("user not exits with provided email");
                }

            }

        }

        VerificationCode isExist = verificationCodeRepository.findByEmail(email);

        if (isExist != null) {
            verificationCodeRepository.delete(isExist);

        }

        String otp = OtpUtil.generateOtp();

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(otp);
        verificationCode.setEmail(email);
        verificationCodeRepository.save(verificationCode);

        String subject = "zosh bazaar Login/signup otp";
        String text = "your login/signup otp is - " + otp;

        emailService.sendVerificationOtpEmail(email, otp, subject, text);
    }

    @Override
    public AuthResponse siging(LoginRequest req) throws Exception {

        String username = req.getEmail();
        String otp = req.getOtp();

        Authentication authentication = authenticate(username, otp);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login success");

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        String roleName = authorities.isEmpty() ? null : authorities.iterator().next().getAuthority();

        authResponse.setRole(USER_ROLE.valueOf(roleName));

        return authResponse;
    }

    private Authentication authenticate(String username, String otp) {
        UserDetails userDetails = customUserService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("invalid username or password");

        }

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(username);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException("wrong otp");
        }
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
    }

}
