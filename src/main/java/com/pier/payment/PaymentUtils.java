package com.pier.payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mercadopago.MP;
import com.pier.business.PaymentErrorException;
import com.pier.model.security.User;
import com.pier.payment.request.PaymentOrder;
import com.pier.payment.request.AdditionalInfo;
import com.pier.payment.request.ErrorResponse;
import com.pier.payment.request.Payer;
import com.pier.payment.request.PayerAddress;
import com.pier.payment.request.Payment;
import com.pier.payment.request.PaymentEvent;
import com.pier.payment.request.Phone;
import com.pier.rest.model.PurchaseOrder;
import com.pier.service.impl.OrderService;

import java.math.BigDecimal;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class PaymentUtils {
	
	@Value("${access_token}")
	String access_token;
	
	@Value("${payment_description}")
	String payment_description;
	
	@Value("${application_name}")
	String application_name;
	
	@Value("${notification_url}")
	String notification_url;
	
	@Value("${ENVIRONMENT}")
	String environment;
	
	@Autowired
	ObjectMapper jsonMapper;
	
	@Autowired
	OrderService orderService;
	
	public void createClient(String email){
		MP mp = new MP (access_token);
		
		mp.sandboxMode(environment.equals("test"));
		
		JSONObject emailJson=new JSONObject();
		try {
			emailJson.append("email", email);
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		
		try {
			mp.post("/v1/customers", emailJson.toString());
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
	}
	
	public Payment pay(PurchaseOrder order, String token, String paymentMethod) throws PaymentErrorException{
		
		MP mp = new MP (access_token);
		mp.sandboxMode(environment.equals("test"));
		
		BigDecimal totalTotal=null;
		
		if(order.getGift()!=null){
			totalTotal=order.getTotal().subtract(order.getGift().getDiscount());
		}else{
			totalTotal=order.getTotal();
		}
		
		String notificationsUrl="";
		
		//check for absolute path
		if(!notification_url.contains("http")){		
		//if the path is not absolute then build the corresponding url based in the current context path
		ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
		builder.scheme("http");
		builder.path("/"+notification_url);		
		notificationsUrl=builder.toUriString();
		}else{
			notificationsUrl=this.notification_url;
		}
		
		Payment payment=new Payment();
		Payer payer=buildPayer(order);
		
		
		payment.setPayer(payer);
		payment.setBinary_mode(false);
		PaymentOrder paymentOrder=new PaymentOrder();
		paymentOrder.setType(PaymentOrder.MERCADOPAGO);
		payment.setExternal_reference(String.valueOf(order.getId()));
		payment.setDescription(payment_description);
		payment.setTransaction_amount(totalTotal.floatValue());
		payment.setCapture(true);//if set to false the money will be reserved and not instantly paid
		payment.setPayment_method_id(paymentMethod);
		payment.setToken(token);
		payment.setStatement_descriptor(application_name);
		if(!(notification_url.isEmpty() || notification_url.equals("disabled")))
		payment.setNotification_url(notificationsUrl);
		//payment.setAdditional_info(buildDetails(order));
		
		JSONObject paymentJson=null;
		Payment response=null;
		
		ErrorResponse error=null;
		
		try {
			
			paymentJson = mp.post("/v1/payments",jsonMapper.writeValueAsString(payment));
			response=jsonMapper.readValue(paymentJson.getJSONObject("response").toString(), Payment.class);
			if(response.getId()==null)
				error=jsonMapper.readValue(paymentJson.toString(), ErrorResponse.class);
			
		} catch (Exception e) {
			throw new PaymentErrorException("default");
		}
			if(error!=null){
				throw new PaymentErrorException(error.getStatus().toString());				
			}
		
		
		return response;
		
	}
	
	private Payer buildPayer(PurchaseOrder order){
		
		Payer payer=new Payer();
		User owner=order.getOwner();
		PayerAddress address=new PayerAddress();
		address.setStreet_name(owner.getAddress().getStreet());
		address.setStreet_number(owner.getAddress().getNumber());
		address.setZip_code(String.valueOf(owner.getAddress().getZipCode()));
		payer.setAddress(address);
		
		payer.setEmail(owner.getEmail());
		payer.setFirst_name(owner.getFirstname());
		payer.setLast_name(owner.getLastname());
		
		Phone phone=new Phone();
		phone.setArea_code("52");
		phone.setNumber(String.valueOf(owner.getPhoneNumber()));
		
		/*payer.setPhone(phone);
		payer.setEntity_type("guest");*/
		
		return payer;
	}
	
	private AdditionalInfo buildDetails(PurchaseOrder order){
		
		return new AdditionalInfo();
		
	}
	
	public PurchaseOrder handleNotification(PaymentEvent event) throws Exception{
		MP mp = new MP (access_token);
		mp.sandboxMode(environment.equals("test"));		
		
		Payment paymentInfo=null;
		
		if (event.getType().equals("payment")) {
			paymentInfo = jsonMapper.readValue(mp.get ("/v1/payments/"+event.getData().getId()).toString(),Payment.class);

			if (paymentInfo!=null) {
				 PurchaseOrder order=orderService.getOrderByPaymentId(paymentInfo.getId());
   			  if(order.getConcluded()!=true){
   				  order.setConcluded(true);
   				  return order;
   			  }
			}
		}	    	
		   return null;	
		   
	}

}
