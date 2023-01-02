package com.example.Account.Service.controller;

import com.example.Account.Service.entity.PaymentEntity;
import com.example.Account.Service.service.PaymentService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BusinessController {

    @Autowired
    PaymentService employeeService;

    @GetMapping("/empl/payment")
    @Transactional
    public ResponseEntity<?> getPayment(@RequestParam(required = false)
                                        @DateTimeFormat(pattern = "MM-yyyy", iso = DateTimeFormat.ISO.DATE)
                                        @JsonFormat(pattern = "MM-yyyy", lenient = OptBoolean.FALSE)
                                        Date period,
                                        Principal user) {
        return ResponseEntity.ok(employeeService.findInfoByMail(user.getName(), period));
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<?> downloadPayments(@RequestBody List<@Valid PaymentEntity> employeeEntities) {
        return ResponseEntity.ok(employeeService.addEmployees(employeeEntities));
    }

    @PutMapping ("/acct/payments")
    public ResponseEntity<?> updatePayments(@Valid @RequestBody PaymentEntity employee) {
        return ResponseEntity.ok(employeeService.updateEmployee(employee));
    }

}
