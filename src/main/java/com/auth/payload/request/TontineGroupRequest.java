package com.auth.payload.request;

import com.auth.models.Currency;
import com.auth.models.Frequency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TontineGroupRequest {
    @NotBlank
    private String name;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    @NotNull
    private Frequency frequency;
}