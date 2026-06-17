package com.read.api.api.controller.urlRedirectRule;

import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleFilter;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

public interface UrlRedirectRuleControllerDocs {
    @GetMapping
    ResponseEntity<Page<UrlRedirectRuleDTO>> findAllFilter(
            @ModelAttribute UrlRedirectRuleFilter filter,
            @ModelAttribute UrlRedirectRulePageRequestDTO page
    );

    @GetMapping("/{id}")
    ResponseEntity<ResponseHTTP<UrlRedirectRuleDTO>> findById(
            @PathVariable Long id
    );
}
