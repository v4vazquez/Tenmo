package com.techelevator.dao;


import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class JdbcUserDaoTests extends BaseDaoTests{

    private JdbcUserDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
        boolean userCreated = sut.create("TEST_USER","test_password");
        Assert.assertTrue(userCreated);
    }

    @Test
    public void createNewUser() {
        User user = sut.findByUsername("TEST_USER");
        Assert.assertEquals("TEST_USER", user.getUsername());
    }

    @Test
    public void findByUserIdTest() {
        User userResult = sut.findByUserId(1002);
        Assert.assertEquals(1002, userResult.getId());
    }

    @Test
    public void findByUsernameTest() {
        User userResult = sut.findByUsername("TEST_USER");
        Assert.assertEquals("TEST_USER", userResult.getUsername());
    }

    @Test
    public void findAllTest() {
        User user = new User();
        user.setId(1001);
        user.setUsername("bob");
        List<User> userResult = sut.findAll(user);
        Assert.assertEquals(2, userResult.size()); // size
    }

    @Test
    public void findIdByUsernameTest() {
        User userResult = sut.findByUsername("bob");
        Assert.assertEquals(1001, userResult.getId());
    }
}
