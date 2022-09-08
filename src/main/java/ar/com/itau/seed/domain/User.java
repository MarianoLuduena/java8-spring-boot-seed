package ar.com.itau.seed.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class User {

    private static final String ACTIVE_USER_FLAG = "1";

    @NonNull String id;
    @NonNull List<Company> enterprises;

    @Value
    @Builder
    public static class Company {
        @NonNull String id;
        @NonNull String state;
        @NonNull String role;
    }

    public boolean hasCompanyAccess(final String enterpriseId) {
        return enterprises.stream()
                .anyMatch(enterprise -> enterpriseId.equals(enterprise.id)
                        && ACTIVE_USER_FLAG.equals(enterprise.state));
    }

}
