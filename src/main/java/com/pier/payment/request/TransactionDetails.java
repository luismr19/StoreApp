package com.pier.payment.request;

public class TransactionDetails {
	
	private String financial_institution;
	private float net_received_amount;
	private float total_paid_amount;
	private float installment_amount;
	private float overpaid_amount;
	private String external_resource_url;
	private String payment_method_reference_id;//For credit card payments is the USN. For offline payment methods, is the reference to give to the cashier or to input into the ATM.
	
	public String getFinancial_institution() {
		return financial_institution;
	}
	public void setFinancial_institution(String financial_institution) {
		this.financial_institution = financial_institution;
	}
	public float getNet_received_amount() {
		return net_received_amount;
	}
	public void setNet_received_amount(float net_received_amount) {
		this.net_received_amount = net_received_amount;
	}
	public float getTotal_paid_amount() {
		return total_paid_amount;
	}
	public void setTotal_paid_amount(float total_paid_amount) {
		this.total_paid_amount = total_paid_amount;
	}
	public float getInstallment_amount() {
		return installment_amount;
	}
	public void setInstallment_amount(float installment_amount) {
		this.installment_amount = installment_amount;
	}
	public float getOverpaid_amount() {
		return overpaid_amount;
	}
	public void setOverpaid_amount(float overpaid_amount) {
		this.overpaid_amount = overpaid_amount;
	}
	public String getExternal_resource_url() {
		return external_resource_url;
	}
	public void setExternal_resource_url(String external_resource_url) {
		this.external_resource_url = external_resource_url;
	}
	public String getPayment_method_reference_id() {
		return payment_method_reference_id;
	}
	public void setPayment_method_reference_id(String payment_method_reference_id) {
		this.payment_method_reference_id = payment_method_reference_id;
	}
	
	
	

}
