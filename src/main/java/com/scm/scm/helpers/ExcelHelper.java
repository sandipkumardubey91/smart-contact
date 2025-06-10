package com.scm.scm.helpers;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.scm.scm.entities.Contact;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelHelper {

    public static void exportToExcel(List<Contact> contacts, ByteArrayOutputStream byteArrayOutputStream)
            throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("contacts");

        // Header
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Name");
        headerRow.createCell(1).setCellValue("Email");
        headerRow.createCell(2).setCellValue("Phone Number");
        headerRow.createCell(3).setCellValue("Address");
        headerRow.createCell(4).setCellValue("Picture");
        headerRow.createCell(5).setCellValue("Description");
        headerRow.createCell(6).setCellValue("Website Link");
        headerRow.createCell(7).setCellValue("LinkedIn Link");
        headerRow.createCell(8).setCellValue("Cloudinary Image PublicId");

        // Data rows
        int rowNum = 1;
        for (Contact contact : contacts) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(contact.getName());
            row.createCell(1).setCellValue(contact.getEmail());
            row.createCell(2).setCellValue(contact.getPhoneNumber());
            row.createCell(3).setCellValue(contact.getAddress());
            row.createCell(4).setCellValue(contact.getPicture());
            row.createCell(5).setCellValue(contact.getDescription());
            row.createCell(6).setCellValue(contact.getWebsiteLink());
            row.createCell(7).setCellValue(contact.getLinkedInLink());
            row.createCell(8).setCellValue(contact.getCloudinaryImagePublicId());
        }

        // Write to the ByteArrayOutputStream
        workbook.write(byteArrayOutputStream);
        workbook.close();
    }

    public static String getCellValue(Row row, int cellIndex) {
        try {
            Cell cell = row.getCell(cellIndex);
            return cell != null ? cell.toString().trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

}
