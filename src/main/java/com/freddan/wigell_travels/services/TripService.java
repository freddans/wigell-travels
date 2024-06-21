package com.freddan.wigell_travels.services;

import com.freddan.wigell_travels.entities.Trip;
import com.freddan.wigell_travels.exceptions.TravelException;
import com.freddan.wigell_travels.repositories.TripRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final TripItemService tripItemService;
    private final Logger logger = Logger.getLogger(BookingService.class);

    @Autowired
    public TripService(TripRepository tripRepository, TripItemService tripItemService) {
        this.tripRepository = tripRepository;
        this.tripItemService = tripItemService;
    }

    public List<Trip> allAvailableTrips() {
        List<Trip> availableTrips = new ArrayList<>();
        for (Trip trip : tripRepository.findAll()) {
            if (trip.getAvailableTickets() > 0) {
                availableTrips.add(trip);
            }
        }
        return availableTrips;
    }

    public Trip findTripById(long id) {
        Optional<Trip> optionalTrip = tripRepository.findById(id);
        if (optionalTrip.isPresent()) {
            return optionalTrip.get();
        } else {
            return null;
        }
    }

    public Trip create(Trip tripInfo) {

        if (tripInfo.getPricePerWeek() != 0 && tripInfo.getPricePerWeek() > 0) {
            if (tripInfo.getHotelName() != null && !tripInfo.getHotelName().isEmpty()) {
                if (tripInfo.getCountry() != null && !tripInfo.getCountry().isEmpty()) {
                    if (tripInfo.getCity() != null && !tripInfo.getCity().isEmpty()) {
                        if (tripInfo.getAvailableTickets() != 0 && tripInfo.getAvailableTickets() > 0) {

                            Trip trip = new Trip(tripInfo.getPricePerWeek(), tripInfo.getHotelName(), tripInfo.getCountry(), tripInfo.getCity(), tripInfo.getAvailableTickets());
                            tripRepository.save(trip);

                            // Create Trip Item
                            tripItemService.create(trip);

                            logger.info("\nAdmin added a new destination.\n" +
                                    "Hotel: " + trip.getHotelName() + ".\n" +
                                    "Country: " + trip.getCountry() + ".\n" +
                                    "City: " + trip.getCity() + ".\n" +
                                    "Available tickets: " + trip.getAvailableTickets() + ".\n");

                            return trip;
                        } else {
                            logger.error("\nERROR: Admin tried to add new destination. Missed to fill in available tickets.\n");
                            throw new TravelException("ERROR: Failed to add new destination. Missed to fill in ''availableTickets'': X");
                        }
                    } else {
                        logger.error("\nERROR: Admin tried to add new destination. Missed to fill in city.\n");
                        throw new TravelException("ERROR: Failed to add new destination. Missed to fill in ''city'': X");
                    }
                } else {
                    logger.error("\nERROR: Admin tried to add new destination. Missed to fill in country.\n");
                    throw new TravelException("ERROR: Failed to add new destination. Missed to fill in ''country'': X");
                }
            } else {
                logger.error("\nERROR: Admin tried to add new destination. Missed to fill in name of hotel.\n");
                throw new TravelException("ERROR: Failed to add new destination. Missed to fill in ''hotelName'': X");
            }
        } else {
            // ERROR: Price per week, hotelname, country, city or availableticket was 0, empty or null
            logger.error("\nERROR: Admin tried to add a new destination. Missed to fill in price per week.\n");
            throw new TravelException("ERROR: Failed to add new destination. Missed to fill in ''pricePerWeek'': X");
        }
    }

    public Trip update(long id, Trip newTripInfo) {
        Trip existingTrip = findTripById(id);

        if (existingTrip != null) {

            StringBuilder changes = new StringBuilder();

            if (newTripInfo.getPricePerWeek() != 0 && newTripInfo.getPricePerWeek() != existingTrip.getPricePerWeek()) {
                existingTrip.setPricePerWeek(newTripInfo.getPricePerWeek());
                changes.append("\nChanged Price per week to: " + newTripInfo.getPricePerWeek() + ".\n");
            }
            if (newTripInfo.getHotelName() != null && !newTripInfo.getHotelName().isEmpty() && !newTripInfo.getHotelName().equals(existingTrip.getHotelName())) {
                existingTrip.setHotelName(newTripInfo.getHotelName());
                changes.append("Changed name of Hotel to: " + newTripInfo.getHotelName() + ".\n");
            }
            if (newTripInfo.getCountry() != null && !newTripInfo.getCountry().isEmpty() && !newTripInfo.getCountry().equals(existingTrip.getCountry())) {
                existingTrip.setCountry(newTripInfo.getCountry());
                changes.append("Changed Country to: " + newTripInfo.getCountry() + ".\n");
            }
            if (newTripInfo.getCity() != null && !newTripInfo.getCity().isEmpty() && !newTripInfo.getCity().equals(existingTrip.getCity())) {
                existingTrip.setCity(newTripInfo.getCity());
                changes.append("Changed City to: " + newTripInfo.getCity() + ".\n");
            }
            if (newTripInfo.getAvailableTickets() != 0 && newTripInfo.getAvailableTickets() != existingTrip.getAvailableTickets()) {
                existingTrip.setAvailableTickets(newTripInfo.getAvailableTickets());
                changes.append("Changed Available tickets to: " + newTripInfo.getAvailableTickets());
            }

            tripRepository.save(existingTrip);

            tripItemService.update(existingTrip);

            logger.info("\nAdmin updated Trip ID: " + existingTrip.getId() + " was updated. " + changes.toString() + ".\n");

            return existingTrip;
        } else {

            // ERROR: Trip with provided ID does not exist;
            logger.error("\nERROR: Admin tried to update Trip ID: " + id + " but Trip with provided ID does not exist.\n" +
                    "Provided Trip ID: " + id + ".\n");
            throw new TravelException("ERROR: Trip with provided ID does not exist.\n" +
                    "Provided Trip ID: " + id);
        }
    }

    public void delete(long id) {
        tripRepository.deleteById(id);
    }

    public void saveOrUpdate(Trip trip) {
        tripRepository.save(trip);
    }
}