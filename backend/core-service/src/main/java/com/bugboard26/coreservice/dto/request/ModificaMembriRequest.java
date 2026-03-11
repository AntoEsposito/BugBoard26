package com.bugboard26.coreservice.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ModificaMembriRequest
{
    @NotEmpty
    private List<Integer> idUtenti;
}
