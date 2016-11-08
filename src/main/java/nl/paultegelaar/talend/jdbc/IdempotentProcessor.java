package nl.paultegelaar.talend.jdbc;

import java.sql.Connection;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

public class IdempotentProcessor{
		
	private JdbcTemplate jdbcTemplate;
				
	public IdempotentProcessor(Connection connection){		
		jdbcTemplate = new JdbcTemplate(new SingleConnectionDataSource(connection, false));
	}	
	
	public boolean check(String identifier, Date storeDate, String sqlStatement){
		try{
			return jdbcTemplate.update(sqlStatement, new Object[]{identifier,storeDate})==1;	
		}catch(Exception e){
			return false;
		}
		
	}

	

	
	
}
