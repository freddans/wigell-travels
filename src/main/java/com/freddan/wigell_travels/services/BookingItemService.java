package com.freddan.wigell_travels.services;

import com.freddan.wigell_travels.VO.BookingItemResponseTemplateVO;
import com.freddan.wigell_travels.VO.Currency;
import com.freddan.wigell_travels.entities.Booking;
import com.freddan.wigell_travels.entities.BookingItem;
import com.freddan.wigell_travels.entities.Customer;
import com.freddan.wigell_travels.entities.TripItem;
import com.freddan.wigell_travels.exceptions.TravelException;
import com.freddan.wigell_travels.repositories.BookingItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingItemService {

    private BookingItemRepository bookingItemRepository;
    private TripItemService tripItemService;
    private CustomerService customerService;
    private RestTemplate restTemplate;

    @Autowired
    public BookingItemService(BookingItemRepository bookingItemRepository, TripItemService tripItemService, CustomerService customerService, RestTemplate restTemplate) {
        this.bookingItemRepository = bookingItemRepository;
        this.tripItemService = tripItemService;
        this.customerService = customerService;
        this.restTemplate = restTemplate;
    }

    public BookingItem findBookingItemById(long id) {
        Optional<BookingItem> optionalBookingItem = bookingItemRepository.findById(id);

        return optionalBookingItem.orElse(null);
    }

    public void create(Booking booking) {
        TripItem trip = tripItemService.findTripItemById(booking.getTrip().getId());

        if (trip != null) {
            trip.setAvailableTickets(trip.getAvailableTickets() - booking.getTickets());
            tripItemService.saveOrUpdate(trip);

            BookingItem bookingItem = new BookingItem(booking.getId(), booking.getDepartureDate(), trip, booking.getCustomer(), booking.getTickets());

            bookingItemRepository.save(bookingItem);

        }
    }

    public void update(long bookingId, long newCustomerId, long newTripId, int tickets) {
        BookingItem existingBookingItem = findBookingItemById(bookingId);
        Customer newCustomer = customerService.findUserById(newCustomerId);
        TripItem oldTrip = tripItemService.findTripItemById(existingBookingItem.getTrip().getId());
        TripItem newTrip = tripItemService.findTripItemById(newTripId);

        if (existingBookingItem != null) {

            if (newCustomer != null) {
                existingBookingItem.setCustomer(newCustomer);
            }
            if (newTrip != null && newTripId != existingBookingItem.getTrip().getId()) {
                int oldTickets = existingBookingItem.getTickets();

                if (tickets > 0) {
                    if (newTrip.getAvailableTickets() >= tickets) {

                        oldTrip.setAvailableTickets(oldTrip.getAvailableTickets() + oldTickets);

                        newTrip.setAvailableTickets(newTrip.getAvailableTickets() - tickets);

                        tripItemService.saveOrUpdate(oldTrip);
                        tripItemService.saveOrUpdate(newTrip);

                        existingBookingItem.setTickets(tickets);

                        existingBookingItem.setTrip(newTrip);
                    } else {
                        // error not enough tickets in stock
                    }
                }
            } else {

                if (tickets > 0 && tickets != existingBookingItem.getTickets()) {

                    if (tickets < existingBookingItem.getTickets()) {

                        int ticketsToGiveBack = existingBookingItem.getTickets() - tickets;

                        if (tickets <= (oldTrip.getAvailableTickets() + ticketsToGiveBack)) {

                            existingBookingItem.setTickets(existingBookingItem.getTickets() - ticketsToGiveBack);
                            existingBookingItem.getTrip().setAvailableTickets(existingBookingItem.getTrip().getAvailableTickets() + ticketsToGiveBack);

                            tripItemService.saveOrUpdate(existingBookingItem.getTrip());
                        } else {
                            // error not enough tickets in stock
                        }


                    } else if (tickets > existingBookingItem.getTickets()) {
                        int ticketsToBuy = tickets - existingBookingItem.getTickets();

                        if ((ticketsToBuy) <= existingBookingItem.getTrip().getAvailableTickets()) {

                            existingBookingItem.setTickets(existingBookingItem.getTickets() + ticketsToBuy);
                            existingBookingItem.getTrip().setAvailableTickets(existingBookingItem.getTrip().getAvailableTickets() - ticketsToBuy);

                            tripItemService.saveOrUpdate(existingBookingItem.getTrip());
                        } else {
                            // error not enough tickets in stock
                        }
                    }
                }
            }
        }

        tripItemService.saveOrUpdate(oldTrip);

        bookingItemRepository.save(existingBookingItem);

        if (tickets == 0) {
            int nrOfTickets = existingBookingItem.getTickets();
            existingBookingItem.getTrip().setAvailableTickets(existingBookingItem.getTrip().getAvailableTickets() + nrOfTickets);
            tripItemService.saveOrUpdate(existingBookingItem.getTrip());

            delete(existingBookingItem);
        }
    }

    public void delete(BookingItem booking) {
        BookingItem bookingToDelete = findBookingItemById(booking.getId());

        if (bookingToDelete != null) {
            bookingItemRepository.delete(bookingToDelete);
        }

    }

    public List<BookingItemResponseTemplateVO> findMyBookingItems(long customerId) {
        Customer customer = customerService.findUserById(customerId);

        if (customer != null) {

            List<BookingItem> myBookings = new ArrayList<>();
            for (BookingItem bookingItem : bookingItemRepository.findAll()) {
                if (bookingItem.getCustomer().equals(customer)) {
                    myBookings.add(bookingItem);
                }
            }


            // all bookings in one list - change to VO now
            List<BookingItemResponseTemplateVO> myBookingsWithCurrency = new ArrayList<>();

            for (BookingItem bookingItem : myBookings) {
                BookingItemResponseTemplateVO vo = new BookingItemResponseTemplateVO();

                double pricePerWeek = (bookingItem.getTrip().getPricePerWeek() * bookingItem.getTickets());

                String url = "http://WIGELL-CURRENCY/api/v1/" + pricePerWeek;


                Currency totalCost = restTemplate.getForObject(url, Currency.class);

                vo.setBookingItem(bookingItem);
                vo.setTotalCost(totalCost);


                myBookingsWithCurrency.add(vo);
            }

            myBookings.clear();

            if (myBookingsWithCurrency.isEmpty()) {
                throw new TravelException("Customer currently has no active bookings");
            } else {
                return myBookingsWithCurrency;
            }
        } else {
            throw new TravelException("ERROR: Customer with provided ID does not exist");
        }
    }

}