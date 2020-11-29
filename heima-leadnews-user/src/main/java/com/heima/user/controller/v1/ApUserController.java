package com.heima.user.controller.v1;

import com.heima.apis.user.ApUserControllerApi;
import com.heima.model.user.pojo.ApUser;
import com.heima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class ApUserController implements ApUserControllerApi {
    @Autowired
    ApUserService apUserService;
    /**
     * 根据id查询app端用户信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Override
    public ApUser findUserById(@PathVariable("id") Integer id) {
        return apUserService.getById(id);
    }
}
