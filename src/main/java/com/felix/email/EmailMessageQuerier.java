package com.felix.email;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.RecipientTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SubjectTerm;

import com.felix.email.search.BodyContentTerm;
import com.felix.email.search.PersonalTerm;

public class EmailMessageQuerier {
	private ArrayList list = new ArrayList();
	// EQ（＝）、GE（>=）、GT（>）、LE（<=）、LT（<）、NE（!=）
	private int comparison = ComparisonTerm.EQ;
	public static final String OR = "or";
	public static final String AND = "and";

	// 默认构造方法
	public EmailMessageQuerier() {
	}

	public void setComparison(String string) {
		if (string.equals(">")) {
			comparison = ComparisonTerm.GE;
		} else if (string.equals("<")) {
			comparison = ComparisonTerm.LE;
		}
	}

	public int getComparison() {
		return this.comparison;
	}

	/**
	 * 根据指定的字符串来查找所有邮件主题中包含此字符串的所有邮件
	 * 
	 * @param subject
	 *            邮件主题中所要包含的字符串
	 */
	public void setSubject(String subject) {
		SearchTerm subterm = new SubjectTerm(subject);
		list.add(subterm);
	}

	public void setRecipients(String recipients) {
		try {
			SearchTerm rterm = new RecipientTerm(Message.RecipientType.TO,
					new InternetAddress(recipients));
			list.add(rterm);
		} catch (Exception ex) {
			// ignore exception
			ex.printStackTrace();
		}
	}

	// public void setRecipientPersons(String recipientPersons){
	// SearchTerm rpTerm = new RecipientPersonTerm(recipientPersons);
	// list.add(rpTerm);
	// }
	//
	/**
	 * 根据指定的字符串来查找发件人地址中包含此字符串的所有邮件
	 * 
	 * @param from
	 *            设置发件人地址中要包含的字符串
	 */
	public void setFrom(String from) {
		try {
			SearchTerm fterm = new FromTerm(new InternetAddress(from));
			list.add(fterm);
		} catch (Exception ex) {
			System.err.println("CoffeeWebMail reportException: "
					+ ex.toString());
			ex.printStackTrace();
		}
	}

