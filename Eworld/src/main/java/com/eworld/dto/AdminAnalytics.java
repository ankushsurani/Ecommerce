package com.eworld.dto;

public class AdminAnalytics {

	private long countTotalSaleOfLastYear;

	private long countTotalSaleOfLastMonth;

	private long ordersOfLastYear;

	private long ordersOfLastMonth;

	private long userJoinedLastYear;

	private long userJoinedLastMonth;

	public AdminAnalytics(long countTotalSaleOfLastYear, long countTotalSaleOfLastMonth, long ordersOfLastYear,
			long ordersOfLastMonth, long userJoinedLastYear, long userJoinedLastMonth) {
		super();
		this.countTotalSaleOfLastYear = countTotalSaleOfLastYear;
		this.countTotalSaleOfLastMonth = countTotalSaleOfLastMonth;
		this.ordersOfLastYear = ordersOfLastYear;
		this.ordersOfLastMonth = ordersOfLastMonth;
		this.userJoinedLastYear = userJoinedLastYear;
		this.userJoinedLastMonth = userJoinedLastMonth;
	}

	public long getCountTotalSaleOfLastYear() {
		return countTotalSaleOfLastYear;
	}

	public void setCountTotalSaleOfLastYear(long countTotalSaleOfLastYear) {
		this.countTotalSaleOfLastYear = countTotalSaleOfLastYear;
	}

	public long getCountTotalSaleOfLastMonth() {
		return countTotalSaleOfLastMonth;
	}

	public void setCountTotalSaleOfLastMonth(long countTotalSaleOfLastMonth) {
		this.countTotalSaleOfLastMonth = countTotalSaleOfLastMonth;
	}

	public long getOrdersOfLastYear() {
		return ordersOfLastYear;
	}

	public void setOrdersOfLastYear(long ordersOfLastYear) {
		this.ordersOfLastYear = ordersOfLastYear;
	}

	public long getOrdersOfLastMonth() {
		return ordersOfLastMonth;
	}

	public void setOrdersOfLastMonth(long ordersOfLastMonth) {
		this.ordersOfLastMonth = ordersOfLastMonth;
	}

	public long getUserJoinedLastYear() {
		return userJoinedLastYear;
	}

	public void setUserJoinedLastYear(long userJoinedLastYear) {
		this.userJoinedLastYear = userJoinedLastYear;
	}

	public long getUserJoinedLastMonth() {
		return userJoinedLastMonth;
	}

	public void setUserJoinedLastMonth(long userJoinedLastMonth) {
		this.userJoinedLastMonth = userJoinedLastMonth;
	}

}
