package com.freddan.wigell_travels.VO;

import com.freddan.wigell_travels.entities.BookingItem;

public class BookingItemResponseTemplateVO {

    private BookingItem bookingItem;
    private Currency totalCost;

    public BookingItemResponseTemplateVO() {
    }

    public BookingItemResponseTemplateVO(BookingItem bookingItem, Currency totalCost) {
        this.bookingItem = bookingItem;
        this.totalCost = totalCost;
    }

    public BookingItem getBookingItem() {
        return bookingItem;
    }

    public void setBookingItem(BookingItem bookingItem) {
        this.bookingItem = bookingItem;
    }

    public Currency getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Currency totalCost) {
        this.totalCost = totalCost;
    }
}