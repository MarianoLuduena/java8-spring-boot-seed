package ar.com.itau.seed.adapter.rest.model.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSearchRestModel {
    private List<UserRestModel> users;
}
