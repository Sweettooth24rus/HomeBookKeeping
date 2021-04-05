package com.work.homebookkeeping;

public class Trans {

    private String date;
    private Double sum;
    private Double sumR;
    private String val;
    private Boolean rub;
    private String Comment;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Double getSumR() {
        return sumR;
    }

    public void setSumR(Double sumR) {
        this.sumR = sumR;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public Boolean getRub() {
        return rub;
    }

    public void setRub(Boolean rub) {
        this.rub = rub;
    }
}
