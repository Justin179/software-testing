package com.amigoscode.testing.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request){

        // 拿傳進來的號碼，去資料庫查
        String phoneNumber = request.getCustomer().getPhoneNumber();
        Optional<Customer> customerOptional = customerRepository.selectCustomerByPhoneNumber(phoneNumber);

        if (customerOptional.isPresent()){ // 等同之前的 if not null
            Customer customer = customerOptional.get();
            if (customer.getName().equals(request.getCustomer().getName())) // 查出個一樣的名字，代表已經註冊過了
                return;
            // 該電話，有查到一個對不上的名字 (該號碼已經被註冊過了)
            throw new IllegalStateException(String.format("phone number [%s] is taken",phoneNumber));
        }

        if (request.getCustomer().getId() == null){
            request.getCustomer().setId(UUID.randomUUID());
        }

        // 註冊新的使用者
        customerRepository.save(request.getCustomer());
    }
}



















