package cs1501_p5;

/**BucketingMapGenerator Class
 * @Morgan Noonan 
 */
import java.util.Map;
import java.util.HashMap;

public class BucketingMapGenerator implements ColorMapGenerator_Inter {

    @Override
    /**@param Pixel[][]
     * @param int
     * @return Pixel[]
     */
    public Pixel[] generateColorPalette(Pixel[][] pixelArray, int numColors) {
        double stepSize = Math.pow(2, 24) / (double) numColors;
        Pixel[] colorPalette = new Pixel[numColors];
        // Fill in the color palette array with evenly spaced colors
        for (int i = 0; i < numColors; i++) {
            int colorValue = (int) (stepSize * (i + 0.5));
            // individual RGB components
            int red = (colorValue >> 16) & 0xFF;
            int green = (colorValue >> 8) & 0xFF;
            int blue = colorValue & 0xFF;

            colorPalette[i] = new Pixel(red, green, blue);
        }
        return colorPalette;
    }

    @Override
     /**
      *@param Pixel[][]
     * @param Pixel[]
     * @return Map<Pixel, Pixel> 
     */
    public Map<Pixel, Pixel> generateColorMap(Pixel[][] pixelArray, Pixel[] initialColorPalette) {
        int numBuckets = initialColorPalette.length;
        Map<Pixel, Pixel> resultMap = new HashMap<>();
        
        // Calculate the bucket size
        int bucketSize = (int) Math.ceil((double) (1 << 24) / numBuckets);
        
        for (Pixel[] row : pixelArray) {
            for (Pixel pixel : row) {
                // Calculate the combined color of the pixel
                int combinedColor = combineColor(pixel);
                int bucketIndex = combinedColor / bucketSize;
                Pixel closestColor = initialColorPalette[bucketIndex];
                resultMap.put(pixel, closestColor);
            }
        }
        return resultMap;
    }
    
    // Combine RGB components into a single 24-bit integer
    private int combineColor(Pixel pixel) {
        return (pixel.getRed() << 16) | (pixel.getGreen() << 8) | pixel.getBlue();
    }
    
}
