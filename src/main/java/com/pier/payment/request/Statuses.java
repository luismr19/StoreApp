package com.pier.payment.request;

public class Statuses {

    public static String pending;
        //The user has not yet completed the payment process
    public static String approved;
        //The payment has been approved and accredited
    public static String authorized;
        //The payment has been authorized but not captured yet
    public static String in_process;
        //Payment is being reviewed
    public static String in_mediation;
        //Users have initiated a dispute
    public static String rejected;
        //Payment was rejected. The user may retry payment.
    public static String cancelled;
        //Payment was cancelled by one of the parties or because time for payment has expired
    public static String refunded;
        //Payment was refunded to the user
    public static String charged_back;
        //Was made a chargeback in the buyer’s credit card 

}
