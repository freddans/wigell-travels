package com.freddan.wigell_travels.services;

import com.freddan.wigell_travels.entities.Trip;
import com.freddan.wigell_travels.entities.TripItem;
import com.freddan.wigell_travels.repositories.TripItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TripItemService {

    private TripItemRepository tripItemRepository;

    @Autowired
    public TripItemService(TripItemRepository tripItemRepository) {
        this.tripItemRepository = tripItemRepository;
    }

    public TripItem findTripItemById(long id) {
        Optional<TripItem> optionalTripItem = tripItemRepository.findById(id);

        return optionalTripItem.orElse(null);
    }

    public void create(Trip trip) {

        TripItem tripItem = new TripItem(trip.getId(), trip.getPricePerWeek(), trip.getHotelName(), trip.getCountry(), trip.getCity());
        tripItemRepository.save(tripItem);
    }

    public void update(Trip newTripInformation) {
        TripItem tripItem = findTripItemById(newTripInformation.getId());

        if (newTripInformation.getId() != 0 && newTripInformation.getId() != tripItem.getId()) {
            tripItem.setId(newTripInformation.getId());
        }
        if (newTripInformation.getPricePerWeek() != 0 && newTripInformation.getPricePerWeek() != tripItem.getPricePerWeek()) {
            tripItem.setPricePerWeek(newTripInformation.getPricePerWeek());
        }
        if (newTripInformation.getHotelName() != null && !newTripInformation.getHotelName().isEmpty() && !newTripInformation.getHotelName().equals(tripItem.getHotelName())) {
            tripItem.setHotelName(newTripInformation.getHotelName());
        }
        if (newTripInformation.getCountry() != null && !newTripInformation.getCountry().isEmpty() && !newTripInformation.getCountry().equals(tripItem.getCountry())) {
            tripItem.setCountry(newTripInformation.getCountry());
        }
        if (newTripInformation.getCity() != null && !newTripInformation.getCity().isEmpty() && !newTripInformation.getCity().equals(tripItem.getCity())) {
            tripItem.setCity(newTripInformation.getCity());
        }

        tripItemRepository.save(tripItem);
    }
}