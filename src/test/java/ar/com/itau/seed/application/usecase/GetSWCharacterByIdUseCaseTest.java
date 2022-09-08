package ar.com.itau.seed.application.usecase;

import ar.com.itau.seed.application.port.out.SWCharacterRepository;
import ar.com.itau.seed.config.ErrorCode;
import ar.com.itau.seed.config.exception.NotFoundException;
import ar.com.itau.seed.domain.SWCharacter;
import ar.com.itau.seed.mock.ExecutorMockFactory;
import ar.com.itau.seed.mock.SWCharacterMockFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@DisplayName("GetSWCharacterById Use Case Test")
class GetSWCharacterByIdUseCaseTest {

    private static final int SW_CHARACTER_ID = 4;
    private static final Executor executor = ExecutorMockFactory.get();

    private final SWCharacterRepository swCharacterRepository = Mockito.mock(SWCharacterRepository.class);

    @Test
    @DisplayName("when get is called it should return a future wrapping a Star Wars character")
    void testGetSWCharacterByIdSuccessfully() {
        final SWCharacter expected = SWCharacterMockFactory.getSWCharacter();
        Mockito.when(swCharacterRepository.getById(SW_CHARACTER_ID))
                .thenReturn(expected);

        final GetSWCharacterByIdUseCase useCase = new GetSWCharacterByIdUseCase(swCharacterRepository, executor);
        final SWCharacter actual = useCase.get(SW_CHARACTER_ID).toCompletableFuture().join();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("when get is called and it fails it should propagate the exception")
    void testGetSWCharacterByIdPropagatesException() {
        final ErrorCode errorCode = ErrorCode.CHARACTER_NOT_FOUND;
        Mockito.when(swCharacterRepository.getById(SW_CHARACTER_ID))
                .thenThrow(new NotFoundException(errorCode));

        final GetSWCharacterByIdUseCase useCase = new GetSWCharacterByIdUseCase(swCharacterRepository, executor);

        final Throwable thrown = Assertions.catchThrowable(
                () -> useCase.get(SW_CHARACTER_ID).toCompletableFuture().join()
        );

        Assertions.assertThat(thrown).isExactlyInstanceOf(CompletionException.class)
                .hasCauseExactlyInstanceOf(NotFoundException.class)
                .getCause().hasMessage(errorCode.getReasonPhrase());
    }

}