	/**
	 * 此类用了自定义的查找器PersonalTerm,此类从javax.mail.search.StringTerm
	 * 中继承下来，重写了StringTerm类的match()方法
	 * <p>
	 * 根据发件人的姓名来查找此发件人发来的所有邮件
	 * </p>
	 * 
	 * @param personal
	 *            设置发件人的姓名
	 */
	public void setPersonal(String personal) {
		try {
			PersonalTerm pterm = new PersonalTerm(personal);
			list.add(pterm);
		} catch (Exception ex) {
			System.err.println("CoffeeWebMail reportException: "
					+ ex.toString());
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * 此类用到了自定义的查找器JAttachTerm,此类从javax.mail.search.SearchTerm中
	 * 直接继承下来，重写了SearchTerm类的match()方法
	 * <p>
	 * 根据是否包含附件来查找匹配的所有邮件
	 * </p>
	 * 
	 * @param flag
	 *            是否有附件的标志 true or false
	 * 
	 *            public void setAttachFlag(boolean flag) { try { SearchTerm
	 *            jaterm = new JAttachTerm(flag); list.add(jaterm); } catch
	 *            (Exception ex) {
	 *            System.err.println("CoffeeWebMail reportException: " +
	 *            ex.toString()); ex.printStackTrace(); } }
	 */
	/**
	 *
	 * 根据给定的字符串来查找邮件正文中包含此字符串的所有邮件
	 * 
	 * @param pattern
	 *            邮件正文要包含的字符串
	 */
	public void setBodyText(String pattern) {
		SearchTerm jbterm = new BodyContentTerm(pattern);
		list.add(jbterm);
	}

	// note: The dateFormat is <yyyy-MM-dd> eg: [2004-7-10]
	public void setReceiveDate(String date) {
		int comparison = getComparison();
		Date recdate = null;
		try {
			recdate = getSearchDate(date);
			SearchTerm recterm = new ReceivedDateTerm(comparison, recdate);
			list.add(recterm);
		} catch (Exception ex) {
			System.err.println("CoffeeWebMail reportException: "
					+ ex.toString());
			ex.printStackTrace();
		}
	}

	public void setSentDate(String date) {
		int comparison = getComparison();
		Date sentdate = null;
		try {
			sentdate = getSearchDate(date);
			SearchTerm recterm = new SentDateTerm(comparison, sentdate);
			list.add(recterm);
		} catch (Exception ex) {
			System.err.println("CoffeeWebMail reportException: "
					+ ex.toString());
			ex.printStackTrace();
		}
	}

	public void setSendTimeInterval(String startDateStr, String endDateStr) {
		Date startDate = null;
		Date endDate = null;

		try {
			startDate = getSearchTime(startDateStr);
			endDate = getSearchTime(endDateStr);
			SearchTerm startDateTerm = new SentDateTerm(ComparisonTerm.GE,
					startDate);
			SearchTerm endDateTerm = new SentDateTerm(ComparisonTerm.LE,
					endDate);
			SearchTerm comparisonAndTerm = new AndTerm(startDateTerm,
					endDateTerm);

			list.add(comparisonAndTerm);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * 返回所有符合搜索条件的邮件数组 return all messages that according the searchterm.
	 * param--srchmode["and" or "or"]
	 * 
	 * @param folder
	 *            Folder 要搜索的邮件夹
	 * @param srchmode
	 *            String 搜索模式，搜索模式为其中之一: MessageQuerier.OR or MessageQuerier.AND
	 * @return Message[] 符合条件的所有消息数组
	 */
	public Message[] getSrchMessages(Folder folder, String srchmode) {
		SearchTerm sterm = null;
		Message[] msgs = null;
		// if(list.size() == 0) return null;
		SearchTerm[] sterms = new SearchTerm[list.size()];
		try {
			for (int i = 0; i < sterms.length; i++) {
				sterms[i] = (SearchTerm) list.get(i);
			}
			if (sterms.length > 1) {
				if (srchmode.equals("and")) {
					// debug info
					// System.out.println("and term :"+sterms.length);
					sterm = new AndTerm(sterms);
				} else if (srchmode.equals("or")) {
					sterm = new OrTerm(sterms);
				}
			} else if (sterms.length == 1) {
				// debug info
				// System.out.println("sterm's length is 1");
				sterm = sterms[0];
			}
			if (sterm == null)
				msgs = folder.getMessages();
			else
				msgs = folder.search(sterm);
		} catch (Exception ex) {
			System.err.println("CoffeeWebMail reportException: "
					+ ex.toString());
			ex.printStackTrace();
		}
		return msgs;
	}

	// 辅助方法
	private Date getSearchDate(String date) {
		String[] ymd = date.split("-");
		int year, month, day;
		Date srchdate = null;
		try {
			year = Integer.parseInt(ymd[0]);
			month = Integer.parseInt(ymd[1]) - 1;
			day = Integer.parseInt(ymd[2]);
			Calendar cal = Calendar.getInstance();
			cal.set(year, month, day);
			srchdate = cal.getTime();
		} catch (Exception ex) {
			System.err.println("CoffeeWebMail reportException: "
					+ ex.toString());
			ex.printStackTrace();
		}
		return srchdate;
	}

	// 辅助方法
	public Date getSearchTime(String timeStr) {
		String[] times = timeStr.split(" ");
		String date = times[0];
		String[] hms = null;
		String[] ymd = date.split("-");
		int year, month, day;
		int hour = 0, minute = 0, second = 0;
		Date srchdate = null;
		try {
			year = Integer.parseInt(ymd[0]);
			month = Integer.parseInt(ymd[1]) - 1;
			day = Integer.parseInt(ymd[2]);
			if (times.length > 1) {
				String time = times[1];
				hms = time.split(":");
				if (hms.length > 0) {
					hour = Integer.parseInt(hms[0]);
				}
				if (hms.length > 1) {
					minute = Integer.parseInt(hms[1]);
				}
				if (hms.length > 2) {
					second = Integer.parseInt(hms[2]);
				}
			}
			Calendar cal = Calendar.getInstance(Locale.CHINA);
			cal.set(year, month, day, hour, minute, second);
			srchdate = cal.getTime();
		} catch (Exception ex) {
			System.err.println("CoffeeWebMail reportException: "
					+ ex.toString());
			ex.printStackTrace();
		}
		return srchdate;
	}

}
