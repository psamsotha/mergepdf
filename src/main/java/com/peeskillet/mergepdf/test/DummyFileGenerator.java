package com.peeskillet.mergepdf.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


public class DummyFileGenerator {

    public static void main(String[] args) throws DocumentException, IOException {
        createPdf("data/one.pdf", "ONE ONE ONE");
        createPdf("data/two.pdf", "TWO TWO TWO");
        createPdf("data/three.pdf", "THREE THREE THREE");
    }

    private static void createPdf(String filename, String data) throws DocumentException, IOException {

        Document document = new Document();

        File outputFile = new File(filename);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null) {
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
        }

        PdfWriter.getInstance(document, new FileOutputStream(filename));
        document.open();
        document.add(new Paragraph(data));
        document.close();
    }
}
