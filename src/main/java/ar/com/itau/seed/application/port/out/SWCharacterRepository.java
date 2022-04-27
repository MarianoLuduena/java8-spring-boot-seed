package ar.com.itau.seed.application.port.out;

import ar.com.itau.seed.domain.SWCharacter;

public interface SWCharacterRepository {
    SWCharacter getById(int id);
}
