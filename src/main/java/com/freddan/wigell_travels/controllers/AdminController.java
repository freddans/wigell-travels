package com.freddan.wigell_travels.controllers;

import com.freddan.wigell_travels.entities.Customer;
import com.freddan.wigell_travels.entities.Trip;
import com.freddan.wigell_travels.exceptions.TravelException;
import com.freddan.wigell_travels.services.CustomerService;
import com.freddan.wigell_travels.services.TravelService;
import com.freddan.wigell_travels.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> createDestination(@RequestBody Trip destination) {
        try {
            return ResponseEntity.ok(tripService.create(destination));
        } catch (TravelException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @PutMapping("/updatedestination/{id}")
    public ResponseEntity<?> updateDestination(@PathVariable("id") long currentTripId, @RequestBody Trip newTripInfo) {
        try {
            return ResponseEntity.ok(tripService.update(currentTripId, newTripInfo));
        } catch (TravelException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @DeleteMapping("/deletedestination/{id}")
    public ResponseEntity<String> deleteDestination(@PathVariable("id") long tripId) {
        return ResponseEntity.ok(travelService.deleteTripWithBookings(tripId));
    }
}