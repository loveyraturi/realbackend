package com.praveen.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.praveen.dao.UsersRepository;
import com.praveen.model.Users;

@Component
public class EmailServiceImpl {

	@Autowired
	private JavaMailSender emailSender;
	@Autowired
	UsersRepository userRepository;

	public Map<String, String> sendSimpleMessage(Map<String, String> req) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		String to = req.get("to");
		String host = req.get("hostName");
		UUID uuid = UUID.randomUUID(); // Generates random UUID
		String data = " click on this link to reset password   https://"+host + "/#/reset-password/"+to+"/" + uuid.toString();
		Users user = userRepository.findByEmail(to);
		String type = req.get("type");
		System.out.println(to);
		System.out.println(user.getEmail());
		if (user != null) {
			user.setUuid(uuid.toString());
			userRepository.save(user);
			if (type.equals("passwordReset")) {
				String from = req.get("from");
				String subject = req.get("subject");
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(from);
				message.setTo(to);
				message.setSubject(subject);
				message.setText(data);
				emailSender.send(message);
			} else if(type.equals("paymentSuccess")) {
				String from = req.get("from");
				String subject = req.get("subject");
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(from);
				message.setTo(to);
				message.setSubject(subject);
				message.setText("Your payment with id "+req.get("paymentId")+" was successfully processed thanks for choosing us");
				emailSender.send(message);
			}else if(type.equals("paymentFailed")) {
				String from = req.get("from");
				String subject = req.get("subject");
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(from);
				message.setTo(to);
				message.setSubject(subject);
				message.setText("Your payment with id "+req.get("paymentId")+" was failed you can try again later");
				emailSender.send(message);
			}else {
				String from = req.get("from");
				String subject = req.get("subject");
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(from);
				message.setTo(to);
				message.setSubject(subject);
				message.setText(data);
				emailSender.send(message);
			}
		}else {
			response.put("status", "false");
			response.put("message", "No user with this email found");
			return response;
		}
		return response;
	}
}