package com.freddan.wigell_travels.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tripitems")
public class TripItem {

    @Id
    private long id;

    private double pricePerWeek;

    @Column(length = 100)
    private String hotelName;

    @Column(length = 50)
    private String country;

    @Column(length = 30)
    private String city;

    private int availableTickets;

    public TripItem() {
    }

    public TripItem(long id, double pricePerWeek, String hotelName, String country, String city, int availableTickets) {
        this.id = id;
        this.pricePerWeek = pricePerWeek;
        this.hotelName = hotelName;
        this.country = country;
        this.city = city;
        this.availableTickets = availableTickets;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPricePerWeek() {
        return pricePerWeek;
    }

    public void setPricePerWeek(double pricePerWeek) {
        this.pricePerWeek = pricePerWeek;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getAvailableTickets() {
        return availableTickets;
    }

    public void setAvailableTickets(int availableTickets) {
        this.availableTickets = availableTickets;
    }
}