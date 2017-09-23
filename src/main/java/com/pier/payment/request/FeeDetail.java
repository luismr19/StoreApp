package com.pier.payment.request;

public class FeeDetail {
	
	private String type;
	private String fee_payer; //Who absorbs the cost
	private float amount;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFee_payer() {
		return fee_payer;
	}
	public void setFee_payer(String fee_payer) {
		this.fee_payer = fee_payer;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	
	

}
