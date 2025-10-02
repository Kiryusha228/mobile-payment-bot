package org.example.model.dto;

import lombok.Data;

@Data
public class PaymentResponseDto {
    private String status;
    private String payment_id;
    private String invoice_id;
    private Double balance;
}