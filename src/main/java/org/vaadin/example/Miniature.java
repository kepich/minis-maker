package org.vaadin.example;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class Miniature {
    private final String fileName;
    private final StreamResource streamResource;
    private final byte[] bytes;

    @Setter
    private int number = 0;
    @Setter
    private int paddingTop = 0;
    @Setter
    private int paddingBottom = 0;
    @Setter
    private int paddingLeft = 0;
    @Setter
    private int paddingRight = 0;

    public Miniature(String fileName, InputStream inputStream) throws IOException {
        this.fileName = fileName;
        this.streamResource = new StreamResource(fileName, () -> inputStream);
        this.bytes = inputStream.readAllBytes();
    }

    public StreamResource getCroppedStreamResource() throws IOException {
        BufferedImage copyOfImage = getCroppedBufferedImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(copyOfImage, "png", baos);
        return new StreamResource(fileName, (InputStreamFactory) () -> new ByteArrayInputStream(baos.toByteArray()));
    }

    public BufferedImage getCroppedBufferedImage() throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        BufferedImage cropped = image.getSubimage(paddingLeft, paddingTop, image.getWidth() - paddingRight - paddingLeft, image.getHeight() - paddingBottom - paddingTop);
        BufferedImage copyOfImage = new BufferedImage(cropped.getWidth(), cropped.getHeight(), image.getType());
        Graphics g = copyOfImage.createGraphics();
        g.drawImage(cropped, 0, 0, null);
        return copyOfImage;
    }
}
