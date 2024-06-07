package com.freddan.wigell_travels.controllers;

import com.freddan.wigell_travels.VO.ResponseTemplateVO;
import com.freddan.wigell_travels.entities.Booking;
import com.freddan.wigell_travels.entities.Trip;
import com.freddan.wigell_travels.services.BookingService;
import com.freddan.wigell_travels.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
public class CustomerController {

    private final BookingService bookingService;
    private final TripService tripService;

    @Autowired
    public CustomerController(BookingService bookingService, TripService tripService) {
        this.bookingService = bookingService;
        this.tripService = tripService;
    }

    @GetMapping("/trips")
    public ResponseEntity<List<Trip>> availableTrips() {
        return ResponseEntity.ok(tripService.allAvailableTrips());
    }

    @PostMapping("/booktrip")
    public ResponseTemplateVO bookTrip(@RequestParam("customerId") int customerId, @RequestParam("tripId") int tripId) {
        return bookingService.createBooking(customerId, tripId);
    }

    @PutMapping("/updatetrip/{id}")
    public ResponseEntity<Booking> updateTrip(@PathVariable("id") int bookingId, @RequestParam("customerId") int customerId, @RequestParam("tripId") int tripId) {
        return ResponseEntity.ok(bookingService.updateBooking(bookingId, customerId, tripId));
    }

    @GetMapping("/mybookings")
    public List<ResponseTemplateVO> myBookings(@RequestParam("customerId") int customerId) {
        return bookingService.findMyBookings(customerId);
    }
}