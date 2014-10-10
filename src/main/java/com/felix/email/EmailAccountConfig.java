package com.felix.email;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class EmailAccountConfig {
	
	private static Log log = LogFactory.getLog(EmailAccountConfig.class);  
	Properties properties = new Properties();
	Properties emailProperties = new Properties();
	private String emailAccountName;
	private String emailUrl;
	private String emailPwd;
	private String smtpHost;
	private String smtpPort;
	private String pop3Host;
	private String pop3Port;
	public EmailAccountConfig(String emailAccountName){
		this.emailAccountName = emailAccountName;
		initAccountConfig();
	}
	
	public void initAccountConfig(){
		
		log.info("init Account Config start");

		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(emailAccountName+".properties");  
		try {
			properties.load(inputStream);
		} catch (Exception e) {
			log.info("email properties file loads fail!");
			e.printStackTrace();
		}
		emailUrl = properties.getProperty("emailUrl");
		emailPwd = properties.getProperty("emailPwd");
		smtpHost = properties.getProperty("smtpHost");
		smtpPort = properties.getProperty("smtpPort");
		pop3Host = properties.getProperty("pop3Host");
		pop3Port = properties.getProperty("pop3Port");
		
		log.info("init Account Config success");
		
	}
	
	
	public Properties getEmailProperties(){
		
		emailProperties.setProperty("mail.smtp.host", smtpHost);
		emailProperties.setProperty("mail.smtp.port", smtpPort);
		emailProperties.setProperty("mail.transport.protocol", "smtp");
		emailProperties.setProperty("mail.smtp.auth", "true");
		emailProperties.setProperty("mail.smtp.starttls.enable", "true");
		emailProperties.setProperty("mail.store.protocol", "pop3");
		emailProperties.setProperty("mail.pop3.host", pop3Host); //
		emailProperties.setProperty("mail.pop3.port", pop3Port); // 端口		
		emailProperties.setProperty("mail.pop3.starttls.enable", "true");

		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		emailProperties.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
		emailProperties.setProperty("mail.pop3.socketFactory.fallback", "false");
		
		
		emailProperties.setProperty("mail.imap.starttls.enable", "true");
		emailProperties.setProperty("mail.imap.host", "outlook.office365.com");
		emailProperties.setProperty("mail.imap.auth.plain.disable", "true");
		emailProperties.setProperty("mail.smtp.port", "587");
		
		
		
		return emailProperties;
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		EmailAccountConfig eaAccountConfig = new EmailAccountConfig("emailAccount");
		eaAccountConfig.initAccountConfig();
	}
	
	public String getSmtpHost() {
		return smtpHost;
	}
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}
	public String getSmtpPort() {
		return smtpPort;
	}
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}
	public String getPop3Host() {
		return pop3Host;
	}
	public void setPop3Host(String pop3Host) {
		this.pop3Host = pop3Host;
	}
	public String getPop3Port() {
		return pop3Port;
	}
	public void setPop3Port(String pop3Port) {
		this.pop3Port = pop3Port;
	}
	public String getEmailPwd() {
		return emailPwd;
	}
	public void setEmailPwd(String emailPwd) {
		this.emailPwd = emailPwd;
	}
	public String getEmailUrl() {
		return emailUrl;
	}
	public void setEmailUrl(String emailUrl) {
		this.emailUrl = emailUrl;
	}


	public String getEmailAccountName() {
		return emailAccountName;
	}


	public void setEmailAccountName(String emailAccountName) {
		this.emailAccountName = emailAccountName;
	}

	public void setEmailProperties(Properties emailProperties) {
		this.emailProperties = emailProperties;
	}
	
	
	
	
}
