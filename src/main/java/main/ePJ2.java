package main;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import map.GridMap;
import renting.*;
import vehicles.*;

/**
 * ePJ2
 */
public class ePJ2 {
  public static String VEHICLES_INPUT_FILE_PATH;
  public static String RENTALS_INPUT_FILE_PATH;
  public static String BILLS_OUTPUT_FILE_PATH;
  public static int DISTANCE_NARROW;
  public static int DISTANCE_WIDE;
  public static int DISCOUNT;
  public static int DISCOUNT_PROM;
  public static int CAR_UNIT_PRICE;
  public static int SCOOTER_UNIT_PRICE;
  public static int BIKE_UNIT_PRICE;
  public static Properties properties = new Properties();
  public static String propertiesFilePath = "src/main/resources/config.properties";
  public static HashMap<String, Integer> users = new HashMap<>(); // da prati sve korisnike radi promocije nakon svakog
                                                                  // 10.
                                                                  // koristenja
  // ovo nvim formatira ovako komentare, nije do mene

  static {
    try {
      properties.load(new FileInputStream(propertiesFilePath));
      VEHICLES_INPUT_FILE_PATH = properties.getProperty("VEHICLES_INPUT_FILE_PATH");
      RENTALS_INPUT_FILE_PATH = properties.getProperty("RENTALS_INPUT_FILE_PATH");
      BILLS_OUTPUT_FILE_PATH = properties.getProperty("BILLS_OUTPUT_FILE_PATH");
      DISTANCE_NARROW = Integer.parseInt(properties.getProperty("DISTANCE_NARROW"));
      DISTANCE_WIDE = Integer.parseInt(properties.getProperty("DISTANCE_WIDE"));
      DISCOUNT = Integer.parseInt(properties.getProperty("DISCOUNT"));
      DISCOUNT_PROM = Integer.parseInt(properties.getProperty("DISCOUNT_PROM"));
      CAR_UNIT_PRICE = Integer.parseInt(properties.getProperty("CAR_UNIT_PRICE"));
      SCOOTER_UNIT_PRICE = Integer.parseInt(properties.getProperty("SCOOTER_UNIT_PRICE"));
      BIKE_UNIT_PRICE = Integer.parseInt(properties.getProperty("BIKE_UNIT_PRICE"));
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public static ArrayList<Vehicle> vehicles = Vehicle.readFromFile(VEHICLES_INPUT_FILE_PATH);
  public static ArrayList<Rental> rentals = Rental.readFromFile(RENTALS_INPUT_FILE_PATH);
  public static JFrame frame = new JFrame("Iznajmljivanje");

  public static void main(String[] args) {
    // vehicles.forEach(System.out::println);
    // rentals.forEach(System.out::println);

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    GridMap gridMap = new GridMap();

    SwingUtilities.invokeLater(() -> {
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
      frame.setSize(800, 800);
      frame.setLayout(new GridLayout(20, 20));
    });

    ConcurrentLinkedQueue<Rental> rentalsQueue = new ConcurrentLinkedQueue<>(rentals);

    while (!rentalsQueue.isEmpty()) {
      ArrayList<RentalThread> batch = new ArrayList<>();
      ArrayList<Rental> rentalsToRemove = new ArrayList<>();
      LocalDateTime currentDateTime = rentalsQueue.peek().getDateTime();
      rentalsQueue.stream()
          .takeWhile(rental -> rental.getDateTime().equals(currentDateTime))
          .forEach(rental -> {
            batch.add(new RentalThread(rental, gridMap));
            rentalsToRemove.add(rental);
          });
      rentalsQueue.removeAll(rentalsToRemove); // najjeftiniji nacin sto se tice
      // duzine i kompleksnosti koda,
      // mada nisam ljubitelj ovoga
      for (RentalThread rt : batch)
        rt.start();
      try {
        for (RentalThread rt : batch)
          rt.join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }

      gridMap.emptyGrid();
    }
  }
}
