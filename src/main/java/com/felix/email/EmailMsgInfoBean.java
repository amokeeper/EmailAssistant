package com.felix.email;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class EmailMsgInfoBean {
	private String bcc;
	private String bodyContent;
	private String cc;
	private String sentDate;
	private String from;
	private String to;
	private String num;
	private String receivedDate;
	private String subject;
	private String flag;
	private List<EmailAttachment> emailAttachments = new ArrayList<EmailAttachment>();
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append("Email NO is: "+num);
		sb.append("\n");
		sb.append("Email From : "+from);
		sb.append("\n");
		sb.append("Email to : "+to);
		sb.append("\n");
		sb.append("Email cc : "+cc);
		sb.append("\n");
		sb.append("Email bcc : "+bcc);
		sb.append("\n");
		sb.append("Email subject : "+subject);
		sb.append("\n");
		sb.append("Email receivedDate is : "+receivedDate);
		sb.append("\n");
		sb.append("Email sentDate is : "+sentDate);
		sb.append("\n");
		sb.append("Email Content is: "+ bodyContent);
		sb.append("\n");
		for (int i = 1; i < emailAttachments.size(); i++) {
			EmailAttachment emailAttachment = emailAttachments.get(i-1);
			sb.append("Email Attachment "+i+" name is : "+emailAttachment.getFileName());
			sb.append("Email Attachment "+i+" path is : "+emailAttachment.getFilePath());
			sb.append("\n");
		}
		
		
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	
	
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public String getBcc() {
		return bcc;
	}
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	public String getBodyContent() {
		return bodyContent;
	}
	public void setBodyContent(String bodyContent) {
		this.bodyContent = bodyContent;
	}
	public String getCc() {
		return cc;
	}
	public void setCc(String cc) {
		this.cc = cc;
	}
	public String getSentDate() {
		return sentDate;
	}
	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getReceivedDate() {
		return receivedDate;
	}
	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<EmailAttachment> getEmailAttachments() {
		return emailAttachments;
	}

	public void addEmailAttachment(EmailAttachment emailAttachment) {
		emailAttachments.add(emailAttachment);
	}

	public void setEmailAttachments(List<EmailAttachment> emailAttachments) {
		this.emailAttachments = emailAttachments;
	}
	

}
