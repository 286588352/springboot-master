package com.liyh.mybatis.service;

import com.liyh.mybatis.entity.User;

import java.util.List;

/**
 * 用户业务接口
 *
 * @Author: liyh
 */
public interface UserService {

    /**
     * 第一种方案，用 for语句循环插入 10万 条数据
     *
     * @param user
     */
    void saveInfo(User user);

    /**
     * 第二种方案，利用mybatis的foreach来实现循环插入 10万 条数据
     *
     * @param list
     */
    void saveList(List<User> list);

    /**
     * 第三种方案，使用sqlSessionFactory实现批量插入 10万 条数据
     *
     * @param list
     */
    void saveBeach(List<User> list);

}
