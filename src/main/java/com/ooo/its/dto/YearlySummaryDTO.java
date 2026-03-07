package com.ooo.its.dto;

public class YearlySummaryDTO {
    private String year;
    private int totalLogin;
    private int totalView;
    private int totalCart;
    private int totalIncome;
    private int totalLerror;
    private int totalRerror;

    public YearlySummaryDTO(String year,int totalLogin,int totalView,int totalCart,int totalIncome,int totalLerror,int totalRerror){
        this.year = year;
        this.totalLogin = totalLogin;
        this.totalView = totalView;
        this.totalCart = totalCart;
        this.totalIncome = totalIncome;
        this.totalLerror = totalLerror;
        this.totalRerror = totalRerror;
    }
    public YearlySummaryDTO(){}
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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
