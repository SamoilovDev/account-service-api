package com.example.Account.Service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;


@Data
@AllArgsConstructor
public class PaymentInfoModel {

    private String name;

    private String lastname;

    private Date period;

    private String salary;


}
