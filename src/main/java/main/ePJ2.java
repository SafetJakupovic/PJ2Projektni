package main;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import vehicles.*;

/**
 * ePJ2
 */
public class ePJ2 {

  public static void main(String[] args) {

    // Automobile auto = new Automobile(
    // "Fiat", "Punto", 15.5, LocalDateTime.of(2024, 6, 1, 9, 0), "Sranje");
    // System.out.println(auto);
    // System.out.println(auto.getID());

    // EBike bajs = new EBike("Fiat", "Munto", 4.3, 2);
    // System.out.println(bajs);
    // System.out.println(bajs.getID());
    // EBike bajs2 = new EBike("Fiat", "Munto", 1.3, 4);
    // System.out.println(bajs2);
    // System.out.println(bajs2.getID());

    // EScooter troti = new EScooter("Fiat", "Munto", 4.3, 2);
    // System.out.println(troti);
    // System.out.println(troti.getID());
    // EScooter troti2 = new EScooter("Fiat", "Munto", 1.3, 4);
    // System.out.println(troti2);
    // System.out.println(troti2.getID());

    try {
      FileWriter fw = new FileWriter("src/main/resources/output/test.txt");
      fw.write("cao");
      fw.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    /*
     * LocalDateTime datum = LocalDateTime.of(2024, 6, 1, 9, 0);
     * DateTimeFormatter formater = DateTimeFormatter.ofPattern("d.M.yyyy
     * HH:mm"); System.out.println(datum.format(formater));
     *
     * String datumstr = new String("01.06.2024 09:00");
     * LocalDateTime datumizstr = LocalDateTime.parse(datumstr, formater);
     * System.out.println(datumizstr);
     */
  }
}
