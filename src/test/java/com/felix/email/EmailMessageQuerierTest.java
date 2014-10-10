package com.felix.email;

import org.testng.annotations.Test;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class EmailMessageQuerierTest {

	EmailMessageQuerier emailMessageQuerier = new EmailMessageQuerier();

	@DataProvider(name = "timeData")
	public static Object[][] primeNumbers() {
		return new Object[][] {{"2010-10-10 23:12:23"},{"2010-10-10 23:12"},{"2010-10-10 23"},{"2010-10-10"}};
	}

	@Test(dataProvider = "timeData")
	public void testGetSearchTime(String timeStr) {
		Date date = emailMessageQuerier.getSearchTime(timeStr);
		
		System.out.println(timeStr+" to time :" + date.toLocaleString());
		
		
	}

}
