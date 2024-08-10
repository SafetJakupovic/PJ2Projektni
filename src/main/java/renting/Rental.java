package renting;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import main.ePJ2;
import vehicles.*;

/**
 * Rent
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

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public String getUserName() {
    return userName;
  }

  public String getVehicleID() {
    return vehicleId;
  }

  public Point getStartLocation() {
    return startLocation;
  }

  public Point getEndLocation() {
    return endLocation;
  }

  public int getDuration() {
    return duration;
  }

  public boolean isBreakdown() {
    return breakdown;
  }

  public boolean isPromotion() {
    return promotion;
  }

  public void decreaseBattery() {
    vehicle.decreaseBattery();
  }

  public int getBatteryLeft() {
    return vehicle.getBatteryLeft();
  }

  public Vehicle getVehicle() {
    return vehicle;
  }

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
            System.err.println(
                "Neispravna linija u fajlu sa iznajmljivanjima: " + line +
                    "\nNeispravan broj argumenata");
            continue;
          }

          LocalDateTime dateTime = LocalDateTime.parse(tokens[0].trim(), formatterDateTime);

          String userName = tokens[1].trim();
          recordUsageCount(ePJ2.users, userName);

          String vehicleId = tokens[2].trim();
          if (Vehicle.getVehicleFromId(vehicleId) == null) {
            System.err.println("Vozilo ne postoji. Linija: " + line);
            continue;
          }

          if (isVehicleOverlapping(rentals, dateTime, vehicleId)) {
            System.err.println("Vozilo se vec koristi. Linija: " + line);
            continue;
          }
          // TODO: ako je auto mora se predat licna vozacka
          // GDJE
          // TODO: Ne moze isto vozilo vise od jednog puta u istom terminu

          Point startLocation = parsePoint(tokens[3].trim());
          Point endLocation = parsePoint(tokens[4].trim());

          int duration = Integer.parseInt(tokens[5].trim());

          boolean breakdown = parseBoolean(tokens[6].trim());
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

  private static boolean isVehicleOverlapping(ArrayList<Rental> rentals,
      LocalDateTime dateTime,
      String vehicleId) {
    for (Rental r : rentals) {
      if (dateTime.equals(r.dateTime) && vehicleId.equals(r.vehicleId))
        return true;
    }
    return false;
  }

  // Stvaranje tacke na osnovu tokena iz linije input fajla
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

  private static void recordUsageCount(HashMap<String, Integer> users,
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
