package com.example.Account.Service.controller;

import com.example.Account.Service.entity.PaymentEntity;
import com.example.Account.Service.model.PaymentInfoModel;
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
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BusinessController {

    @Autowired
    private PaymentService employeeService;

    @GetMapping("/empl/payment")
    @Transactional
    public ResponseEntity<List<PaymentInfoModel>> getPayment(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "MM-yyyy", iso = DateTimeFormat.ISO.DATE)
            @JsonFormat(pattern = "MM-yyyy", lenient = OptBoolean.FALSE)
            Date period,
            Principal user
    ) {
        return employeeService.findInfoByMail(user.getName(), period);
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<Map<String, String>> downloadPayments(@RequestBody List<@Valid PaymentEntity> paymentEntities) {
        return employeeService.addPayments(paymentEntities);
    }

    @PutMapping ("/acct/payments")
    public ResponseEntity<Map<String, String>> updatePayments(@Valid @RequestBody PaymentEntity payment) {
        return employeeService.updatePayment(payment);
    }

}
