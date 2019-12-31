package com.example.ffaid.service;

import com.example.ffaid.dao.UserDao;
import com.example.ffaid.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author DIX
 * @version 1.0
 * @description
 * @date 2019/11/29 19:57
 */
@Service
public class UserService {
    @Autowired
    UserDao userDao;


    public User userLogin(String username, String password) {
        return userDao.userLogin(username, password);
    }

    public int userRegister(User user) {
        return userDao.userRegister(user);
    }

    public int update(User user) {
        return userDao.update(user);
    }

    public int logicalDelete(Integer id) {
        return userDao.logicalDelete(id);
    }

    public User getUser(Integer id) {
        return userDao.getUser(id);
    }

}
