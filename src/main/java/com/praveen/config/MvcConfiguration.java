package com.praveen.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { "com.praveen.service", "com.praveen.controller", "com.praveen.dao", "com.praveen.model",
		"com.praveen.config" })
public class MvcConfiguration extends WebMvcConfigurerAdapter {
	@Value("${spring.mail.username}")
	String username;
	@Value("${spring.mail.password}")
	String password;
	@Value("${spring.mail.host}")
	String smtpHost;
	@Value("${spring.mail.port}")
	int smtpPort;
	@Value("${spring.mail.properties.mail.smtp.ssl.enable}")
	boolean sslEnabled;
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/view/");
		resolver.setSuffix(".jsp");
		resolver.setViewClass(JstlView.class);
		registry.viewResolver(resolver);
	}

	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(smtpHost);
		mailSender.setPort(smtpPort);
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.starttls.enable", "");
//		props.put("mail.smtp.ssl.enable", "none");
//		props.put("mail.session.mail.transport.protocol","smtps");
		props.put("mail.debug", "true");
		return mailSender;
	}
}