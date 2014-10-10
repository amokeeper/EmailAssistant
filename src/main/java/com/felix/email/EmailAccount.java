package com.felix.email;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.felix.tools.SimpleAuthenticator;
import com.sun.mail.imap.IMAPStore;

public class EmailAccount {
	private static Log log = LogFactory.getLog(EmailAccount.class);  
	Properties properties = new Properties();
	private EmailAccountConfig emailAccountConfig;
	private Session session;
	private Store store;
	private IMAPStore imapStore ;
	private Authenticator authenticator;
	
	public EmailAccount(String emailAccountName){
		emailAccountConfig = new EmailAccountConfig(emailAccountName);
	}
	public Store connect(EmailProtocolType emailProtocolType){
		Store storeTemp = null;
		try {
			log.info("email is logging now");
			final String emaiUrl = emailAccountConfig.getEmailUrl();
			final String emailPwd = emailAccountConfig.getEmailPwd();
			authenticator = new SimpleAuthenticator(emaiUrl,emailPwd );
			Properties properties = emailAccountConfig.getEmailProperties();
			session = Session.getDefaultInstance(properties, authenticator);
			
			switch (emailProtocolType) {
			case POP3:
				log.info("email pop3");
				store = session.getStore("pop3");
				break;
			case IMAP:	
				log.info("email imap");
				store = (IMAPStore) session.getStore("imap");
				break;
			default:
				store = session.getStore();
				break;
			}
			store.connect();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		log.info("email Login success");
		return store;
	}
	
	public void logout(){
		
		
	}
	
	public static void main(String[] args) {
		EmailAccount ea = new EmailAccount("emailAccount");
	
	}
	public Session getSession() {
		return session;
	}
	public void setSession(Session session) {
		this.session = session;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	public IMAPStore getImapStore() {
		return imapStore;
	}
	public void setImapStore(IMAPStore imapStore) {
		this.imapStore = imapStore;
	}

}
