package com.freddan.wigell_travels.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "bookingitems")
public class BookingItem {

    @Id
    private long id;

    private Date departureDate;

    @ManyToOne
    @JoinColumn(name = "tripitem_id")
    private TripItem trip;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private int tickets;

    public BookingItem() {
    }

    public BookingItem(long id, Date departureDate, TripItem trip, Customer customer, int tickets) {
        this.id = id;
        this.departureDate = departureDate;
        this.trip = trip;
        this.customer = customer;
        this.tickets = tickets;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public TripItem getTrip() {
        return trip;
    }

    public void setTrip(TripItem trip) {
        this.trip = trip;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getTickets() {
        return tickets;
    }

    public void setTickets(int tickets) {
        this.tickets = tickets;
    }
}