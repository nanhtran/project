package com.zosh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zosh.modal.User;

public interface UserRepositoty extends JpaRepository<User, Long> {

}
