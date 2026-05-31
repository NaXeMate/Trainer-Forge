package dev.trainerforge.dto.input;

import jakarta.validation.constraints.NotBlank;

public record LoginInputDto(
    @NotBlank String username,
    @NotBlank String password
) {}
