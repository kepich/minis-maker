package org.vaadin.example.service;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import org.vaadin.example.Miniature;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_4BYTE_ABGR;

@Service
@SessionScope
public class PNGBuilderService {
    public static final int SPACING_MM = 13;
    public static final int A4_WIDTH_PX = 2480;
    public static final int A4_HEIGHT_PX = 3508;
    public static final float PX_IN_MM = 11.8114478114F;
    public static final float SPACING_PX = SPACING_MM * PX_IN_MM;

    private final MiniaturesService miniaturesService;

    public PNGBuilderService(MiniaturesService miniaturesService) {
        this.miniaturesService = miniaturesService;
    }

    public byte[] getPagePNGBytes() throws IOException {
        BufferedImage result = new BufferedImage(A4_WIDTH_PX, A4_HEIGHT_PX, TYPE_4BYTE_ABGR);
        Graphics g = result.createGraphics();

        int y = (int) SPACING_PX;
        for (Miniature miniature : miniaturesService.miniatures()) {
            BufferedImage croppedBufferedImage = miniature.getCroppedBufferedImage();
            for (int i = 0, x = (int) SPACING_PX; i < miniature.getNumber(); i++) {
                if (x + croppedBufferedImage.getWidth() > (A4_WIDTH_PX - SPACING_PX)) {
                    x = (int) SPACING_PX;
                    y += croppedBufferedImage.getHeight();
                }
                g.drawImage(croppedBufferedImage, x, y, null);
                x += croppedBufferedImage.getWidth();
            }
            y += croppedBufferedImage.getHeight();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(result, "png", baos);
        return baos.toByteArray();
    }

    public StreamResource getPagePNGStreamResource() {
        return new StreamResource("result.png", (InputStreamFactory) () -> {
            try {
                return new ByteArrayInputStream(getPagePNGBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
