package com.zang.cloud.mall.user.service;


import com.zang.cloud.mall.common.exception.ImoocException;
import com.zang.cloud.mall.user.model.pojo.User;

public interface UserService {
    User getUser();

    void register(String userName, String password) throws ImoocException;

    User login(String userName, String password) throws ImoocException;

    void updateInformation(User user) throws ImoocException;

    boolean checkAdminRole(User user);
}
