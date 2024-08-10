package vehicles;

import java.time.LocalDate;
import main.ePJ2;

/**
 * Automobile
 */
public class Automobile extends Vehicle {
  private LocalDate dateOfPurchase;
  private String description;

  public Automobile(String ID, String manufacturer, String model, double price,
      LocalDate dateOfPurchase, String description) {
    super(ID, manufacturer, model, price);
    this.dateOfPurchase = dateOfPurchase;
    this.description = description;
    this.unitPrice = Integer.parseInt(ePJ2.properties.getProperty("CAR_UNIT_PRICE"));
  }

  public void setDateOfPurchase(LocalDate dateOfPurchase) {
    this.dateOfPurchase = dateOfPurchase;
  }

  public LocalDate getDateOfPurchase() {
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
