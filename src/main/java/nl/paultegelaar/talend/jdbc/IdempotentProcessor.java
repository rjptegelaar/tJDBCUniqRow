//Copyright 2017 Paul Tegelaar
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
package nl.paultegelaar.talend.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

public class IdempotentProcessor{
		
	private Connection connection;
	private static final Logger logger = Logger.getLogger("IdempotentProcessor");
				
	public IdempotentProcessor(Connection connection) throws SQLException{		
		
		if(connection==null){
			throw new SQLException("Connection to database is null");
		}
		
		logger.finest("Initializing IdempotentProcessor using JDBC Connection...");		
		if(!connection.getAutoCommit()){
			logger.warning("Autocommit not enabled on connection, don't forget to commit after each row.");
		}
		
		this.connection = connection;

		
		logger.finest("Done initializing IdempotentProcessor.");
	}	
	
	public boolean check(String identifier, String sqlStatement, boolean hash) throws SQLException{
		
		
		if(!StringUtils.isNoneBlank(identifier, sqlStatement)){
			throw new IllegalArgumentException("ID column and SQL statement cannot be empty");
		}
		
		if(hash){
			identifier = DigestUtils.md2Hex(identifier);	
		}
		
		
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
