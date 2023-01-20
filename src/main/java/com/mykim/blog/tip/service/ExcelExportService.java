package com.mykim.blog.tip.service;

import com.mykim.blog.tip.enums.ExportType;
import org.springframework.stereotype.Service;

import static com.mykim.blog.tip.enums.ExportType.EXCEL;

@Service
public class ExcelExportService implements ExportService {
    @Override
    public String export() {
        // logic...
        return "EXCEL파일";
    }

    @Override
    public ExportType getExportType() {
        return EXCEL;
    }
}
