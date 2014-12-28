package org.fireflow.engine.entity.nutz;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.engine.entity.runtime.WorkItemState;

public class WorkItemStateValueAdaptor extends EntityStateValueAdaptor {

	public Object get(ResultSet rs, String colName) throws SQLException {
		int value = rs.getInt(colName);
		return WorkItemState.fromValue(value);
	}

}
