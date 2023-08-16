package com.liyh.mybatis.mapper;

import com.liyh.mybatis.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mapper接口
 *
 * @Author: liyh
 */
@Mapper
public interface UserMapper {

    void saveInfo(User user);

    void saveList(@Param("list") List<User> list);

}
