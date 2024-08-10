package vehicles;

import main.ePJ2;

/**
 * EBike
 */
public class EBike extends Vehicle {
  private int range;

  public EBike(String ID, String manufacturer, String model, double price,
      int range) {
    super(ID, manufacturer, model, price);
    this.range = range;
    this.unitPrice = Integer.parseInt(ePJ2.properties.getProperty("BIKE_UNIT_PRICE"));
  }

  public void setAutonomy(int range) {
    this.range = (range > 0) ? range : this.range;
  }

  public int getAutonomy() {
    return range;
  }

  @Override
  public String toString() {
    return super.toString() + "\nDomet sa jednim punjenjem: " + range;
  }
}
