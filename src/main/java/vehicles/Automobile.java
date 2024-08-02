package vehicles;

import java.util.Date;

/**
 * Automobile
 */
public class Automobile extends Vehicle {
  private static int idNum;
  private String ID;
  private Date dateOfPurchase;
  private String description;
  private int numOfSeats;

  public Automobile(String manufacturer, String model, double price,
      Date dateOfPurchase, String description, int numOfSeats) {
    super(manufacturer, model, price);
    this.dateOfPurchase = dateOfPurchase;
    this.description = description;
    this.numOfSeats = numOfSeats;
    idNum++;
    this.ID = "A" + idNum;
  }

  public String getID() {
    return ID;
  }

  public void setDateOfPurchase(Date dateOfPurchase) {
    this.dateOfPurchase = dateOfPurchase;
  }

  public Date getDateOfPurchase() {
    return dateOfPurchase;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setNumOfSeats(int numOfSeats) {
    this.numOfSeats = (numOfSeats > 0) ? numOfSeats : this.numOfSeats;
  }

  public int getNumOfSeats() {
    return numOfSeats;
  }

  @Override
  public String toString() {
    return super.toString() + "\nDatum nabavke: " + dateOfPurchase +
        "\nBroj sjedista: " + numOfSeats + "\nOpis: " + description;
  }
}
