package br.com.socialNetwork.rest.dto.login;

import lombok.Data;

@Data
public class UpdateField {
    private String field;

    public UpdateField(String field, String newValue) {
        this.field = field;
        this.newValue = newValue;
    }

    private String newValue;
}
