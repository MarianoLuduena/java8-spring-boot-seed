package ar.com.itau.seed.application.port.in;

import ar.com.itau.seed.domain.SWCharacter;

import java.util.concurrent.CompletionStage;

public interface GetSWCharacterByIdQuery {
    CompletionStage<SWCharacter> get(int id);
}
