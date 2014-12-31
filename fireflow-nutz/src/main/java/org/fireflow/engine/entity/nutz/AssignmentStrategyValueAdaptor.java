package org.fireflow.engine.entity.nutz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.fireflow.model.resourcedef.WorkItemAssignmentStrategy;
import org.nutz.dao.jdbc.ValueAdaptor;

public class AssignmentStrategyValueAdaptor implements ValueAdaptor {

	public Object get(ResultSet rs, String colName) throws SQLException {
		String value = rs.getString(colName);
		return WorkItemAssignmentStrategy.fromValue(value);
	}

	public void set(PreparedStatement stat, Object obj, int index)
			throws SQLException {
		WorkItemAssignmentStrategy strategy = (WorkItemAssignmentStrategy)obj;
		
		stat.setString(index, strategy.getValue());
	}

}
