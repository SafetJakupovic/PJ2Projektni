package renting;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import javax.swing.SwingUtilities;
import main.ePJ2;
import map.GridMap;

/**
 * Sadrži pojedinačno iznajmljivanje, to jeste objekat klase {@code Rental} i
 * mapu predstavljenu pomoću {@code GridMap}.
 */
public class RentalThread extends Thread {
  // vjerovatno sam mogao sve u Rental klasu, ali bi mi bilo ruzno iako bi bilo
  // mozda cak brze
  private Rental rental;
  private GridMap gridMap;

  // ovo troje ne spominjem u dokumentaciji jer ne predstvljaju sustinu klase,
  // ali je moguce da grijesim, ne znam koja je praksa sto se toga tice
  private DateTimeFormatter fileDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
  private DateTimeFormatter outputDateFormatter = DateTimeFormatter.ofPattern("d.M.yyyy. HH:mm");
  private int DISCOUNT_USES = 10; // neka ovdje radi testiranja

  /**
   * Konstruiše novi {@code RentalThread} objekat.
   *
   * @param rental  {@code Rental} objekat čije će se iznajmljivanje izvršiti
   * @param gridMap mapa na kojoj se izvršava iznajmljivanje predstavljeno kao
   *                {@code GridMap} objekat
   */
  public RentalThread(Rental rental, GridMap gridMap) {
    this.rental = rental;
    this.gridMap = gridMap;
  }

