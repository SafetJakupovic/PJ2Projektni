package vehicles;

/**
 * EBike
 */
public class EBike extends Vehicle {
  private static int idNum;
  private String ID;
  private int autonomy;

  public EBike(String manufacturer, String model, double price, int autonomy) {
    super(manufacturer, model, price);
    this.autonomy = autonomy;
    idNum++;
    this.ID = "B" + idNum;
  }

  public String getID() {
    return ID;
  }

  public void setAutonomy(int autonomy) {
    this.autonomy = (autonomy > 0) ? autonomy : this.autonomy;
  }

  public int getAutonomy() {
    return autonomy;
  }

  @Override
  public String toString() {
    return super.toString() + "\nDomet sa jednim punjenjem: " + autonomy;
  }
}
