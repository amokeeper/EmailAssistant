package com.felix.email;

public class EmailServerTool {
	
	private EmailAccount emailAccount;
	private EmailMsgReceiver emailMsgShow;
	private EmailMessageQuerier emailMessageQuerier;
	
	public void showMsg(){
		
		emailAccount = new EmailAccount("emailAccount");
		emailMessageQuerier = new EmailMessageQuerier();
//		emailMessageQuerier.setSubject("测试邮件");
		
//		emailMessageQuerier.setFrom("Jennifer Xi  <yu.xi@symbio.com>");
		emailMessageQuerier.setPersonal("Jennifer Xi");
		emailMessageQuerier.setBodyText("信必优的小伙伴们");
//		emailMessageQuerier.setSentDate("2014-9-11");
//		emailMessageQuerier.setSendTimeInterval("2014-9-11 8:11:00", "2014-9-11 12:11:00");
		emailMsgShow = new EmailMsgReceiver(emailAccount.connect(EmailProtocolType.IMAP),emailMessageQuerier);
		emailMsgShow.receiveMsg();
		
	}
	
	
	public static void main(String[] args) {
		EmailServerTool est = new EmailServerTool();
		est.showMsg();
	}

}