  /**
   * Izvršava, tj. simuliše, se iznajmljivanje.
   *
   * Prvo se provjerava da li se iznajmljivanje dešava u užem ili širem dijelu
   * grada da bi se primjereno izračunala cijena na kraju.
   * Vozilo se kreće od početne ka krajnjoj tački tako što se trenutna lokacija
   * i po x i po y osi smanjuje ili povećava u zavisnosti da li je krajnja tačka
   * manja ili veca po x i y osi u odnosu na trenutnu, tj. moguće je kretanje u
   * oba smjera odjednom čime je moguće dijagonalno kretanje. Sinhronizovano se
   * ažurira mapa tako da se na ispravnim poljima upiše gdje je koje vozilo u
   * datom trenutku kao i njegovu preostalu bateriju. Kada se iznamljivanje
   * izvrši ćelija postaje zelena ako je sve u redu, a crvena ako se desio kvar
   * ili ako je baterija prazna. Na kraju se izračunava cijena i račun se
   * upisuje u zaseban fajl na putanji definisanoj u config fajlu.
   */
  @Override
  public void run() {
    Point startLocation = rental.getStartLocation();
    Point endLocation = rental.getEndLocation();
    int duration = rental.getDuration();
    int steps = Math.max(Math.abs(endLocation.x - startLocation.x),
        Math.abs(endLocation.y - startLocation.y));
    long sleepTime = (duration * 1000) / steps;

    boolean isDistanceWide = (startLocation.x < 5 || startLocation.x > 14 || startLocation.y < 5 ||
        startLocation.y > 14 || endLocation.x < 5 || endLocation.x > 14 ||
        endLocation.y < 5 || endLocation.y > 14)
            ? true
            : false;

    Point currentLocation = new Point(startLocation);

    for (int i = 0; i < steps && rental.getBatteryLeft() > 0; i++) {
      Point previousLocation = new Point(currentLocation);

      if (currentLocation.x < endLocation.x)
        currentLocation.x++;
      else if (currentLocation.x > endLocation.x)
        currentLocation.x--;

      if (currentLocation.y < endLocation.y)
        currentLocation.y++;
      else if (currentLocation.y > endLocation.y)
        currentLocation.y--;
      rental.decreaseBattery();

      try {
        // koristio sam prvo InvokeLater, ali se znalo destiti da ostanu
        // prethodne celije neobrisane ako se vozila brzo krecu pored toga je
        // super radilo, potpuno isto
        SwingUtilities.invokeAndWait(() -> {
          synchronized (gridMap.getGridMap()[previousLocation.x][previousLocation.y]) {
            String previousVehiclesInCell = gridMap.getGridMap()[previousLocation.x][previousLocation.y].getText();
            String updatedCellString = removeCorrectVehicleFromCell(previousVehiclesInCell, rental.getVehicleID());
            gridMap.getGridMap()[previousLocation.x][previousLocation.y].setText(updatedCellString);
            gridMap.getGridMap()[previousLocation.x][previousLocation.y].setBackground(Color.GRAY);
          }
          synchronized (gridMap.getGridMap()[currentLocation.x][currentLocation.y]) {
            String currentVehiclesInCell = gridMap.getGridMap()[currentLocation.x][currentLocation.y]
                .getText();
            String newString = currentVehiclesInCell.replace(
                "</html>", "<br>" + rental.getVehicleID() + ", " +
                    rental.getBatteryLeft() + "%"
                    + "</html>");
            gridMap.getGridMap()[currentLocation.x][currentLocation.y].setText(
                newString);
            gridMap.getGridMap()[currentLocation.x][currentLocation.y]
                .setBackground(Color.BLUE);
          }
        });
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt(); // "obnavlja" interrupt status
      } catch (InvocationTargetException ex) {
        ex.printStackTrace();
      }

      try {
        Thread.sleep(sleepTime);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }

    if (rental.getBatteryLeft() > 0 && !rental.isBreakdown())
      gridMap.getGridMap()[currentLocation.x][currentLocation.y].setBackground(
          Color.GREEN);
    else
      gridMap.getGridMap()[currentLocation.x][currentLocation.y].setBackground(
          Color.RED);

    Rental.recordUsageCount(ePJ2.users, rental.getUserName());
    try (BufferedWriter bw = new BufferedWriter(
        new FileWriter(ePJ2.BILLS_OUTPUT_FILE_PATH +
            rental.getDateTime().format(fileDateFormatter) + "_" +
            rental.getVehicleID() + ".txt"))) {
      bw.write("Datum i vrijeme: " +
          rental.getDateTime().format(outputDateFormatter) + "\n");
      bw.write("Korisnik: " + rental.getUserName() + "\n");
      bw.write("ID prevoznog sredstva: " + rental.getVehicleID() + "\n");
      bw.write("Pocetna lokacija: " + rental.printStartLocation() + "\n");
      bw.write("Odrediste: " + rental.printEndLocation() + "\n");
      bw.write("Trajanje: " + rental.getDuration() + "\n");

      bw.write("Osnovna cijena: " + rental.getVehicle().getUnitPrice() + "\n");
      bw.write("Udaljenost: " +
          (isDistanceWide ? "Siri dio grada\n" : "Uzi dio grada\n"));
      bw.write("Kvarovi: " + (rental.isBreakdown() ? "da\n" : "ne\n"));
      double overall = rental.getVehicle().getUnitPrice() * rental.getDuration() *
          (isDistanceWide ? ePJ2.DISTANCE_WIDE : ePJ2.DISTANCE_NARROW);
      double promotionDiscount = (rental.isPromotion() ? (overall * ePJ2.DISCOUNT_PROM / 100) : 0);
      bw.write("Promocija: " + promotionDiscount + "\n");
      double discount = ((ePJ2.users.get(rental.getUserName()) % DISCOUNT_USES == 0)
          ? (overall * ePJ2.DISCOUNT / 100)
          : 0);
      bw.write("Popust: " + discount + "\n");
      bw.write("UKUPNO: " + overall + "\n");
      double priceToPay = (overall - promotionDiscount - discount) * (rental.isBreakdown() ? 0 : 1);
      bw.write("IZNOS ZA PLACANJE: " + priceToPay);
      ePJ2.resultsModel.addRow(
          new Object[] { rental.getDateTime().format(outputDateFormatter),
              rental.getUserName(), rental.getVehicleID(),
              rental.printStartLocation(), rental.printEndLocation(),
              rental.getDuration(), rental.getVehicle().getUnitPrice(),
              (isDistanceWide ? "Siri dio grada\n" : "Uzi dio grada\n"),
              (rental.isBreakdown() ? "da\n" : "ne\n"), promotionDiscount,
              discount, priceToPay });
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    // rental.rechargeBattery(50);
  }

  /**
   * Uklanja ispravno vozilo iz ćelije.
   *
   * Prilikom kretanja vozila moze se desiti da se više od jednog nalazi u istoj
   * ćeliji zbog čega se mora ukloniti samo jedno, ispravno vozilo.
   */
  private String removeCorrectVehicleFromCell(String cellString,
      String vehicleID) {
    String startTag = "<br>" + vehicleID + ", ";
    int startIndex = cellString.indexOf(startTag);
    if (startIndex == -1)
      return cellString; // ako trazeno vozilo nije tu
    int endIndex = cellString.indexOf("</html>", startIndex);
    int nextVehicleIndex = cellString.indexOf("<br>", startIndex + startTag.length());
    if (nextVehicleIndex != -1 && nextVehicleIndex < endIndex)
      endIndex = nextVehicleIndex;
    String updatedCellString = cellString.substring(0, startIndex) + cellString.substring(endIndex);
    return updatedCellString.replaceAll(
        "(?m)^[\t]*\r?\n", ""); // da se rijesi bilo kakvih praznih linija
  }

  // radi testiranja
  /*
   * @Override
   * public String toString() {
   * return "> " + rental.getVehicleID();
   * }
   */
}
