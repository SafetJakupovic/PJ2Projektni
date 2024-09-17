package vehicles;

import java.time.LocalDate;
import main.ePJ2;

/**
 * Automobile klasa nasljeđuje Vehicle klasu. Sadrži dodatna polja o datumu
 * kupovine i opisu automobila kao i odgovarajuće gettere i settere.
 */
public class Automobile extends Vehicle {
  /** Datum kupovine automobila. */
  private LocalDate dateOfPurchase;
  /** Opis automobila. */
  private String description;

  /**
   * Konstruiše novi {@code Automobile} objekat.
   *
   * Poziva konstruktor klase {@code Vehicle} kojem se prosljeđu ID,
   * manufacturer, model i price.
   *
   * @param ID             ID automobila
   * @param manufacturer   proizvođac automobila
   * @param model          model automobila
   * @param price          cijena automobila
   * @param dateOfPurchase datum kupovine automobila
   * @param description    opis automobila
   */
  public Automobile(String ID, String manufacturer, String model, double price,
      LocalDate dateOfPurchase, String description) {
    super(ID, manufacturer, model, price);
    this.dateOfPurchase = dateOfPurchase;
    this.description = description;
    this.unitPrice = Integer.parseInt(ePJ2.properties.getProperty("CAR_UNIT_PRICE"));
    this.breakdownCoefficient = 0.07;
  }

  /**
   * Postavlja datum kupovine automobila.
   *
   * @param dateOfPurchase datum kupovine
   */
  public void setDateOfPurchase(LocalDate dateOfPurchase) {
    this.dateOfPurchase = dateOfPurchase;
  }

  /**
   * Vraća datum kupovine automobila.
   *
   * @return datum kupovine automobila
   */
  public LocalDate getDateOfPurchase() {
    return dateOfPurchase;
  }

  /**
   * Postavlja opis automobila.
   *
   * @param description opis automobila
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Vraća opis automobila.
   *
   * @return String opisa automobila
   */
  public String getDescription() {
    return description;
  }

  /**
   * Poziva toString metodu klase {@code Vehicle} i ispisuje datum nabavke i
   * opis.
   */
  @Override
  public String toString() {
    return super.toString() + "\nDatum nabavke: " + dateOfPurchase +
        "\nOpis: " + description;
  }
}
