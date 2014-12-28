package org.fireflow.engine.entity.nutz;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.namespace.QName;

import org.nutz.dao.jdbc.ValueAdaptor;

public class QNameValueAdaptor implements ValueAdaptor {

	public Object get(ResultSet rs, String colName) throws SQLException {
		String s = (String) rs.getString(colName);
		if (s==null || s.trim().equals(""))return null;
		return QName.valueOf(s);
	}

	public void set(PreparedStatement stat, Object obj, int index)
			throws SQLException {
		if (obj != null) {
			stat.setString(index,((QName)obj).toString());
		} else {
			stat.setNull(index, java.sql.Types.VARCHAR);
		}
		
	}

}
