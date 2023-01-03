package com.example.Account.Service.service;

import com.example.Account.Service.entity.PaymentEntity;
import com.example.Account.Service.entity.UserEntity;
import com.example.Account.Service.model.PaymentInfoModel;
import com.example.Account.Service.repository.PaymentRepo;
import com.example.Account.Service.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private UserRepo userRepo;

    public Map<String, String> addPayments(List<PaymentEntity> paymentEntities) {

        paymentEntities.forEach( payment -> {
            Optional<UserEntity> user = userRepo.findUserEntityByEmailIgnoreCase(payment.getUserEmail());
            user.ifPresent(userEntity -> {
                userEntity.getPayments().add(payment);
                userRepo.save(userEntity);
            });
            if (user.isEmpty()) paymentRepo.save(payment);
        });

        paymentRepo.saveAll(paymentEntities);
        return Map.of("status", "Added successfully!");
    }

    public Map<String, String> updatePayment(PaymentEntity payment) {
        if (paymentRepo.findByUserEmailIgnoreCase(payment.getUserEmail()).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee doesn't exist!");
        } else {
            UserEntity user = userRepo.findUserEntityByEmailIgnoreCase(payment.getUserEmail()).get();
            user.getPayments().add(payment);
            userRepo.save(user);
            return Map.of("status", "Updated successfully!");
        }
    }

    public List<PaymentInfoModel> findInfoByMail(String email, Date period) {
        List<PaymentEntity> foundedPayments = Objects.equals(period, null)
                ? paymentRepo.findAllByUserEmail(email)
                : List.of(paymentRepo.findByUserEmailAndPeriod(email, period).get());

        if (userRepo.findUserEntityByEmailIgnoreCase(email).isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);


        UserEntity user = userRepo.findUserEntityByEmailIgnoreCase(email).get();

        return foundedPayments.stream()
                .map(employee -> new PaymentInfoModel(user.getName(),
                    user.getLastName(),
                    employee.getPeriod(),
                    prepareSalary(employee.getSalary())
                ))
                .toList();
    }

    private String prepareSalary(Double salary) {
        long dollars = salary.longValue();
        int cents = (int) ((salary % 1) * 100);
        return "%d dollar(s) %d cent(s)".formatted(dollars, cents);
    }

}
