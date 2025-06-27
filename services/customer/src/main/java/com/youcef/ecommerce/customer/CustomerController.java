package com.youcef.ecommerce.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<String> createCustomer(@RequestBody @Valid CustomerRequest customerRequest){
        return ResponseEntity.ok(customerService.createCustomer(customerRequest));
    }

    @PutMapping
    public ResponseEntity<Void> updateCustomer(@RequestBody @Valid CustomerRequest customerRequest){
        customerService.updateCustomer(customerRequest);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> listCustomers() {
        return ResponseEntity.ok(customerService.listCustomers());
    }

    @GetMapping(path = "/exists/{customerId}")
    public ResponseEntity<Boolean> existsById(@PathVariable String customerId) {
        return ResponseEntity.ok(customerService.existsById(customerId));
    }

    @GetMapping(path = "/{customerId}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable String customerId) {
        return ResponseEntity.ok(customerService.getById(customerId));
    }

    @DeleteMapping(path = "/{customerId}")
    public ResponseEntity<Void> deleteById(@PathVariable String customerId) {
        customerService.deleteById(customerId);
        return ResponseEntity.accepted().build();
    }
}
