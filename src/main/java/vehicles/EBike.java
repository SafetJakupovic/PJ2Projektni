package vehicles;

import main.ePJ2;

/**
 * EBike nasljeđuje {@code Vehicle} klasu. Sadrži dodatno polje za domet i
 * odgovarajući getter i setter.
 */
public class EBike extends Vehicle {
  /** Domet bicikla. */
  private int range;

  /**
   * Konstruiše novi {@code EBike} objekat.
   *
   * Poziva konstruktor klase {@code Vehicle} kojem se prosljeđuju ID,
   * manufacturer, model i price.
   *
   * @param ID           ID bicikla
   * @param manufacturer proizvođač bicikla
   * @param model        model bicikla
   * @param price        cijena bicikla
   * @param range        domet bicikla
   */
  public EBike(String ID, String manufacturer, String model, double price,
      int range) {
    super(ID, manufacturer, model, price);
    this.range = range;
    this.unitPrice = Integer.parseInt(ePJ2.properties.getProperty("BIKE_UNIT_PRICE"));
    this.breakdownCoefficient = 0.04;
  }

  /**
   * Postavlja vrijednost dometa bicikla.
   *
   * @param range domet bicikla
   */
  public void setAutonomy(int range) {
    // ovo sam davno pisao, ne znam zasto je ovako formulisano samo ovdje haha
    this.range = (range > 0) ? range : this.range;
  }

  /**
   * Vraća domet bicikla.
   *
   * @return domet bicikla kao cjelobrojna vrijednost
   */
  public int getAutonomy() {
    return range;
  }

  /**
   * Poziva toString metodu klase {@code Vehicle} i ispisuje domet.
   */
  @Override
  public String toString() {
    return super.toString() + "\nDomet sa jednim punjenjem: " + range;
  }
}
