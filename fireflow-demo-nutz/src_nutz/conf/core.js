var ioc = {
		
		config : {
			type : "org.nutz.ioc.impl.PropertiesProxy",
			fields : {
				paths : ["/conf/myapp.properties"]
			}
		},
		
		dataSource : {
			type :"com.mchange.v2.c3p0.ComboPooledDataSource",
			events : {
				depose :"close"
			},
			fields : {
				driverClass : {java :"$config.get('db-driver')"},
				jdbcUrl             : {java :"$config.get('db-url')"},
				user	        : {java :"$config.get('db-username')"},
				password        : {java :"$config.get('db-password')"},
				minPoolSize     : 5,
				initialPoolSize : 10,
				maxPoolSize     : 100,
				testConnectionOnCheckin : true,
				automaticTestTable : "C3P0TestTable",
				idleConnectionTestPeriod : 18000,
				maxIdleTime : 300,
				maxStatements : 0,
				numHelperThreads : 5
			}
		},
		// Dao
		dao : {
			type :'org.fireflow.demo.FireflowDemoDao',
			args : [ {refer :"dataSource"}]
		}
	};


