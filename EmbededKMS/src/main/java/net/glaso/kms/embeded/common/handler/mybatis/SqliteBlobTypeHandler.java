package net.glaso.kms.embeded.common.handler.mybatis;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BlobTypeHandler;

public class SqliteBlobTypeHandler extends BlobTypeHandler {

	@Override
	public byte[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return cs.getBytes( columnIndex );
	}
	
	@Override
	public byte[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return rs.getBytes( columnName );
	}
	
	@Override
	public byte[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getBytes( columnIndex );
	}
	
	
}	