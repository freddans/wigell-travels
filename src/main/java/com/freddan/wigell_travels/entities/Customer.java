package com.freddan.wigell_travels.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "travel_customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column (unique = true, length = 50)
    private String username;

    @Column(length = 20)
    private String firstName;

    @Column(length = 35)
    private String lastName;

    @Column(length = 35)
    private String address;

    public Customer() {
    }

    public Customer(String username, String firstName, String lastName, String address) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}