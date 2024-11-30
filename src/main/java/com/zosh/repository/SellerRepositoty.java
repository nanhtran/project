package com.zosh.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zosh.modal.Seller;

public interface SellerRepositoty extends JpaRepository<Seller, Long> {

    Seller findByEmail(String email);

}
