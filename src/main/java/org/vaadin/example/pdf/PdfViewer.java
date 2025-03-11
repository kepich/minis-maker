package org.vaadin.example.pdf;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamRegistration;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import org.vaadin.example.Miniature;
import org.vaadin.example.service.MiniaturesService;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;

public class PdfViewer extends Image {
    private final MiniaturesService miniaturesService;

    public PdfViewer(MiniaturesService miniaturesService) throws IOException {
        this.miniaturesService = miniaturesService;
        setWidthFull();
        update();
    }

//    private void createPdf(BufferedImage image) throws IOException, DocumentException {
//        Document document = new Document();
//        PdfWriter.getInstance(document, new FileOutputStream("iTextHelloWorld.pdf"));
//
//        document.open();
//        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
//        Chunk chunk = new Chunk("Hello World", font);
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ImageIO.write(image, "png", baos);
//
//        Image img = Image.getInstance(baos.toByteArray());
//        document.add(img);
////        TODO fix
//
//        document.add(chunk);
//        document.close();
//    }

    private StreamResource createLayout() throws IOException {
        BufferedImage result = new BufferedImage(2480, 3508, TYPE_4BYTE_ABGR);
        Graphics g = result.createGraphics();

        int y = 0;
        for(Miniature miniature: miniaturesService.miniatures()) {
            int x = 0;
            BufferedImage croppedBufferedImage = miniature.getCroppedBufferedImage();
            for(int i = 0; i < miniature.getNumber(); i++) {
                g.drawImage(croppedBufferedImage, x, y, null);
                x += croppedBufferedImage.getWidth();
            }
            y += croppedBufferedImage.getHeight();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(result, "png", baos);
        return new StreamResource("result", (InputStreamFactory) () -> new ByteArrayInputStream(baos.toByteArray()));
    }

    public void update() throws IOException {
        StreamResource resource = createLayout();
        setSrc(resource);

//        StreamResource resource = new StreamResource(fileName,
//            () -> new BufferedInputStream(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(fileName))));
//        StreamRegistration registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);
//        setSrc(registration.getResourceUri().toString());
    }
}
