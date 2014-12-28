/**
 * Copyright 2007-2010 非也
 * All rights reserved. 
 * 
 * This library is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License v3 as published by the Free Software
 * Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, see http://www.gnu.org/licenses/lgpl.html.
 *
 */
package org.fireflow.simulation.client;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.fireflow.client.WorkflowSession;
import org.fireflow.simulation.support.BreakPoint;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;



/**
 *
 * @author 非也 nychen2000@163.com
 * Fire Workflow 官方网站：www.firesoa.com 或者 www.fireflow.org
 *
 */
@ContextConfiguration(locations = {
		"classpath:/fireflow_config/FireflowContext_Simulator.xml","classpath:/fireflow_config/FireflowContext-Main.xml"})
public class SimulatorStatementTest  extends AbstractJUnit4SpringContextTests  {
	public static final String url = "http://127.0.0.1:9099/FireWorkflowServices";
	@Test
	public void testAddBreakPoint() throws MalformedURLException{
		BreakPoint breakPoint = new BreakPoint();
		breakPoint.setProcessId("process1");
		breakPoint.setElementId("process1.main.trnasition1");
		
		WorkflowSession session = SimulatorSessionFactory.createSimulatorSession(url, "zhangsan", "123");
		
		SimulatorStatement stmt = (SimulatorStatement)session.createWorkflowStatement();
		stmt.addBreakPoint(breakPoint);
	}
	
	/**
	 * 测试HSQLDB 内存数据库在多线程下的访问情况，看看是否会报错。
	 */
	@Test
	public void testMultiThreadHsqlDB(){
		DataSource dataSource = (DataSource)this.applicationContext.getBean("simulatorDataSource");
		for (int i=0;i<20;i++){
			Thread thread = new Thread(new TestRunner(dataSource,i));
			
			thread.start();
			
		}
	}
	
	public class TestRunner implements Runnable{
		DataSource dataSource = null;
		int index = 0;
		TestRunner(DataSource ds,int idx){
			dataSource = ds;
			index = idx;
		}
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			Connection conn = null;
			try {
				System.out.println("=========================");
				conn = dataSource.getConnection();
				System.out.println("==connection is =="+conn);
				Statement stmt = conn.createStatement();
				
				stmt.execute("insert into t_ff_rt_token" +
						"(id,process_instance_id,element_instance_id,BUSINESS_PERMITTED,value," +
						"step_number,state,process_id,version,process_type,element_id)" +
						" values("+index+",'procInst_123','element_inst_123','1',1,9,11,'proc-123',1,'FPDL20','element-123')");

				
				ResultSet rs = stmt.executeQuery("select count(*) from t_ff_rt_token");
				if(rs!=null && rs.next()){
					int count = rs.getInt(1);
					System.out.println("==Token 表有"+count+"条记录");
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				if (conn!=null){
					try {
						conn.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}
}
