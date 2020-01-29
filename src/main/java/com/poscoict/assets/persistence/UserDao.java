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
        String query = "SELECT * FROM User";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<User>(User.class));
    }

    public int insert(String ownerKey, String ownerId) {
        String query = "INSERT INTO User(ownerKey, ownerId) VALUES(?, ?)";
        return jdbcTemplate.update(query, ownerKey, ownerId);
    }

    public Map<String, Object> getOwnerKey(String ownerId) {
        return jdbcTemplate.queryForMap("select * from User where ownerId = ?", ownerId);
    }

    public Map<String, Object> getOwnerId(String ownerKey) {
        return jdbcTemplate.queryForMap("select * from User where ownerKey = ?", ownerKey);
    }

    public SqlRowSet getUserByUserId(String ownerId) {
        return this.jdbcTemplate.queryForRowSet("select * from User where ownerId = ?", ownerId);
    }
}
