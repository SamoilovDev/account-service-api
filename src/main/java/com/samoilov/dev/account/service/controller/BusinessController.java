package com.samoilov.dev.account.service.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import com.samoilov.dev.account.service.dto.PaymentDto;
import com.samoilov.dev.account.service.dto.ResponseStatusDto;
import com.samoilov.dev.account.service.entity.PaymentEntity;
import com.samoilov.dev.account.service.entity.UserProfileEntity;
import com.samoilov.dev.account.service.model.PaymentInfoModel;
import com.samoilov.dev.account.service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BusinessController {

    private final PaymentService paymentService;

    @Operation(summary = "Get a list of payments for a specific period", responses = {
            @ApiResponse(responseCode = "200", description = "Found the payments",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = PaymentInfoModel.class),
                                    arraySchema = @Schema(implementation = List.class)
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid date supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payments not found", content = @Content)
    })
    @GetMapping("/empl/payment")
    public ResponseEntity<List<PaymentInfoModel>> getPayment(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "MM-yyyy", iso = DateTimeFormat.ISO.DATE)
            @JsonFormat(pattern = "MM-yyyy", lenient = OptBoolean.FALSE)
            Date period,
            Principal user) {
        return paymentService.findInfoByMail(user.getName(), period);
    }

    @Operation(summary = "Add a list of payments", responses = {
            @ApiResponse(responseCode = "201", description = "Payments created", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid payment details supplied", content = @Content)
    })
    @PostMapping("/acct/payments")
    public ResponseEntity<ResponseStatusDto> downloadPayments(
            @Valid @RequestBody List<PaymentDto> payments,
            @AuthenticationPrincipal UserProfileEntity userProfileEntity) {
        return paymentService.addPayments(payments, userProfileEntity);
    }

    @Operation(summary = "Update a payment", responses = {
            @ApiResponse(responseCode = "200", description = "Payment updated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid payment details supplied", content = @Content),
            @ApiResponse(responseCode = "404", description = "Payment not found", content = @Content)
    })
    @PutMapping("/acct/payments")
    public ResponseEntity<ResponseStatusDto> updatePayments(@Valid @RequestBody PaymentEntity payment) {
        return paymentService.updatePayment(payment);
    }

}