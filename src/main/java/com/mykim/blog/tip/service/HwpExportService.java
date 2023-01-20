package com.mykim.blog.tip.service;

import com.mykim.blog.tip.enums.ExportType;
import org.springframework.stereotype.Service;

import static com.mykim.blog.tip.enums.ExportType.HWP;

@Service
public class HwpExportService implements ExportService {
    @Override
    public String export() {
        // logic...
        return "한글파일";
    }

    @Override
    public ExportType getExportType() {
        return HWP;
    }
}
