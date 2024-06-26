/**
 *  Car for CS1501 Project 3
 * @author    Morgan Noonan
 */

package cs1501_p3;

public class Car implements Car_Inter {
    private String VIN;
    private String make;
    private String model;
    private int price;
    private int mileage;
    private String color;

    public Car(String v, String ma, String mo, int p, int mi, String c) {
        this.VIN = v;
        this.make = ma;
        this.model = mo;
        this.price = p;
        this.mileage = mi;
        this.color = c;
    }

    @Override
    public String getVIN() {
        return VIN;
    }

    @Override
    public String getMake() {
        return make;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public int getMileage() {
        return mileage;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public void setPrice(int newPrice) {
        price = newPrice;
    }

    @Override
    public void setMileage(int newMileage) {
        mileage = newMileage;
    }

    @Override
    public void setColor(String newColor) {
        color = newColor;
    }

}