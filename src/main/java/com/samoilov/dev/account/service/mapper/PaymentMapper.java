package com.samoilov.dev.account.service.mapper;

import com.samoilov.dev.account.service.dto.PaymentDto;
import com.samoilov.dev.account.service.entity.PaymentEntity;
import com.samoilov.dev.account.service.entity.UserProfileEntity;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PaymentMapper {

    public PaymentEntity mapPaymentDtoToEntity(PaymentDto paymentDto, @Nullable UserProfileEntity userProfileEntity) {
        return Optional.ofNullable(paymentDto)
                .map(pd -> PaymentEntity.builder()
                        .id(pd.getId())
                        .user(userProfileEntity)
                        .userEmail(pd.getUserEmail())
                        .salary(pd.getSalary())
                        .period(pd.getPeriod())
                        .build())
                .orElse(null);
    }

}
