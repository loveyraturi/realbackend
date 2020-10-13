package com.praveen.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.praveen.dao.UsersRepository;
import com.praveen.model.Users;

@Component
public class EmailServiceImpl {

	@Autowired
	private JavaMailSender emailSender;
	@Autowired
	UsersRepository userRepository;
	@Value("${spring.mail.username}")
	String username;

	public Map<String, String> sendSimpleMessage(Map<String, String> req) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		String to = req.get("to");
		String host = req.get("hostName");
		UUID uuid = UUID.randomUUID(); // Generates random UUID
		String data = " click on this link to reset password   https://" + host + "/realestateui/#/reset-password/" + to
				+ "/" + uuid.toString();
		Users user = userRepository.findByEmail(to);
		String type = req.get("type");
		System.out.println(to);
		System.out.println(user.getEmail());
		if (user != null) {
			user.setUuid(uuid.toString());
			userRepository.save(user);
			if (type.equals("passwordReset")) {
				String from = username;
				String subject = req.get("subject");
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(from);
				message.setTo(to);
				message.setSubject(subject);
				message.setText(data);
				emailSender.send(message);
			} else if (type.equals("paymentSuccess")) {
				String from = username;
				String subject = req.get("subject");
				MimeMessage message = emailSender.createMimeMessage();
				MimeMessageHelper helper = null;
				String paymentPlan = req.get("paymentPlan");
				String paymentType = req.get("paymentType");
				try {
					helper = new MimeMessageHelper(message, true);
					helper.setFrom(from);
					helper.setTo(to);
					if (paymentPlan.equals("execlusive")) {
						if (paymentType.equals("tenant")) {
							helper.setText(
									"<html><body><p>Hi from,</p><p>Thank You for subscribing TENANT EXECLUSIVE PLAN worth Rs"
											+ req.get("amount") + "/- </p><p>" + req.get("paymentId")
											+ "</p><p>1 year validity</p><p>Services you can avail</p><p>1.House Rental  Search as per your preference</p><p>2.Online House Verification</p><p>3.Appointment Fixing for House Visit</p><p>6.Your Number Privacy</p><p>7.Rent Negotiations</p><p>8.House Visit Assistance</p><p>9.Rental Agreement Assistance</p><p>Please reach on support@ownertenants.com from 11 to 6 PM(Mon to Sat) for contacting your Dedicated Relationship Manager.</p><p>Terms & Conditions</p><p>•	We Search Houses from Listed property in our platform.We also have  online & offline team to facilitate with right rentals match with Tenant preferences but we don’t provide any kind of Guarantee.</p><p>•	Amount paid as Subscription Fees is non refundable at any case.</p><p>•	It’s a sole discretion of Tenant to opt for the plan before understanding the services he can avail & benefits for him.</p><p>•	As an goodwill gestures if you don’t find rentals from our platform  your validity will extended upto 12 months from the date of subscription.For availing extention of Validity please mail us on support@ownertenants.com  with proof of rental accomodations(rental agreement/address/owner name & contact numbers) within 7 days from expiry of Membership.</p></body></html>",
									true);
						} else {
							helper.setText(
									"<html><body><p>Hi,</p><p>Thank You for subscribing Owner EXECLUSIVE PLAN worth Rs"+ req.get("amount") +"/- </p><p>"+req.get("paymentId")+" </p><p>1 year validity</p><p>Services you can avail</p><p>1.Free Listing of your property </p><p>2.Tenant Search as per your preference</p><p>3.Online Tenant Verification</p><p>4.Appointment Fixing for House Visit</p><p>5.Your Number Privacy </p><p>6.Rent Negotiations</p><p>7.House Visit Assistance</p><p>8.Rental Agreement Assistance</p><p>9.Professional Photoshoot of your property</p><p>Please reach on support@ownertenants.com from 11 to 6 PM(Mon to Sat) for contacting your Dedicated Relationship Manager.</p><p>Terms & Conditions</p><p>•	We Search Tenant from Registered Users in our platform.We also have  online & offline team to facilitate with right rentals match with Owner preferences but we don’t provide any kind of Guarantee.</p><p>•	Amount paid as Subscription Fees is non refundable at any case.</p><p>•	It’s a sole discretion of House Owner to opt for the plan before understanding the services he can avail & benefits for him.</p><p>•	As an goodwill gestures if you don’t find Tenants from our platform your validity will extended upto 12 months from the date of subscription.For availing extention of Validity please mail us on support@ownertenants.com with proof of rental accomodations(rental agreement/address/owner name & contact numbers) within 7 days of expiry of Membership.</p>",
									true);
						}
					} else {
						if (paymentType.equals("tenant")) {
							helper.setText(
									"<html><body><p>Hi from,</p><p>Thank You for subscribing TENANT STANDAR PLAN worth Rs"
											+ req.get("amount") + "/- </p><p>" + req.get("paymentId")
											+ "</p><p>1 year validity</p><p>Services you can avail</p><p>1.House Rental  Search as per your preference</p><p>2.Online House Verification</p><p>3.Appointment Fixing for House Visit</p><p>6.Your Number Privacy</p><p>7.Rent Negotiations</p><p>8.House Visit Assistance</p><p>9.Rental Agreement Assistance</p><p>Please reach on support@ownertenants.com from 11 to 6 PM(Mon to Sat) for contacting your Dedicated Relationship Manager.</p><p>Terms & Conditions</p><p>•	We Search Houses from Listed property in our platform.We also have  online & offline team to facilitate with right rentals match with Tenant preferences but we don’t provide any kind of Guarantee.</p><p>•	Amount paid as Subscription Fees is non refundable at any case.</p><p>•	It’s a sole discretion of Tenant to opt for the plan before understanding the services he can avail & benefits for him.</p><p>•	As an goodwill gestures if you don’t find rentals from our platform  your validity will extended upto 12 months from the date of subscription.For availing extention of Validity please mail us on support@ownertenants.com  with proof of rental accomodations(rental agreement/address/owner name & contact numbers) within 7 days from expiry of Membership.</p></body></html>",
									true);
						} else {
							helper.setText(
									"<html><body><p>Hi,</p><p>Thank You for subscribing Owner STANDAR PLAN worth Rs"+ req.get("amount") +"/- </p><p>"+req.get("paymentId")+" </p><p>1 year validity</p><p>Services you can avail</p><p>1.Free Listing of your property </p><p>2.Tenant Search as per your preference</p><p>3.Online Tenant Verification</p><p>4.Appointment Fixing for House Visit</p><p>5.Your Number Privacy </p><p>6.Rent Negotiations</p><p>7.House Visit Assistance</p><p>8.Rental Agreement Assistance</p><p>9.Professional Photoshoot of your property</p><p>Please reach on support@ownertenants.com from 11 to 6 PM(Mon to Sat) for contacting your Dedicated Relationship Manager.</p><p>Terms & Conditions</p><p>•	We Search Tenant from Registered Users in our platform.We also have  online & offline team to facilitate with right rentals match with Owner preferences but we don’t provide any kind of Guarantee.</p><p>•	Amount paid as Subscription Fees is non refundable at any case.</p><p>•	It’s a sole discretion of House Owner to opt for the plan before understanding the services he can avail & benefits for him.</p><p>•	As an goodwill gestures if you don’t find Tenants from our platform your validity will extended upto 12 months from the date of subscription.For availing extention of Validity please mail us on support@ownertenants.com with proof of rental accomodations(rental agreement/address/owner name & contact numbers) within 7 days of expiry of Membership.</p>",
									true);
						}
					}

				} catch (MessagingException e) {
					e.printStackTrace();
				}
				// SimpleMailMessage message = new SimpleMailMessage();
				// message.setFrom(from);
				// message.setTo(to);
				// message.setSubject(subject);
				// message.setText("Your payment with id "+req.get("paymentId")+" was
				// successfully processed thanks for choosing us");
				emailSender.send(message);
			} else if (type.equals("paymentFailed")) {
				 String from = username;
				String subject = req.get("subject");
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(from);
				message.setTo(to);
				message.setSubject(subject);
				message.setText("Your payment with id " + req.get("paymentId") + " was failed you can try again later");
				emailSender.send(message);
			} else {
				String from = username;
				String subject = req.get("subject");
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(from);
				message.setTo(to);
				message.setSubject(subject);
				message.setText(data);
				emailSender.send(message);
			}
		} else {
			response.put("status", "false");
			response.put("message", "No user with this email found");
			return response;
		}
		return response;
	}
}