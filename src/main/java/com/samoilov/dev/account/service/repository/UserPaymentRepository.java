package com.samoilov.dev.account.service.repository;

import com.samoilov.dev.account.service.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface UserPaymentRepository extends JpaRepository<PaymentEntity, Long> {

    @Query("SELECT p FROM PaymentEntity p WHERE p.userEmail = :email AND (:period IS NULL OR p.period = :period)")
    List<PaymentEntity> findByEmailAndOptionalPeriod(@Param("email") String email, @Param("period") Date period);
}