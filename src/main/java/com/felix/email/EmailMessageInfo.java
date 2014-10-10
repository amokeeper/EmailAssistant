package com.felix.email;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import javax.mail.internet.ParseException;

public class EmailMessageInfo {
	
	private static EmailMessageInfo emailMessageInfo = new EmailMessageInfo();
	
	private EmailMessageInfo(){
		
	}
	public static EmailMessageInfo getInstance(Message msg){
		message = msg;
		return emailMessageInfo;
	}
	
	static Message message;
	
	static boolean showStructure = false;
	static boolean saveAttachments = false;
	static String saveAttchPath;
	public static EmailMsgInfoBean createMsgInfoBean() throws MessagingException, IOException{
		EmailMsgInfoBean emailMsgInfoBean = new EmailMsgInfoBean();
		emailMsgInfoBean.setBcc(getBcc());
		emailMsgInfoBean.setCc(getCc());
		emailMsgInfoBean.setFrom(getFrom());
		emailMsgInfoBean.setNum(getNum());
		emailMsgInfoBean.setReceivedDate(getReceivedDate());
		emailMsgInfoBean.setSentDate(getSentDate());
		emailMsgInfoBean.setSubject(getSubject());
		emailMsgInfoBean.setTo(getTo());
		emailMsgInfoBean.setBodyContent(getBody());
		if (hasAttachments()) {
			emailAttachments.clear();
			saveAttchMent(message);
			emailMsgInfoBean.setEmailAttachments(getEmailAttachments());
		}
		
		
		emailMsgInfoBean.setFlag(getFlag());
		emailMsgInfoBean.toString();
		return emailMsgInfoBean;
	}

	/**
	 * Returns the bcc field.   bcc:blind carbon copy
	 */
	public static String getBcc() throws MessagingException {
		return formatAddresses(message.getRecipients(Message.RecipientType.BCC));
	}

	/**
	 * Returns the body of the message (if it's plain text).
	 */
	public static String getBody() throws MessagingException, java.io.IOException {
		emailContent.setLength(0);

		getPartBody(message);

		return emailContent.toString();
		
	}
	
	static StringBuffer emailContent = new StringBuffer();
	static List<EmailAttachment> emailAttachments = new ArrayList<EmailAttachment>();
	static int level = 0;
	static String indentStr = "----------------";
	static StringBuffer emailHtmlContent = new StringBuffer();
	
	
	
	
	public static void getPartBody(Part p) throws MessagingException, IOException{
		if (p.isMimeType("text/plain")) {
//			emailContent.append("----this is email text/plain content----\n");
			emailContent.append((String) p.getContent()+"\n");
//			pr("this is email text/plain content");
//			pr((String) p.getContent());
		}else if (p.isMimeType("text/html")) {
			emailHtmlContent.append((String) p.getContent());
//			pr("this is email text/html content");
//			pr((String) p.getContent());
		}else if (p.isMimeType("multipart/*")) {
//			pr("this is a Multipart");
//			emailContent.append("----this is a Multipart----");
			
			Multipart mp = (Multipart) p.getContent();
			level++;
			int count = mp.getCount();
			for (int i = 0; i < count; i++) {
				getPartBody(mp.getBodyPart(i));
			}
			level--;
		}else if (p.isMimeType("message/rfc822")) {
//			emailContent.append("This is a Nested Message");
//			pr("This is a Nested Message");
			level++;
			getPartBody((Part) p.getContent());
			level--;
		}else {
//			pr("--------------------------------------------");
//			emailContent.append("this is Mail attachment");
		}
	}
	
	
	
	
	public static void pr(String s) {
		if (showStructure)
			System.out.println(indentStr.substring(0, level * 2));
		System.out.println(s);
	}
	
	

	
	public  void dumpPart(Part p) throws Exception {
		
		String ct = p.getContentType();
		try {
			System.out.println("CONTENT-TYPE: " + (new ContentType(ct)).toString());
		} catch (ParseException pex) {
			System.out.println("BAD CONTENT-TYPE: " + ct);
		}
		String filename = p.getFileName();
		if (filename != null)
			System.out.println("FILENAME: " + filename);
		/*
		 * Using isMimeType to determine the content type avoids fetching the
		 * actual content data until we need it.
		 */
		if (p.isMimeType("text/plain")) {
			System.out.println("This is plain text");
			System.out.println("---------------------------");
			if (!showStructure && !saveAttachments)
				System.out.println((String) p.getContent());
		} else if (p.isMimeType("multipart/*")) {
			System.out.println("This is a Multipart");
			System.out.println("---------------------------");
			Multipart mp = (Multipart) p.getContent();
			level++;
			int count = mp.getCount();
			for (int i = 0; i < count; i++)
				dumpPart(mp.getBodyPart(i));
			level--;
		} else if (p.isMimeType("message/rfc822")) {
			System.out.println("This is a Nested Message");
			System.out.println("---------------------------");
			level++;
			dumpPart((Part) p.getContent());
			level--;
		} else {
			if (!showStructure && !saveAttachments) {
				/*
				 * If we actually want to see the data, and it's not a MIME type
				 * we know, fetch it and check its Java type.
				 */
				Object o = p.getContent();
				if (o instanceof String) {
					System.out.println("This is a string");
					System.out.println("---------------------------");
					System.out.println((String) o);
				} else if (o instanceof InputStream) {
					System.out.println("This is just an input stream");
					System.out.println("---------------------------");
					InputStream is = (InputStream) o;
					int c;
					while ((c = is.read()) != -1)
						System.out.write(c);
				} else {
					System.out.println("This is an unknown type");
					System.out.println("---------------------------");
					System.out.println(o.toString());
				}
			} else {
				// just a separator
				System.out.println("---------------------------");
			}
		}
	
	
	
	}
	
	
	
	
	
