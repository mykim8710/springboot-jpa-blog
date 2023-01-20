package com.mykim.blog.tip.service;

import com.mykim.blog.tip.enums.ExportType;

public interface ExportService {
    String export();
    ExportType getExportType();
}
