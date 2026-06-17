package com.read.api.api.controller.url;

import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.url.AccessContextDTO;
import com.read.api.api.dto.url.UrlDTO;
import com.read.api.api.dto.url.UrlFilter;
import com.read.api.application.usecase.interfaces.url.FindAllUrlUseCase;
import com.read.api.application.usecase.interfaces.url.FindUrlByIdUseCase;
import com.read.api.application.usecase.interfaces.url.FindUrlByShortCodeUseCase;
import com.read.api.domain.enums.BrowserEnum;
import com.read.api.domain.enums.OperatingSystemEnum;
import com.read.api.domain.model.UrlModel;
import com.read.api.utils.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/url")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UrlController implements UrlControllerDocs {

    UrlMapperController mapper;
    FindAllUrlUseCase findAll;
    FindUrlByShortCodeUseCase findByShortCode;
    FindUrlByIdUseCase findById;

    @Override
    public ResponseEntity<ResponseHTTP<UrlDTO>> findById(Long id) {
        Result<UrlModel> result = findById.execute(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHTTP.error(result.getMessage()));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHTTP.success(
                                mapper.toDTO(result.getValue()),
                                "Url found"
                        )
                );
    }

    @Override
    public ResponseEntity<Page<UrlDTO>> findAllFilter(
            UrlFilter filter,
            UrlPageRequestDTO page
    ) {
        Page<UrlModel> result = findAll.execute(filter, page.toPageable());

        var items = result.map(mapper::toDTO);

        return ResponseEntity.ok(items);
    }

    @Override
    public ResponseEntity<ResponseHTTP<UrlDTO>> redirectShortCode(
            String shortCode,
            String userAgent,
            String password,
            HttpServletRequest request
    ) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        OperatingSystemEnum os = parseOperatingSystem(userAgent);
        BrowserEnum browser = parseBrowser(userAgent);

        AccessContextDTO accessContext = new AccessContextDTO(
                Optional.ofNullable(ip),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.ofNullable(os),
                Optional.ofNullable(browser),
                Optional.of(request.getUserPrincipal() != null),
                Optional.ofNullable(password)
        );

        Result<UrlModel> result = findByShortCode.execute(shortCode, accessContext);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHTTP.error(result.getMessage()));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(ResponseHTTP.success(mapper.toDTO(result.getValue()), "Redirect successful"));
    }

    private OperatingSystemEnum parseOperatingSystem(String userAgent) {
        if (userAgent == null) return null;
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) return OperatingSystemEnum.WINDOWS;
        if (ua.contains("mac")) return OperatingSystemEnum.MACOS;
        if (ua.contains("linux")) return OperatingSystemEnum.LINUX;
        if (ua.contains("android")) return OperatingSystemEnum.ANDROID;
        if (ua.contains("iphone") || ua.contains("ipad")) return OperatingSystemEnum.IOS;
        return null;
    }

    private BrowserEnum parseBrowser(String userAgent) {
        if (userAgent == null) return null;
        String ua = userAgent.toLowerCase();
        if (ua.contains("chrome")) return BrowserEnum.CHROME;
        if (ua.contains("firefox")) return BrowserEnum.FIREFOX;
        if (ua.contains("safari") && !ua.contains("chrome")) return BrowserEnum.SAFARI;
        if (ua.contains("edge")) return BrowserEnum.EDGE;
        return null;
    }

}