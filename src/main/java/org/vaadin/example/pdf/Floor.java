package org.vaadin.example.pdf;

import lombok.Getter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static org.vaadin.example.service.PNGBuilderService.*;

public class Floor {
    private final ArrayList<Floor> subFloors = new ArrayList<>();

    @Getter
    private final int height;

    private int availableSize;
    private int numberOfMinis = 0;
    private BufferedImage image = null;

    public Floor(int height) {
        this(height, (int) (A4_WIDTH_PX - SPACING_PX - SPACING_PX));
    }

    private Floor(int height, int availableSize) {
        this.height = height;
        this.availableSize = availableSize;
    }

    public int putMinis(BufferedImage img, int n) {
        if (img.getHeight() > height || img.getWidth() > availableSize) {
            return n;
        }

        int minisNeedToPush = n;

        if (this.image == null) {
            while (minisNeedToPush > 0 && img.getWidth() <= availableSize) {
                this.image = img;
                numberOfMinis++;
                availableSize -= img.getWidth();
                minisNeedToPush--;
            }
        } else {
            if (subFloors.isEmpty()) {
                this.subFloors.add(new Floor(img.getHeight(), availableSize));
            }

            for (Floor subfloor: subFloors) {
                if (minisNeedToPush > 0) {
                    minisNeedToPush = subfloor.putMinis(img, minisNeedToPush);
                } else {
                    break;
                }
            }
        }

        return minisNeedToPush;
    }

    public boolean draw(int baseX, int baseY, Graphics g) {
        if (baseY + height <= (A4_HEIGHT_PX - SPACING_PX)) {
            int x = baseX;
            for (int i = 0; i < numberOfMinis; i++) {
                g.drawImage(image, x, baseY, null);
                x += image.getWidth();
            }

            int y = baseY;
            for (Floor subFloor: subFloors) {
                subFloor.draw(x, y, g);
                y += subFloor.height;
            }
            return true;
        }
        return false;
    }
}
