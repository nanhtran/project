package com.zosh.service;

import com.zosh.response.SignupRequest;

public interface AuthService {
    String createUser(SignupRequest req);
}