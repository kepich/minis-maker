package org.vaadin.example;

import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import lombok.Getter;
import lombok.Setter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static java.awt.Image.SCALE_SMOOTH;
import static org.vaadin.example.service.PNGBuilderService.PX_IN_MM;

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
    @Setter
    private int baseWidthMm = 25;

    public Miniature(String fileName, InputStream inputStream) throws IOException {
        this.fileName = fileName;
        this.streamResource = new StreamResource(fileName, () -> inputStream);
        this.bytes = inputStream.readAllBytes();
    }

    public StreamResource getCroppedStreamResource() throws IOException {
        BufferedImage copyOfImage = getSingleImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(copyOfImage, "png", baos);
        return new StreamResource(fileName, (InputStreamFactory) () -> new ByteArrayInputStream(baos.toByteArray()));
    }

    public BufferedImage getDoubledImage() throws IOException {
        BufferedImage image = getSingleImage();
        BufferedImage copyOfImage = new BufferedImage(image.getWidth(null), image.getHeight(null) * 2, image.getType());
        Graphics2D g = copyOfImage.createGraphics();

        AffineTransform at = new AffineTransform();
        at.concatenate(AffineTransform.getScaleInstance(1, -1));
        at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight(null)));

        g.drawImage(image, 0, image.getHeight(null), null);
        g.transform(at);
        g.drawImage(image, 0, 0, null);

        return copyOfImage;
    }

    public BufferedImage getSingleImage() throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
        Image cropped = getCroppedImage(image);
        BufferedImage copyOfImage = new BufferedImage(cropped.getWidth(null), cropped.getHeight(null), image.getType());
        Graphics2D g = copyOfImage.createGraphics();
        g.drawImage(cropped, 0, 0, null);
        g.setColor(Color.BLACK);
        g.drawLine(0, 0,  cropped.getWidth(null), 0);
        g.drawLine(0, cropped.getHeight(null) - 1, cropped.getWidth(null), cropped.getHeight(null) - 1);
        g.drawLine(0, 0, 0, cropped.getHeight(null));
        g.drawLine(cropped.getWidth(null) - 1, 0, cropped.getWidth(null) - 1, cropped.getHeight(null));

        return copyOfImage;
    }

    private Image getCroppedImage(BufferedImage image) {
        int width = image.getWidth() - paddingRight - paddingLeft;
        int height = image.getHeight() - paddingBottom - paddingTop;
        float scaleRatio = baseWidthMm / (width / PX_IN_MM);
        Image cropped = image
            .getSubimage(paddingLeft, paddingTop, width, height)
            .getScaledInstance((int) (width * scaleRatio), (int) (height * scaleRatio), SCALE_SMOOTH);
        return cropped;
    }
}
