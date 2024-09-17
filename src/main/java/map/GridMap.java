package map;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import main.ePJ2;

/**
 * Predstavlja mapu kao 20x20 matricu {@link JLabel} ćelija.
 *
 * Proširuje {@link JPanel} i inicijalizuje mapu. Ćelije su dio 2D niza {@code
 * JLabel}.
 */
public class GridMap extends JPanel {
  /** Dimenzija mape. Podrazumijevano 20x20. */
  private final int gridSize = 20;
  /** Sama mapa predstavljanja kao 2D niz {@code JLabel} objekata. */
  private final JLabel[][] cells;

  /**
   * Konstruiše {@code GridMap} sa 20x20 mapom ćelija.
   *
   * Svaka ćelija se inicijalizuje kao {@link JLabel} sa crnim ivicama,
   * centriranim tekstom i sivom pozadinom. Mapa se dodaje {@code gridPanel} u
   * {@link ePJ2} klasi.
   *
   */
  public GridMap() {
    cells = new JLabel[gridSize][gridSize];
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        cells[i][j] = new JLabel("<html></html>", SwingConstants.CENTER);
        cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cells[i][j].setOpaque(true);
        cells[i][j].setBackground(Color.GRAY);
        ePJ2.gridPanel.add(cells[i][j]);
      }
    }
  }

  /**
   * Vraća 2D niz koji predstavlja mapu.
   *
   * Niz sadrži {@link JLabel} objekte od kojih svaki predstavlja ćeliju mape.
   *
   * @return 2D niz {@link JLabel} koji predstavlja mapu
   */
  public JLabel[][] getGridMap() {
    return cells;
  }

  /**
   * Resetuje sve ćelije na početno stanje.
   *
   * Briše se tekst svih ćelija i postavlja boja pozadine na sivu.
   *
   */
  public void emptyGrid() {
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        cells[i][j].setText("<html></html>");
        cells[i][j].setBackground(Color.GRAY);
      }
    }
  }
}
