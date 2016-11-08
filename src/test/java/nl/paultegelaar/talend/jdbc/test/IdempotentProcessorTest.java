package nl.paultegelaar.talend.jdbc.test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import junit.framework.Assert;
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
		st.execute("create table idempotent(id varchar(255) not null unique, created timestamp)");
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
	public void happyTest(){
		
		Assert.assertTrue(idempotentProcessor.check("test", new Date(), "insert into idempotent values(?, ?)"));
		Assert.assertTrue(idempotentProcessor.check("test1", new Date(), "insert into idempotent values(?, ?)"));
		
	}
	
	@Test
	public void unhappyTest(){
		
		Assert.assertTrue(idempotentProcessor.check("test", new Date(), "insert into idempotent values(?, ?)"));
		Assert.assertFalse(idempotentProcessor.check("test", new Date(), "insert into idempotent values(?, ?)"));
		
	}
	
	@Test
	public void testParallel() throws SQLException, InterruptedException{
		
		for (int i = 0; i < 5; i++) {
			TestRunner tr = new TestRunner(idempotentProcessor, String.valueOf(i));
		}
		Thread.sleep(10000);
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
				Assert.assertTrue(idempotentProcessor.check(name + i, new Date(), "insert into idempotent values(?, ?)"));
			}
			System.out.println("Done running thread: " + name);
		}
	}
	

}
