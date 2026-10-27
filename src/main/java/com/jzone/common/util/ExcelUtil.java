package com.jzone.common.util;

import cn.hutool.core.collection.CollUtil;
import com.jzone.framework.exception.BusinessException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 轻量工具类（基于 Apache POI，仅支持 xlsx）
 */
public final class ExcelUtil {

    private ExcelUtil() {}

    /**
     * 导出 xlsx 文件到浏览器
     *
     * @param response HttpServletResponse
     * @param fileName 下载文件名（不带扩展名）
     * @param sheetName Sheet 名称
     * @param headers  表头
     * @param rows     数据行
     */
    public static void exportXlsx(HttpServletResponse response,
                                  String fileName,
                                  String sheetName,
                                  List<String> headers,
                                  List<List<String>> rows) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // 表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // 写表头
            Row headerRow = sheet.createRow(0);
            if (CollUtil.isNotEmpty(headers)) {
                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers.get(i));
                    cell.setCellStyle(headerStyle);
                }
            }

            // 写数据
            if (CollUtil.isNotEmpty(rows)) {
                for (int i = 0; i < rows.size(); i++) {
                    Row row = sheet.createRow(i + 1);
                    List<String> rowData = rows.get(i);
                    for (int j = 0; j < rowData.size(); j++) {
                        row.createCell(j).setCellValue(rowData.get(j) == null ? "" : rowData.get(j));
                    }
                }
            }

            // 自适应列宽
            for (int i = 0; i < (CollUtil.isNotEmpty(headers) ? headers.size() : 0); i++) {
                sheet.autoSizeColumn(i);
                // 最小宽度 10，最大 50
                int width = sheet.getColumnWidth(i);
                width = Math.max(width, 256 * 10);
                width = Math.min(width, 256 * 50);
                sheet.setColumnWidth(i, width);
            }

            setExcelResponse(response, fileName);
            try (ServletOutputStream out = response.getOutputStream()) {
                workbook.write(out);
                out.flush();
            }
        } catch (IOException e) {
            throw new BusinessException("导出 Excel 失败: " + e.getMessage());
        }
    }

    /**
     * 读取 Excel 文件，支持 xls 和 xlsx 格式，返回所有行（第0行为表头）
     */
    public static List<List<String>> readXlsx(InputStream inputStream) {
        List<List<String>> result = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return result;
            }
            DataFormatter formatter = new DataFormatter();
            for (Row row : sheet) {
                List<String> rowData = new ArrayList<>();
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                    rowData.add(cell == null ? "" : formatter.formatCellValue(cell));
                }
                result.add(rowData);
            }
        } catch (IOException e) {
            throw new BusinessException("读取 Excel 失败: " + e.getMessage());
        }
        return result;
    }

    private static void setExcelResponse(HttpServletResponse response, String fileName) throws IOException {
        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.reset();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedName + ".xlsx");
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
    }
}
