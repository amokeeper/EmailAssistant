package com.felix.email.search;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.SearchTerm;

import com.felix.email.EmailMessageInfo;

public class BodyContentTerm extends SearchTerm {
	private String content;

	public BodyContentTerm(String content) {
		this.content = content;
	}

	@Override
	public boolean match(Message msg) {
		try {
			
			EmailMessageInfo emailMessageInfo = EmailMessageInfo.getInstance(msg);
			String contentType = msg.getContentType().toLowerCase();
			String bodyContent = emailMessageInfo.getBody();
			if (bodyContent.contains(content)) {
				return true;
			}
			
		} catch (MessagingException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof BodyContentTerm)) {
			return false;
		}
		return super.equals(obj);
	}

}
