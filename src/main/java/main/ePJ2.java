package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;
import map.GridMap;
import renting.*;
import vehicles.*;

/**
 * Glavna klasa koja sadrži main metodu kao i mnoga polja koja koriste druge
 * klase.
 */
public class ePJ2 {
  /** Podrazumijevani konstruktor. */
  public ePJ2() {
  }

  /** Putanja fajla koji sadrži sva vozila. */
  public static String VEHICLES_INPUT_FILE_PATH;
  /** Putanja fajla koji sadrži sva iznajmljivanja. */
  public static String RENTALS_INPUT_FILE_PATH;
  /** Putanja gdje će biti kreirani računi. */
  public static String BILLS_OUTPUT_FILE_PATH;
  /** Putanja gdje će biti kreiran sumarni izvještaj. */
  public static String SUMMARY_RESULTS_FILE_PATH;
  /** Putanja gdje će biti kreirani dnevni izvještaji. */
  public static String DAILY_RESULTS_FILE_PATH;
  /** Cijena vožnje u užem dijelu grada. */
  public static int DISTANCE_NARROW;
  /** Cijena vožnje u širem dijelu grada. */
  public static int DISTANCE_WIDE;
  /** Iznos popusta za svako deseto iznajmljivanje. */
  public static int DISCOUNT;
  /** Iznos popusta prilikom pormocije. */
  public static int DISCOUNT_PROM;
  /** Cijena vožnje automobilom. */
  public static int CAR_UNIT_PRICE;
  /** Cijena vožnje trotinetom. */
  public static int SCOOTER_UNIT_PRICE;
  /** Cijena vožnje biciklom. */
  public static int BIKE_UNIT_PRICE;
  /** Putanja gdje će biti kreiran fajl */
  public static String BREAKDOWNS_OUTPUT_FILE_PATH;
  /** Objekat konfiguracionog fajla. */
  public static Properties properties = new Properties();
  /** Putanja do konfiguracionog fajla. */
  public static String propertiesFilePath = "src/main/resources/config.properties";
  /**
   * Hešmapa koja sadrži sve korisnike i prati koliko je koji puta iznajmljivao
   * vozilo.
   */
  public static HashMap<String, Integer> users = new HashMap<>();
  /** Glavni prozor. */
  public static JFrame frame = new JFrame("ePJ2");
  /** Prozor za kvarove. */
  public static JFrame breakdownsFrame = new JFrame("Kvarovi");
  /** Panel u glavnom prozoru koji sadrži mapu. */
  public static JPanel gridPanel = new JPanel(new GridLayout(20, 20));

  // iskreno kasno sam skontao da se mozda bas bas tabela trazi, kao sto sam
  // radio za rezultate i kvarove, ali posto bih morao raditi tri tabele sa
  // nekoliko kolona za svaku, to bi bilo veoma neuredno i za UI i za kod,
  // svakako ne vrsi neku pravu funkciju osim ispisa tako da sam odlucio
  // ostaviti za vozila samo listu
  /** Panel za ispis svih vozila koji sadrži tri kolone. */
  public static JPanel vehiclesPanel = new JPanel(new GridLayout(1, 3));
  /** List model za automobile. */
  public static DefaultListModel<String> carListModel = new DefaultListModel<>(); // upravlja podacima
  /** JList za automobile koji prima {@code carListModeli}. */
  private static JList<String> carList = new JList<>(carListModel); // upravlja ispisom, izgledom, funkcionalnoscu,
                                                                    // generalno bitne stvari, automatski se
                                                                    // azurira kada i DefautListModel
  /** List model za bicikla. */
  public static DefaultListModel<String> bikeListModel = new DefaultListModel<>();
  /** JList za bicikla koji prima {@code bikeListModel} */
  private static JList<String> bikeList = new JList<>(bikeListModel);
  /** List model za trotinete. */
  public static DefaultListModel<String> scooterListModel = new DefaultListModel<>();
  /** JList za trotinete koji prima {@code scooterListModel}. */
  private static JList<String> scooterList = new JList<>(scooterListModel);

  /** Panel za rezultate poslovanja. */
  public static JPanel resultsPanel = new JPanel();
  /** Kategorije rezultata poslovanja. */
  private static String[] resultsColumnNames = {
      "Datum i vrijeme", "Korisnik", "ID vozila", "Pocetna lokacija",
      "Odrediste", "Trajanje", "Osnovna cijena", "Udaljenost",
      "Kvarovi", "Promocija", "Popust", "Ukupno za placanje" };
  /**
   * Table model za rezultate poslovanje koji koristi {@code
   * resultsColumnNames}.
   */
  public static DefaultTableModel resultsModel = new DefaultTableModel(resultsColumnNames, 0);
  /** JTable za rezultate poslovanja koji koristi {@code resultsModel}. */
  private static JTable resultsTable = new JTable(resultsModel);

  /** Panel za kvarove. */
  private static JPanel breakdownsPanel = new JPanel();
  /** Kolone kvarova. */
  private static String[] breakdownsColumnNames = {
      "Vrsta prevoznog sredstva", "ID", "Vrijeme", "Opis kvara" };
  /** Table model za kvarove koji koristi {@code breakdownsColumnNames}. */
  public static DefaultTableModel breakdownsModel = new DefaultTableModel(breakdownsColumnNames, 0);
  /** JTable za kvarove koji koristi {@code breakdownsModel}. */
  private static JTable breakdownsTable = new JTable(breakdownsModel);

