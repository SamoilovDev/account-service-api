package com.samoilov.dev.account.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoModel {

    private String name;

    private String lastname;

    private Date period;

    private String salary;

}
