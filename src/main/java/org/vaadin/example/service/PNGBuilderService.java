package org.vaadin.example.service;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import org.vaadin.example.pdf.Floor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

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

    @SneakyThrows
    public List<byte[]> getPagePNGBytes() {
        ArrayList<Floor> floors = miniaturesService.getFloors();
        ArrayList<byte[]> pages = new ArrayList<>();
        while (!floors.isEmpty()) {
            BufferedImage result = new BufferedImage(A4_WIDTH_PX, A4_HEIGHT_PX, TYPE_4BYTE_ABGR);
            Graphics g = result.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, A4_WIDTH_PX, A4_HEIGHT_PX);

            int y = (int) SPACING_PX;
            int x = (int) SPACING_PX;

            ArrayList<Floor> nextPage = new ArrayList<>();
            for (Floor floor : floors) {
                if (!floor.draw(x, y, g)) {
                    nextPage.add(floor);
                    continue;
                }

                y += floor.getHeight();
            }
            floors = nextPage;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(result, "png", baos);
            pages.add(baos.toByteArray());
        }

        return pages;
    }

    @SneakyThrows
    public StreamResource getPagesStreamResource() {
        List<byte[]> resources = getPagePNGBytes();
        int imageWidth = (int) (A4_WIDTH_PX + 2 * SPACING_PX);
        int onePageHeight = (int) (A4_HEIGHT_PX + SPACING_PX);
        int imageHeight = onePageHeight * resources.size();
        BufferedImage result = new BufferedImage(imageWidth, imageHeight, TYPE_4BYTE_ABGR);
        Graphics g = result.getGraphics();
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, imageWidth, imageHeight);

        int y = (int) SPACING_PX;
        for (byte[] bytes : resources) {
            g.drawImage(ImageIO.read(new ByteArrayInputStream(bytes)), (int) SPACING_PX, y, null);
            y += onePageHeight;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(result, "png", baos);
        return new StreamResource("result.png", (InputStreamFactory) () -> new ByteArrayInputStream(baos.toByteArray()));
    }
}
