package com.freddan.wigell_travels.services;

import com.freddan.wigell_travels.VO.Currency;
import com.freddan.wigell_travels.VO.ResponseTemplateVO;
import com.freddan.wigell_travels.entities.Booking;
import com.freddan.wigell_travels.entities.Customer;
import com.freddan.wigell_travels.entities.Trip;
import com.freddan.wigell_travels.exceptions.TravelException;
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

    public ResponseTemplateVO createBooking(long customerId, long tripId, int tickets) {
        Customer customer = customerService.findUserById(customerId);
        Trip trip = tripService.findTripById(tripId);

        if (customer != null) {
            if (trip != null) {
                if (tickets >= 1) {
                    if (trip.getAvailableTickets() >= tickets) {
                        double costPerWeek = (trip.getPricePerWeek() * tickets);

                        // Create a departureDate 1 month ahead in time
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MONTH, 1);
                        Date date;
                        date = cal.getTime();

                        Booking booking = new Booking(date, trip, customer, tickets);

                        trip.setAvailableTickets(trip.getAvailableTickets() - tickets);
                        tripService.saveOrUpdate(trip);

                        bookingRepository.save(booking);

                        // Create a bookingItem that wont be deleted
                        bookingItemService.create(booking);

                        ResponseTemplateVO vo = new ResponseTemplateVO();

                        Currency totalCost = restTemplate.getForObject("http://WIGELL-CURRENCY/api/v1/" + costPerWeek, Currency.class);

                        vo.setBooking(booking);
                        vo.setTotalCost(totalCost);

                        logger.info("\nCustomer " + customer.getFirstName() + " '" + customer.getUsername() + "' " + customer.getLastName() + " booked a trip to " + trip.getCity() + ", " + trip.getCountry() + ".\n");

                        return vo;
                    } else {
                        logger.error("\nERROR: Customer tried to book more tickets than in stock\n");
                        throw new TravelException("ERROR: Not enough tickets in stock.\n" +
                                "You tried to book " + tickets + " out of " + trip.getAvailableTickets() + " available tickets");
                    }
                } else {
                    // tickets are 0 or less
                    logger.error("\nERROR: Customer tried to order less than 0 tickets");
                    throw new TravelException("ERROR: Amount of tickets has to be 1 or more");
                }
            } else {
                // trip is null
                logger.error("\nERROR: Trip with provided ID does not exist.\n" +
                        "Provided Trip ID: " + tripId);
                throw new TravelException("ERROR: Trip with provided ID does not exist.\n" +
                        "Provided Trip ID: " + tripId);
            }


        } else {

            // ERROR customer is null
            logger.error("\nERROR: Customer with provided ID does not exist.\n" +
                    "Provided Customer ID: " + customerId + ".\n");
            throw new TravelException("ERROR: Customer with provided ID does not exist.\n" +
                    "Provided Customer ID: " + customerId);
        }
    }

    public Booking updateBooking(long bookingId, long customerId, long tripId, int tickets) {
        Booking existingBooking = findBookingById(bookingId);
        Customer newCustomer = customerService.findUserById(customerId);
        Trip newTrip = tripService.findTripById(tripId);

        if (existingBooking != null) {

            StringBuilder changes = new StringBuilder();

            if (newCustomer != null && newCustomer.getId() != existingBooking.getCustomer().getId()) {
                existingBooking.setCustomer(newCustomer);
                changes.append("\nChanged Customer to ID: " + newCustomer.getId() + ".\n");

            } else if (newCustomer == null) {
                logger.error("\nERROR: Customer with provided ID does not exist.\n");
                throw new TravelException("ERROR: Customer with provided ID does not exist.\n" +
                        "Provided Customer ID: " + customerId);
            }
            // If new trip is added
            if (newTrip != null && newTrip.getId() != existingBooking.getTrip().getId()) {

                // get old trip
                Trip oldTrip = existingBooking.getTrip();

                // get old tickets
                int oldTickets = existingBooking.getTickets();


                if (tickets > 0) {
                    // if availableTickets are enough for booking
                    if (newTrip.getAvailableTickets() >= tickets) {

                        // give back old tickets
                        oldTrip.setAvailableTickets(oldTrip.getAvailableTickets() + oldTickets);

                        // buy new tickets
                        newTrip.setAvailableTickets(newTrip.getAvailableTickets() - tickets);

                        // save old and new trip
                        tripService.saveOrUpdate(oldTrip);
                        tripService.saveOrUpdate(newTrip);

                        existingBooking.setTickets(tickets);

                        existingBooking.setTrip(newTrip);
                        changes.append("Changed Trip to ID: " + newTrip.getId() + ".\n");
                        changes.append("Changed Tickets to: " + tickets);

                    } else {
                        logger.error("\nERROR: Customer tried to order more tickets than there are in stock.");
                        throw new TravelException("ERROR: Not enough tickets in stock.\n" +
                                "You tried to book " + tickets + " out of " + newTrip.getAvailableTickets() + " available tickets");
                    }
                } else if (tickets < 0) {
                    logger.error("\nERROR: Customer tried to book less than 1 ticket\n");
                    throw new TravelException("ERROR: Amount of tickets has to be 1 or more");
                }
            } else if (newTrip == null) {
                logger.error("\nERROR: Customer tried to update Trip but provided Trip ID doesnt exist.\n");
                throw new TravelException("ERROR: Trip with provided ID does not exist.\n" +
                        "Provided Trip ID: " + tripId);
            }
            else {
                // if the trip is the same


                if (tickets > 0 && tickets != existingBooking.getTickets()) {
                    if (tickets < existingBooking.getTickets()) {
                        int ticketsToGiveBack = existingBooking.getTickets() - tickets;

                        existingBooking.setTickets(existingBooking.getTickets() - ticketsToGiveBack);
                        existingBooking.getTrip().setAvailableTickets(existingBooking.getTrip().getAvailableTickets() + ticketsToGiveBack);

                        // save trip
                        tripService.saveOrUpdate(existingBooking.getTrip());
                        changes.append("Reduced tickets to: " + existingBooking.getTickets() + ".\n");

                    } else if (tickets > existingBooking.getTickets()) {

                        int ticketsToBuy = tickets - existingBooking.getTickets();
                        if (ticketsToBuy <= existingBooking.getTrip().getAvailableTickets()) {

                            existingBooking.setTickets(existingBooking.getTickets() + ticketsToBuy);
                            existingBooking.getTrip().setAvailableTickets(existingBooking.getTrip().getAvailableTickets() - ticketsToBuy);

                            tripService.saveOrUpdate(existingBooking.getTrip());
                            changes.append("Added more tickets to: " + existingBooking.getTickets());
                        } else {
                            logger.error("\nERROR: Customer tried to order more tickets than there are in stock.");
                            throw new TravelException("ERROR: Not enough tickets in stock.\n" +
                                    "You tried to book " + tickets + " out of " + (existingBooking.getTrip().getAvailableTickets()+existingBooking.getTickets()) + " available tickets");
                        }
                    }
                } if (tickets < 0) {
                    logger.error("\nERROR: Customer tried to book less than 1 ticket\n");
                    throw new TravelException("ERROR: Amount of tickets has to be 1 or more");
                }
            }

            bookingRepository.save(existingBooking);

            bookingItemService.update(bookingId, customerId, tripId, tickets);



            if (tickets == 0) {
                int nrOfTickets = existingBooking.getTickets();
                existingBooking.getTrip().setAvailableTickets(existingBooking.getTrip().getAvailableTickets() + nrOfTickets);
                tripService.saveOrUpdate(existingBooking.getTrip());

                delete(existingBooking);

                logger.info("\nCustomer deleted his booking by un-booking tickets.\n");

                throw new TravelException("Booking deleted");
            } else {
                logger.info("\nBooking ID: " + existingBooking.getId() + " was updated. " + changes.toString() + ".\n");

                return existingBooking;
            }

        } else {
            logger.error("\nERROR: Booking with provided ID: " + bookingId + " does not exist.\n");
            throw new TravelException("ERROR: Booking with provided ID: " + bookingId + " does not exist.");
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