package com.freddan.wigell_travels.VO;

import com.freddan.wigell_travels.entities.Booking;

public class ResponseTemplateVO {

    private Booking booking;
    private Currency totalCost;

    public ResponseTemplateVO() {
    }

    public ResponseTemplateVO(Booking booking, Currency totalCost) {
        this.booking = booking;
        this.totalCost = totalCost;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Currency getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Currency totalCost) {
        this.totalCost = totalCost;
    }
}