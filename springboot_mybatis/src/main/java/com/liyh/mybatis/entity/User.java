package com.liyh.mybatis.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户实体类
 *
 * @Author: liyh
 */
@Data
public class User implements Serializable {
    // 姓名
    private String name;
    // 性别
    private String gender;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 备注
    private String remark;
}
