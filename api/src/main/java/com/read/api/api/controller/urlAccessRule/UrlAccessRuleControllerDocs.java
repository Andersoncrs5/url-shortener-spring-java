package com.read.api.api.controller.urlAccessRule;

import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.urlAccessRule.UrlAccessRuleDTO;
import com.read.api.api.dto.urlAccessRule.UrlAccessRuleFilter;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface UrlAccessRuleControllerDocs {
    @GetMapping("/{id}")
    ResponseEntity<ResponseHTTP<UrlAccessRuleDTO>> findById(
            @PathVariable Long id
    );

    @GetMapping
    ResponseEntity<Page<UrlAccessRuleDTO>> findAllFilter(
            @ModelAttribute UrlAccessRuleFilter filter,
            @ModelAttribute UrlAccessRulePageRequestDTO page
    );

    @GetMapping("/exists")
    ResponseEntity<ResponseHTTP<Boolean>> exists(
            @RequestParam Long urlId,
            @RequestParam UrlAccessRuleTypeEnum type,
            @RequestParam String ruleValue
    );
}
