package com.poscoict.assets.persistence;

import com.poscoict.assets.model.User_Sig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class User_sigDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<User_Sig> listForBeanPropertyRowMapper(String ownerKey) {
        String query = "SELECT * FROM User_Sig where ownerKey = " + "'" + ownerKey + "'";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<User_Sig>(User_Sig.class));
    }

    public int insert(String ownerKey, int sigNum) {
        String query = "INSERT INTO User_Sig(ownerKey, signum) VALUES(?, ?)";
        return this.jdbcTemplate.update(query, ownerKey, sigNum);//sign.getSignID(), sign.getSignPath());
    }

    public Map<String, Object>/*List<Doc>*/ getUserSig(String _ownerKey) throws Exception {

        return jdbcTemplate.queryForMap("select * from User_Sig where ownerKey = ?", _ownerKey);
        //String query = "select * from test";
        //return jdbcTemplate.query(query, new BeanPropertyRowMapper<Doc>(Doc.class));
    }

    public Map<String, Object>/*List<Doc>*/ getUserid(int _signum) throws Exception {

        return jdbcTemplate.queryForMap("select * from User_Sig where signum = ?", _signum);
        //String query = "select * from test";
        //return jdbcTemplate.query(query, new BeanPropertyRowMapper<Doc>(Doc.class));
    }
}
