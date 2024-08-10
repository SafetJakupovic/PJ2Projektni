package vehicles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import main.ePJ2;

/**
 * Vehicle
 */
public abstract class Vehicle {
  protected String ID;
  protected String manufacturer; // moze biti i zasebna klasa tbh
  protected String model;
  protected double price;
  protected int batteryLeft;
  protected int unitPrice;

  public Vehicle() {
  }

  public Vehicle(String ID, String manufacturer, String model, double price) {
    this.ID = ID;
    this.manufacturer = manufacturer;
    this.model = model;
    this.price = price;
    batteryLeft = 100;
  }

  public String getId() {
    return ID;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setModel(String model) {
    this.model = model;
  }

  public String getModel() {
    return model;
  }

  public void setPrice(double price) {
    this.price = (price > 0) ? price : this.price;
  }

  public double getPrice() {
    return price;
  }

  public int getBatteryLeft() {
    return batteryLeft;
  }

  // nije mi padalo na pamet koji bi bio najlogicniji nacin da se baterija trosi
  public void decreaseBattery() {
    batteryLeft -= 2;
  }

  public int getUnitPrice() {
    return unitPrice;
  }

  public void recharge(int rechargePercentage) {
    this.batteryLeft = (this.batteryLeft + rechargePercentage >= 100)
        ? 100
        : this.batteryLeft + rechargePercentage;
  }

  public static ArrayList<Vehicle> readFromFile(String pathToFile) {
    ArrayList<Vehicle> vehicles = new ArrayList<>();
    DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("d.M.yyyy.");
    try (BufferedReader br = new BufferedReader(
        new FileReader("src/main/resources/input/vehicles.csv"))) {
      String line;
      br.readLine();
      while ((line = br.readLine()) != null) {
        String[] tokens = line.split(",");
        // TODO: mozda provjeriti broj elemenata linije
        // greske van opsega
        // nedovoljan broj elemenata...
        String id = tokens[0].trim();
        if (vehicles.stream().anyMatch(vehicle -> vehicle.getId().equals(id)))
          System.err.println("Pronadjen duplikat!");
        String manufacturer = tokens[1].trim();
        String model = tokens[2].trim();
        double price = Double.parseDouble(tokens[4].trim());
        String type = tokens[8].trim();
        if ("automobil".equalsIgnoreCase(type)) {
          LocalDate dateOfPurchase = LocalDate.parse(tokens[3].trim(), formatterDate);
          String description = tokens[6];
          vehicles.add(new Automobile(id, manufacturer, model, price,
              dateOfPurchase, description));
        } else if ("bicikl".equalsIgnoreCase(type)) {
          int range = Integer.parseInt(tokens[5].trim());
          vehicles.add(new EBike(id, manufacturer, model, price, range));
        } else if ("trotinet".equalsIgnoreCase(type)) {
          int maxVelocity = Integer.parseInt(tokens[6].trim());
          vehicles.add(
              new EScooter(id, manufacturer, model, price, maxVelocity));
        } else {
          throw new Exception("Neispravan unos prevoznog sredstva!");
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return vehicles;
  }

  public static Vehicle getVehicleFromId(String vehicleId) {
    for (Vehicle v : ePJ2.vehicles) {
      if (vehicleId.equalsIgnoreCase(v.getId()))
        return v;
    }
    return null;
  }

  @Override
  public String toString() {
    return "ID: " + ID + "\n" + manufacturer + " " + model +
        "\nCijena: " + price + "\n" + batteryLeft + "% baterije";
  }
}
