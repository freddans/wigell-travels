package com.freddan.wigell_travels.controllers;

import com.freddan.wigell_travels.VO.BookingItemResponseTemplateVO;
import com.freddan.wigell_travels.VO.ResponseTemplateVO;
import com.freddan.wigell_travels.entities.Trip;
import com.freddan.wigell_travels.exceptions.TravelException;
import com.freddan.wigell_travels.services.BookingItemService;
import com.freddan.wigell_travels.services.BookingService;
import com.freddan.wigell_travels.services.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
public class CustomerController {

    private final BookingService bookingService;
    private final TripService tripService;
    private final BookingItemService bookingItemService;

    @Autowired
    public CustomerController(BookingService bookingService, TripService tripService, BookingItemService bookingItemService) {
        this.bookingService = bookingService;
        this.tripService = tripService;
        this.bookingItemService = bookingItemService;
    }

    @GetMapping("/trips")
    public ResponseEntity<List<Trip>> availableTrips() {
        return ResponseEntity.ok(tripService.allAvailableTrips());
    }

    @PostMapping("/booktrip")
    public ResponseEntity<?> bookTrip(@RequestParam("customerId") long customerId, @RequestParam("tripId") long tripId,
                                       @RequestParam("tickets") int tickets) {
        try {
            return ResponseEntity.ok(bookingService.createBooking(customerId, tripId, tickets));
        } catch (TravelException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @PutMapping("/updatetrip/{id}")
    public ResponseEntity<?> updateTrip(@PathVariable("id") long bookingId, @RequestParam("customerId") long customerId,
                                              @RequestParam("tripId") long tripId, @RequestParam("tickets") int tickets) {
        try {
            return ResponseEntity.ok(bookingService.updateBooking(bookingId, customerId, tripId, tickets));
        } catch (TravelException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }

    @GetMapping("/mybookings")
    public ResponseEntity<?> myBookingItems(@RequestParam("customerId") long customerId) {
        try {
            List<BookingItemResponseTemplateVO> bookings = bookingItemService.findMyBookingItems(customerId);
            return ResponseEntity.ok(bookings);
        } catch (TravelException e) {
            return ResponseEntity.status(HttpStatus.OK).body(e.getMessage());
        }
    }
}