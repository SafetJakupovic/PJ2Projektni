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
 * RentalThread
 */
// vjerovatno sam mogao sve u Rental klasu, ali bi mi bilo ruzno iako bi bilo
// mozda cak brze
public class RentalThread extends Thread {
  private Rental rental;
  private GridMap gridMap;
  private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

  public RentalThread(Rental rental, GridMap gridMap) {
    this.rental = rental;
    this.gridMap = gridMap;
  }

  @Override
  public void run() {
    Point startLocation = rental.getStartLocation();
    Point endLocation = rental.getEndLocation();
    int duration = rental.getDuration();
    int steps = Math.max(Math.abs(endLocation.x - startLocation.x),
        Math.abs(endLocation.y - startLocation.y));
    long sleepTime = (duration * 1000) / steps;

    boolean isDistanceWide = false;

    Point currentLocation = new Point(startLocation);

    for (int i = 0; i < steps; i++) {
      Point previousLocation = new Point(currentLocation);
      isDistanceWide = (currentLocation.x < 5 || currentLocation.x > 14 ||
          currentLocation.y < 5 || currentLocation.y > 14)
              ? true
              : false;

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

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(
        ePJ2.BILLS_OUTPUT_FILE_PATH + rental.getDateTime().format(formatter) +
            "_" + rental.getVehicleID() + ".txt"))) {
      bw.write("Osnovna cijena: " + rental.getVehicle().getUnitPrice() + "\n");
      bw.write("Udaljenost: " +
          (isDistanceWide ? "Siri dio grada\n" : "Uzi dio grada\n"));
      bw.write("Kvarovi: " + (rental.isBreakdown() ? "da\n" : "ne\n"));
      long overall = rental.getVehicle().getUnitPrice() * rental.getDuration() *
          (isDistanceWide ? ePJ2.DISTANCE_WIDE : ePJ2.DISTANCE_NARROW);
      long promotionDiscount = (rental.isPromotion() ? (overall * ePJ2.DISCOUNT_PROM / 100) : 0);
      bw.write("Promocija: " + promotionDiscount + "\n");
      long discount = ((ePJ2.users.get(rental.getUserName()) % 10 == 0)
          ? (overall * ePJ2.DISCOUNT / 100)
          : 0);
      bw.write("Popust: " + discount + "\n");
      bw.write("UKUPNO: " + overall + "\n");
      long priceToPay = overall - promotionDiscount - discount;
      bw.write("IZNOS ZA PLACANJE: " + priceToPay * (rental.isBreakdown() ? 0 : 1));
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  // TODO: mijenjati boje pozadine, trebacu vjerovatno proslijediti koordinate
  // mozda i necu, mozda mogu iz run() metode
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
  @Override
  public String toString() {
    return "> " + rental.getVehicleID();
  }
}
