package ar.com.itau.seed.domain;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class SWCharacter {
    @NonNull String name;
    Integer height;
    Integer mass;
    @NonNull String hairColor;
    @NonNull String eyeColor;
    @NonNull String birthYear;
    @NonNull String gender;
    @NonNull LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
