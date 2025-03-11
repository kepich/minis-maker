package org.vaadin.example.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.example.service.MiniaturesService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;

import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;

public class PdfViewer extends IFrame {
    private final MiniaturesService miniaturesService;

    public PdfViewer(String fileName, MiniaturesService miniaturesService) {
        this.miniaturesService = miniaturesService;
        setSizeFull();
        StreamResource resource = new StreamResource(fileName,
            () -> new BufferedInputStream(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName))));
        StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
        setSrc(registration.getResourceUri().toString());
    }

    private void createPdf() throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("iTextHelloWorld.pdf"));

        document.open();
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Chunk chunk = new Chunk("Hello World", font);

//        Image img = Image.getInstance();
//        document.add(img);
//        TODO fix

        document.add(chunk);
        document.close();
    }

    private StreamResource createLayout() throws IOException {
        BufferedImage result = new BufferedImage(2480, 3508,  TYPE_4BYTE_ABGR);
        Graphics g = result.createGraphics();

        miniaturesService.miniatures().forEach(miniature -> {
            try {
                g.drawImage(miniature.getCroppedBufferedImage(), 0, 0, null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(result, "png", baos);
        return new StreamResource("result", (InputStreamFactory) () -> new ByteArrayInputStream(baos.toByteArray()));
    }
}
