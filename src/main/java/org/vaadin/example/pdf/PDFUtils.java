package org.vaadin.example.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public final class PDFUtils {
    private PDFUtils() {
    }

    @SneakyThrows
    public static StreamResource createPDFStreamResource(List<byte[]> imageBytes) {
        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 0, 0, 0, 0);
        PdfWriter.getInstance(document, pdfStream);
        document.open();
        for (byte[] bytes : imageBytes) {
            Image image = Image.getInstance(bytes);
            image.scalePercent(24F, 24F);
            document.add(image);
        }
        document.close();

        return new StreamResource("result.pdf", (InputStreamFactory) () -> new ByteArrayInputStream(pdfStream.toByteArray()));
    }
}
