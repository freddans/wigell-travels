package com.freddan.wigell_travels.services;

import com.freddan.wigell_travels.entities.Customer;
import com.freddan.wigell_travels.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> allUsers() {
        return customerRepository.findAll();
    }

    public Customer findUserById(long id) {
        Optional<Customer> optionalUser = customerRepository.findById(id);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            return null;
        }
    }
}