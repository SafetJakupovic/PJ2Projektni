package vehicles;

/**
 * Vehicle
 */
public abstract class Vehicle {
  protected String manufacturer; // moze biti i zasebna klasa tbh
  protected String model;
  protected double price;
  protected int batteryLeft;

  public Vehicle(String manufacturer, String model, double price) {
    this.manufacturer = manufacturer;
    this.model = model;
    this.price = price;
    batteryLeft = 100;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getModel() {
    return model;
  }

  public void setPrice(double price) {
    this.price = (price > 0) ? price : this.price;
  }

  public double getPrice() {
    return price;
  }

  public void recharge(int rechargePercentage) {
    this.batteryLeft = (this.batteryLeft + rechargePercentage >= 100)
        ? 100
        : this.batteryLeft + rechargePercentage;
  }

  @Override
  public String toString() {
    return manufacturer + " " + model + "\nCijena: " + price + "\n" +
        batteryLeft + "% baterije";
  }
}
