/*ClusteringMapGenerator Class
 * Author: Morgan Noonan
 */
package cs1501_p5;

import java.util.HashMap;
import java.util.Map;

public class ClusteringMapGenerator implements ColorMapGenerator_Inter {
    DistanceMetric_Inter metric;

    public ClusteringMapGenerator(DistanceMetric_Inter distanceMetric) {
        metric = distanceMetric;
    }

    @Override
    /**@param Pixel[][]
     * @param int
     * @return Pixel[] 
     */
    public Pixel[] generateColorPalette(Pixel[][] pixelArray, int numColors) {
        Pixel[] palette = new Pixel[numColors];
        if (numColors == 0){
            return palette;}

        boolean[][] marked = new boolean[pixelArray.length][pixelArray[0].length];
        double max;
        double distance;
        double minDist;
        int maxX = 0;
        int maxY = 0;
        Pixel maxPixel;
        Pixel currentPixel;
        palette[0] = pixelArray[0][0];
        marked[0][0] = true;

        for (int i = 1; i < numColors; i++) {
            maxPixel = null;
            max = 0;
            for (int x = 0; x < pixelArray.length; x++) {
                for (int y = 0; y < pixelArray[0].length; y++) {
                    if (!marked[x][y]) {
                        currentPixel = pixelArray[x][y];
                        minDist = Double.MAX_VALUE;
                        int j = 0;
                        while ((j < palette.length) && (palette[j] != null)) {
                            distance = metric.colorDistance(palette[j], currentPixel);
                            if (distance < minDist)
                                minDist = distance;
                            j++;
                        }
                        if (minDist >= max && !check(currentPixel, palette)) {
                            max = minDist;
                            maxPixel = currentPixel;
                            maxX = x;
                            maxY = y;
                        }
                    }
                }
            }
            palette[i] = maxPixel;
            marked[maxX][maxY] = true;
        }
        return palette;
    }

    private boolean check(Pixel currentPixel, Pixel[] arr) {
        for (Pixel pixel : arr) {
            if (pixel == null)
                break;
            if (pixel.equals(currentPixel))
                return true;
        }
        return false;
    }

    @Override
     /**@param Pixel[][]
     * @param Pixel[]
     * @return Map<Pixel, Pixel>
     */
public Map<Pixel, Pixel> generateColorMap(Pixel[][] pixelArray, Pixel[] initialColorPalette) {
    if (initialColorPalette.length == 0) {
        return null;
    }
    Map<Pixel, Integer> map;
    boolean loop;

    map = new HashMap<>();
    for (Pixel[] row : pixelArray) {
        for (Pixel curr : row) {
            double minDist = Double.MAX_VALUE;
            int closest = 0, j = 0;
            while ((j < initialColorPalette.length) && (initialColorPalette[j] != null)) {
                double distance = metric.colorDistance(initialColorPalette[j], curr);
                if (distance < minDist) {
                    minDist = distance;
                    closest = j;
                }
                j++;
            }
            map.put(curr, closest);
        }
    }

    // Loop condition check
    while (true) {
        loop = false;
        for (int i = 0; i < initialColorPalette.length; i++) {
            double count = 0, redTotal = 0, greenTotal = 0, blueTotal = 0;
            for (Pixel[] row : pixelArray) {
                for (Pixel curr : row) {
                    if (map.get(curr) == i) {
                        count++;
                        redTotal += curr.getRed();
                        greenTotal += curr.getGreen();
                        blueTotal += curr.getBlue();
                    }
                }
            }
            int redAvg = (int) (redTotal / count);
            int greenAvg = (int) (greenTotal / count);
            int blueAvg = (int) (blueTotal / count);
            if (initialColorPalette[i] != null
                    && (initialColorPalette[i].getRed() != redAvg || initialColorPalette[i].getGreen() != greenAvg
                            || initialColorPalette[i].getBlue() != blueAvg)) {
                loop = true;
            }
            initialColorPalette[i] = new Pixel(redAvg, greenAvg, blueAvg);
        }
        if (!loop) {
            break;
        }
        // Re-calculate map
        map = new HashMap<>();
        for (Pixel[] row : pixelArray) {
            for (Pixel curr : row) {
                double minDist = Double.MAX_VALUE;
                int closest = 0, j = 0;
                while ((j < initialColorPalette.length) && (initialColorPalette[j] != null)) {
                    double distance = metric.colorDistance(initialColorPalette[j], curr);
                    if (distance < minDist) {
                        minDist = distance;
                        closest = j;
                    }
                    j++;
                }
                map.put(curr, closest);
            }
        }
    }

    Map<Pixel, Pixel> colorMap = new HashMap<>();
    for (Pixel[] row : pixelArray) {
        for (Pixel curr : row) {
            colorMap.put(curr, initialColorPalette[map.get(curr)]);
        }
    }
    return colorMap;
}
}
