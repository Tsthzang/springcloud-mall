package com.zang.clou.mall.zuul.feign;

import com.zang.cloud.mall.user.model.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "cloud-mall-user")
public interface UserFeignClient {
    /**
     * 校验是否是管理员
     */
    @PostMapping("/checkAdminRole")
    public boolean checkAdminRole(User user);
}
