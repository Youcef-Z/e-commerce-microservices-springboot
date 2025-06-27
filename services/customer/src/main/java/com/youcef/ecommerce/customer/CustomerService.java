package com.youcef.ecommerce.customer;

import com.youcef.ecommerce.exceptions.CustomerNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public String createCustomer(@Valid CustomerRequest customerRequest) {
        Customer customer = customerRepository.save(customerMapper.toCustomer(customerRequest));
        return customer.getId();
    }

    public void updateCustomer(@Valid CustomerRequest customerRequest) {
        Customer customer = customerRepository.findById(customerRequest.id())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerRequest.id()));
        mergeCustomer(customer, customerRequest);
        customerRepository.save(customer);
    }

    private void mergeCustomer(Customer customer, @Valid CustomerRequest customerRequest) {
        if (StringUtils.isNotBlank(customerRequest.firstName())){
            customer.setFirstName(customerRequest.firstName());
        }
        if (StringUtils.isNotBlank(customerRequest.lastName())){
            customer.setLastName(customerRequest.lastName());
        }
        if (StringUtils.isNotBlank(customerRequest.email())){
            customer.setEmail(customerRequest.email());
        }
        if (customerRequest.address() != null){
            customer.setAddress(customerRequest.address());
        }
    }


    public List<CustomerResponse> listCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toCustomerResponse)
                .toList();
    }

    public Boolean existsById(String customerId) {
        return customerRepository.findById(customerId).isPresent();
    }

    public CustomerResponse getById(String customerId) {
        return customerRepository.findById(customerId)
                .map(customerMapper::toCustomerResponse)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));
    }

    public void deleteById(String customerId) {
        customerRepository.deleteById(customerId);
    }
}
