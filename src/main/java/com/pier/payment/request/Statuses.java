package com.pier.payment.request;

public class Statuses {

    public static String pending="pending";
        //The user has not yet completed the payment process
    public static String approved="approved";
        //The payment has been approved and accredited
    public static String authorized="authorized";
        //The payment has been authorized but not captured yet
    public static String in_process="in_process";
        //Payment is being reviewed
    public static String in_mediation="in_mediation";
        //Users have initiated a dispute
    public static String rejected="rejected";
        //Payment was rejected. The user may retry payment.
    public static String cancelled="cancelled";
        //Payment was cancelled by one of the parties or because time for payment has expired
    public static String refunded="refunded";
        //Payment was refunded to the user
    public static String charged_back="charged_back";
        //Was made a chargeback in the buyer’s credit card 
    public static String open="open";
        //Order without payments
    public static String closed="closed";
        //Order with payments covering total amount
    public static String expired="expired";
       //Order expired
    
    

}
