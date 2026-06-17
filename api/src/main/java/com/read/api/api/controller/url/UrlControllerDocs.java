package com.read.api.api.controller.url;

import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.url.UrlDTO;
import com.read.api.api.dto.url.UrlFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

public interface UrlControllerDocs {
    @GetMapping("/{id}")
    ResponseEntity<ResponseHTTP<UrlDTO>> findById(
            @PathVariable Long id
    );

    @GetMapping
    ResponseEntity<Page<UrlDTO>> findAllFilter(
            @ModelAttribute UrlFilter filter,
            @ModelAttribute UrlPageRequestDTO page
    );

    @GetMapping("/r/{shortCode}")
    ResponseEntity<ResponseHTTP<UrlDTO>> redirectShortCode(
            @PathVariable String shortCode,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Url-Password", required = false) String password,
            HttpServletRequest request
    );
}
