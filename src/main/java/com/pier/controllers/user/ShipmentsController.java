package com.pier.controllers.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pier.business.PurchaseOperationsDelegate;
import com.pier.payment.request.ShippingOptionsRequest;

@RestController
@RequestMapping("shipments")
public class ShipmentsController {
	
	@Autowired
	PurchaseOperationsDelegate purchaseOps;
	
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> getPrices(@RequestBody ShippingOptionsRequest shippingOptionsRequest){
		
		return ResponseEntity.ok(purchaseOps.calculateShippingOptions(shippingOptionsRequest));
				
	}
	

}
