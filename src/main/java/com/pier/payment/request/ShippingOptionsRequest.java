package com.pier.payment.request;

public class ShippingOptionsRequest {
	
	String dimensions;
	String zip_code;
	String item_price;
	String free_method;
	
	public String getDimensions() {
		return dimensions;
	}
	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}
	public String getZip_code() {
		return zip_code;
	}
	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}
	public String getItem_price() {
		return item_price;
	}
	public void setItem_price(String item_price) {
		this.item_price = item_price;
	}
	public String getFree_method() {
		return free_method;
	}
	public void setFree_method(String free_method) {
		this.free_method = free_method;
	}	

}
