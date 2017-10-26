package com.pier;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import com.pier.config.SpringConfiguration;
import com.pier.config.WebConfiguration;

@WebAppConfiguration
@ContextConfiguration(classes={WebConfiguration.class,SpringConfiguration.class})
public class DomainAwareBase extends AbstractJUnit4SpringContextTests {
	
	 private final String deleteScript = "src/main/resources/sql/cleanupx.sql";
	 private final String populateScript = "src/main/resources/sql/populatex.sql";

	 @Autowired
	    private JdbcTemplate jdbcTemplate;

	    @SuppressWarnings("deprecation")
		@Before
	    public void deleteAllDomainEntities() {
	        JdbcTestUtils.executeSqlScript(jdbcTemplate, new EncodedResource(new FileSystemResource(deleteScript)), false);
	        //JdbcTestUtils.executeSqlScript(jdbcTemplate, new EncodedResource(new FileSystemResource(populateScript)), false);
	                
	    }

}
