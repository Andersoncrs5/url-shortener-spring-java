package com.read.api.api.controller.deadLetterEvent;

import com.read.api.api.controller.base.RestApiController;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.deadLetterEvent.DeadLetterEventDTO;
import com.read.api.api.dto.deadLetterEvent.DeadLetterEventFilter;
import com.read.api.application.usecase.interfaces.deadLetterEvent.FindAllDeadLetterEventUseCase;
import com.read.api.application.usecase.interfaces.deadLetterEvent.FindDeadLetterEventByIdUseCase;
import com.read.api.domain.model.DeadLetterEventModel;
import com.read.api.utils.annotation.ratelimit.RateLimited;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

@RequiredArgsConstructor
@RestApiController("v1/dead-letter-event")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeadLetterEventController implements DeadLetterEventControllerDocs {

    DeadLetterEventMapperController mapper;
    FindAllDeadLetterEventUseCase findAll;
    FindDeadLetterEventByIdUseCase findById;

    @Override
    @RateLimited("read-low")
    public ResponseEntity<Page<DeadLetterEventDTO>> findAllFilter(
            DeadLetterEventFilter filter,
            DeadLetterEventPageRequestDTO page
    ) {
        Page<DeadLetterEventModel> result = findAll.execute(filter, page.toPageable());

        var items = result.map(mapper::toDTO);

        return ResponseEntity.ok(items);
    }

    @Override
    @RateLimited("read-strong")
    public ResponseEntity<ResponseHTTP<DeadLetterEventDTO>> findById(Long id) {
        Result<DeadLetterEventModel> result = findById.execute(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHTTP.error(result.getMessage()));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHTTP.success(
                                mapper.toDTO(result.getValue()),
                                "Dead Letter Event found"
                        )
                );
    }

}
