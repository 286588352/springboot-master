package com.liyh.mybatis.controller;

import com.liyh.mybatis.entity.User;
import com.liyh.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试接口
 *
 * @Author: liyh
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 第一种方案，用 for语句循环插入 10万 条数据
     */
    @GetMapping("/test1")
    public String test1(int count) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setName("方案1测试" + i);
            user.setGender("男");
            user.setUsername("方案1测试");
            user.setPassword("方案1测试");
            user.setRemark("方案1测试");
            userService.saveInfo(user);
        }
        stopWatch.stop();
        System.out.println("第一种方案，用 for语句循环插入耗时：" + stopWatch.getTotalTimeMillis());
        return "操作完成";
    }

    /**
     * 第二种方案，利用mybatis的foreach来实现循环插入 10万 条数据
     */
    @GetMapping("/test2")
    public String test2(int count) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setName("方案2测试" + i);
            user.setGender("男");
            user.setUsername("方案2测试");
            user.setPassword("方案2测试");
            user.setRemark("方案2测试");
            list.add(user);
        }
        userService.saveList(list);
        stopWatch.stop();
        System.out.println("第二种方案，利用mybatis的foreach来实现循环插入耗时：" + stopWatch.getTotalTimeMillis());
        return "操作完成";
    }

    /**
     * 第三种方案，使用sqlSessionFactory实现批量插入 10万 条数据
     */
    @GetMapping("/test3")
    public String test3(int count) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<User> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setName("方案3测试" + i);
            user.setGender("男");
            user.setUsername("方案3测试");
            user.setPassword("方案3测试");
            user.setRemark("方案3测试");
            list.add(user);
        }
        userService.saveBeach(list);
        stopWatch.stop();
        System.out.println("第三种方案，使用sqlSessionFactory实现批量插入：" + stopWatch.getTotalTimeMillis());
        return "操作完成";
    }

}
