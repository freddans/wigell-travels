package com.freddan.wigell_travels.services;

import com.freddan.wigell_travels.entities.Trip;
import com.freddan.wigell_travels.repositories.TripRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        return tripRepository.findAll();
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

        if (tripInfo.getPricePerWeek() != 0 && tripInfo.getPricePerWeek() > 0 &&
                tripInfo.getHotelName() != null && !tripInfo.getHotelName().isEmpty() &&
                tripInfo.getCountry() != null && !tripInfo.getCountry().isEmpty() &&
                tripInfo.getCity() != null && !tripInfo.getCity().isEmpty()) {

            Trip trip = new Trip(tripInfo.getPricePerWeek(), tripInfo.getHotelName(), tripInfo.getCountry(), tripInfo.getCity());
            tripRepository.save(trip);

            // Create Trip Item
            tripItemService.create(trip);

            logger.info("\nAdmin added a new destination.\n" +
                    "Hotel: " + tripInfo.getHotelName() + ".\n" +
                    "Country: " + tripInfo.getCountry() + ".\n" +
                    "City: " + tripInfo.getCity() + ".\n");

            return trip;
        } else {
            // ERROR: Price per week, hotelname, country or city was 0, empty or null
            logger.error("\nERROR: Admin tried to add a new destination. Either name of Hotel, Country or City was not filled in.\n");
            return null;
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
                changes.append("Changed City to: " + newTripInfo.getCity());
            }

            tripRepository.save(existingTrip);

            tripItemService.update(existingTrip);

            logger.info("\nAdmin updated Trip ID: " + existingTrip.getId() + " was updated. " + changes.toString() + ".\n");

            return existingTrip;
        } else {

            // ERROR: Trip with provided ID does not exist;
            logger.error("\nERROR: Admin tried to update Trip ID: " + id + " but Trip with provided ID does not exist.\n" +
                    "Provided Trip ID: " + id + ".\n");
            return null;
        }
    }

    public void delete(long id) {
        tripRepository.deleteById(id);

    }
}