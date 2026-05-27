package com.write.api.core.domain.model;

import com.write.api.core.domain.enums.QrCodeFormatEnum;
import com.write.api.core.domain.enums.QrCodeStatusEnum;

import java.time.LocalDateTime;

public class UrlQrCodeModel {

    private Long id;

    private Long urlId;

    private String filePath;

    private String fileUrl;

    private Integer width;

    private Integer height;

    private QrCodeFormatEnum format;

    private QrCodeStatusEnum status;

    private String foregroundColor;

    private String backgroundColor;

    private boolean withLogo;

    private Long generatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}