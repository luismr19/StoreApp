package com.pier;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.jdbc.JdbcTestUtils;

@ContextConfiguration(locations={"/persistence-beans.xml"})
public class DomainAwareBase extends AbstractJUnit4SpringContextTests {
	
	 private final String deleteScript = "src/main/resources/sql/cleanup.sql";
	 private final String populateScript = "src/main/resources/sql/populate.sql";

	 @Autowired
	    private JdbcTemplate jdbcTemplate;

	    @SuppressWarnings("deprecation")
		@Before
	    public void deleteAllDomainEntities() {
	        JdbcTestUtils.executeSqlScript(jdbcTemplate, new EncodedResource(new FileSystemResource(deleteScript)), false);
	        //JdbcTestUtils.executeSqlScript(jdbcTemplate, new EncodedResource(new FileSystemResource(populateScript)), false);
	                
	    }

}
