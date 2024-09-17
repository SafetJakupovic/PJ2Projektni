package renting;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import main.ePJ2;
import vehicles.*;

/**
 * Predstvlja jedno iznajmljivanje.
 *
 * Generisu se automatski pokretanjem programa i citanjem fajla sa
 * iznamljivanjima. Sadrzi datum i vrijeme iznamljivanja, ID korisnika, ID
 * vozila koje se iznamljuje, koordinate za pocetnu i krajnju lokaciju, trajanje
 * u sekundama. Takodje sadrzi boolean-e koji govore da li se desio kvar i da li
 * postoji promocija.
 */
public class Rental {
  private LocalDateTime dateTime;
  private String userName;
  private String vehicleId;
  private Point startLocation;
  private Point endLocation;
  private int duration;
  private boolean breakdown;
  private boolean promotion;
  private Vehicle vehicle;

  /**
   * Konstruiše novi {@code Rental} objekat.
   *
   * @param dateTime      datum i vrijeme kada iznajmljivanje počinje
   * @param userName      ime korisnika koji iznajmljuje vozilo
   * @param vehicleId     ID vozila koje se iznajmljije
   * @param startLocation početna lokacija iznajmljivanja, reprezentovana kao
   *                      {@code Point}
   * @param endLocation   odredište iznajmljivanja, reprezentovana kao {@code
   *     Point}
   * @param duration      trajanje iznajmljivanja u sekundama
   * @param breakdown     da li je tokom iznajmljivanja nastao kvar
   * @param promotion     da li postoji promocija
   */
  public Rental(LocalDateTime dateTime, String userName, String vehicleId,
      Point startLocation, Point endLocation, int duration,
      boolean breakdown, boolean promotion) {
    this.dateTime = dateTime;
    this.userName = userName;
    this.vehicleId = vehicleId;
    this.startLocation = startLocation;
    this.endLocation = endLocation;
    this.duration = duration;
    this.breakdown = breakdown;
    this.promotion = promotion;

    for (Vehicle v : ePJ2.vehicles) {
      if (vehicleId.equals(v.getId())) {
        this.vehicle = v;
        break;
      }
    }
  }

  /**
   * Vraća vrijeme i datum iznajmljivanja.
   *
   * @return LocalDateTime iznajmljivanja
   */
  public LocalDateTime getDateTime() {
    return dateTime;
  }

  /**
   * Vraća korisnika koji iznajmljuje.
   *
   * @return String korisnickog imena
   */

  public String getUserName() {
    return userName;
  }

  /**
   * Vraća ID vozila koje se iznajmljuje.
   *
   * @return String ID-ja vozila
   */
  public String getVehicleID() {
    return vehicleId;
  }

  /**
   * Vraća početnu lokaciju iznajmljivanja.
   *
   * @return Point pocetne lokacije
   */
  public Point getStartLocation() {
    return startLocation;
  }

  // jesam mogao napraviti jednu metodu negdje koja ispisuje sve Point objekte
  // ovako, ali ovo mi je urednije
  /**
   * Ispisuje početnu lokaciju iznajmljivanja.
   *
   * Formatirano ispisuje pocetnu lokaciju jer ispis Point objekta je neuredan.
   *
   * @return String pocetne lokacije formatiran kao [x,y]
   */
  public String printStartLocation() {
    return "[" + startLocation.x + "," + startLocation.y + "]";
  }

  /**
   * Vraća odredište iznajmljivanja.
   *
   * @return Point odredista
   */
  public Point getEndLocation() {
    return endLocation;
  }

  /**
   * Ispisuje odredište iznajmljivanja.
   *
   * Formatirano ispisuje odrediste jer ispis Point objekta je neuredan.
   *
   * @return String odredista formatiran kao [x,y]
   */
  public String printEndLocation() {
    return "[" + endLocation.x + "," + endLocation.y + "]";
  }

  /**
   * Vraća trajanje iznajmljivanja.
   *
   * @return Cjelobrojna vrijednost trajanja iznajmljivanja izrazeno u sekundama
   */
  public int getDuration() {
    return duration;
  }

  /**
   * Vraća da li se tokom iznamljivanja desio kvar.
   *
   * @return boolean da li je nastao kvar
   */
  public boolean isBreakdown() {
    return breakdown;
  }

