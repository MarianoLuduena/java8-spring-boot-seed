package ar.com.itau.seed.application.usecase;

import ar.com.itau.seed.application.port.in.GetSWCharacterByIdQuery;
import ar.com.itau.seed.application.port.out.SWCharacterRepository;
import ar.com.itau.seed.domain.SWCharacter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

@Component
@Slf4j
public class GetSWCharacterByIdUseCase implements GetSWCharacterByIdQuery {

    private final SWCharacterRepository swCharacterRepository;
    private final Executor executor;

    public GetSWCharacterByIdUseCase(
            SWCharacterRepository swCharacterRepository,
            @Qualifier("asyncExecutor") Executor executor
    ) {
        this.swCharacterRepository = swCharacterRepository;
        this.executor = executor;
    }

    @Override
    public CompletionStage<SWCharacter> get(int id) {
        return CompletableFuture.supplyAsync(() -> doGet(id), executor);
    }

    private SWCharacter doGet(int id) {
        log.info("Calling repository to get character with ID {}", id);
        final SWCharacter swCharacter = swCharacterRepository.getById(id);
        log.info("Got {} from repository", swCharacter);
        return swCharacter;
    }

}
