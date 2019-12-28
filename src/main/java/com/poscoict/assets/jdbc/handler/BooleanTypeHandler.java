package com.poscoict.assets.jdbc.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BooleanTypeHandler implements TypeHandler<Boolean> {

    private Logger logger = LogManager.getLogger(BooleanTypeHandler.class);

    public Boolean getResult(ResultSet rs, String columnName) throws SQLException {
        String s = rs.getString(columnName);
        return parseBoolean(s);
    }

    public Boolean getResult(ResultSet rs, int columnIndex) throws SQLException {
        String s = rs.getString(columnIndex);

        return parseBoolean(s);
    }

    public Boolean getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String s = cs.getString(columnIndex);
        return parseBoolean(s);
    }

    public void setParameter(PreparedStatement ps, int i, Boolean bool, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parseString(bool));
    }

    // "Y" or "N" -> true or false
    private boolean parseBoolean(String s) {
    	
        boolean bool = false;

        if (s == null) {
            return false;
        }

        s = s.trim().toUpperCase();

        if (s.length() == 0) {
            return false;
        }

        // allow "Y" or "N"
        if ("Y".equals(s) == false && "N".equals(s) == false) {
            throw new PersistenceException("value must be \"Y\" or \"N\".");
        }

        bool = "Y".equals(s);

        if (logger.isDebugEnabled()) {
            logger.debug("\"" + s + "\" -> " + bool);
        }

        return bool;
    }

    // true or false -> "Y" or "N"
    private String parseString(Boolean bool) {
        String s = (bool != null && bool == true) ? "Y" : "N";

        if (logger.isDebugEnabled()) {
            logger.debug(bool + " -> " + "\"" + s + "\"");
        }

        return s;
    }
}
