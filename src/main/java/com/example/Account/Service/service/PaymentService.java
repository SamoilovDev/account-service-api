package com.example.Account.Service.service;

import com.example.Account.Service.entity.PaymentEntity;
import com.example.Account.Service.entity.UserEntity;
import com.example.Account.Service.model.PaymentInfoModel;
import com.example.Account.Service.repository.PaymentRepo;
import com.example.Account.Service.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private UserRepo userRepo;

    public ResponseEntity<Map<String, String>> addPayments(List<PaymentEntity> paymentEntities) {

        paymentEntities.forEach( payment -> {
            userRepo.findUserEntityByEmailIgnoreCase(payment.getUserEmail()).ifPresent(userEntity -> {
                userEntity.getPayments().add(payment);
                userRepo.save(userEntity);
            });
            paymentRepo.save(payment);
        });
        log.info("Add all payments to users");

        return ResponseEntity.ok(Map.of("status", "Added successfully!"));
    }

    public ResponseEntity<Map<String, String>> updatePayment(PaymentEntity payment) {
        UserEntity user = userRepo.findUserEntityByEmailIgnoreCase(payment.getUserEmail()).orElseThrow(() -> {
            log.info("Create new Exception to send request");
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee doesn't exist!");
        });

        log.info("Find user's entity and add new payment");
        user.getPayments().add(payment);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("status", "Updated successfully!"));
    }

    public ResponseEntity<List<PaymentInfoModel>> findInfoByMail(String email, Date period) {
        List<PaymentEntity> foundedPayments = Objects.equals(period, null)
                ? paymentRepo.findAllByUserEmail(email)
                : paymentRepo.findAllByUserEmailAndPeriod(email, period);
        log.info("Founded all eligible payments");

        UserEntity user = userRepo.findUserEntityByEmailIgnoreCase(email).orElseThrow(() -> {
            log.info("Create new exception, because user's entity doesn't exist");
            return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });

        log.info("Prepare and send all founded payments");
        return ResponseEntity.ok(
                foundedPayments.stream()
                        .map(
                                employee -> new PaymentInfoModel(
                                        user.getName(),
                                        user.getLastName(),
                                        employee.getPeriod(),
                                        prepareSalary(employee.getSalary())
                                )
                        )
                        .toList()
        );
    }

    private String prepareSalary(Double salary) {
        long dollars = salary.longValue();
        long cents = (long) ((salary % 1) * 100);

        log.info("Prepare salary...");
        return "%d dollar(s) %d cent(s)".formatted(dollars, cents);
    }

}
