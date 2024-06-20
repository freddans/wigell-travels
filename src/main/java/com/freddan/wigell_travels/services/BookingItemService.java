package com.freddan.wigell_travels.services;

import com.freddan.wigell_travels.VO.BookingItemResponseTemplateVO;
import com.freddan.wigell_travels.VO.Currency;
import com.freddan.wigell_travels.entities.Booking;
import com.freddan.wigell_travels.entities.BookingItem;
import com.freddan.wigell_travels.entities.Customer;
import com.freddan.wigell_travels.entities.TripItem;
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

            BookingItem bookingItem = new BookingItem(booking.getId(), booking.getDepartureDate(), trip, booking.getCustomer());
            bookingItemRepository.save(bookingItem);


        } else {
            // ERROR: TripItem ID does not exist.
        }
    }

    public void update(long bookingId, long newCustomerId, long newTripId) {
        BookingItem bookingItem = findBookingItemById(bookingId);
        Customer oldCustomer = customerService.findUserById(bookingItem.getCustomer().getId());
        TripItem oldTrip = tripItemService.findTripItemById(bookingItem.getTrip().getId());


        // If trip ip is not same as bookingItem trip ID or tripId is 0
        if (oldTrip.getId() != newTripId && newTripId != 0) {
            // send the trip to update trip
            TripItem newTrip = tripItemService.findTripItemById(newTripId);
            if (newTrip != null) {
                bookingItem.setTrip(newTrip);
            }
        }
        if (oldCustomer.getId() != newCustomerId && newCustomerId != 0) {
            Customer newCustomer = customerService.findUserById(newCustomerId);

            bookingItem.setCustomer(newCustomer);
        }

        bookingItemRepository.save(bookingItem);
    }

    public List<BookingItemResponseTemplateVO> findMyBookingItems(long customerId) throws IllegalAccessException {
        Customer customer = customerService.findUserById(customerId);

        if (customer != null) {
            System.out.println("GOOD: Customer EXIST");
            List<BookingItem> myBookings = new ArrayList<>();
            System.out.println("GOOD: CREATED myBookings LIST... scanning for bookingItems");
            for (BookingItem bookingItem : bookingItemRepository.findAll()) {
                if (bookingItem.getCustomer().equals(customer)) {
                    myBookings.add(bookingItem);
                }
            }

            if (myBookings.isEmpty()) {
                System.out.println("BAD: myBookings is EMPTY");
            } else {
                System.out.println("GOOD: myBookings HAS BOOKINGS");
            }

            // all bookings in one list - change to VO now
            List<BookingItemResponseTemplateVO> myBookingsWithCurrency = new ArrayList<>();
            System.out.println("GOOD: CREATED myBookingsWithCurrency");
            for (BookingItem bookingItem : myBookings) {
                BookingItemResponseTemplateVO vo = new BookingItemResponseTemplateVO();
                System.out.println("GOOD: CREATED NEW: vo");
                try {

                    System.out.println("GOOD: CREATING THE URL STRING");

                    String url = "http://WIGELL-CURRENCY/api/v1/currency/" + bookingItem.getTrip().getPricePerWeek();

                    System.out.println("GOOD: URL SUCCESSFUL AND IS: " + url);

                    Currency totalCost = restTemplate.getForObject(url, Currency.class);

                    System.out.println("CREATED CURRENCY: totalCost");

                    vo.setBookingItem(bookingItem);
                    vo.setTotalCost(totalCost);

                    System.out.println("GOOD: SET BOOKING ITEM AND TOTAL CIST");

                    myBookingsWithCurrency.add(vo);
                    System.out.println("GOOD: ADDED vo TO booking LIST");
                } catch (RestClientException e) {
                    e.printStackTrace();

                    throw new IllegalAccessException("Error fetching currency data");
                }
            }

            myBookings.clear();
            System.out.println("CLEARED myBOOKINGS and returning myBookingsWithCurrency-LIST");

            return myBookingsWithCurrency;
        } else {
            System.out.println("ERROR: Customer with provided ID does not exist");
            throw new IllegalAccessException("Customer with provided ID does not exist");
        }
    }
}