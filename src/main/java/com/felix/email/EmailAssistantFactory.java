package com.felix.email;

public class EmailAssistantFactory {
	
	public EmailAssistant createEmailAssistant(){
		return new EmailAssistant();
	}
	
	public EmailMessageQuerier createEmailMessageQuerier(){
		return new EmailMessageQuerier();
	}

}
