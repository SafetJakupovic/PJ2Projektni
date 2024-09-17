package vehicles;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import main.ePJ2;

/**
 * Predstavlja jedno vozilo.
 *
 * Generišu se pri pokretanju programa čitajući fajl koji sadrži sva vozila.
 * Sadrži ID vozila, proizvođača vozila, model, nabavnu cijenu, preostali
 * postotak baterije, cijenu kretanja i koeficijent za popravak kvara.
 */
public abstract class Vehicle implements Serializable {
  /** ID vozila. */
  protected String ID;
  /** Proizvođač vozila. */
  protected String manufacturer;
  /** Model vozila. */
  protected String model;
  /** Nabavna cijena vozila. */
  protected double price;
  /** Preostali postotak baterije. */
  protected int batteryLeft;
  /** Osnovna cijena kretanja vozila. */
  protected int unitPrice;
  /** Koeficijent popravka kvara. */
  protected double breakdownCoefficient;

  /**
   * Podrazumijevani konstruktor.
   */
  public Vehicle() {
  }

  /**
   * Konstruiše novi {@code Vehicle} objekat.
   *
   * Konstruiše se vozilo sa podacima zajedničkim za sve vrste. Na početku ima
   * 100% baterije.
   *
   * @param ID           ID vozila
   * @param manufacturer proizvođač vozila
   * @param model        model vozila
   * @param price        cijena nabavke vozila
   */
  public Vehicle(String ID, String manufacturer, String model, double price) {
    this.ID = ID;
    this.manufacturer = manufacturer;
    this.model = model;
    this.price = price;
    batteryLeft = 100;
  }

  /**
   * Vraća ID vozila.
   *
   * @return String koji predstavlja ID vozila
   */
  public String getId() {
    return ID;
  }

  /**
   * Postavlja proizvođača vozila.
   *
   * @param manufacturer proizvodjac vozila
   */
  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  /**
   * Vraća proizvođača vozila.
   *
   * @return String proizvođača vozila
   */
  public String getManufacturer() {
    return manufacturer;
  }

  /**
   * Postavlja modela vozila.
   *
   * @param model model vozila
   */
  public void setModel(String model) {
    this.model = model;
  }

  /**
   * Vraća model vozila.
   *
   * @return String modela vozila.
   */
  public String getModel() {
    return model;
  }

  /**
   * Postavlja nabavne cijene vozila.
   *
   * @param price nabavna cijena vozila
   */
  public void setPrice(double price) {
    this.price = (price > 0) ? price : this.price;
  }

  /**
   * Vraća nabavnu cijenu vozila.
   *
   * @return double nabavne cijene vozila
   */
  public double getPrice() {
    return price;
  }

  /**
   * Vraća preostali postotak baterije kao cjelobrojnu vrijednost.
   *
   * @return Cjelobrojnu vrijednost preostale baterije
   */
  public int getBatteryLeft() {
    return batteryLeft;
  }

  // nije mi padalo na pamet koji bi bio najlogicniji nacin da se baterija trosi
  /**
   * Smanjenje baterije.
   *
   * Svaki poziv metode smanjuje preostalu bateriju za 2%.
   */
  public void decreaseBattery() {
    batteryLeft -= 2;
  }

  /**
   * Vraća osnovnu cijenu iznamljivanja vozila.
   *
   * Svako vozilo ima različitu osnovnu cijenu tokom kretanja, tj. koliko kosta
   * jedno pomjeranje na mapi. Osnovne cijene su definisane u config fajlu.
   *
   * @return Cjelobrojna vrijednost osnovne cijene.
   */
  public int getUnitPrice() {
    return unitPrice;
  }

  /**
   * Vraća koeficijent vozila.
   *
   * Svako vozilo ima različiti koeficijent koji se uzima u obzir prilikom
   * računanja popravka kvara: 0.07 za automobile, 0.04 za bicikle, 0.02 za
   * trotinete.
   *
   * @return double vrijednost koeficijenta popravka
   */
  public double getBreakdownCoefficient() {
    return breakdownCoefficient;
  }

