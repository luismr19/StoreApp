package com.pier.payment.request;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Payment {
  
  private String id;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
  private LocalDateTime date_created;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
  private LocalDateTime date_approved;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
  private LocalDateTime date_last_updated;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
  private LocalDateTime money_release_date;
  private Integer collector_id;
  private String operation_type;
  private Payer payer;
  private Boolean binary_mode;//When set to true, the payment can only be approved or rejected. Otherwise in_process status is added
  private Boolean live_mode;// specifies sandbox or production (readable)
  private PaymentOrder order;
  private String external_reference; //this has to be the PurchaseOrder id
  private String description;
  private Object metadata; //Valid JSON that can be attached to the payment to record additional attributes of the merchant 
  private String currency_id;
  private Float transaction_amount; //Product cost
  private Float transaction_amount_refunded;
  private Float coupon_amount; //Amount of the coupon discount 
  private Integer campaign_id; //Discount campaign ID 
  private String coupon_code; //Discount campaign with a specific code
  private TransactionDetails transaction_details;
  private List<FeeDetail> fee_details;
  private Integer differential_pricing_id; //Id of the scheme for the absorption of financing fee 
  private Float application_fee; //Fee collected by a marketplace or MercadoPago Application
  private String status; 
  private String status_detail; //Gives more detailed information on the current state or rejection cause 
  private Boolean capture=true; //Determines if the payment should be captured (true, default value) or just reserved (false)
  private Boolean captured;//Determines if the capture operation was performed (just for credit cards) 
  private String call_for_authorize_id; //Identifier that must be provided to the issuing bank to authorize the payment
  private String payment_method_id; //Payment method chosen to do the payment  https://api.mercadopago.com/sites/:site_id/payment_methods
  private String issuer_id; //Payment method issuer
  private String payment_type_id; //Type of payment method chosen https://api.mercadopago.com/payment_types
  private String token;
  private Card card;
  private String statement_descriptor; //How will look the payment in the card bill (e.g.: MERCADOPAGO) 
  private Integer installments=1; //Selected quantity of installments
  private String notification_url;//URL where mercadopago will send notifications associated to changes in this payment 
  private String callback_url; //URL where mercadopago does the final redirect (only for bank transfers)
  private List<Refund> refunds; //List of refunds that were made to this payment https://api.mercadopago.com/v1/payments/:payment_id/refunds/:refunds_id
  private AdditionalInfo additional_info; //Data that could improve fraud analysis and conversion rates. Try to send as much information as possible. 
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public LocalDateTime getDate_created() {
    return date_created;
  }
  public void setDate_created(LocalDateTime date_created) {
    this.date_created = date_created;
  }
  public LocalDateTime getDate_approved() {
    return date_approved;
  }
  public void setDate_approved(LocalDateTime date_approved) {
    this.date_approved = date_approved;
  }
  public LocalDateTime getDate_last_updated() {
    return date_last_updated;
  }
  public void setDate_last_updated(LocalDateTime date_last_updated) {
    this.date_last_updated = date_last_updated;
  }
  public LocalDateTime getMoney_release_date() {
    return money_release_date;
  }
  public void setMoney_release_date(LocalDateTime money_release_date) {
    this.money_release_date = money_release_date;
  }
  public Integer getCollector_id() {
    return collector_id;
  }
  public void setCollector_id(Integer collector_id) {
    this.collector_id = collector_id;
  }
  public String getOperation_type() {
    return operation_type;
  }
  public void setOperation_type(String operation_type) {
    this.operation_type = operation_type;
  }
  public Payer getPayer() {
    return payer;
  }
  public void setPayer(Payer payer) {
    this.payer = payer;
  }
  public Boolean isBinary_mode() {
    return binary_mode;
  }
  public void setBinary_mode(Boolean binary_mode) {
    this.binary_mode = binary_mode;
  }
  public Boolean isLive_mode() {
    return live_mode;
  }
  public void setLive_mode(Boolean live_mode) {
    this.live_mode = live_mode;
  }
  public PaymentOrder getOrder() {
    return order;
  }
  public void setOrder(PaymentOrder order) {
    this.order = order;
  }
  public String getExternal_reference() {
    return external_reference;
  }
  public void setExternal_reference(String external_reference) {
    this.external_reference = external_reference;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public Object getMetadata() {
    return metadata;
  }
  public void setMetadata(Object metadata) {
    this.metadata = metadata;
  }
  public String getCurrency_id() {
    return currency_id;
  }
  public void setCurrency_id(String currency_id) {
    this.currency_id = currency_id;
  }
  public Float getTransaction_amount() {
    return transaction_amount;
  }
  public void setTransaction_amount(Float transaction_amount) {
    this.transaction_amount = transaction_amount;
  }
  public Float getTransaction_amount_refunded() {
    return transaction_amount_refunded;
  }
  public void setTransaction_amount_refunded(Float transaction_amount_refunded) {
    this.transaction_amount_refunded = transaction_amount_refunded;
  }
  public Float getCoupon_amount() {
    return coupon_amount;
  }
  public void setCoupon_amount(Float coupon_amount) {
    this.coupon_amount = coupon_amount;
  }
  public Integer getCampaign_id() {
    return campaign_id;
  }
  public void setCampaign_id(Integer campaign_id) {
    this.campaign_id = campaign_id;
  }
  public String getCoupon_code() {
    return coupon_code;
  }
  public void setCoupon_code(String coupon_code) {
    this.coupon_code = coupon_code;
  }
  public TransactionDetails getTransaction_details() {
    return transaction_details;
  }
  public void setTransaction_details(TransactionDetails transaction_details) {
    this.transaction_details = transaction_details;
  }
  public List<FeeDetail> getFee_details() {
    return fee_details;
  }
  public void setFee_details(List<FeeDetail> fee_details) {
    this.fee_details = fee_details;
  }
  public Integer getDifferential_pricing_id() {
    return differential_pricing_id;
  }
  public void setDifferential_pricing_id(Integer differential_pricing_id) {
    this.differential_pricing_id = differential_pricing_id;
  }
  public Float getApplication_fee() {
    return application_fee;
  }
  public void setApplication_fee(Float application_fee) {
    this.application_fee = application_fee;
  }
  public String getStatus() {
    return status;
  }
  public void setStatus(String status) {
    this.status = status;
  }
  public String getStatus_detail() {
    return status_detail;
  }
  public void setStatus_detail(String status_detail) {
    this.status_detail = status_detail;
  }
  public Boolean isCapture() {
    return capture;
  }
  public void setCapture(Boolean capture) {
    this.capture = capture;
  }
  public Boolean isCaptured() {
    return captured;
  }
  public void setCaptured(Boolean captured) {
    this.captured = captured;
  }
  public String getCall_for_authorize_id() {
    return call_for_authorize_id;
  }
  public void setCall_for_authorize_id(String call_for_authorize_id) {
    this.call_for_authorize_id = call_for_authorize_id;
  }
  public String getPayment_method_id() {
    return payment_method_id;
  }
  public void setPayment_method_id(String payment_method_id) {
    this.payment_method_id = payment_method_id;
  }
  public String getIssuer_id() {
    return issuer_id;
  }
  public void setIssuer_id(String issuer_id) {
    this.issuer_id = issuer_id;
  }
  public String getPayment_type_id() {
    return payment_type_id;
  }
  public void setPayment_type_id(String payment_type_id) {
    this.payment_type_id = payment_type_id;
  }
  public String getToken() {
    return token;
  }
  public void setToken(String token) {
    this.token = token;
  }
  public Card getCard() {
    return card;
  }
  public void setCard(Card card) {
    this.card = card;
  }
  public String getStatement_descriptor() {
    return statement_descriptor;
  }
  public void setStatement_descriptor(String statement_descriptor) {
    this.statement_descriptor = statement_descriptor;
  }
  public Integer getInstallments() {
    return installments;
  }
  public void setInstallments(Integer installments) {
    this.installments = installments;
  }
  public String getNotification_url() {
    return notification_url;
  }
  public void setNotification_url(String notification_url) {
    this.notification_url = notification_url;
  }
  public String getCallback_url() {
    return callback_url;
  }
  public void setCallback_url(String callback_url) {
    this.callback_url = callback_url;
  }
  public List<Refund> getRefunds() {
    return refunds;
  }
  public void setRefunds(List<Refund> refunds) {
    this.refunds = refunds;
  }
  public AdditionalInfo getAdditional_info() {
    return additional_info;
  }
  public void setAdditional_info(AdditionalInfo additional_info) {
    this.additional_info = additional_info;
  }
 
}
