package ar.com.itau.seed.adapter.rest.model;

import ar.com.itau.seed.domain.SWCharacter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Value
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SWCharacterRestModel {

    String name;
    String height;
    String mass;
    String hairColor;
    String skinColor;
    String eyeColor;
    String birthYear;
    String gender;
    @JsonProperty("homeworld") String homeWorld;
    List<String> films;
    LocalDateTime created;
    LocalDateTime edited;

    public SWCharacter toDomain() {
        final Integer nullableHeight =
                Optional.ofNullable(height).filter(it -> height.chars().allMatch(Character::isDigit))
                        .map(Integer::valueOf)
                        .orElse(null);
        final Integer nullableMass =
                Optional.ofNullable(mass).filter(it -> mass.chars().allMatch(Character::isDigit))
                        .map(Integer::valueOf)
                        .orElse(null);
        return SWCharacter.builder()
                .name(name)
                .height(nullableHeight)
                .mass(nullableMass)
                .hairColor(hairColor)
                .eyeColor(eyeColor)
                .birthYear(birthYear)
                .gender(gender)
                .createdAt(created)
                .updatedAt(edited)
                .build();
    }

}
