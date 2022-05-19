package ar.com.itau.seed.adapter.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserPermissionCheckRestModel {
    Boolean result;
}
