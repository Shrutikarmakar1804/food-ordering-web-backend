package com.foodsphere.service;

import com.foodsphere.model.User;

public interface UserService {

    public User findUserByJwt(String jwt) throws Exception;

    public User findUserByEmail(String email) throws Exception;
}