package com.poscoict.assets.jdbc.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.poscoict.posledger.chain.DateUtil;

public class DayDateTypeHandler extends BaseTypeHandler<Date> {
	
	private final String format = "yyyyMMdd";

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType) throws SQLException {
		ps.setString(i, DateUtil.formatDate(parameter, format));
	}

	@Override
	public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
		if (StringUtils.isNotEmpty(rs.getString(columnName)))
			return DateUtil.stringToDate(rs.getString(columnName), format);
		return null;
	}

	@Override
	public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		if (StringUtils.isNotEmpty(rs.getString(columnIndex)))
			return DateUtil.stringToDate(rs.getString(columnIndex), format);
		return null;
	}

	@Override
	public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		if (StringUtils.isNotEmpty(cs.getString(columnIndex)))
			return DateUtil.stringToDate(cs.getString(columnIndex), format);
		return null;
	}
}