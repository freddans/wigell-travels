package com.freddan.wigell_travels.services;

import com.freddan.wigell_travels.entities.Booking;
import com.freddan.wigell_travels.entities.Trip;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TravelService {

    private final TripService tripService;
    private final BookingService bookingService;
    private final Logger logger = Logger.getLogger(BookingService.class);

    @Autowired
    public TravelService(TripService tripService, BookingService bookingService) {
        this.tripService = tripService;
        this.bookingService = bookingService;
    }

    public List<Booking> findBookingsForTrip(int tripId) {
        Trip trip = tripService.findTripById(tripId);
        List<Booking> bookings = bookingService.allBookings();
        List<Booking> tripBookings = new ArrayList<>();

        for (Booking booking : bookings) {
            if (booking.getTrip().equals(trip)) {
                tripBookings.add(booking);
            }
        }

        return tripBookings;
    }

    public String deleteTripWithBookings(int tripId) {
        Trip trip = tripService.findTripById(tripId);

        if (trip != null) {
            List<Booking> bookingsToDelete = findBookingsForTrip(tripId);

            if (!bookingsToDelete.isEmpty()) {

                for (Booking booking : bookingsToDelete) {

                    bookingService.delete(booking);
                }

                tripService.delete(tripId);

                logger.info("\nAdmin deleted Trip with ID: " + tripId + ".\n");

                return "Deleted Trip and bookings with trip";
            } else {

                tripService.delete(tripId);

                logger.info("\nAdmin deleted Trip with ID: " + tripId + ".\n");

                return "Deleted Trip";
            }
        } else {
            logger.error("\nERROR: Admin tried to delete Trip but provided Trip ID does not exist.\n" +
                    "Provided Trip ID: " + tripId + ".\n");

            return "ERROR: Trip with provided ID does not exist.\n" +
                    "Provided ID: " + tripId;
        }
    }
}