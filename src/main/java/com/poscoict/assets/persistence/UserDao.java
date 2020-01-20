package com.poscoict.assets.persistence;

import com.poscoict.assets.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<User> listForBeanPropertyRowMapper() {
        String query = "SELECT * FROM user";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<User>(User.class));
    }

    public int insert(String ownerKey, String ownerId) {
        String query = "INSERT INTO user(ownerKey, ownerId) VALUES(?, ?)";
        return jdbcTemplate.update(query, ownerKey, ownerId);
    }

    public /*User*/Map<String, Object> getUser(String ownerId) {


        return jdbcTemplate.queryForMap("select * from user where ownerId = ?", ownerId);

        //ResultSet rs;
        /*try {
            return (User) jdbcTemplate.queryForObject("select * from user where id = ?", new Object[]{userId}, new User(rs.getId(), rs.getPasswd()));
        } catch(Exception e) {

        }*/

        //return null;
    }

    public SqlRowSet getUserByUserId(String ownerId) {

        return this.jdbcTemplate.queryForRowSet("select * from user where ownerId = ?", ownerId);
    }
}
