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
package nl.paultegelaar.talend.jdbc.test;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import nl.paultegelaar.talend.jdbc.IdempotentProcessor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "/storage-application-context.xml" })
public class IdempotentProcessorTest {

	@Autowired
	private DataSource testDataSource;
	
	private IdempotentProcessor idempotentProcessor;
	
	

	
	@Before
	public void before() throws SQLException{
		if(idempotentProcessor==null){
			idempotentProcessor = new IdempotentProcessor(testDataSource.getConnection());
		}
		//Just create a table, all in memory anyway
		Statement st = testDataSource.getConnection().createStatement();
		st.execute("create table idempotent(id varchar(255) not null unique)");
		st.close();
					
		
	}

	@After
	public void end() throws SQLException{
		//Just drop when done
		Statement st = testDataSource.getConnection().createStatement();
		st.execute("drop table idempotent");
		st.close();		
	}

	
	@Test
	public void happyTest() throws SQLException{
		
		assertTrue(idempotentProcessor.check("test", "insert into idempotent (id) values(?)", false));
		assertTrue(idempotentProcessor.check("test1", "insert into idempotent (id) values(?)", false));
		
	}
	
	@Test
	public void unhappyTest() throws SQLException{

		assertTrue(idempotentProcessor.check("test", "insert into idempotent (id) values(?)", false));
		assertFalse(idempotentProcessor.check("test", "insert into idempotent (id) values(?)", false));
		
	}
	
	@Test
	public void happyTestHash() throws SQLException{
		
		assertTrue(idempotentProcessor.check("test", "insert into idempotent (id) values(?)", true));
		assertTrue(idempotentProcessor.check("test1", "insert into idempotent (id) values(?)", true));
		
	}
	
	@Test
	public void unhappyTestHash() throws SQLException{
		
		assertTrue(idempotentProcessor.check("test", "insert into idempotent (id) values(?)", true));
		assertFalse(idempotentProcessor.check("test", "insert into idempotent (id) values(?)", true));
		
	}
	
	@Test
	public void testParallel() throws SQLException, InterruptedException{
		
		for (int i = 0; i < 5; i++) {
			@SuppressWarnings("unused")
			TestRunner tr = new TestRunner(idempotentProcessor, String.valueOf(i));
		}
		Thread.sleep(2000);
	}
	
	public class TestRunner implements Runnable {

		private String name;
		
		private TestRunner(IdempotentProcessor idempotentProcessor, String name) {
			System.out.println("Spawning thread: " + name);
			this.name = name;
			Thread worker = new Thread(this);
			worker.start();
		}
		
		public void run() {
			System.out.println("Starting run with thread: " + name);
			for (int i = 0; i < 100; i++) {
				try {
					assertTrue(idempotentProcessor.check(name + i, "insert into idempotent (id) values(?)", false));
				} catch (SQLException e) {					
					e.printStackTrace();
				}
			}
			System.out.println("Done running thread: " + name);
		}
	}
	

}
