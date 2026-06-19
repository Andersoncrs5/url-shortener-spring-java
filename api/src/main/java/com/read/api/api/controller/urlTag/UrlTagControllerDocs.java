package com.read.api.api.controller.urlTag;

import com.read.api.api.controller.base.DefaultApiResponses;
import com.read.api.api.controller.base.JwtProtected;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.tag.UrlTagDTO;
import com.read.api.api.dto.tag.UrlTagFilter;
import com.read.api.utils.validation.isId.IsId;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Url Tag")
@JwtProtected
@DefaultApiResponses
public interface UrlTagControllerDocs {
    @GetMapping
    ResponseEntity<Page<UrlTagDTO>> findAllFilter(
            @ModelAttribute UrlTagFilter filter,
            @ModelAttribute UrlTagPageRequestDTO page
    );

    @GetMapping("/{id}")
    ResponseEntity<ResponseHTTP<UrlTagDTO>> findById(
            @PathVariable @IsId Long id
    );

    @GetMapping("/name-exists")
    ResponseEntity<ResponseHTTP<Boolean>> nameExists(
            @RequestParam String name
    );

    @GetMapping("/slug-exists")
    ResponseEntity<ResponseHTTP<Boolean>> slugExists(
            @RequestParam String slug
    );
}
