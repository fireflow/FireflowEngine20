package org.fireflow.engine.entity.nutz;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.engine.entity.runtime.ProcessInstanceState;

public class ProcessInstanceStateValueAdaptor extends EntityStateValueAdaptor {

	public Object get(ResultSet rs, String colName) throws SQLException {
		int value = rs.getInt(colName);
		return ProcessInstanceState.fromValue(value);
	}

}
