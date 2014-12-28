package org.fireflow.engine.entity.nutz;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.fireflow.engine.entity.EntityState;
import org.nutz.dao.jdbc.ValueAdaptor;

public abstract class EntityStateValueAdaptor implements ValueAdaptor {



	public void set(PreparedStatement stat, Object obj, int index)
			throws SQLException {
		EntityState entityState = (EntityState)obj;
		stat.setInt(index, entityState.getValue());

	}

}
