package com.zang.cloud.mall.cartorder.fegin;

import com.zang.cloud.mall.user.model.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "cloud-mall-user")
public interface UserFeginClient {

    @GetMapping("/getUser")
    User getUser();
}
