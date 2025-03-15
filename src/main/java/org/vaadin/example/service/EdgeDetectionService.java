package org.vaadin.example.service;

import org.vaadin.example.Miniature;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.Arrays;

import static org.apache.commons.lang3.ObjectUtils.max;
import static org.apache.commons.lang3.ObjectUtils.min;

public class EdgeDetectionService {

    private static BufferedImage blur(BufferedImage img) {
        int radius = 3;
        int size = radius * 2 + 1;
        float weight = 1.0f / (size * size);
        float[] data = new float[size * size];

        Arrays.fill(data, weight);

        Kernel kernel = new Kernel(size, size, data);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        //tbi is BufferedImage
        return op.filter(img, null);
    }

    private static int getIndex(int x, int maxX, int minX) {
        if (x < minX) {
            return minX;
        } else if (x > maxX) {
            return maxX;
        } else {
            return x;
        }
    }

    private static int getGreyValue(int rgb) {
        Color clr = new Color(rgb);
        int red = clr.getRed();
        int green = clr.getGreen();
        int blue = clr.getBlue();
        return Math.round((float) (red + green + blue) / 3);
    }

    private static int[][] horizontalSobel(BufferedImage input, int width, int height) {
        // set up horizontal sobel kernel
        int[][] horizSobel = new int[width][height];
        int[][] dx = new int[3][3];
        dx[0][0] = -1;
        dx[0][1] = 0;
        dx[0][2] = 1;
        dx[1][0] = -2;
        dx[1][1] = 0;
        dx[1][2] = 2;
        dx[2][0] = -1;
        dx[2][1] = 0;
        dx[2][2] = 1;
        // calculate horizontal sobel values and store in the horizSobel array
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int sum = 0;
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j - 1, height - 1, 0)))
                    * dx[0][0];
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j, height - 1, 0))) * dx[0][1];
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j + 1, height - 1, 0)))
                    * dx[0][2];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j - 1, height - 1, 0))) * dx[1][0];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j, height - 1, 0))) * dx[1][1];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j + 1, height - 1, 0))) * dx[1][2];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j - 1, height - 1, 0)))
                    * dx[2][0];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j, height - 1, 0))) * dx[2][1];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j + 1, height - 1, 0)))
                    * dx[2][2];
                horizSobel[i][j] = sum;
            }
        }

        return horizSobel;
    }

    private static int[][] verticalSobel(BufferedImage input, int width, int height) {
        // set up vertical sobel kernel
        int[][] vertSobel = new int[width][height];
        int[][] dy = new int[3][3];
        dy[0][0] = -1;
        dy[0][1] = -2;
        dy[0][2] = -1;
        dy[1][0] = 0;
        dy[1][1] = 0;
        dy[1][2] = 0;
        dy[2][0] = 1;
        dy[2][1] = 2;
        dy[2][2] = 1;
        // calculate vertical sobel value and store in vertSobel array
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int sum = 0;
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j - 1, height - 1, 0)))
                    * dy[0][0];
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j, height - 1, 0))) * dy[0][1];
                sum += getGreyValue(input.getRGB(getIndex(i - 1, width - 1, 0), getIndex(j + 1, height - 1, 0)))
                    * dy[0][2];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j - 1, height - 1, 0))) * dy[1][0];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j, height - 1, 0))) * dy[1][1];
                sum += getGreyValue(input.getRGB(getIndex(i, width - 1, 0), getIndex(j + 1, height - 1, 0))) * dy[1][2];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j - 1, height - 1, 0)))
                    * dy[2][0];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j, height - 1, 0))) * dy[2][1];
                sum += getGreyValue(input.getRGB(getIndex(i + 1, width - 1, 0), getIndex(j + 1, height - 1, 0)))
                    * dy[2][2];
                vertSobel[i][j] = sum;
            }
        }
        return vertSobel;
    }

    public static void sobelEdgeDetection(BufferedImage input, Miniature miniature) {
        int width = input.getWidth();
        int height = input.getHeight();
        int[][] sobel = new int[width][height];
        // perform horizontal and vertical sobel
        BufferedImage blured = blur(input);
        int[][] horizSobel = horizontalSobel(blured, width, height);
        int[][] vertSobel = verticalSobel(blured, width, height);
        // store combined results in a sobel matrix
        int maxValue = Integer.MIN_VALUE;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double horizSobelSquared = Math.pow(horizSobel[i][j], 2);
                double vertSobelSquared = Math.pow(vertSobel[i][j], 2);
                int value = (int) Math.round(Math.sqrt(horizSobelSquared + vertSobelSquared));
                sobel[i][j] = value;
                // keep track of max value in the matrix
                if (value > maxValue) {
                    maxValue = value;
                }
            }
        }

        setBoards(maxValue, sobel, width, height, miniature);
    }

    private static void setBoards(int maxValue, int[][] sobel, int width, int height, Miniature miniature) {
        double scaleCoeff = 255.0 / maxValue;
        int BRIGHTNESS_THRESHOLD = (int) (55 / scaleCoeff);

        int leftBoard = width;
        int rightBoard = 0;
        int topBoard = height;
        int bottomBoard = 0;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (sobel[i][j] > BRIGHTNESS_THRESHOLD) {
                    leftBoard = min(leftBoard, i);
                    rightBoard = max(rightBoard, i);
                    topBoard = min(topBoard, j);
                    bottomBoard = max(bottomBoard, j);
                }
            }
        }

        int baseLeft = width;
        int baseRight = 0;
        for (int i = leftBoard; i < rightBoard; i++) {
            for (int j = (int) (topBoard + (bottomBoard - topBoard) * 0.6); j < bottomBoard; j++) {
                if (sobel[i][j] > BRIGHTNESS_THRESHOLD) {
                    baseLeft = min(baseLeft, i);
                    baseRight = max(baseRight, i);
                }
            }
        }

        miniature.setPaddingLeft(leftBoard);
        miniature.setPaddingRight(width - rightBoard);
        miniature.setPaddingTop(topBoard);
        miniature.setPaddingBottom(height - bottomBoard);
        miniature.setBaseOffsetLeft(baseLeft - leftBoard);
        miniature.setBaseOffsetRight(rightBoard - baseRight);
    }
}