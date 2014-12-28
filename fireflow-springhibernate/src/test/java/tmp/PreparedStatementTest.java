package tmp;

import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;

import javax.sql.DataSource;

import org.fireflow.service.AbsTestContext;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;


@SuppressWarnings("unused")
//hibernate
@ContextConfiguration(locations = { "classpath:/applicationContext.xml"
                              })
public class PreparedStatementTest extends AbsTestContext{

	@Test
	public void testPreparedStatement()throws Exception{
//		String selectSQL = "select t_ff_rt_variable.* ,t_ff_rt_process_instance.suspended from t_ff_rt_variable,t_ff_rt_process_instance " +
//				" where t_ff_rt_variable.scope_id=t_ff_rt_process_instance.id and  scope_id=? and t_ff_rt_variable.process_id=?";
		
		String selectSQL = "select * from t_ff_rt_workitem where id=? and (owner_id like ? or state=? )";
		String updateSQL = "update t_ff_rt_workitem set owner_id=? ,owner_type=? where id=?";
		String insertSQL = "insert into t_ff_rt_workitem (id,state) values (?,?)";
		String deleteSQL = "delete from t_ff_rt_workitem where id=?";
		String wrongSQL = "insert into t_abc(a,b,c) values(?,?,?)";
		
		DataSource ds = (DataSource)this.applicationContext.getBean("MyDataSource");
		Connection conn = ds.getConnection();
		
		PreparedStatement selectPstmt = conn.prepareStatement(selectSQL);
		ParameterMetaData pmtdt = selectPstmt.getParameterMetaData();
		int count = pmtdt.getParameterCount();
		System.out.println("selectSQL 参数数量是 ："+count);
		for (int i=1;i<=count;i++){
			System.out.println("第"+i+"个参数是："+pmtdt.getParameterTypeName(1));
		}
		
		ResultSetMetaData rmtdt = selectPstmt.getMetaData();
		
		PreparedStatement updatePstmt = conn.prepareStatement(updateSQL);

		ParameterMetaData updatePmtdt = updatePstmt.getParameterMetaData();
		count = updatePmtdt.getParameterCount();
		System.out.println("selectSQL 参数数量是 ："+count);
		for (int i=1;i<=count;i++){
			System.out.println("第"+i+"个参数是："+updatePmtdt.getParameterTypeName(1));
		}
		
		rmtdt = updatePstmt.getMetaData();
	}
}
