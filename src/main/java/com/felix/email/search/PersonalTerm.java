package com.felix.email.search;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.StringTerm;

import com.felix.email.EmailMessageInfo;

public class PersonalTerm extends StringTerm {

	public PersonalTerm(String pattern) {
		super(pattern);
	}

	@Override
	public boolean match(Message msg) {
		
		EmailMessageInfo emailMessageInfo = EmailMessageInfo.getInstance(msg);
		try {
			String from = emailMessageInfo.getFrom();
			if (from.contains(pattern)) {
				System.out.println("Email from : "+from);
				return true;
			}
			
			
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return false;
	}

}
