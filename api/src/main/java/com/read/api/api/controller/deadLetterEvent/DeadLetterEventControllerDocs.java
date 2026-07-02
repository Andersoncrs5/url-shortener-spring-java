package com.read.api.api.controller.deadLetterEvent;

import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.deadLetterEvent.DeadLetterEventDTO;
import com.read.api.api.dto.deadLetterEvent.DeadLetterEventFilter;
import com.read.api.utils.validation.isId.IsId;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

public interface DeadLetterEventControllerDocs {

    @GetMapping("/{id}")
    ResponseEntity<ResponseHTTP<DeadLetterEventDTO>> findById(
            @PathVariable @IsId Long id
    );

    @GetMapping
    ResponseEntity<Page<DeadLetterEventDTO>> findAllFilter(
            @ModelAttribute DeadLetterEventFilter filter,
            @ModelAttribute DeadLetterEventPageRequestDTO page
    );

}