	public String getEmailContent() {
		
		return emailContent.toString();
	}

	public void setEmailContent(StringBuffer emailContent) {
		this.emailContent = emailContent;
	}

	public static List<EmailAttachment> getEmailAttachments() {
		return emailAttachments;
	}

	public   void setEmailAttachments(List<EmailAttachment> emailAttachments) {
		this.emailAttachments = emailAttachments;
	}

	/**
	 * Returns the cc field.
	 */
	public static String getCc() throws MessagingException {
		return formatAddresses(message.getRecipients(Message.RecipientType.CC));
	}

	/**
	 * Returns the date the message was sent (or received if the sent date is
	 * null.
	 */
	public String getDate() throws MessagingException {
		Date date;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if ((date = message.getSentDate()) != null)
			return (df.format(date));
		else if ((date = message.getReceivedDate()) != null)
			return (df.format(date));
		else
			return "";
	}

	/**
	 * Returns the from field.
	 */
	public static String getFrom() throws MessagingException {
		return formatAddresses(message.getFrom());
	}

	/**
	 * Returns the address to reply to.
	 */
	public String getReplyTo() throws MessagingException {
		Address[] a = message.getReplyTo();
		if (a.length > 0)
			return ((InternetAddress) a[0]).getAddress();
		else
			return "";
	}

	/**
	 * Returns the javax.mail.Message object.
	 */
	public Message getMessage() {
		return message;
	}

	/**
	 * Returns the message number.
	 */
	public static String getNum() {
		return (Integer.toString(message.getMessageNumber()));
	}

	/**
	 * Returns the received date field.
	 */
	public static String getReceivedDate() throws MessagingException {
		if (hasReceivedDate()){
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			
			return (df.format(message.getReceivedDate()));
		}else
			return "";
	}

	/**
	 * Returns the sent date field.
	 */
	public static String getSentDate() throws MessagingException {
		if (hasSentDate()){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return (df.format(message.getSentDate()));
			
		}else
			return "";
	}

	/**
	 * Returns the subject field.
	 */
	public static String getSubject() throws MessagingException {
		if (hasSubject())
			return message.getSubject();
		else
			return "";
	}

	/**
	 * Returns the to field.
	 */
	public static String getTo() throws MessagingException {
		return formatAddresses(message.getRecipients(Message.RecipientType.TO));
	}

	/**
	 * Method for checking if the message has attachments.
	 */
	public static boolean hasAttachments() throws java.io.IOException,
			MessagingException {
		boolean hasAttachments = false;
		if (message.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) message.getContent();
			if (mp.getCount() > 1)
				hasAttachments = true;
		}

