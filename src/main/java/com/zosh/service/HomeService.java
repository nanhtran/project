package com.zosh.service;

import java.util.List;

import com.zosh.modal.Home;
import com.zosh.modal.HomeCategory;

public interface HomeService {
    public Home createHomePageDate(List<HomeCategory> allCategories);

}