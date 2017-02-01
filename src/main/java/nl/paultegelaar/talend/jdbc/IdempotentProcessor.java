package nl.paultegelaar.talend.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.lang3.ObjectUtils;

public class IdempotentProcessor{
		
	private Connection connection;
	private static final Logger logger = Logger.getLogger("IdempotentProcessor");
				
	public IdempotentProcessor(Connection connection) throws SQLException{		
		
		logger.finest("Initializing IdempotentProcessor using JDBC Connection...");
		if(ObjectUtils.allNotNull(connection, connection.getClientInfo())){
			logger.finest(connection.getClientInfo().toString());
		}else if(connection!=null){
			logger.finest("Failed to retrieve conntection client information");
		}
		
		if(!connection.getAutoCommit()){
			logger.warning("Autocommit not enabled on connection, don't forget to commit after each row.");
		}
		
		this.connection = connection;

		
		logger.finest("Done initializing IdempotentProcessor.");
	}	
	
	public boolean check(String identifier, String sqlStatement) throws SQLException{
		logger.finest("Checking for ID: " + identifier + " using query " + sqlStatement);
		PreparedStatement ps = null;
		try{
			ps = connection.prepareStatement(sqlStatement);
			ps.setString(1, identifier);
			
			return ps.executeUpdate()==1;	
		}catch(Exception e){						
			return false;
		}finally{			
			ps.close();
		}
	}

	

	
	
}
