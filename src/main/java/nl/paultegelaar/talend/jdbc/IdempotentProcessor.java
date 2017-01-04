package nl.paultegelaar.talend.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class IdempotentProcessor{
		
	private JdbcTemplate jdbcTemplate;
	private static final Logger logger = Logger.getLogger("IdempotentProcessor");
				
	public IdempotentProcessor(Connection connection) throws SQLException{		
		
		logger.info("Initializing IdempotentProcessor using JDBC Connection...");
		if(connection!=null && connection.getClientInfo()!=null){
			logger.info(connection.getClientInfo().toString());
		}else if(connection!=null){
			logger.info("Failed to retrieve conntection client information");
		}else{
			logger.severe("Connection is null, failed to start component.");
		}
		jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(connection, false));

		
		logger.info("Done initializing IdempotentProcessor, created JDBCTemplate using Connection.");
	}	
	
	public boolean check(String identifier, String sqlStatement){
		try{
			return jdbcTemplate.update(sqlStatement, identifier)==1;	
		}catch(Exception e){
			return false;
		}
		
	}

	

	
	
}
