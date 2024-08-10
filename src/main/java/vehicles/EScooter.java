package vehicles;

import main.ePJ2;

/**
 * EScooter
 */
public class EScooter extends Vehicle {
  private int maxVelocity;

  public EScooter(String ID, String manufacturer, String model, double price,
      int maxVelocity) {
    super(ID, manufacturer, model, price);
    this.maxVelocity = maxVelocity;
    this.unitPrice = Integer.parseInt(ePJ2.properties.getProperty("SCOOTER_UNIT_PRICE"));
  }

  public void setMaxVelocity(int maxVelocity) {
    this.maxVelocity = (maxVelocity > 0) ? maxVelocity : this.maxVelocity;
  }

  public int getMaxVelocity() {
    return maxVelocity;
  }

  @Override
  public String toString() {
    return super.toString() + "\nMaksimalna brzina: " + maxVelocity;
  }
}
