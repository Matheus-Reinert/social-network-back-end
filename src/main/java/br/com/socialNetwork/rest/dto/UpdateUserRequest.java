package br.com.socialNetwork.rest.dto;


import lombok.Data;
import java.util.List;

@Data
public class UpdateUserRequest {
    private List<UpdateField> updateFields;

    public UpdateUserRequest(){

    }

    public UpdateUserRequest(List<UpdateField> updateFields){
        this.updateFields = updateFields;
    }
}
