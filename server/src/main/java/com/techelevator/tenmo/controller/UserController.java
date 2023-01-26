package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping(path = "/userlist")
public class UserController {

    private JdbcUserDao jdbcUserDao;
    private UserDao userDao;

    public UserController(JdbcUserDao userDao) {
        this.userDao = userDao;
    }

    // this method gets a list of all users (expected for the principle (logged-in user))
    @RequestMapping(method = RequestMethod.GET)
    public List<User> listAllUsers(@Valid Principal principal) {
        String name = principal.getName();
        return userDao.findAll(userDao.findByUsername(name));
    }
}
