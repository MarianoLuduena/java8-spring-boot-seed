package ar.com.itau.seed.adapter.controller.model;

import ar.com.itau.seed.domain.SWCharacter;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class SWCharacterControllerModel {

    @NonNull String name;
    Integer height;
    Integer mass;
    @NonNull String hairColor;
    @NonNull String eyeColor;
    @NonNull String birthYear;
    @NonNull String gender;
    @NonNull LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static SWCharacterControllerModel from(SWCharacter domain) {
        return SWCharacterControllerModel.builder()
                .name(domain.getName())
                .height(domain.getHeight())
                .mass(domain.getMass())
                .hairColor(domain.getHairColor())
                .eyeColor(domain.getEyeColor())
                .birthYear(domain.getBirthYear())
                .gender(domain.getGender())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

}
