package com.samoilov.dev.account.service.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.samoilov.dev.account.service.entity.PaymentEntity;
import com.samoilov.dev.account.service.model.PaymentInfoModel;
import com.samoilov.dev.account.service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BusinessController {

    private final PaymentService employeeService;

    @GetMapping("/empl/payment")
    public ResponseEntity<List<PaymentInfoModel>> getPayment(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "MM-yyyy", iso = DateTimeFormat.ISO.DATE)
            @JsonFormat(pattern = "MM-yyyy", lenient = OptBoolean.FALSE)
            Date period,
            Principal user) {
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