  /**
   * Vraća da li tokom iznamljivanja postoji promocija.
   *
   * @return boolean da li postoji promocija
   */
  public boolean isPromotion() {
    return promotion;
  }

  /**
   * Smanjuje bateriju vozila pozivajući funkciju tog vozila za smanjenje
   * baterije.
   */
  public void decreaseBattery() {
    vehicle.decreaseBattery();
  }

  /**
   * Ispisuje preostali postotak baterije.
   *
   * @return Cjelobrojna vrijednost koja predstavlja preostalu bateriju
   */
  public int getBatteryLeft() {
    return vehicle.getBatteryLeft();
  }

  /**
   * Puni bateriju za određenju količinu.
   *
   * @param rechargePercentage iznos za koji se puni baterija.
   */
  public void rechargeBattery(int rechargePercentage) {
    vehicle.recharge(rechargePercentage);
  }

  /**
   * Vraća vozilo koje se iznajmljuje.
   *
   * @return Vehicle objekat vozila koje se iznamljuje
   */
  public Vehicle getVehicle() {
    return vehicle;
  }

  /**
   * Cita fajl iznamljivanja i stavlja u ArrayList.
   *
   * Prima putanju do fajla koji sadrzi pojedinacnja iznamljivanja.
   * Ukoliko vozilo ne postoji u listi vozila nece se dodati to iznajmljivanje.
   * Ukoliko se isto vozilo koristi dva puta u istom terminu dodace se samo prvo
   * pojavljivanje.
   *
   * @param pathToFile putanja do fajla sa iznajmljivanjima
   * @return ArrayList koji sadrzi iznamljivanja unutar {@code pathToFile}
   */
  public static ArrayList<Rental> readFromFile(String pathToFile) {
    ArrayList<Rental> rentals = new ArrayList<>();
    DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm");

    try (BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {
      String line;
      br.readLine();
      while ((line = br.readLine()) != null) {
        try {
          String[] tokens = line.split(
              ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // provjerava da li nakon
                                                    // zareza ima nula ili paran
                                                    // broj navodnika da bi se
                                                    // kooordinate ispravno
                                                    // citale
          if (tokens.length < 8) {
            System.err.println("Neispravan broj argumenata. Linija: " + line);
            continue;
          }

          LocalDateTime dateTime = LocalDateTime.parse(tokens[0].trim(), formatterDateTime);

          String userName = tokens[1].trim();

          String vehicleId = tokens[2].trim();
          if (Vehicle.getVehicleFromId(vehicleId) == null) {
            System.err.println("Vozilo ne postoji. Linija: " + line);
            continue;
          }

          if (isVehicleOverlapping(rentals, dateTime, vehicleId)) {
            System.err.println("Vozilo se vec koristi. Linija: " + line);
            continue;
          }
          Point startLocation = parsePoint(tokens[3].trim());
          Point endLocation = parsePoint(tokens[4].trim());
          if (startLocation.x < 0 || startLocation.x > 20 ||
              startLocation.y < 0 || startLocation.y > 20 ||
              endLocation.x < 0 || endLocation.x > 20 || endLocation.y < 0 ||
              endLocation.y > 20) {
            throw new IllegalArgumentException("Neispravne koordinate.");
          }

          int duration = Integer.parseInt(tokens[5].trim());

          boolean breakdown = parseBoolean(tokens[6].trim());
          if (breakdown) {
            String breakdownDescription = ""; // ovo se niotkud pojavilo, ostavicu ovako samo
            ePJ2.breakdownsModel.addRow(new Object[] {
                Vehicle.getVehicleFromId(vehicleId).getClass().getSimpleName(),
                vehicleId, dateTime.format(formatterDateTime),
                breakdownDescription });
          }

          boolean promotion = parseBoolean(tokens[6].trim());

          rentals.add(new Rental(dateTime, userName, vehicleId, startLocation,
              endLocation, duration, breakdown, promotion));
        } catch (NumberFormatException ex) {
          System.err.println("Neispravan format broja. Linija: " + line);
        } catch (DateTimeParseException ex) {
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
    Collections.sort(rentals, Comparator.comparing(Rental::getDateTime));
    return rentals;
  }

  /**
   * Provjerava da li se vozilo vec koristi.
   *
   * Provjerava da li se u vozilo koristi vise od jednog puta u istom terminu u
   * fajlu sa iznajmljivanjima
   *
   * @param rentals   lista iznamljivanja
   * @param dateTime  termin za koji se provjerava
   * @param vehicleID ID vozila koji se koristi
   * @return boolean koji govori da li se vozilo vec koristi
   */
  private static boolean isVehicleOverlapping(ArrayList<Rental> rentals,
      LocalDateTime dateTime,
      String vehicleId) {
    for (Rental r : rentals) {
      if (dateTime.equals(r.dateTime) && vehicleId.equals(r.vehicleId))
        return true;
    }
    return false;
  }

  /**
   * Parsiranje tacke.
   *
   * Osigurava da je tacka u fajlu sa iznajmljivanjima ispravno formatirana pri
   * citanju
   *
   * @param coordinate token koji nastaje pri citanju linije iznajmljivanja koji
   *                   sadrzi koordinate kao String
   * @return Point objekat koji nastaje pri parsiranju String-a
   */
  private static Point parsePoint(String coordinate)
      throws IllegalArgumentException {
    try {
      coordinate = coordinate.substring(1, coordinate.length() -
          1); // uklanjaju se navodnici
      String xy[] = coordinate.split(",");
      return new Point(Integer.parseInt(xy[0].trim()),
          Integer.parseInt(xy[1].trim()));
    } catch (Exception ex) {
      throw new IllegalArgumentException("Neispravan format koordinata.");
    }
  }

  /**
   * Parsiranje boolean-a.
   *
   * Osigurava da je boolean vrijednost u fajlu sa iznajmljivanjima ispravno
   * formatirana pri citanju, case insensitive Da = true Ne = false
   *
   * @param String token koji nastaje pri citanju linije iznajmljivanja koji
   *               sadrzi boolean vrijednost kao String
   * @return boolean koji nastaje pri citanju "da" ili "ne"
   */
  private static boolean parseBoolean(String str)
      throws IllegalArgumentException {
    if (str.equalsIgnoreCase("da"))
      return true;
    else if (str.equalsIgnoreCase("ne"))
      return false;
    else
      throw new IllegalArgumentException("Neispravna boolean vrijednost: " +
          str);
  }

  /**
   * Racuna rezultate poslovanja.
   *
   * Na osnovu racuna koji su generisani nakon svakog iznajmljivanja racunaju se
   * podaci za dnevni i sumarni izvjestaj:
   * - ukupan prihod (suma svih iznosa za placanje na svim racunima),
   * - ukupan popust (suma svih iznosa popusta sa svih racuna),
   * - ukupno promocije (suma vijednosti svih promocija sa svih racuna),
   * - ukupan iznos svih voznji u uzem i sirem dijelu grada,
   * - ukupan iznos za odrzavanje (ukupan prihod * 0.2),
   * - ukupan iznos za popravke kvarova (koeficijent * cijena nabavke pokvarenog
   * prevoznog sredstva),
   * - ukupni troskovi kompanije (20% ukupnog prihoda),
   * - ukupan porez (ukupan prihod - ukupan iznos za odrzavanje - ukupan iznos
   * za popravke kvarova - troskovi kompanije) * 10%
   * Izvjestaji se ispisuju u fajlovima za svaki dan pojedinacno i sumarno
   */
  public static void businessResults() {
    Double totalIncome, totalDiscount, totalPromotion, totalRidesAmount,
        totalUpkeep, totalRepair, totalCompanyExpenses, totalTax;
    totalIncome = totalDiscount = totalPromotion = totalRidesAmount = totalUpkeep = totalRepair = totalCompanyExpenses = totalTax = 0.0;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy. HH:mm");
    DateTimeFormatter fileDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter outputDateFormatter = DateTimeFormatter.ofPattern("d.M.yyyy.");
    String inputPath = ePJ2.BILLS_OUTPUT_FILE_PATH; // ironicno, jel te
    File folder = new File(inputPath);
    String outputPathSummary = ePJ2.SUMMARY_RESULTS_FILE_PATH;
    String outputPathDaily = ePJ2.DAILY_RESULTS_FILE_PATH;

    Map<LocalDate, Double> priceToPaySums = new HashMap<>();
    Map<LocalDate, Double> promotionSums = new HashMap<>();
    Map<LocalDate, Double> discountSums = new HashMap<>();
    Map<LocalDate, Double> repairSums = new HashMap<>();

    try {
      List<File> files = List.of(folder.listFiles());
      if (files == null)
        throw new IOException();
      for (File file : files) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
          Double priceToPay = 0.0;
          Double discount = 0.0;
          Double promotion = 0.0;
          Double repair = 0.0;
          LocalDateTime dateTime = null;

          String line;
          while ((line = br.readLine()) != null) {
            if (line.startsWith("IZNOS ZA PLACANJE:"))
              priceToPay = Double.parseDouble(line.split(":")[1].trim());
            else if (line.startsWith("Promocija:"))
              promotion = Double.parseDouble(line.split(":")[1].trim());
            else if (line.startsWith("Popust:"))
              discount = Double.parseDouble(line.split(":")[1].trim());
            else if (line.startsWith("Datum i vrijeme:"))
              dateTime = LocalDateTime.parse(line.split("vrijeme:")[1].trim(),
                  formatter);
            else if (line.startsWith("ID prevoznog sredstva:")) {
              String vehicleId = line.split(":")[1].trim();
              Vehicle vehicle = Vehicle.getVehicleFromId(vehicleId);
              repair = vehicle.getPrice() * vehicle.getBreakdownCoefficient();
            }
          }

          if (dateTime != null) {
            LocalDate date = dateTime.toLocalDate();
            priceToPaySums.merge(date, priceToPay, Double::sum);
            promotionSums.merge(date, promotion, Double::sum);
            discountSums.merge(date, discount, Double::sum);
            repairSums.merge(date, repair, Double::sum);
          }
        } catch (IOException ex) {
          System.err.println("Greska pri citanju fajla: " + file.getName() +
              ": " + ex.getMessage());
        }
      }

      for (LocalDate date : priceToPaySums.keySet()) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(
            outputPathDaily + date.format(fileDateFormatter) + ".txt"))) {
          bw.write("Datum: " + date.format(outputDateFormatter) + "\n");
          bw.write("Ukupan prihod: " + priceToPaySums.get(date) + "\n");
          bw.write("Ukupan popust: " + discountSums.get(date) + "\n");
          bw.write("Ukupno promocije: " + promotionSums.get(date) + "\n");
          bw.write("Ukupan iznos svih voznji: \n"); // TODO: ovo iskreno pojma
                                                    // nemam sta treba biti
          bw.write("Ukupan iznos za odrzavanje: " +
              (priceToPaySums.get(date) * 0.2) + "\n");
          bw.write("Ukupan iznos za popravke kvarova: " + repairSums.get(date) +
              "\n");

          totalIncome += priceToPaySums.get(date);
          totalDiscount += discountSums.get(date);
          totalPromotion += promotionSums.get(date);
          totalUpkeep += (priceToPaySums.get(date) * 0.2);
          totalRepair += repairSums.get(date);
          totalCompanyExpenses += (priceToPaySums.get(date) * 0.2); // ???
        } catch (IOException ex) {
          ex.printStackTrace();
        }
      }
    } catch (IOException ex) {
      System.err.println("Greska pri citanju fajlova u direktorijumu: " +
          folder + ": " + ex.getMessage());
    }
    totalTax = (totalIncome - totalUpkeep - totalRepair - totalCompanyExpenses) * 0.1;
    try (BufferedWriter bw = new BufferedWriter(
        new FileWriter(outputPathSummary + "Sumarni izvjestaj.txt"))) {
      bw.write("Ukupan prihod: " + totalIncome + "\n");
      bw.write("Ukupan popust: " + totalDiscount + "\n");
      bw.write("Ukupno promocije: " + totalPromotion + "\n");
      // bw.write("Ukupan iznos svih voznji: \n");
      bw.write("Ukupan iznos za odrzavanje: " + totalUpkeep + "\n");
      bw.write("Ukupan iznos za popravke kvarova: " + totalRepair + "\n");
      bw.write("Ukupni troskovi kompanije: " + totalCompanyExpenses + "\n");
      bw.write("Ukupan porez: " + totalTax +
          "\n"); // u primjerima na sajtu ima tako mnogo kvarova da je
                 // porez u minusu
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Serijalizovanje vozila koja su se pokvarila.
   *
   * Citaju se racuni iznamljivanja i provjerava se da se na vozilu desio kvar.
   * Ukoliko jeste, zapisuju se vozilo i cijena kvara u zaseban .ser fajl.
   */
  public static void serializeBrokenVehicles() {
    String inputPath = ePJ2.BILLS_OUTPUT_FILE_PATH;
    String outputPath = ePJ2.BREAKDOWNS_OUTPUT_FILE_PATH;
    File folder = new File(inputPath);
    try {
      List<File> files = List.of(folder.listFiles());
      if (files == null)
        throw new IOException();
      for (File file : files) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
          String line;
          String vehicleId = "";
          int i = 0;
          while ((line = br.readLine()) != null) {
            if (line.startsWith("ID prevoznog sredstva: "))
              vehicleId = line.split(":")[1].trim();
            if (line.equals("Kvarovi: da")) {
              Vehicle vehicle = Vehicle.getVehicleFromId(vehicleId);
              try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                  outputPath + vehicleId + (i++) + ".ser"))) {
                oos.writeObject(vehicle);
                oos.writeDouble(vehicle.getBreakdownCoefficient() *
                    vehicle.getPrice());
              }
            }
          }
        } catch (IOException ex) {
          System.err.println("Greska pri citanju fajla: " + file.getName() +
              ": " + ex.getMessage());
        }
      }
    } catch (IOException ex) {
      System.err.println("Greska pri citanju fajlova u direktorijumu: " +
          folder + ": " + ex.getMessage());
    }
  }

  /**
   * Deserijalizovanje vozila koja su se pokvarila.
   *
   * Citaju se fajlovi nastali u {@code serializeBrokenVehicles}. Citaju se
   * vozilo i cijena popravke i ispisuju u tabelu koja se prikazuje ukoliko
   * korisnik to zeli.
   */
  public static void deserializeBrokenVehicles() {
    String inputPath = ePJ2.BREAKDOWNS_OUTPUT_FILE_PATH;
    File folder = new File(inputPath);
    try {
      List<File> files = List.of(folder.listFiles());
      if (files == null)
        throw new IOException();
      for (File file : files) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
          Vehicle vehicle = (Vehicle) ois.readObject();
          double repair = ois.readDouble();
          ePJ2.breakdownsPricesModel.addRow(new Object[] {
              vehicle.getId(), vehicle.getManufacturer(), vehicle.getModel(),
              vehicle.getPrice(), vehicle.getBreakdownCoefficient(), repair });
          // System.out.println(vehicle);
          // System.out.printf("Cijena popravka: %.2f \n", repair);
        } catch (IOException ex) {
          System.err.println("Greska pri citanju fajla: " + file.getName() +
              ": " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
          ex.printStackTrace();
        }
      }
    } catch (IOException ex) {
      System.err.println("Greska pri citanju fajlova u direktorijumu: " +
          folder + ": " + ex.getMessage());
    }
  }

  /**
   * Prati broj iznamljivanja korisnika.
   *
   * Broji koliko puta je svaki korisnik iznajmio vozilo u svrhu racunanja
   * popusta.
   *
   * @param users HashMap-a koja sadrzi ID korisnika i broj koristenja
   * @param user  ID korisnika koji trenutno iznamljuje vozilo, da mu se doda
   *              novo koristenje, ili da se on doda u HashMap-u
   */
  public static void recordUsageCount(HashMap<String, Integer> users,
      String user) {
    if (users.containsKey(user))
      users.put(user,
          users.get(user) +
              1); // ako je vec iznajmljivao povecava se broj koristenja
    else
      users.put(user, 1); // u suprotnom se dodaje jer prvi put koristi
  }

  @Override
  public String toString() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm");
    return "Iznajmljivanje{Datum=" + dateTime.format(formatter) +
        ",\n  korisnik=" + userName + ",\n  ID vozila=" + vehicleId +
        ",\n  pocetna lokacija=" + startLocation +
        ",\n  odrediste=" + endLocation + ",\n  trajanje=" + duration +
        ",\n  kvar=" + (breakdown ? "da" : "ne") +
        ",\n  promocija=" + (promotion ? "da" : "ne") + "}";
  }
}
