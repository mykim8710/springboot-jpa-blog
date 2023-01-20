package com.mykim.blog.tip.api;

import com.mykim.blog.tip.enums.ExportType;
import com.mykim.blog.tip.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ExportApiController {
    private final HwpExportService hwpExportService;
    private final PdfExportService pdfExportService;
    private final ExcelExportService excelExportService;

    @GetMapping("/api/v1/export")
    public String exportApiV1(@RequestParam String type) {
        log.info("[GET] /api/v1/export?type={}", type);

        if(type.equals("PDF")) {
            return pdfExportService.export();
        } else if(type.equals("HWP")) {
            return hwpExportService.export();
        } else if(type.equals("EXCEL")) {
            return excelExportService.export();
        } else {
            throw new RuntimeException("지원하지 않는 형식이거나 잘못된 요청입니다.");
        }
    }


    private final List<ExportService> exportServices;

    @GetMapping("/api/v2/export")
    public String exportApiV2(@RequestParam String type) {
        log.info("[GET] /api/v2/export?type={}", type);
        log.info("exportServices={}", exportServices);
        ExportService findService = exportServices.stream()
                                                    .filter(exportService -> exportService.getExportType().equals(ExportType.valueOf(type)))
                                                    .findAny()
                                                    .orElseThrow(() -> new RuntimeException("지원하지 않는 형식이거나 잘못된 요청입니다."));
        return findService.export();
    }

    private final ExportServiceFinder exportServiceFinder;
    @GetMapping("/api/v3/export")
    public String exportApiV3(@RequestParam String type) {
        log.info("[GET] /api/v3/export?type={}", type);
        ExportService service = exportServiceFinder.findService(type);
        return service.export();
    }


    private final Map<String, ExportService> exportServiceMap;

    @GetMapping("/api/v4/export")
    public String exportApiV4(@RequestParam String type) {
        log.info("[GET] /api/v4/export?type={}", type);
        log.info("exportServiceMap={}, key={}", exportServiceMap, exportServiceMap.keySet());

        String key = type.toLowerCase() + "ExportService";
        ExportService exportService = exportServiceMap.get(key);
        return exportService.export();
    }

}
