package com.amigoscode.testing.customer;

import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(
        properties = {
                "spring.jpa.properties.javax.persistence.validation.mode=none"
        } // for column -> not null to take effect
)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository underTest;

    @Test
    void itShouldSelectCustomerByPhoneNumber() {
        // Given sth
        UUID id = UUID.randomUUID();
        String phoneNumber = "0000";
        Customer customer = new Customer(id, "Abel", phoneNumber);

        // When invoke the method
        underTest.save(customer);

        // Then -> the actual assertion
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);

        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldNotSelectCustomerByPhoneNumberWhenNumberDoesNotExists() {
        // Given
        String phoneNumber = "0000";
        // When
        Optional<Customer> optionalCustomer = underTest.selectCustomerByPhoneNumber(phoneNumber);
        // Then
        assertThat(optionalCustomer).isNotPresent();

    }

    // 測試crudRepo 的save
    @Test
    void itShouldSaveCustomer() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Abel", "0000");

        // When (執行功能) -> 寫一筆資料進去
        underTest.save(customer);

        // Then (驗證執行完的結果，是否符合預期) -> 看剛剛的資料有沒有寫成功?
        Optional<Customer> optionalCustomer = underTest.findById(id);
        assertThat(optionalCustomer)
                .isPresent() // 如果只是寫到這邊就收工也可以… 再下面是更仔細的測試
                .hasValueSatisfying(c -> {
//                    assertThat(c.getId()).isEqualTo(id);
//                    assertThat(c.getName()).isEqualTo("Abel");
//                    assertThat(c.getPhoneNumber()).isEqualTo("0000a");
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        // Given -> setting up
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "0000");

        // When (執行功能) -> 寫一筆資料進去
        assertThatThrownBy(()-> underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.name")
                        .isInstanceOf(DataIntegrityViolationException.class);


        // Then (驗證執行完的結果，是否符合預期) -> 看剛剛的資料有沒有寫成功?
//        assertThat(underTest.findById(id)).isNotPresent();
    }





    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        // Given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Alex", null);

        // When
        // Then
        assertThatThrownBy(()->underTest.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.phoneNumber; nested exception is org.hibernate.PropertyValueException: not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);

//        assertThat(underTest.findById(id)).isNotPresent();
    }


}