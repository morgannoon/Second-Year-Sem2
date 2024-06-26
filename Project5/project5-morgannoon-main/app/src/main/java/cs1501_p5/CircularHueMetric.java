/**CircularHueMetric class
 * author: Morgan Noonan
 */
package cs1501_p5;
public class CircularHueMetric implements DistanceMetric_Inter{

    @Override
      /**
      *@param Pixel
     * @param Pixel
     * @return double 
     */
    public double colorDistance(Pixel p1, Pixel p2) {
        int hueDiff = Math.abs(p1.getHue() - p2.getHue());
        return Math.min(hueDiff, 360 - hueDiff);
    }
}
