package vehicles;

import main.ePJ2;

/**
 * EScooter klasa nasljeđuje {@code Vehicle} klasu. Sadrži dodatno polje za
 * maksimalnu brzinu te odgovarajući getter i setter.
 */
public class EScooter extends Vehicle {
  /** Maksimalna brzina trotineta. */
  private int maxVelocity;

  /**
   * Konstruiše novi {@code EScooter} objekat.
   *
   * Poziva konstruktor klase {@code Vehicle} kojem se prosljeđu ID,
   * manufacturer, model i price.
   *
   * @param ID           ID trotineta
   * @param manufacturer proizvođač trotineta
   * @param model        model trotineta
   * @param price        cijena trotineta
   * @param maxVelocity  maksimalna brzina trotineta
   */
  public EScooter(String ID, String manufacturer, String model, double price,
      int maxVelocity) {
    super(ID, manufacturer, model, price);
    this.maxVelocity = maxVelocity;
    this.unitPrice = Integer.parseInt(ePJ2.properties.getProperty("SCOOTER_UNIT_PRICE"));
    this.breakdownCoefficient = 0.02;
  }

  /**
   * Postavlja maksimalnu brzinu trotineta.
   *
   * @param maxVelocity maksimalna brzina trotineta
   */
  public void setMaxVelocity(int maxVelocity) {
    this.maxVelocity = (maxVelocity > 0) ? maxVelocity : this.maxVelocity;
  }

  /**
   * Vraća maksimalnu brzinu trotineta.
   *
   * @return maksimalna brzina trotineta kao cjelobrojna vrijednost
   */
  public int getMaxVelocity() {
    return maxVelocity;
  }

  /**
   * Poziva toString metodu klase {@code Vehicle} i ispisuje maksimalnu brzinu.
   */
  @Override
  public String toString() {
    return super.toString() + "\nMaksimalna brzina: " + maxVelocity;
  }
}
