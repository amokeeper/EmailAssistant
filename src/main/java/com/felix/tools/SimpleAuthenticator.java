package com.felix.tools;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class SimpleAuthenticator extends Authenticator {
	private String user;  
    private String pwd;  
 
    public SimpleAuthenticator(String user, String pwd) {  
        this.user = user;  
        this.pwd = pwd;  
    }  
 
    @Override 
    protected PasswordAuthentication getPasswordAuthentication() {  
        return new PasswordAuthentication(user, pwd);  
    }  

}
