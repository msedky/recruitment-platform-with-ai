package com.recruitment.platform.service.impl;

import com.recruitment.platform.exception.CvParseException;
import com.recruitment.platform.service.CvParserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class CvParserServiceImpl implements CvParserService {

    private static final String PDF_CONTENT_TYPE = "application/pdf";
    private static final String DOCX_CONTENT_TYPE =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

    @Override
    public String extractText(Resource resource, String contentType) {
        if (PDF_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            return extractFromPdf(resource);
        } else if (DOCX_CONTENT_TYPE.equalsIgnoreCase(contentType)) {
            return extractFromDocx(resource);
        } else {
            throw new CvParseException("Unsupported content type: " + contentType, null);
        }
    }

    private String extractFromPdf(Resource resource) {
        // PDFBox 3.x API — Loader.loadPDF(byte[]) replaces PDDocument.load(InputStream)
        // PDFBox 3.x removed direct InputStream loading — requires byte array
        try (InputStream inputStream = resource.getInputStream();
             PDDocument document = Loader.loadPDF(inputStream.readAllBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.info("Successfully extracted text from PDF: {}", resource.getFilename());
            return text;

        } catch (IOException e) {
            log.error("Failed to extract text from PDF: {}", e.getMessage());
            throw new CvParseException("Failed to extract text from PDF", e);
        }
    }

    private String extractFromDocx(Resource resource) {
        try (InputStream inputStream = resource.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {

            List<XWPFParagraph> paragraphs = document.getParagraphs();
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph paragraph : paragraphs) {
                String text = paragraph.getText();
                if (text != null && !text.isBlank()) {
                    sb.append(text).append("\n");
                }
            }
            log.info("Successfully extracted text from DOCX: {}", resource.getFilename());
            return sb.toString();

        } catch (IOException e) {
            log.error("Failed to extract text from DOCX: {}", e.getMessage());
            throw new CvParseException("Failed to extract text from DOCX", e);
        }
    }
}