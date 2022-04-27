package ar.com.itau.seed.mock;

import ar.com.itau.seed.adapter.controller.model.SWCharacterControllerModel;
import ar.com.itau.seed.adapter.rest.model.SWCharacterRestModel;
import ar.com.itau.seed.domain.SWCharacter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SWCharacterMockFactory {

    private static final String NAME = "Darth Vader";
    private static final int HEIGHT = 202;
    private static final int MASS = 136;
    private static final String HAIR_COLOR = "none";
    private static final String SKIN_COLOR = "white";
    private static final String EYE_COLOR = "yellow";
    private static final String BIRTH_YEAR = "41.9BBY";
    private static final String GENDER = "male";
    private static final String HOME_WORLD = "https://swapi.dev/api/planets/1/";
    private static final LocalDateTime CREATED_AT = LocalDateTime.parse("2014-12-10T15:18:20.704000");
    private static final LocalDateTime UPDATED_AT = LocalDateTime.parse("2014-12-20T21:17:50.313000");

    private static final List<String> FILMS = Collections.unmodifiableList(
            Arrays.asList(
                    "https://swapi.dev/api/films/1/",
                    "https://swapi.dev/api/films/2/",
                    "https://swapi.dev/api/films/3/",
                    "https://swapi.dev/api/films/6/"
            )
    );

    public static SWCharacterControllerModel getSWCharacterControllerModel() {
        return SWCharacterControllerModel.builder()
                .name(NAME)
                .height(HEIGHT)
                .mass(MASS)
                .hairColor(HAIR_COLOR)
                .eyeColor(EYE_COLOR)
                .birthYear(BIRTH_YEAR)
                .gender(GENDER)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    public static SWCharacter getSWCharacter() {
        return SWCharacter.builder()
                .name(NAME)
                .height(HEIGHT)
                .mass(MASS)
                .hairColor(HAIR_COLOR)
                .eyeColor(EYE_COLOR)
                .birthYear(BIRTH_YEAR)
                .gender(GENDER)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .build();
    }

    public static SWCharacterRestModel getSWCharacterRestModel() {
        return SWCharacterRestModel.builder()
                .name(NAME)
                .height(String.valueOf(HEIGHT))
                .mass(String.valueOf(MASS))
                .hairColor(HAIR_COLOR)
                .skinColor(SKIN_COLOR)
                .eyeColor(EYE_COLOR)
                .birthYear(BIRTH_YEAR)
                .gender(GENDER)
                .homeWorld(HOME_WORLD)
                .films(FILMS)
                .created(CREATED_AT)
                .edited(UPDATED_AT)
                .build();
    }

}
