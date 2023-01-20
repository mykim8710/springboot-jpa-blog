package com.mykim.blog.tip.service;

import com.mykim.blog.tip.enums.ExportType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExportServiceFinder {
    private final List<ExportService> exportServices;

    public ExportService findService(String type) {
        return exportServices.stream()
                .filter(exportService -> exportService.getExportType().equals(ExportType.valueOf(type)))
                .findAny()
                .orElseThrow(() -> new RuntimeException("지원하지 않는 형식이거나 잘못된 요청입니다."));
    }

}
