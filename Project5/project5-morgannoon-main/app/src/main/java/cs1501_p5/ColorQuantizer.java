/**ColorQuantizer Class
 * author: Morgan Noonan
 */
package cs1501_p5;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.imageio.ImageIO;

public class ColorQuantizer implements ColorQuantizer_Inter {
    private Pixel[][] pixelArray;
    private ColorMapGenerator_Inter colorMapGenerator;

    public ColorQuantizer(Pixel[][] pixelArray, ColorMapGenerator_Inter gen) {
        this.pixelArray = pixelArray;
        this.colorMapGenerator = gen;
    }

    public ColorQuantizer(String bmpFilename, ColorMapGenerator_Inter gen) {
        this.colorMapGenerator = gen;
        try {
            BufferedImage image = ImageIO.read(new File(bmpFilename));
            // Get image dimensions
            int width = image.getWidth();
            int height = image.getHeight();
            pixelArray = new Pixel[height][width];
            // Extract pixel information and store it in pixelArray
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;
                    pixelArray[y][x] = new Pixel(red, green, blue);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    /**@param int
     * @return Pixel[][]
     */
public Pixel[][] quantizeTo2DArray(int numColors) {
    // Generate initial palette with the specified number of colors
    Pixel[] initialPalette = colorMapGenerator.generateColorPalette(pixelArray, numColors);
    Map<Pixel, Pixel> colorMap = colorMapGenerator.generateColorMap(pixelArray, initialPalette);
    
    // Replace original pixels with quantized pixels in a new 2D array
    Pixel[][] quantizedArray = new Pixel[pixelArray.length][pixelArray[0].length];
    for (int i = 0; i < pixelArray.length; i++) {
        for (int j = 0; j < pixelArray[i].length; j++) {
            Pixel originalPixel = pixelArray[i][j];
            Pixel quantizedPixel = colorMap.get(originalPixel);
            
            // If quantizedPixel is null, find the closest color in the palette
            if (quantizedPixel == null) {
                quantizedPixel = findClosestColor(originalPixel, initialPalette);
            }
            quantizedArray[i][j] = quantizedPixel;
        }
    }
    return quantizedArray;
}

// Find the closest color in the palette to the given pixel
private Pixel findClosestColor(Pixel pixel, Pixel[] palette) {
    Pixel closestColor = palette[0]; // Start with the first color in the palette
    double minDistance = Double.MAX_VALUE;

    for (Pixel color : palette) {
        // Calculate the Euclidean distance
        SquaredEuclideanMetric calculate = new SquaredEuclideanMetric();
        double distance = calculate.colorDistance(pixel, color);

        if (distance < minDistance) {
            minDistance = distance;
            closestColor = color;
        }
    }

    return closestColor;
}


    @Override
      /**
      *@param String
     * @param int
     */
    public void quantizeToBMP(String fileName, int numColors) {
        Pixel[] initialPalette = colorMapGenerator.generateColorPalette(pixelArray, numColors);
        Map<Pixel, Pixel> colorMap = colorMapGenerator.generateColorMap(pixelArray, initialPalette);
        // Replace original pixels with quantized pixels in the pixelArray
        for (int i = 0; i < pixelArray.length; i++) {
            for (int j = 0; j < pixelArray[i].length; j++) {
                Pixel originalPixel = pixelArray[i][j];
                Pixel quantizedPixel = colorMap.get(originalPixel);
                pixelArray[i][j] = quantizedPixel;
            }
        }
        BufferedImage quantizedImage = new BufferedImage(pixelArray[0].length, pixelArray.length,
                BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < pixelArray.length; y++) {
            for (int x = 0; x < pixelArray[y].length; x++) {
                Pixel pixel = pixelArray[y][x];
                int rgb = (pixel.getRed() << 16) | (pixel.getGreen() << 8) | pixel.getBlue();
                quantizedImage.setRGB(x, y, rgb);
            }
        }
        try {
            File outputFile = new File(fileName);
            ImageIO.write(quantizedImage, "bmp", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
