package com.felix.email;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.UIDFolder;
import javax.mail.internet.ContentType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.ParseException;
import javax.mail.search.FlagTerm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.mail.imap.IMAPStore;

public class EmailMsgReceiver {
	private static Log log = LogFactory.getLog(EmailMsgReceiver.class);
	private String mbox = null;
	static boolean showMessage = false;
	static boolean showAlert = false;
	static boolean saveAttachments = false;
	static boolean showStructure = false;
	static boolean verbose = true;
	static int attnum = 1;
	private Store store;
	private Folder folder;
	static int limitTimeout = 10000;
	private EmailMessageQuerier emailMessageQuerier;
	public EmailMsgReceiver(Store store,EmailMessageQuerier emailMessageQuerier) {
		this.store = store;
		this.emailMessageQuerier = emailMessageQuerier;
		
	}

	
	private void getFolder() throws MessagingException{
		folder = store.getDefaultFolder();
		if (folder == null) {
			log.info("No default folder");
			System.exit(1);
		}
		if (mbox == null)
			mbox = "INBOX";
		folder = folder.getFolder(mbox);
		if (folder == null) {
			log.info("Invalid folder");
			System.exit(1);
		}
	}
	
	
	
	
	public void setMbox(String mbox){
		this.mbox = mbox;
	}
	
	public String getMbox(){
		return mbox;
	}
	
	
	private void openFolder() throws MessagingException{
		try {
			folder.open(Folder.READ_WRITE);
		} catch (MessagingException ex) {
			folder.open(Folder.READ_ONLY);
		}
	}
	
	private void getMsgCountInfo() throws MessagingException{
		
		int totalMessages = folder.getMessageCount();
		int newMessages = folder.getNewMessageCount();
		int unreadMessages = folder.getUnreadMessageCount();
		int deletedMessages = folder.getDeletedMessageCount();
		log.info("Total messages = " + totalMessages);
		log.info("New messages = " + newMessages);
		log.info("unread Messages = " + unreadMessages);
		log.info("deleted Messages = " + deletedMessages);
		if (totalMessages == 0) {
			log.info("Empty folder");
			folder.close(false);
			store.close();
			System.exit(1);
		}
		
	}
	
	public void receiveMsg() {
		int msgnum =-1;
		try {
			
			// try to open read/write and if that fails try read-only
			getFolder();
			openFolder();
			getMsgCountInfo();
			
			if (msgnum == -1) {
				// Attributes & Flags for all messages ..
				Message[] msgs = folder.getMessages();
				// Use a suitable FetchProfile
				FetchProfile fp = new FetchProfile();
				fp.add(FetchProfile.Item.ENVELOPE);
				fp.add(FetchProfile.Item.FLAGS);
				fp.add(UIDFolder.FetchProfileItem.UID);
				fp.add("X-Mailer");
				folder.fetch(msgs, fp);
				
				msgs = emailMessageQuerier.getSrchMessages(folder, "and");
				
				
				for (int i = 0; i < msgs.length; i++) {
					
					System.out.println("-------------------------------");
					
					System.out.println("MESSAGE #" + (i + 1) + ":");
					
					System.out.println("getMessageNumber #" + msgs[i].getMessageNumber() + ":");
					
				}
			} else {
				log.info("Getting message number: " + msgnum);
				Message m = null;
				try {
					m = folder.getMessage(msgnum);
					
					EmailMessageInfo.getInstance(m).createMsgInfoBean();
					
//					dumpPart(m);
				} catch (IndexOutOfBoundsException iex) {
					
					log.error("Message number out of range");
				
				}
			}
			folder.close(false);
			store.close();
		} catch (Exception ex) {
			log.error("Oops, got exception! " + ex.getMessage());
			
			ex.printStackTrace();

		}
	}

