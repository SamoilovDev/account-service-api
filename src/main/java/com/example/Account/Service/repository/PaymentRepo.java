package com.example.Account.Service.repository;

import com.example.Account.Service.entity.PaymentEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface PaymentRepo extends CrudRepository<PaymentEntity, Long> {
    Optional<PaymentEntity> findByUserEmailIgnoreCase(String userEmail);

    Optional<PaymentEntity> findByUserEmailAndPeriod(String email, Date period);

    List<PaymentEntity> findAllByUserEmail(String userEmail);
}
