package com.read.api.api.dto.url;

import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.ContinentEnum;
import com.read.api.domain.enums.OperatingSystemEnum;

import java.util.Optional;

public record AccessContextDTO(
        Optional<String> ip,
        Optional<String> countryCode,
        Optional<String> region,
        Optional<ContinentEnum> continent,
        Optional<OperatingSystemEnum> os,
        Optional<BrowserEnum> browser,
        Optional<Boolean> authenticated,
        Optional<String> password
) {}
