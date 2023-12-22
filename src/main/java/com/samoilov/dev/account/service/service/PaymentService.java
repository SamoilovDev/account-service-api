package com.samoilov.dev.account.service.service;

import com.samoilov.dev.account.service.entity.PaymentEntity;
import com.samoilov.dev.account.service.entity.UserEntity;
import com.samoilov.dev.account.service.model.PaymentInfoModel;
import com.samoilov.dev.account.service.repository.UserPaymentRepository;
import com.samoilov.dev.account.service.repository.UserAccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserPaymentRepository userPaymentRepository;

    private final UserAccountRepository userAccountRepository;

    @Transactional
    public ResponseEntity<Map<String, String>> addPayments(List<PaymentEntity> paymentEntities) {
        paymentEntities.forEach(payment -> {
            userAccountRepository.findUserEntityByEmailIgnoreCase(payment.getUserEmail()).ifPresent(userEntity -> {
                userEntity.getPayments().add(payment);
                userAccountRepository.save(userEntity);
            });
            userPaymentRepository.save(payment);
        });

        return ResponseEntity.ok(Map.of("status", "Added successfully!"));
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updatePayment(PaymentEntity payment) {
        UserEntity user = userAccountRepository.findUserEntityByEmailIgnoreCase(payment.getUserEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee doesn't exist!"));

        user.getPayments().add(payment);
        userAccountRepository.save(user);

        return ResponseEntity.ok(Map.of("status", "Updated successfully!"));
    }

    @Transactional
    public ResponseEntity<List<PaymentInfoModel>> findInfoByMail(String email, Date period) {
        UserEntity user = userAccountRepository.findUserEntityByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<PaymentInfoModel> foundedPayments = userPaymentRepository.findByEmailAndOptionalPeriod(email, period)
                .stream()
                .map(employee -> new PaymentInfoModel(
                        user.getName(),
                        user.getLastName(),
                        employee.getPeriod(),
                        this.prepareSalary(employee.getSalary())
                ))
                .toList();

        return ResponseEntity.ok(foundedPayments);
    }

    private String prepareSalary(Double salary) {
        long dollars = salary.longValue();
        long cents = (long) ((salary % 1) * 100);

        log.info("Prepare salary...");
        return "%d dollar(s) %d cent(s)".formatted(dollars, cents);
    }

}
