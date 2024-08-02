package vehicles;

/**
 * EScooter
 */
public class EScooter extends Vehicle {
  private static int idNum;
  private String ID;
  private int maxVelocity;

  public EScooter(String manufacturer, String model, double price,
      int maxVelocity) {
    super(manufacturer, model, price);
    this.maxVelocity = maxVelocity;
    EScooter.idNum++;
    this.ID = "T" + idNum;
  }

  public String getID() {
    return ID;
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
