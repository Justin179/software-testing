package com.amigoscode.testing.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerRegistrationRequest {


    private final Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    public CustomerRegistrationRequest(@JsonProperty Customer customer) {
        this.customer = customer;
    }
    @Override
    public String toString() {
        return "CustomerRegistrationRequest{" +
                "customer=" + customer +
                '}';
    }
}
