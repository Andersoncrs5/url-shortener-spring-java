package com.write.api.application.dto.urlAccessRule;

import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;

public record UrlAccessRuleFilterDTO(

        Long urlId,

        UrlAccessRuleTypeEnum type,

        Boolean active

) {
}