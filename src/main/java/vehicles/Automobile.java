package vehicles;

import java.time.LocalDateTime;

/**
 * Automobile
 */
public class Automobile extends Vehicle {
  private static int idNum;
  private String ID;
  private LocalDateTime dateOfPurchase;
  private String description;

  public Automobile(String manufacturer, String model, double price,
      LocalDateTime dateOfPurchase, String description) {
    super(manufacturer, model, price);
    this.dateOfPurchase = dateOfPurchase;
    this.description = description;
    idNum++;
    this.ID = "A" + idNum;
  }

  public String getID() {
    return ID;
  }

  public void setDateOfPurchase(LocalDateTime dateOfPurchase) {
    this.dateOfPurchase = dateOfPurchase;
  }

  public LocalDateTime getDateOfPurchase() {
    return dateOfPurchase;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  // TODO: skontati sta i kako za vise mjesta

  @Override
  public String toString() {
    return super.toString() + "\nDatum nabavke: " + dateOfPurchase +
        "\nOpis: " + description;
  }
}
