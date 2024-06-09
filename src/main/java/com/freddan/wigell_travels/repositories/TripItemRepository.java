package com.freddan.wigell_travels.repositories;

import com.freddan.wigell_travels.entities.TripItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripItemRepository extends JpaRepository<TripItem, Long> {
}
