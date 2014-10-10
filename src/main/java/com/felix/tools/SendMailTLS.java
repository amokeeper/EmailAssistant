package com.felix.tools;

import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.MailSSLSocketFactory;

public class SendMailTLS {
	public static void main(String[] args) throws GeneralSecurityException, AddressException, MessagingException {
		final String username = "yuefei.zhu@symbio.com";
		final String password = "Amo53739576";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
//		props.put("mail.smtp.timeout", "60000");
		props.put("mail.smtp.host", "smtp.office365.com");
		props.put("mail.smtp.port", 25);
//		props.put("mail.smtp.socketFactory.class",
//                "javax.net.ssl.SSLSocketFactory");
//        MailSSLSocketFactory sf = new MailSSLSocketFactory();
//        sf.setTrustAllHosts(true);
		
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
		 MimeMessage message = new MimeMessage(session);
         message.setFrom(new InternetAddress(username));
         message.setRecipients(Message.RecipientType.TO,
                 InternetAddress.parse("53739576@qq.com"));
         message.setSubject("test subject");
         message.setText("body");
         Transport t = session.getTransport("smtp");
         try {
             t.connect("smtp.office365.com", username, password);
             t.sendMessage(message, message.getAllRecipients());
         }catch(Exception e){
             System.out.println(e);
         } finally {
             t.close();
         }
	}
}