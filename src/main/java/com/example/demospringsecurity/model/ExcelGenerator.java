package com.example.demospringsecurity.model;

import com.example.demospringsecurity.dto.ReportUserDto;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public class ExcelGenerator {
    private ReportUserDto reportCurrentUser;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public ExcelGenerator(ReportUserDto reportCurrentUser) {
        this.reportCurrentUser = reportCurrentUser;
        workbook = new XSSFWorkbook();
    }

    private void writeHeader() {
        sheet = workbook.createSheet("Report");
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        createCell(row,
                0,
                "Username",
                style);
        createCell(row,
                1,
                "Number of posts written last week",
                style);
        createCell(row,
                2,
                "Number of new friends",
                style);
        createCell(row,
                3,
                "new number of likes",
                style);
        createCell(row,
                4,
                "new number of comments",
                style);
    }

    private void createCell(Row row, int columnCount, Object valueOfCell, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (valueOfCell instanceof Integer) {
            cell.setCellValue((Integer) valueOfCell);
        } else if (valueOfCell instanceof Long) {
            cell.setCellValue((Long) valueOfCell);
        } else if (valueOfCell instanceof String) {
            cell.setCellValue((String) valueOfCell);
        } else {
            cell.setCellValue((Boolean) valueOfCell);
        }
        cell.setCellStyle(style);
    }

    private void write() {
        Row row = sheet.createRow(1);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);
//        int columnCount = 0;
        createCell(row,
                0,
                reportCurrentUser.getUserName(),
                style);
        createCell(row,
                1,
                reportCurrentUser.getPostsLastWeek(),
                style);
        createCell(row,
                2,
                reportCurrentUser.getNewFriendLastWeek(),
                style);
        createCell(row,
                3,
                reportCurrentUser.getNewLikesLastWeek(),
                style);
        createCell(row,
                4,
                reportCurrentUser.getNewCommentsLastWeek(),
                style);
    }

    public void generateExcelFile(HttpServletResponse response) throws IOException {
        writeHeader();
        write();
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
