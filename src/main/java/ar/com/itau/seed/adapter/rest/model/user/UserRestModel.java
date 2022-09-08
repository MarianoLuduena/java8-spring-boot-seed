package ar.com.itau.seed.adapter.rest.model.user;

import ar.com.itau.seed.domain.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRestModel {

    private String id;
    private String documentNumber;
    private String documentType;
    private String username;
    private List<UserCompanyRestModel> enterprises;

    public User toDomain() {
        return User.builder()
                .id(id)
                .enterprises(enterprises.stream().map(UserCompanyRestModel::toDomain).collect(Collectors.toList()))
                .build();
    }

}
