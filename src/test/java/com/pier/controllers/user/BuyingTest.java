package com.pier.controllers.user;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;

import static com.pier.TestUtil.*;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pier.rest.model.Brand;
import com.pier.rest.model.Category;
import com.pier.rest.model.Product;
import com.pier.rest.model.ProductType;
import com.pier.service.ProductDao;


@RunWith(SpringRunner.class)
@ContextConfiguration(value={"classpath:persistence-beans.xml","classpath:controllers.xml"})
@WebAppConfiguration
public class BuyingTest {

	private MockMvc mockMvc;
	
	private HttpMessageConverter mappingJackson2HttpMessageConverter;	
	
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
	            MediaType.APPLICATION_JSON.getSubtype(),
	            Charset.forName("utf8"));

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	ProductDao dao;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
		
	Product goldStandard;
	
	Product bestBcaa;
	
	Product mPglutamine;
	
	Product combatCrunchBars;
	
	
	@Before
	public void setup() throws Exception{
		
		setConverters();
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
		
		FileSystemResource script=new FileSystemResource("src/main/resources/sql/cleanup.sql");
        JdbcTestUtils.executeSqlScript(jdbcTemplate, new EncodedResource(script), false);       
        
		Brand optimum=new Brand("ON","Optimum Nutrition");
		Brand bpiSports=new Brand("bpi","BPI Sports");
		Brand musclePharm=new Brand("MP","Muscle Pharm");
		
		Category muscleGrowth=new Category("muscle growth");
		Category recovery=new Category("muscle recovery");
		Category performance=new Category("performance");
		Category strength=new Category("strength");
		Category food=new Category("food");
		
		ProductType protein= new ProductType("Protein");
		ProductType bcaas= new ProductType("Aminoacid");
		ProductType snack=new ProductType("snack");
		ProductType glutamine=new ProductType("glutamine");
		
		
		goldStandard=new Product(optimum,new BigDecimal("903.50"),"Gold Standard",
				"24g of Whey Protein with Amino Acids for Muscle Recovery and Growth",
				Arrays.asList(muscleGrowth),protein,15L,true);
		bestBcaa=new Product(bpiSports,new BigDecimal("555.19"),"BEST BCAA","Amino Acid Powder for Workout Recovery",
				Arrays.asList(performance,recovery,strength),bcaas,5L,true);
		mPglutamine=new Product(musclePharm,new BigDecimal("290.11"),"GLUTAMINE",
				"Pharmaceutical Quality Post-Workout Recovery Powder"
				+ ", Supports Rebuilding and Recovery from the Toughest Workouts",
				Arrays.asList(recovery),glutamine,10L,true);
		combatCrunchBars=new Product(musclePharm,new BigDecimal("53.65"),"Combat Crunch Bars",
				"Delicious Protein Bar with Only 210 Calories"
				+ ", gluten Free, Low Sugar, & Low Net Carb Multi-Layered Baked Bar",
				Arrays.asList(food),snack,50L,true);
		
		dao.update(goldStandard);
		dao.update(bestBcaa);
		dao.update(mPglutamine);
		dao.update(combatCrunchBars);		
		
		
	}
	
	@Test
	public void testStandardPurchase() throws IOException, Exception{
		
				
		mockMvc.perform(post("/addToCart")
				.contentType(contentType)
				.header("Authorization",sampleToken)
				.content(json(goldStandard))).andExpect(status().isOk());
		mockMvc.perform(post("/addToCart")
				.contentType(contentType)
				.header("Authorization",sampleToken)
				.content(json(mPglutamine)));
	}
	
	@SuppressWarnings("unchecked")
	protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        String result=mockHttpOutputMessage.getBodyAsString();
        return result;
    }
	
	  void setConverters() {
	        this.mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();

	        assertNotNull("the JSON message converter must not be null",
	                this.mappingJackson2HttpMessageConverter);
	    }
	
	
	
}
