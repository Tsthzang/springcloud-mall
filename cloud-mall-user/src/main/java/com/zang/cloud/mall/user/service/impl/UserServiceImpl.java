package com.zang.cloud.mall.user.service.impl;

import com.zang.cloud.mall.common.exception.ImoocException;
import com.zang.cloud.mall.common.exception.ImoocMallExceptionEnum;
import com.zang.cloud.mall.common.utils.MD5Utils;
import com.zang.cloud.mall.user.model.dao.UserMapper;
import com.zang.cloud.mall.user.model.pojo.User;
import com.zang.cloud.mall.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public User getUser() {
        return userMapper.selectByPrimaryKey(1);
    }

    /**
     * 用户注册功能
     * @param userName
     * @param password
     * @throws ImoocException
     */
    @Override
    public void register(String userName, String password) throws ImoocException {
        //查询用户名是否存在
        User result = userMapper.selecteByName(userName);
        if(result != null){
            throw new ImoocException(ImoocMallExceptionEnum.NAME_EXISTED);
        }
        //写到数据库
        User user = new User();
        user.setUsername((userName));
        try {
            user.setPassword(MD5Utils.getMD5Str(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        int count = userMapper.insertSelective(user);
        if(count == 0 ){
            throw new ImoocException(ImoocMallExceptionEnum.INSERT_FAILED);
        }
    }

    /**
     * 用户登录功能
     * @param userName
     * @param password
     * @return
     * @throws ImoocException
     */
    @Override
    public User login(String userName, String password) throws ImoocException {
        String md5Password = null;
        try {
            md5Password = MD5Utils.getMD5Str(password);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        User user = userMapper.selecteLogin(userName, md5Password);
        if(user == null){
            throw new ImoocException(ImoocMallExceptionEnum.WRONG_PASSWORD);
        }
        return user;
    }

    /**
     * 更新个性签名功能
     * @param user
     * @throws ImoocException
     */
    @Override
    public void updateInformation(User user) throws ImoocException{
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if (updateCount > 1) {
            throw new ImoocException(ImoocMallExceptionEnum.UPDATE_FAILED);
        }
    }

    /**
     * 检查是否为管理员用户
     * @param user
     * @return
     * 1：普通用户 / 2：管理员
     */
    @Override
    public boolean checkAdminRole(User user) {
        return user.getRole().equals(2);
    }
}