  /** Panel za cijene popravke kvarova. */
  public static JPanel breakdownsPricesPanel = new JPanel();
  /** Kolone za cijene popravke kvarova. */
  private static String[] breakdownsPricesColumnNames = { "ID",
      "Proizvodjac",
      "Model",
      "Cijena nabavke",
      "Koeficijent popravka",
      "Cijena popravka" };
  /**
   * Table model za cijene popravke kvarova koji koristi {@code
   * breakdownsPricesColumnNames}.
   */
  public static DefaultTableModel breakdownsPricesModel = new DefaultTableModel(breakdownsPricesColumnNames, 0);
  /**
   * JTable za cijene popravke kvarova koji koristi {@code
   * breakdownsPricesModel}.
   */
  private static JTable breakdownsPricesTable = new JTable(breakdownsPricesModel);

  static {
    try {
      properties.load(new FileInputStream(propertiesFilePath));
      VEHICLES_INPUT_FILE_PATH = properties.getProperty("VEHICLES_INPUT_FILE_PATH");
      RENTALS_INPUT_FILE_PATH = properties.getProperty("RENTALS_INPUT_FILE_PATH");
      BILLS_OUTPUT_FILE_PATH = properties.getProperty("BILLS_OUTPUT_FILE_PATH");
      SUMMARY_RESULTS_FILE_PATH = properties.getProperty("SUMMARY_RESULTS_FILE_PATH");
      DAILY_RESULTS_FILE_PATH = properties.getProperty("DAILY_RESULTS_FILE_PATH");
      DISTANCE_NARROW = Integer.parseInt(properties.getProperty("DISTANCE_NARROW"));
      DISTANCE_WIDE = Integer.parseInt(properties.getProperty("DISTANCE_WIDE"));
      DISCOUNT = Integer.parseInt(properties.getProperty("DISCOUNT"));
      DISCOUNT_PROM = Integer.parseInt(properties.getProperty("DISCOUNT_PROM"));
      CAR_UNIT_PRICE = Integer.parseInt(properties.getProperty("CAR_UNIT_PRICE"));
      SCOOTER_UNIT_PRICE = Integer.parseInt(properties.getProperty("SCOOTER_UNIT_PRICE"));
      BIKE_UNIT_PRICE = Integer.parseInt(properties.getProperty("BIKE_UNIT_PRICE"));
      BREAKDOWNS_OUTPUT_FILE_PATH = properties.getProperty("BREAKDOWNS_OUTPUT_FILE_PATH");
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    // glup je nacin, znam
    carListModel.addElement("Automobili");
    carListModel.addElement("-----------------");
    bikeListModel.addElement("Bicikli");
    bikeListModel.addElement("-----------------");
    scooterListModel.addElement("Trotineti");
    scooterListModel.addElement("----------------");
  }

  /**
   * ArrayList svih vozila koja se čitaju iz fajla na putanji {@code
   * VEHICLES_INPUT_FILE_PATH}.
   */
  public static ArrayList<Vehicle> vehicles = Vehicle.readFromFile(VEHICLES_INPUT_FILE_PATH);
  /**
   * ArrayList svih iznamljivanja koja se čitaju iz fajla na putanji {@code
   * RENTALS_INPUT_FILE_PATH}.
   */
  public static ArrayList<Rental> rentals = Rental.readFromFile(RENTALS_INPUT_FILE_PATH);

  /**
   * Glavna metoda.
   *
   * @param args argumenti
   */
  public static void main(String[] args) {
    // vehicles.forEach(System.out::println);
    // rentals.forEach(System.out::println);

    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    GridMap gridMap = new GridMap();

    vehiclesPanel.add(new JScrollPane(carList));
    vehiclesPanel.add(new JScrollPane(bikeList));
    vehiclesPanel.add(new JScrollPane(scooterList));

    resultsPanel.setLayout(
        new BorderLayout()); // da ispuni sirinom cijeli prozor
    resultsPanel.add(new JScrollPane(resultsTable));

    breakdownsPanel.setLayout(
        new BorderLayout()); // da ispuni visinom cijeli prozor
    breakdownsPanel.add(new JScrollPane(breakdownsTable));

    frame.setSize(800, 800);
    gridPanel.setPreferredSize(new Dimension(600, 600));
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
    frame.setLayout(new BorderLayout());
    frame.add(gridPanel, BorderLayout.CENTER);
    frame.add(vehiclesPanel, BorderLayout.WEST);
    frame.add(breakdownsPanel, BorderLayout.EAST);
    frame.add(resultsPanel, BorderLayout.SOUTH);
    frame.pack();

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
      rentalsQueue.removeAll(rentalsToRemove);
      for (RentalThread rt : batch)
        rt.start();
      try {
        for (RentalThread rt : batch)
          rt.join();
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
      try {
        Thread.sleep(5000);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
      gridMap.emptyGrid();
    }

    Rental.businessResults();

    Rental.serializeBrokenVehicles();

    int response = JOptionPane.showConfirmDialog(
        null, "Da li zelite vidjeti pokvarena vozila?",
        "Prikaz pokvarenih vozila", JOptionPane.YES_NO_OPTION);
    if (response == JOptionPane.YES_OPTION) {
      breakdownsFrame.setSize(400, 400);
      JScrollPane breakdownsPricesScrollPane = new JScrollPane(breakdownsPricesTable);
      breakdownsFrame.add(breakdownsPricesScrollPane);
      breakdownsFrame.setVisible(true);
      Rental.deserializeBrokenVehicles();
    }
  }
}
