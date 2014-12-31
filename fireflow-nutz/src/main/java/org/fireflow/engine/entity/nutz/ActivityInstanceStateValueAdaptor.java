package org.fireflow.engine.entity.nutz;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.engine.entity.runtime.ActivityInstanceState;

public class ActivityInstanceStateValueAdaptor extends EntityStateValueAdaptor {

	public Object get(ResultSet rs, String colName) throws SQLException {
		int value = rs.getInt(colName);
		return ActivityInstanceState.fromValue(value);
	}

}
