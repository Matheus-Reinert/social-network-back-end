package br.com.socialNetwork.rest.dto.login;

import lombok.Data;

@Data
public class UpdateField {
    private String field;
    private String newValue;
}
