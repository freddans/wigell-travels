package com.freddan.wigell_travels.controllers;

import com.freddan.wigell_travels.entities.Customer;
import com.freddan.wigell_travels.entities.Trip;
import com.freddan.wigell_travels.services.CustomerService;
import com.freddan.wigell_travels.services.TravelService;
import com.freddan.wigell_travels.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
public class AdminController {

    private final CustomerService customerService;
    private final TripService tripService;
    private final TravelService travelService;

    @Autowired
    public AdminController(CustomerService customerService, TripService tripService, TravelService travelService) {
        this.customerService = customerService;
        this.tripService = tripService;
        this.travelService = travelService;
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.allUsers());
    }

    @PostMapping("/add-destination")
    public ResponseEntity<Trip> createDestination(@RequestBody Trip destination) {
        return ResponseEntity.ok(tripService.create(destination));
    }

    @PutMapping("/updatedestination/{id}")
    public ResponseEntity<Trip> updateDestination(@PathVariable("id") long currentTripId, @RequestBody Trip newTripInfo) {
        return ResponseEntity.ok(tripService.update(currentTripId, newTripInfo));
    }

    @DeleteMapping("/deletedestination/{id}")
    public ResponseEntity<String> deleteDestination(@PathVariable("id") long tripId) {
        return ResponseEntity.ok(travelService.deleteTripWithBookings(tripId));
    }
}