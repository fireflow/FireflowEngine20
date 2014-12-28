package org.fireflow.engine.entity.nutz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.engine.entity.runtime.ScheduleJobState;
import org.nutz.dao.jdbc.ValueAdaptor;

public class ScheduleJobStateValueAdaptor implements ValueAdaptor {

	
	public Object get(ResultSet rs, String colName) throws SQLException {
		int value = rs.getInt(colName);
		return ScheduleJobState.fromValue(value);
	}

	public void set(PreparedStatement stat, Object obj, int index)
			throws SQLException {
		ScheduleJobState state = (ScheduleJobState)obj;
		stat.setInt(index, state.getValue());

	}

}
