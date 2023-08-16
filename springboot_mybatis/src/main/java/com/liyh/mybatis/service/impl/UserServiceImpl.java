package com.liyh.mybatis.service.impl;

import com.liyh.mybatis.entity.User;
import com.liyh.mybatis.mapper.UserMapper;
import com.liyh.mybatis.service.UserService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户业务实现类
 *
 * @Author: liyh
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public void saveInfo(User user) {
        userMapper.saveInfo(user);
    }

    @Override
    public void saveList(List<User> list) {
        userMapper.saveList(list);
    }

    @Override
    public void saveBeach(List<User> list) {
        // ExecutorType.SIMPLE: 这个执行器类型不做特殊的事情。它为每个语句的执行创建一个新的预处理语句。
        // ExecutorType.REUSE: 这个执行器类型会复用预处理语句。
        // ExecutorType.BATCH: 这个执行器会批量执行所有更新语句,如果 SELECT 在它们中间执行还会标定它们是 必须的,来保证一个简单并易于理解的行为。

        // 关闭session的自动提交
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            list.stream().forEach(user -> userMapper.saveInfo(user));
            // 提交数据
            sqlSession.commit();
            sqlSession.rollback();
        } catch (Exception e) {
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
    }
}
