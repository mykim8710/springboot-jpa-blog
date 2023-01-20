package com.mykim.blog.tip.service;

import com.mykim.blog.tip.enums.ExportType;
import org.springframework.stereotype.Service;

import static com.mykim.blog.tip.enums.ExportType.PDF;

@Service
public class PdfExportService implements ExportService {
    @Override
    public String export() {
        // logic...
        return "PDF파일";
    }

    @Override
    public ExportType getExportType() {
        return PDF;
    }
}
