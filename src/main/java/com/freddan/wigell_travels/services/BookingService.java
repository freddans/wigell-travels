package com.freddan.wigell_travels.services;

import com.freddan.wigell_travels.VO.Currency;
import com.freddan.wigell_travels.VO.ResponseTemplateVO;
import com.freddan.wigell_travels.entities.Booking;
import com.freddan.wigell_travels.entities.Customer;
import com.freddan.wigell_travels.entities.Trip;
import com.freddan.wigell_travels.repositories.BookingRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final CustomerService customerService;
    private final TripService tripService;
    private final BookingItemService bookingItemService;
    private final RestTemplate restTemplate;
    private final Logger logger = Logger.getLogger(BookingService.class);

    @Autowired
    public BookingService(BookingRepository bookingRepository, CustomerService customerService, TripService tripService, BookingItemService bookingItemService, RestTemplate restTemplate) {
        this.bookingRepository = bookingRepository;
        this.customerService = customerService;
        this.tripService = tripService;
        this.bookingItemService = bookingItemService;
        this.restTemplate = restTemplate;
    }

    public List<Booking> allBookings() {
        return bookingRepository.findAll();
    }

    public Booking findBookingById(long id) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);

        if (optionalBooking.isPresent()) {
            return optionalBooking.get();
        } else {
            return null;
        }
    }

    public ResponseTemplateVO createBooking(long customerId, long tripId) {
        Customer customer = customerService.findUserById(customerId);
        Trip trip = tripService.findTripById(tripId);

        if (customer != null && trip != null) {
            double costPerWeek = trip.getPricePerWeek();

            // Create a departureDate 1 month ahead in time
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 1);
            Date date;
            date = cal.getTime();

            Booking booking = new Booking(date, trip, customer);

            bookingRepository.save(booking);

            // Create a bookingItem that wont be deleted
            bookingItemService.create(booking);

            ResponseTemplateVO vo = new ResponseTemplateVO();

            Currency totalCost = restTemplate.getForObject("http://WIGELL-CURRENCY/api/v1/currency/" + costPerWeek, Currency.class);

            vo.setBooking(booking);
            vo.setTotalCost(totalCost);

            logger.info("\nCustomer " + customer.getFirstName() + " '" + customer.getUsername() + "' " + customer.getLastName() + " booked a trip to " + trip.getCity() + ", " + trip.getCountry() + ".\n");

            return vo;
        } else {

            // ERROR customer or trip is null
            logger.error("\nERROR: Either the provided ID for Customer or Trip does not exist.\n" +
                    "Provided Customer ID: " + customerId + ".\n" +
                    "Provided Trip ID: " + tripId + ".\n");
            return null;
        }
    }

    public Booking updateBooking(long bookingId, long customerId, long tripId) {
        Booking existingBooking = findBookingById(bookingId);
        Customer newCustomer = customerService.findUserById(customerId);
        Trip newTrip = tripService.findTripById(tripId);

        if (existingBooking != null) {

            StringBuilder changes = new StringBuilder();

            if (newCustomer != null) {
                existingBooking.setCustomer(newCustomer);
                changes.append("\nChanged Customer to ID: " + newCustomer.getId() + ".\n");
            }
            if (newTrip != null) {
                existingBooking.setTrip(newTrip);
                changes.append("Changed Trip to ID: " + newTrip.getId());
            }

            bookingRepository.save(existingBooking);

            bookingItemService.update(bookingId, customerId, tripId);

            logger.info("\nBooking ID: " + existingBooking.getId() + " was updated. " + changes.toString() + ".\n");

            return existingBooking;
        } else {
            logger.error("\nERROR: Booking with provided ID: " + bookingId + " does not exist.\n");
            return null;
        }
    }

    public void delete(Booking booking) {
        Booking bookingToDelete = findBookingById(booking.getId());

        if (bookingToDelete != null) {
            bookingRepository.delete(bookingToDelete);

            logger.info("\nAdmin deleted Booking with ID: " + bookingToDelete.getId() + ".\n");
        } else {

            logger.error("\nERROR: Admin tried to delete Booking but provided Booking does not exist.\n" +
                    "Provided Booking ID: " + booking.getId() + ".\n");
        }

    }
}