package com.praveen.service;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ch.qos.logback.classic.Level;

public class TestMail {

	public static void main(String[] args) 
    {    
        String to = "praveenraturi3@yahoo.com";
        String from = "support@ownertenants.com";
        String host = "smtp.ownertenants.com";
        final String username = "support@ownertenants.com";
        final String password = "%)SUa@c6";
        Properties properties = System.getProperties();

        properties.put("mail.smtp.starttls.enable", "false");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.debug", "true");
        properties.put("mail.smtp.ssl.enable", "false");


//        Session session = Session.getDefaultInstance(properties);
       
        Session session = Session.getDefaultInstance(properties, 
                new javax.mail.Authenticator() { 
                   
                  //override the getPasswordAuthentication method 
                  protected PasswordAuthentication  
                                 getPasswordAuthentication() { 
                                               
                      return new PasswordAuthentication(username,  
                                                       password); 
                  } 
                }); 
        session.setDebug(true);
      
        try {

        	 Message message = new MimeMessage(session);     
             
             // header field of the header. 
             message.setFrom(new InternetAddress(username));  
               
             message.setRecipients(Message.RecipientType.TO, 
                 InternetAddress.parse(to)); 
             message.setSubject("hello"); 
             message.setText("Yo it has been sent"); 
   
             Transport.send(message);         //send Message 
   
             System.out.println("Done");
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
           System.out.println(e);
        }  
    }
}
