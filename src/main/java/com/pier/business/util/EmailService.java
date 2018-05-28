package com.pier.business.util;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
	
	@Value("${mail.sender}")
	String sender;

	@Value("${mail.auth}")
	private String mailAuth;
  
    @Autowired
    public JavaMailSenderImpl emailSender;
 
    public void sendSimpleMessage(String to, String subject, String text) {
    	
    	Session session = Session.getInstance(emailSender.getJavaMailProperties(),
  			  new javax.mail.Authenticator() {
  				protected PasswordAuthentication getPasswordAuthentication() {
  					return new PasswordAuthentication(emailSender.getUsername(), emailSender.getPassword());
  				}
  			  });
    	
    	Message message = new MimeMessage(session);
    	try {
    		message.setFrom(new InternetAddress(sender, "Mxphysique.com"));			
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);
		} catch (UnsupportedEncodingException | MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}