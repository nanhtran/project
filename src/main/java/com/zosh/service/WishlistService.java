package com.zosh.service;

import com.zosh.exceptions.ProductException;
import com.zosh.modal.Product;
import com.zosh.modal.User;
import com.zosh.modal.Wishlist;

public interface WishlistService {
    Wishlist createWishlist(User user);

    Wishlist getWishlistByUserId(User user) throws Exception;

    Wishlist addProductToWishlist(User user, Product product) throws Exception;
}