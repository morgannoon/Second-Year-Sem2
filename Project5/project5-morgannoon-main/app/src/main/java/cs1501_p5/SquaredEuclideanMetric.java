/*SquaredEuclideanMetric Class
 * author: Morgan Noonan
 */
package cs1501_p5;
public class SquaredEuclideanMetric implements DistanceMetric_Inter{

    @Override
    /**@param Pixel
     * @param Pixel
     * @return double
     */
    public double colorDistance(Pixel p1, Pixel p2) {
        int diffRed = p1.getRed() - p2.getRed();
        int diffGreen = p1.getGreen() - p2.getGreen();
        int diffBlue = p1.getBlue() - p2.getBlue();
        return Math.pow(diffRed, 2) + Math.pow(diffGreen, 2) + Math.pow(diffBlue, 2);
    }
    
}
