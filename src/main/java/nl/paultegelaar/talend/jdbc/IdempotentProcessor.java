package nl.paultegelaar.talend.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class IdempotentProcessor {

	private String sqlStatement;
	private PreparedStatement pStatement;
	
	
	public IdempotentProcessor(String sqlStatement, Connection dbConnection) throws SQLException {
		super();
		this.sqlStatement = sqlStatement;
		
		pStatement = dbConnection.prepareStatement(sqlStatement);
		
	}



	public boolean check(String identifier, Date storeDate){
		
		
		
		return false;
	}


	
	
}
