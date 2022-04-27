package ar.com.itau.seed.adapter.controller;

import ar.com.itau.seed.adapter.controller.model.SWCharacterControllerModel;
import ar.com.itau.seed.application.port.in.GetSWCharacterByIdQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.concurrent.CompletionStage;

@RestController
@RequestMapping("/api/v1/characters")
@Slf4j
@Validated
public class SWCharacterControllerAdapter {

    private final GetSWCharacterByIdQuery getSWCharacterByIdQuery;

    public SWCharacterControllerAdapter(GetSWCharacterByIdQuery getSWCharacterByIdQuery) {
        this.getSWCharacterByIdQuery = getSWCharacterByIdQuery;
    }

    @GetMapping("/{id}")
    public CompletionStage<SWCharacterControllerModel> get(
            @NotNull @Positive @PathVariable("id") final Integer id
    ) {
        log.info("Call to get character by ID {}", id);
        return getSWCharacterByIdQuery.get(id)
                .thenApply(domain -> {
                    final SWCharacterControllerModel response = SWCharacterControllerModel.from(domain);
                    log.info("Replying to get character by ID request with {}", response);
                    return response;
                });
    }

}
