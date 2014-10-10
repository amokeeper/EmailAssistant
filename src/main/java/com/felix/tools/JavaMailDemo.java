package com.felix.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class JavaMailDemo {

	// 收 POP 服务器 pop3.live.com
	// 接收 POP 邮件端口 995
	// 要求 POP SSL 是
	// 用户名 您的 Windows Live ID（例如：example555@hotmail.com）
	// 密码 用于登录 Windows Live 的密码
	// 发送 SMTP 服务器 smtp.live.com
	// 发送 SMTP 邮件端口 25 或 587
	// 要求验证 是（您的 Windows Live ID 和密码）
	// 要求 TLS/SSL 是（如果可以请选择 TLS，否则请选择 SSL）

	private MimeMessage msg = null;
	private String saveAttchPath = "";
	private StringBuffer bodytext = new StringBuffer();
	private String dateformate = "yy-MM-dd HH:mm";

	private static String from = "yuefei.zhu@symbio.com";
	private static String to = "53739576@qq.com";
	private static String subject = "使用Authenticator子类进行用户身份认证";
	private static String body = "使用Authenticator子类进行用户身份认证的测试！！！";
	private static String host = "smtp.office365.com";
	
	public static void main(String[] args) throws MessagingException,IOException {
		
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
		Properties props = new Properties();
		props.setProperty("mail.smtp.port", "587");
		props.setProperty("mail.transport.protocol", "smtp");
		props.setProperty("mail.smtp.host", host);
		props.setProperty("mail.smtp.auth", "true");
		props.setProperty("mail.smtp.starttls.enable", "true");
		props.setProperty("mail.store.protocol", "pop3");
		props.setProperty("mail.pop3.port", "995"); // 端口
		props.setProperty("mail.pop3.host", "outlook.office365.com"); //
		props.setProperty("mail.pop3.starttls.enable", "true");
		props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.pop3.socketFactory.fallback", "false");
		
		props.setProperty("mail.imap.starttls.enable", "true");


		props.setProperty("mail.imap.host", "outlook.office365.com");
		props.setProperty("mail.imap.auth.plain.disable", "true");
		props.setProperty("mail.smtp.port", "587");
		
//		props.setProperty("mail.pop3.socketFactory.port", "995");

		SimpleAuthenticator simpleAuthenticator = new SimpleAuthenticator(
				"yuefei.zhu@symbio.com", "Amo53739576");
		Session session = Session
				.getDefaultInstance(props,simpleAuthenticator);
		// session.setDebug(true);
		Store store = session.getStore("imap");
		store.connect();

		Folder inbox = store.getFolder("INBOX");
		
		System.out.println("收件箱的邮件数：" + inbox.getMessageCount());
		
		inbox.open(Folder.READ_ONLY);
		
		
		FetchProfile profile = new FetchProfile();
		
		
		profile.add(FetchProfile.Item.ENVELOPE);
		
		
		Message[] messages = inbox.getMessages();
		
		
		inbox.fetch(messages, profile);
		
		
		System.out.println("收件箱的邮件数：" + messages.length);
		
		
		 for (int i = 0; i < messages.length; i++) {
		 //邮件发送者
		 String from = decodeText(messages[i].getFrom()[0].toString());
		 InternetAddress ia = new InternetAddress(from);
		 System.out.println("FROM:" +
		 ia.getPersonal()+'('+ia.getAddress()+')');
		 //邮件标题
		 System.out.println("TITLE:" + messages[i].getSubject());
		 //邮件大小
		 System.out.println("SIZE:" + messages[i].getSize());
		 //邮件发送时间
		 System.out.println("DATE:" + messages[i].getSentDate());
		
		
		 }


		// MimeMessage message = new MimeMessage(session);
		// JavaMailDemo jmDemo = new JavaMailDemo(message);
		// jmDemo.sendMessage();
		//
		// store.connect();
		// Folder folder = store.getFolder("INBOX");
		// folder.open(Folder.READ_ONLY);
		// Message msgs[] = folder.getMessages();
		// int count = msgs.length;
		// System.out.println("Message Count:"+count);
		// JavaMailDemo rm = null;
		// for(int i=0;i<count;i++){
		// rm = new JavaMailDemo((MimeMessage) msgs[i]);
		// rm.recive(msgs[i],i);;
		// }
		//

	}

	public JavaMailDemo(MimeMessage msg) {
		this.msg = msg;
	}

	public void setMsg(MimeMessage msg) {
		this.msg = msg;
	}

	protected static String decodeText(String text)
			throws UnsupportedEncodingException {
		if (text == null)
			return null;
		if (text.startsWith("=?GB") || text.startsWith("=?gb"))
			text = MimeUtility.decodeText(text);
		else
			text = new String(text.getBytes("ISO8859_1"));
		return text;
	}

	public void sendMessage() {

		try {
			// Set the from address
			msg.setFrom(new InternetAddress(from));

			// Set the to address
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Set the subject
			msg.setSubject("测试程序！");

			// Set the content
			msg.setText("这是用java写的发送电子邮件的测试程序！");

			msg.saveChanges();

			Transport.send(msg);

		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 获取发送邮件者信息
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public String getFrom() throws MessagingException {
		InternetAddress[] address = (InternetAddress[]) msg.getFrom();
		String from = address[0].getAddress();
		if (from == null) {
			from = "";
		}
		String personal = address[0].getPersonal();
		if (personal == null) {
			personal = "";
		}
		String fromaddr = personal + "<" + from + ">";
		return fromaddr;
	}

	/**
	 * 获取邮件收件人，抄送，密送的地址和信息。根据所传递的参数不同 "to"-->收件人,"cc"-->抄送人地址,"bcc"-->密送地址
	 * 
	 * @param type
	 * @return
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public String getMailAddress(String type) throws MessagingException,
			UnsupportedEncodingException {
		String mailaddr = "";
		String addrType = type.toUpperCase();
		InternetAddress[] address = null;

		if (addrType.equals("TO") || addrType.equals("CC")
				|| addrType.equals("BCC")) {
			if (addrType.equals("TO")) {
				address = (InternetAddress[]) msg
						.getRecipients(Message.RecipientType.TO);
			}
			if (addrType.equals("CC")) {
				address = (InternetAddress[]) msg
						.getRecipients(Message.RecipientType.CC);
			}
			if (addrType.equals("BCC")) {
				address = (InternetAddress[]) msg
						.getRecipients(Message.RecipientType.BCC);
			}

			if (address != null) {
				for (int i = 0; i < address.length; i++) {
					String mail = address[i].getAddress();
					if (mail == null) {
						mail = "";
					} else {
						mail = MimeUtility.decodeText(mail);
					}
					String personal = address[i].getPersonal();
					if (personal == null) {
						personal = "";
					} else {
						personal = MimeUtility.decodeText(personal);
					}
					String compositeto = personal + "<" + mail + ">";
					mailaddr += "," + compositeto;
				}
				mailaddr = mailaddr.substring(1);
			}
		} else {
			throw new RuntimeException("Error email Type!");
		}
		return mailaddr;
	}

	/**
	 * 获取邮件主题
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws MessagingException
	 */
	public String getSubject() throws UnsupportedEncodingException,
			MessagingException {
		String subject = "";
		subject = MimeUtility.decodeText(msg.getSubject());
		if (subject == null) {
			subject = "";
		}
		return subject;
	}

	/**
	 * 获取邮件发送日期
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public String getSendDate() throws MessagingException {
		Date sendDate = msg.getSentDate();
		SimpleDateFormat smd = new SimpleDateFormat(dateformate);
		return smd.format(sendDate);
	}

	/**
	 * 获取邮件正文内容
	 * 
	 * @return
	 */
	public String getBodyText() {

		return bodytext.toString();
	}

	/**
	 * 解析邮件，将得到的邮件内容保存到一个stringBuffer对象中，解析邮件 主要根据MimeType的不同执行不同的操作，一步一步的解析
	 * 
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void getMailContent(Part part) throws MessagingException,
			IOException {

		String contentType = part.getContentType();
		int nameindex = contentType.indexOf("name");
		boolean conname = false;
		if (nameindex != -1) {
			conname = true;
		}
		System.out.println("CONTENTTYPE:" + contentType);
		if (part.isMimeType("text/plain") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("text/html") && !conname) {
			bodytext.append((String) part.getContent());
		} else if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int count = multipart.getCount();
			for (int i = 0; i < count; i++) {
				getMailContent(multipart.getBodyPart(i));
			}
		} else if (part.isMimeType("message/rfc822")) {
			getMailContent((Part) part.getContent());
		}

	}

	/**
	 * 判断邮件是否需要回执，如需回执返回true，否则返回false
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public boolean getReplySign() throws MessagingException {
		boolean replySign = false;
		String needreply[] = msg.getHeader("Disposition-Notification-TO");
		if (needreply != null) {
			replySign = true;
		}
		return replySign;
	}

	/**
	 * 获取此邮件的message-id
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public String getMessageId() throws MessagingException {
		return msg.getMessageID();
	}

	/**
	 * 判断此邮件是否已读，如果未读则返回false，已读返回true
	 * 
	 * @return
	 * @throws MessagingException
	 */
	public boolean isNew() throws MessagingException {
		boolean isnew = false;
		Flags flags = ((Message) msg).getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		System.out.println("flags's length:" + flag.length);
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isnew = true;
				System.out.println("seen message .......");
				break;
			}
		}

		return isnew;
	}

	/**
	 * 判断是是否包含附件
	 * 
	 * @param part
	 * @return
	 * @throws MessagingException
	 * @throws IOException
	 */
	public boolean isContainAttch(Part part) throws MessagingException,
			IOException {
		boolean flag = false;

		String contentType = part.getContentType();
		if (part.isMimeType("multipart/*")) {
			Multipart multipart = (Multipart) part.getContent();
			int count = multipart.getCount();
			for (int i = 0; i < count; i++) {
				BodyPart bodypart = multipart.getBodyPart(i);
				String dispostion = bodypart.getDisposition();
				if ((dispostion != null)
						&& (dispostion.equals(Part.ATTACHMENT) || dispostion
								.equals(Part.INLINE))) {
					flag = true;
				} else if (bodypart.isMimeType("multipart/*")) {
					flag = isContainAttch(bodypart);
				} else {
					String conType = bodypart.getContentType();
					if (conType.toLowerCase().indexOf("appliaction") != -1) {
						flag = true;
					}
					if (conType.toLowerCase().indexOf("name") != -1) {
						flag = true;
					}
				}
			}
		} else if (part.isMimeType("message/rfc822")) {
			flag = isContainAttch((Part) part.getContent());
		}

		return flag;
	}

	/**
	 * 保存附件
	 * 
	 * @param part
	 * @throws MessagingException
	 * @throws IOException
	 */
	public void saveAttchMent(Part part) throws MessagingException, IOException {
		String filename = "";
		if (part.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) part.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				BodyPart mpart = mp.getBodyPart(i);
				String dispostion = mpart.getDisposition();
				if ((dispostion != null)
						&& (dispostion.equals(Part.ATTACHMENT) || dispostion
								.equals(Part.INLINE))) {
					filename = mpart.getFileName();
					if (filename.toLowerCase().indexOf("gb2312") != -1) {
						filename = MimeUtility.decodeText(filename);
					}
					saveFile(filename, mpart.getInputStream());
				} else if (mpart.isMimeType("multipart/*")) {
					saveAttchMent(mpart);
				} else {
					filename = mpart.getFileName();
					if (filename != null
							&& (filename.toLowerCase().indexOf("gb2312") != -1)) {
						filename = MimeUtility.decodeText(filename);
					}
					saveFile(filename, mpart.getInputStream());
				}
			}

		} else if (part.isMimeType("message/rfc822")) {
			saveAttchMent((Part) part.getContent());
		}
	}

	/**
	 * 获得保存附件的地址
	 * 
	 * @return
	 */
	public String getSaveAttchPath() {
		return saveAttchPath;
	}

	/**
	 * 设置保存附件地址
	 * 
	 * @param saveAttchPath
	 */
	public void setSaveAttchPath(String saveAttchPath) {
		this.saveAttchPath = saveAttchPath;
	}

	/**
	 * 设置日期格式
	 * 
	 * @param dateformate
	 */
	public void setDateformate(String dateformate) {
		this.dateformate = dateformate;
	}

	/**
	 * 保存文件内容
	 * 
	 * @param filename
	 * @param inputStream
	 * @throws IOException
	 */
	private void saveFile(String filename, InputStream inputStream)
			throws IOException {
		String osname = System.getProperty("os.name");
		String storedir = getSaveAttchPath();
		String sepatror = "";
		if (osname == null) {
			osname = "";
		}

		if (osname.toLowerCase().indexOf("win") != -1) {
			sepatror = "//";
			if (storedir == null || "".equals(storedir)) {
				storedir = "E://temp";
			}
		} else {
			sepatror = "/";
			storedir = "/temp";
		}

		File storefile = new File(storedir + sepatror + filename);
		System.out.println("storefile's path:" + storefile.toString());

		BufferedOutputStream bos = null;
		BufferedInputStream bis = null;

		try {
			bos = new BufferedOutputStream(new FileOutputStream(storefile));
			bis = new BufferedInputStream(inputStream);
			int c;
			while ((c = bis.read()) != -1) {
				bos.write(c);
				bos.flush();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			bos.close();
			bis.close();
		}

	}

	public void recive(Part part, int i) throws MessagingException, IOException {
		System.out.println("------------------START-----------------------");
		System.out.println("Message" + i + " subject:" + getSubject());
		System.out.println("Message" + i + " from:" + getFrom());
		System.out.println("Message" + i + " isNew:" + isNew());
		boolean flag = isContainAttch(part);
		System.out.println("Message" + i + " isContainAttch:" + flag);
		System.out.println("Message" + i + " replySign:" + getReplySign());
		getMailContent(part);
		System.out.println("Message" + i + " content:" + getBodyText());
		setSaveAttchPath("E://temp//" + i);
		if (flag) {
			saveAttchMent(part);
		}
		System.out.println("------------------END-----------------------");
	}

}