		return hasAttachments;
	}

	/**
	 * Method for checking if the message has a bcc field.
	 */
	public boolean hasBcc() throws MessagingException {
		return (message.getRecipients(Message.RecipientType.BCC) != null);
	}

	/**
	 * Method for checking if the message has a cc field.
	 */
	public boolean hasCc() throws MessagingException {
		return (message.getRecipients(Message.RecipientType.CC) != null);
	}

	/**
	 * Method for checking if the message has a date field.
	 */
	public boolean hasDate() throws MessagingException {
		return (hasSentDate() || hasReceivedDate());
	}

	/**
	 * Method for checking if the message has a from field.
	 */
	public boolean hasFrom() throws MessagingException {
		return (message.getFrom() != null);
	}

	/**
	 * Method for checking if the message has the desired mime type.
	 */
	public boolean hasMimeType(String mimeType) throws MessagingException {
		return message.isMimeType(mimeType);
	}

	/**
	 * Method for checking if the message has a received date field.
	 */
	public static boolean hasReceivedDate() throws MessagingException {
		return (message.getReceivedDate() != null);
	}

	/**
	 * Method for checking if the message has a sent date field.
	 */
	public static boolean hasSentDate() throws MessagingException {
		return (message.getSentDate() != null);
	}

	/**
	 * Method for checking if the message has a subject field.
	 */
	public static boolean hasSubject() throws MessagingException {
		return (message.getSubject() != null);
	}

	/**
	 * Method for checking if the message has a to field.
	 */
	public boolean hasTo() throws MessagingException {
		return (message.getRecipients(Message.RecipientType.TO) != null);
	}

	/**
	 * Method for mapping a message to this MessageInfo class.
	 */
	public void setMessage(Message message) {
		this.message = message;
	}

	/**
	 * Utility method for formatting msg header addresses.
	 */
	private static String formatAddresses(Address[] addrs) {
		if (addrs == null)
			return "";
		StringBuffer strBuf = new StringBuffer(getDisplayAddress(addrs[0]));
		for (int i = 1; i < addrs.length; i++) {
			strBuf.append(", ").append(getDisplayAddress(addrs[i]));
		}
		return strBuf.toString();
	}

	/**
	 * Utility method which returns a string suitable for msg header display.
	 */
	private static String getDisplayAddress(Address a) {
		String pers = null;
		String addr = null;
		if (a instanceof InternetAddress
				&& ((pers = ((InternetAddress) a).getPersonal()) != null)) {
			addr = pers + "  " + "<" + ((InternetAddress) a).getAddress()
					+ ">";
		} else
			addr = a.toString();
		return addr;
	}
	
	
	private static String getFlag() throws MessagingException{
		Flags flags = message.getFlags();
		StringBuffer sb = new StringBuffer();
		Flags.Flag[] sf = flags.getSystemFlags(); // get the system flags
		boolean first = true;
		for (int i = 0; i < sf.length; i++) {
			String s;
			Flags.Flag f = sf[i];
			if (f == Flags.Flag.ANSWERED)
				s = "\\Answered";
			else if (f == Flags.Flag.DELETED)
				s = "\\Deleted";
			else if (f == Flags.Flag.DRAFT)
				s = "\\Draft";
			else if (f == Flags.Flag.FLAGGED)
				s = "\\Flagged";
			else if (f == Flags.Flag.RECENT)
				s = "\\Recent";
			else if (f == Flags.Flag.SEEN)
				s = "\\Seen";
			else
				continue; // skip it
			if (first)
				first = false;
			else
				sb.append(' ');
			sb.append(s);
		}
		String[] uf = flags.getUserFlags(); // get the user flag strings
		for (int i = 0; i < uf.length; i++) {
			if (first)
				first = false;
			else
				sb.append(' ');
			sb.append(uf[i]);
		}
		
		return sb.toString();
	}
	/**
　　　　 * 【判断此邮件是否已读，如果未读返回返回false,反之返回true】
　　　　 */
	public static boolean isNew() throws MessagingException {
		boolean isnew = false;
		Flags flags = message.getFlags();
		Flags.Flag[] flag = flags.getSystemFlags();
		System.out.println("flags's length: " + flag.length);
		for (int i = 0; i < flag.length; i++) {
			if (flag[i] == Flags.Flag.SEEN) {
				isnew = true;
				System.out.println("seen Message.......");
				break;
			}
		}
		return isnew;
	}
	
	
	/**
     * 判断是是否包含附件
     * @param part
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    public static boolean isContainAttch(Part part) throws MessagingException, IOException{
        boolean flag = false;
        
        String contentType = part.getContentType();
        if(part.isMimeType("multipart/*")){
            Multipart multipart = (Multipart) part.getContent();
            int count = multipart.getCount();
            for(int i=0;i<count;i++){
                BodyPart bodypart = multipart.getBodyPart(i);
                String dispostion = bodypart.getDisposition();
                if((dispostion != null)&&(dispostion.equals(Part.ATTACHMENT)||dispostion.equals(Part.INLINE))){
                    flag = true;
                }else if(bodypart.isMimeType("multipart/*")){
                    flag = isContainAttch(bodypart);
                }else{
                    String conType = bodypart.getContentType();
                    if(conType.toLowerCase().indexOf("appliaction")!=-1){
                        flag = true;
                    }
                    if(conType.toLowerCase().indexOf("name")!=-1){
                        flag = true;
                    }
                }
            }
        }else if(part.isMimeType("message/rfc822")){
            flag = isContainAttch((Part) part.getContent());
        }
        
        return flag;
    }
    /**
     * 保存附件
     * @param part
     * @throws MessagingException
     * @throws IOException
     */
    public static void saveAttchMent(Part part) throws MessagingException, IOException{
        String filename = "";
        if(part.isMimeType("multipart/*")){
            Multipart mp = (Multipart) part.getContent();
            for(int i=0;i<mp.getCount();i++){
                BodyPart mpart = mp.getBodyPart(i);
                String dispostion = mpart.getDisposition();
                if((dispostion != null)&&(dispostion.equals(Part.ATTACHMENT)||dispostion.equals(Part.INLINE))){
                    filename = mpart.getFileName();
                    if(filename.toLowerCase().indexOf("gb2312")!=-1){
                        filename = MimeUtility.decodeText(filename);
                    }
                    saveFile(filename,mpart.getInputStream());
                }else if(mpart.isMimeType("multipart/*")){
                    saveAttchMent(mpart);
                }else{
                    filename = mpart.getFileName();
                    if(filename != null&&(filename.toLowerCase().indexOf("gb2312")!=-1)){
                        filename = MimeUtility.decodeText(filename);
                        saveFile(filename,mpart.getInputStream());
                    }
                    
                }
            }
            
        }else if(part.isMimeType("message/rfc822")){
            saveAttchMent((Part) part.getContent());
        }
    }
    
    /**
     * 保存文件内容
     * @param filename
     * @param inputStream
     * @throws IOException
     */
    private static void saveFile(String filename, InputStream inputStream) throws IOException {
    	
        String osname = System.getProperty("os.name");
        String storedir = getSaveAttchPath();
        String sepatror = "";
        if(osname == null){
            osname = "";
        }
        
        if(osname.toLowerCase().indexOf("win")!=-1){
            sepatror = "//";
            if(storedir==null||"".equals(storedir)){
                storedir = "e://temp";
            }
        }else{
            sepatror = "/";
            storedir = "/temp";
        }
        
        File storefile = new File(storedir+sepatror+filename);
    

        System.out.println("storefile's path:"+storefile.toString());
        
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        
        try {
            bos = new BufferedOutputStream(new FileOutputStream(storefile));
            bis = new BufferedInputStream(inputStream);
            int c;
            while((c= bis.read())!=-1){
                bos.write(c);
                bos.flush();
            }
            EmailAttachment emailAttachment = new EmailAttachment();
            emailAttachment.setFilePath(storefile.toString());
            emailAttachment.setFileName(filename);
            emailAttachments.add(emailAttachment);
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            bos.close();
            bis.close();
        }
        
        
        
        
    }

	public static String getSaveAttchPath() {
		return saveAttchPath;
	}

	public void setSaveAttchPath(String saveAttchPath) {
		this.saveAttchPath = saveAttchPath;
	}
    
    
}
