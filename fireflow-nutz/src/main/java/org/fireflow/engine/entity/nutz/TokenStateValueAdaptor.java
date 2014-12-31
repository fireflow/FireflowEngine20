package org.fireflow.engine.entity.nutz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.pvm.kernel.TokenState;
import org.nutz.dao.jdbc.ValueAdaptor;

public class TokenStateValueAdaptor implements ValueAdaptor {

	public Object get(ResultSet rs, String colName) throws SQLException {
		int value = rs.getInt(colName);
		
		return TokenState.fromValue(value);
	}

	public void set(PreparedStatement stat, Object obj, int index)
			throws SQLException {
		TokenState state = (TokenState)obj;
		stat.setInt(index, state.getValue());

	}

}
