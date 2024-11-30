package com.zosh.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.zosh.domain.USER_ROLE;
import com.zosh.modal.Seller;
import com.zosh.modal.User;
import com.zosh.repository.SellerRepositoty;
import com.zosh.repository.UserRepositoty;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CustomUserServiceImpl implements UserDetailsService {

    private final UserRepositoty userRepositoty;
    private final SellerRepositoty sellerRepositoty;
    private static final String SELLER_PREFIX = "seller_";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.startsWith(SELLER_PREFIX)) {

            String actualUsername = username.substring(SELLER_PREFIX.length());
            Seller seller = sellerRepositoty.findByEmail(actualUsername);

            if (seller != null) {
                return buildUserDetails(seller.getEmail(), seller.getPassword(), seller.getRole());
            }

        } else {
            User user = userRepositoty.findByEmail(username);
            if (user != null) {
                return buildUserDetails(user.getEmail(), user.getPassword(), user.getRole());

            }
        }
        throw new UsernameNotFoundException("user or seller not found with email - " + username);
    }

    private UserDetails buildUserDetails(String email, String password, USER_ROLE role) {
        if (role == null)
            role = USER_ROLE.ROLE_CUSTOMER;

        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority("ROLE_" + role));

        return new org.springframework.security.core.userdetails.User(email, password, authorityList);
    }

}
