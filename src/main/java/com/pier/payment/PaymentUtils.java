package com.pier.payment;
import com.mercadopago.MP;
import com.pier.rest.model.PurchaseOrder;

import java.math.BigDecimal;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PaymentUtils {
	
	@Value("${access_token}")
	String access_token;
	
	public void createClient(String email){
		MP mp = new MP (access_token);
		
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
	
	public void pay(PurchaseOrder order, String token, String paymentMethod){
		
		MP mp = new MP (access_token);
		
		BigDecimal totalTotal=order.getTotal().subtract(order.getGift().getDiscount());
		
		
		try {
			
			JSONObject payment = mp.post("/v1/payments","");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