  /**
   * Puni se baterija vozila.
   *
   * Vozilo se puni za prosliedjenu vrijednost do maksimalnih 100%.
   *
   * @param rechargePercentage postotak za koji se puni baterija
   */
  public void recharge(int rechargePercentage) {
    this.batteryLeft = (this.batteryLeft + rechargePercentage >= 100)
        ? 100
        : this.batteryLeft + rechargePercentage;
  }

  /**
   * Čita fajl vozila i stavlja u ArrayList.
   *
   * Prima putanju do fajla sa svim vozilima i kreira ArrayList koji sadrži sva
   * vozila. Više od jednog vozila ne može imati isti ID.
   *
   * @param pathToFile putanja do fajla sa vozilima
   * @return ArrayList koji sadrzi sva vozila
   */
  public static ArrayList<Vehicle> readFromFile(String pathToFile) {
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("d.M.yyyy.");
    try (BufferedReader br = new BufferedReader(
        new FileReader("src/main/resources/input/vehicles.csv"))) {
      String line;
      br.readLine();
      while ((line = br.readLine()) != null) {
        try {
          String[] tokens = line.split(",");
          if (tokens.length < 8) {
            System.err.println("Neispravan broj argumenata. Linija: " + line);
            continue;
          }
          String id = tokens[0].trim();
          if (vehicles.stream().anyMatch(
              vehicle -> vehicle.getId().equals(id))) {
            System.err.println("Pronadjen duplikat vozila. Linija: " + line);
            continue;
          }
          String manufacturer = tokens[1].trim();
          String model = tokens[2].trim();
          double price = Double.parseDouble(tokens[4].trim());
          String type = tokens[8].trim();
          if ("automobil".equalsIgnoreCase(type)) {
            LocalDate dateOfPurchase = LocalDate.parse(tokens[3].trim(), formatterDate);
            String description = tokens[6];
            vehicles.add(new Automobile(id, manufacturer, model, price,
                dateOfPurchase, description));
            ePJ2.carListModel.addElement(id + "; " + manufacturer + "; " +
                model + "; " + price + "; " +
                dateOfPurchase + "; " + description);
          } else if ("bicikl".equalsIgnoreCase(type)) {
            int range = Integer.parseInt(tokens[5].trim());
            vehicles.add(new EBike(id, manufacturer, model, price, range));
            ePJ2.bikeListModel.addElement(id + "; " + manufacturer + "; " +
                model + "; " + price + "; " + range);
          } else if ("trotinet".equalsIgnoreCase(type)) {
            int maxVelocity = Integer.parseInt(tokens[6].trim());
            vehicles.add(
                new EScooter(id, manufacturer, model, price, maxVelocity));
            ePJ2.scooterListModel.addElement(id + "; " + manufacturer + "; " +
                model + "; " + price + "; " +
                maxVelocity);
          } else {
            throw new IllegalArgumentException(
                "Neispravan unos prevoznog sredstva!");
          }
        } catch (NumberFormatException ex) {
          System.err.println("Neispravan format broja. Linija: " + line);
        } catch (DateTimeException ex) {
          System.err.println("Neispravan format datuma. Linija: " + line);
        } catch (IllegalArgumentException ex) {
          System.err.println(ex.getMessage() + " Linija: " + line);
        }
      }
    } catch (FileNotFoundException ex) {
      System.err.println("Fajl nije pronadjen: " + pathToFile);
    } catch (IOException ex) {
      System.err.println("I/O error citajuci fajl: " + ex.getMessage());
    }
    return vehicles;
  }

  /**
   * Vraća {@code Vehicle} objekat na osnovu proslijedjenog ID-ja.
   *
   * @param vehicleId ID vozila koje se traži
   * @return Vehicle objekat vozila sa odgovarajućim ID-jem
   */
  public static Vehicle getVehicleFromId(String vehicleId) {
    for (Vehicle v : ePJ2.vehicles) {
      if (vehicleId.equalsIgnoreCase(v.getId()))
        return v;
    }
    return null;
  }

  /**
   * Ispisuje ID, proizvođača, cijenu i preostali postotak baterije vozila.
   */
  @Override
  public String toString() {
    return "ID: " + ID + "\n  " + manufacturer + " " + model +
        "\n  Cijena: " + price + "\n  " + batteryLeft + "% baterije";
  }
}