	public static void dumpPart(Part p) throws Exception {
		if (p instanceof Message)
			dumpEnvelope((Message) p);
		/**
		 * Dump input stream .. InputStream is = p.getInputStream(); // If "is"
		 * is not already buffered, wrap a BufferedInputStream // around it. if
		 * (!(is instanceof BufferedInputStream)) is = new
		 * BufferedInputStream(is); int c; while ((c = is.read()) != -1)
		 * System.out.write(c);
		 **/
		String ct = p.getContentType();
		try {
			pr("CONTENT-TYPE: " + (new ContentType(ct)).toString());
		} catch (ParseException pex) {
			pr("BAD CONTENT-TYPE: " + ct);
		}
		String filename = p.getFileName();
		if (filename != null)
			pr("FILENAME: " + filename);
		/*
		 * Using isMimeType to determine the content type avoids fetching the
		 * actual content data until we need it.
		 */
		if (p.isMimeType("text/plain")) {
			pr("This is plain text");
			pr("---------------------------");
			if (!showStructure && !saveAttachments)
				System.out.println((String) p.getContent());
		} else if (p.isMimeType("multipart/*")) {
			pr("This is a Multipart");
			pr("---------------------------");
			Multipart mp = (Multipart) p.getContent();
			level++;
			int count = mp.getCount();
			for (int i = 0; i < count; i++)
				dumpPart(mp.getBodyPart(i));
			level--;
		} else if (p.isMimeType("message/rfc822")) {
			pr("This is a Nested Message");
			pr("---------------------------");
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
					pr("This is a string");
					pr("---------------------------");
					System.out.println((String) o);
				} else if (o instanceof InputStream) {
					pr("This is just an input stream");
					pr("---------------------------");
					InputStream is = (InputStream) o;
					int c;
					while ((c = is.read()) != -1)
						System.out.write(c);
				} else {
					pr("This is an unknown type");
					pr("---------------------------");
					pr(o.toString());
				}
			} else {
				// just a separator
				pr("---------------------------");
			}
		}
		/*
		 * If we're saving attachments, write out anything that looks like an
		 * attachment into an appropriately named file. Don't overwrite existing
		 * files to prevent mistakes.
		 */
		if (saveAttachments && level != 0 && !p.isMimeType("multipart/*")) {
			String disp = p.getDisposition();
			// many mailers don't include a Content-Disposition
			if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
				if (filename == null)
					filename = "Attachment" + attnum++;
				pr("Saving attachment to file " + filename);
				try {
					File f = new File(filename);
					if (f.exists())
						// XXX - could try a series of names
						throw new IOException("file exists");
					((MimeBodyPart) p).saveFile(f);
				} catch (IOException ex) {
					pr("Failed to save attachment: " + ex);
				}
				pr("---------------------------");
			}
		}
	}

	public static void dumpEnvelope(Message m) throws Exception {
		pr("This is the message envelope");
		pr("---------------------------");
		Address[] a;
		// FROM
		if ((a = m.getFrom()) != null) {
			for (int j = 0; j < a.length; j++)
				pr("FROM: " + a[j].toString());
		}
		// TO
		if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
			for (int j = 0; j < a.length; j++) {
				pr("TO: " + a[j].toString());
				InternetAddress ia = (InternetAddress) a[j];
				if (ia.isGroup()) {
					InternetAddress[] aa = ia.getGroup(false);
					for (int k = 0; k < aa.length; k++)
						pr(" GROUP: " + aa[k].toString());
				}
			}
		}
		// SUBJECT
		pr("SUBJECT: " + m.getSubject());
		// DATE
		Date d = m.getSentDate();
		pr("SendDate: " + (d != null ? d.toString() : "UNKNOWN"));
		// FLAGS
		Flags flags = m.getFlags();
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
		pr("FLAGS: " + sb.toString());
		// X-MAILER
		String[] hdrs = m.getHeader("X-Mailer");
		if (hdrs != null)
			pr("X-Mailer: " + hdrs[0]);
		else
			pr("X-Mailer NOT available");
	}

	static String indentStr = " ";
	static int level = 0;

	/**
	 * Print a, possibly indented, string.
	 */
	public static void pr(String s) {
		if (showStructure)
			log.info(indentStr.substring(0, level * 2));

		log.info(s);
	}
}
