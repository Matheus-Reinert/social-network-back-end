package br.com.socialNetwork.rest.dto;

import lombok.Data;

@Data
public class UpdateField {
    private String field;
    private String newValue;
}
