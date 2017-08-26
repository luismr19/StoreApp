package com.pier.controllers.user;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
import com.pier.business.PromotionBehavior;
import com.pier.config.SpringConfiguration;
import com.pier.config.WebConfiguration;
import com.pier.rest.model.Address;
import com.pier.rest.model.Brand;
import com.pier.rest.model.Category;
import com.pier.rest.model.Flavor;
import com.pier.rest.model.Product;
import com.pier.rest.model.ProductType;
import com.pier.rest.model.Promotion;
import com.pier.rest.model.PromotionRule;
import com.pier.rest.model.PurchaseOrder;
import com.pier.service.ProductDao;
import com.pier.service.ProductTypeDao;
import com.pier.service.PromotionDao;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.impl.FlavorService;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes={WebConfiguration.class,SpringConfiguration.class})
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
	ProductDao productDao;
	
	@Autowired
	PromotionDao promoDao;
	
	@Autowired
	ProductTypeDao  productTypeDao;
	
	@Autowired
	PurchaseOrderDao orderDao;
	
	@Autowired
    private WebApplicationContext webApplicationContext;
	
	@Autowired
	FlavorService flavorService;
	
		
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
		
		Flavor chocolate=flavorService.generateFlavor("chocolate",3L);
		Flavor vanilla=flavorService.generateFlavor("vanilla",3L);
		Flavor none=flavorService.generateFlavor("none",3L);
		
		flavorService.persistFlavors(chocolate,vanilla,none);
		
		
		goldStandard=new Product(optimum,new BigDecimal("903.50"),"Gold Standard",
				"24g of Whey Protein with Amino Acids for Muscle Recovery and Growth",
				new HashSet<Category>(Arrays.asList(muscleGrowth)),protein,15L,true);
		goldStandard.setFlavors(Arrays.asList(
				chocolate,vanilla));
		
		bestBcaa=new Product(bpiSports,new BigDecimal("555.19"),"BEST BCAA","Amino Acid Powder for Workout Recovery",
				new HashSet<Category>(Arrays.asList(performance,recovery,strength)),bcaas,5L,true);
		bestBcaa.setFlavors(Arrays.asList(none));
		
		mPglutamine=new Product(musclePharm,new BigDecimal("290.11"),"GLUTAMINE",
				"Pharmaceutical Quality Post-Workout Recovery Powder"
				+ ", Supports Rebuilding and Recovery from the Toughest Workouts",
				new HashSet<Category>(Arrays.asList(recovery)),glutamine,10L,true);
		mPglutamine.setFlavors(Arrays.asList(none));
		
		combatCrunchBars=new Product(musclePharm,new BigDecimal("53.65"),"Combat Crunch Bars",
				"Delicious Protein Bar with Only 210 Calories"
				+ ", gluten Free, Low Sugar, & Low Net Carb Multi-Layered Baked Bar",
				new HashSet<Category>(Arrays.asList(food)),snack,50L,true);
		combatCrunchBars.setFlavors(Arrays.asList(vanilla));
		
		productDao.update(goldStandard);
		productDao.update(bestBcaa);
		productDao.update(mPglutamine);
		productDao.update(combatCrunchBars);		
		
		
		
	}
	
	@Test
	public void testStandardPurchase() throws IOException, Exception{
		
		goldStandard.setPrice(new BigDecimal("0.00"));	
		goldStandard.getProductFlavors().iterator().next().getProduct().setPrice(new BigDecimal("0.00"));
		mockMvc.perform(post("/addToCart")
				.contentType(contentType)
				.header("Authorization",sampleToken)
				.content(json(goldStandard.getProductFlavors().iterator().next()))).andExpect(status().isOk());
		mockMvc.perform(post("/addToCart")
				.contentType(contentType)
				.header("Authorization",sampleToken)
				.content(json(mPglutamine.getProductFlavors().iterator().next())));
		mockMvc.perform(get("/cart")
				.contentType(contentType)
				.header("Authorization",sampleToken));
	}
	
	@SuppressWarnings({ "unchecked", "unused" })
	@Test
	public void testPurchaseWithPromotions() throws IOException, Exception{
		
		Promotion laborDay=new Promotion();
		laborDay.setInclusive(false);
		laborDay.setDescription("get a free snack bar in the purchase of a protein");
		laborDay.setDisplayName("labor day giveaway!");
		laborDay.setEnabled(true);
		LocalDateTime startDate=LocalDateTime.of(2017, Month.APRIL,28,0,00);
		LocalDateTime endDate=LocalDateTime.of(2017, Month.MAY,10,0,00);
		laborDay.setStartDate(startDate);
		laborDay.setEndDate(endDate);
		
		PromotionRule laborDayRule=new PromotionRule();		
		ProductType protein=productTypeDao.find("name", "protein").get(0);
		
		laborDayRule.setProductTypes(new HashSet<ProductType>(Arrays.asList(protein)));
		@SuppressWarnings("rawtypes")
		List products=productDao.find("name", "Combat Crunch Bars");
		laborDayRule.setGiveAway(products);
		laborDayRule.setMinAmount(0);
		laborDayRule.setMinPurchase(BigDecimal.ZERO);		
		laborDayRule.setBehavior(PromotionBehavior.TOTALDISCOUNT);
		laborDay.setPromotionrule(laborDayRule);
				
		Address addr=new Address();
		addr.setCountry("Mexico");
		addr.setCity("Guadalajara");
		addr.setDistrict("Guadalajara");
		addr.setNumber(24);
		addr.setState("Jalisco");
		addr.setStreet("nomeacuerdo");
		addr.setZipCode(4578);			
		
		promoDao.add(laborDay);
		
		mockMvc.perform(post("/addToCart")
				.contentType(contentType)
				.header("Authorization",sampleToken)
				.content(json(goldStandard.getProductFlavors().iterator().next()))).andExpect(status().isOk());
		
		mockMvc.perform(put("/checkout")
				.content(json(addr))
				.contentType(contentType)
				.header("Authorization",sampleToken)).andExpect(status().isOk());
		
		mockMvc.perform(put("/completePurchase")
				.contentType(contentType)
				.header("Authorization",sampleToken)).andExpect(status().isOk());
		
		List<PurchaseOrder> orders=orderDao.list();
		
		
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
