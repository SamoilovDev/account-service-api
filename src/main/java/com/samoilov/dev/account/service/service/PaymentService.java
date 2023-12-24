package com.samoilov.dev.account.service.service;

import com.samoilov.dev.account.service.dto.PaymentDto;
import com.samoilov.dev.account.service.dto.ResponseStatusDto;
import com.samoilov.dev.account.service.entity.PaymentEntity;
import com.samoilov.dev.account.service.entity.UserProfileEntity;
import com.samoilov.dev.account.service.mapper.PaymentMapper;
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

    private final PaymentMapper paymentMapper;

    public static final String SUCCESSFUL_ADDITION = "Added successfully!";
    public static final String SALARY_FORMAT = "%d dollar(s) %d cent(s)";
    public static final String EMPLOYEE_NOT_FOUND = "Employee doesn't exist!";
    public static final String UPDATE_SUCCESS = "Updated successfully!";

    @Transactional
    public ResponseEntity<ResponseStatusDto> addPayments(List<PaymentDto> payments, UserProfileEntity userProfileEntity) {
        payments.stream()
                .map(p -> paymentMapper.mapPaymentDtoToEntity(p, userProfileEntity))
                .forEach(userPaymentRepository::save);

        return ResponseEntity.ok(
                ResponseStatusDto.builder().status(SUCCESSFUL_ADDITION).build()
        );
    }

    @Transactional
    public ResponseEntity<ResponseStatusDto> updatePayment(PaymentEntity payment) {
        userAccountRepository.findUserEntityByEmailOrFirstAndLastName(payment.getUserEmail(), null)
                .ifPresentOrElse(
                        user -> {
                            user.getPayments().add(payment);
                            userAccountRepository.save(user);
                        },
                        () -> {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, EMPLOYEE_NOT_FOUND);
                        }
                );

        return ResponseEntity.ok(
                ResponseStatusDto.builder().status(UPDATE_SUCCESS).build()
        );
    }

    public ResponseEntity<List<PaymentInfoModel>> findInfoByMail(String email, Date period) {
        UserProfileEntity user = userAccountRepository.findUserEntityByEmailOrFirstAndLastName(email, null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        List<PaymentInfoModel> foundedPayments = userPaymentRepository.findByEmailAndOptionalPeriod(email, period)
                .stream()
                .map(payment -> PaymentInfoModel.builder()
                        .name(user.getName())
                        .lastname(user.getLastName())
                        .period(payment.getPeriod())
                        .salary(this.prepareSalary(payment.getSalary()))
                        .build())
                .toList();

        return ResponseEntity.ok(foundedPayments);
    }

    private String prepareSalary(Double salary) {
        long dollars = salary.longValue();
        long cents = (long) ((salary % 1) * 100);

        return SALARY_FORMAT.formatted(dollars, cents);
    }

}
