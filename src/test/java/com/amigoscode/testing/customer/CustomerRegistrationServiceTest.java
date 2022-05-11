package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {


    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;


    @Mock // 成對1
    private CustomerRepository customerRepository;
    private CustomerRegistrationService underTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this); // 成對1
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer(){
        // given -> setting up
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);
        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());

        // when
        underTest.registerNewCustomer(request);

        // then
        then(customerRepository).should().save(customerArgumentCaptor.capture());

        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualTo(customer); // test cases
    }



    @Test
    void itShouldNotSaveCustomerWhenCustomerExists(){
        // given
        String phoneNumber = "000099";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Maryam", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... an existing customer is returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer));

        // when
        underTest.registerNewCustomer(request);

        // then
        then(customerRepository).should(never()).save(any());
        then(customerRepository).should().selectCustomerByPhoneNumber(phoneNumber);
        then(customerRepository).shouldHaveNoMoreInteractions();

    }

    @Test
    void itShouldThrowWhenPhoneNumberIsTaken(){
        // given a phone number and a customer
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Maryam", phoneNumber);
        Customer customer2 = new Customer(UUID.randomUUID(), "John", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... an existing customer is returned
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.of(customer2));

        // when
        // then
        assertThatThrownBy(()-> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s] is taken", phoneNumber));

        //
        then(customerRepository).should(never()).save(any(Customer.class));
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull(){
        // given
        String phoneNumber = "000099";
        Customer customer = new Customer(null, "Maryam", phoneNumber);

        // ... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        // ... no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber))
                .willReturn(Optional.empty());

        // when
        underTest.registerNewCustomer(request);

        // then
        then(customerRepository).should().save(customerArgumentCaptor.capture());

        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToIgnoringGivenFields(customer,"id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }




}


















