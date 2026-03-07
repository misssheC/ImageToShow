package com.ooo.its.dto;
public class MonthlySummaryDTO {
    private String yearMonth;
    private int totalLogin;
    private int totalView;
    private int totalCart;
    private int totalIncome;
    private int totalLerror;
    private int totalRerror;

    public MonthlySummaryDTO(String yearMonth , int totalLogin,int totalView,int totalCart,int totalIncome,int totalLerror,int totalRerror){
        this.yearMonth = yearMonth;
        this.totalLogin = totalLogin;
        this.totalView = totalView;
        this.totalCart = totalCart;
        this.totalIncome = totalIncome;
        this.totalLerror = totalLerror;
        this.totalRerror = totalRerror;
    }
    public MonthlySummaryDTO(){}

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public int getTotalLogin() {
        return totalLogin;
    }

    public void setTotalLogin(int totalLogin) {
        this.totalLogin = totalLogin;
    }

    public int getTotalView() {
        return totalView;
    }

    public void setTotalView(int totalView) {
        this.totalView = totalView;
    }

    public int getTotalCart() {
        return totalCart;
    }

    public void setTotalCart(int totalCart) {
        this.totalCart = totalCart;
    }

    public int getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(int totalIncome) {
        this.totalIncome = totalIncome;
    }

    public int getTotalLerror() {
        return totalLerror;
    }

    public void setTotalLerror(int totalLerror) {
        this.totalLerror = totalLerror;
    }

    public int getTotalRerror() {
        return totalRerror;
    }

    public void setTotalRerror(int totalRerror) {
        this.totalRerror = totalRerror;
    }
}
