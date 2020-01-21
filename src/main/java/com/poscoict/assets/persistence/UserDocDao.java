package com.poscoict.assets.persistence;

import com.poscoict.assets.model.UserDocVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UserDocDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<UserDocVo> listForBeanPropertyRowMapper(String ownerKey) {
        String query = "SELECT * FROM User_Doc where ownerKey = " + "'" + ownerKey + "'";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<UserDocVo>(UserDocVo.class));
    }

    public List<UserDocVo> listForBeanPropertyRowMapperByDocNum(int docnum) {
        String query = "SELECT * FROM User_Doc where docnum = " + "'" + docnum + "'";
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<UserDocVo>(UserDocVo.class));
    }

    public int insert(String ownerKey, int _docnum) {
        String query = "INSERT INTO User_Doc(ownerKey, docnum) VALUES(?, ?)";
        return this.jdbcTemplate.update(query, ownerKey, _docnum);//sign.getSignID(), sign.getSignPath());
    }

    public Map<String, Object>/*List<Doc>*/ getUserDoc(String ownerKey) throws Exception {

        return jdbcTemplate.queryForMap("select * from User_Doc where ownerKey = ?", ownerKey);
        //String query = "select * from test";
        //return jdbcTemplate.query(query, new BeanPropertyRowMapper<Doc>(Doc.class));
    }

    public int updateOwnerKeyByDocNum(String ownerKey, int docnum) {
        //String query = "UPDATE User_Doc SET ownerKey = " + "'" + ownerKey + "'" + "WHERE docnum = " + docnum;
        return jdbcTemplate.update("UPDATE User_Doc SET ownerKey = ? WHERE docnum = ?", ownerKey, docnum);
    }

}
