package map;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import main.ePJ2;

public class GridMap extends JPanel {
  private final int gridSize = 20;
  private final JLabel[][] cells;

  public GridMap() {
    cells = new JLabel[gridSize][gridSize];
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        cells[i][j] = new JLabel("<html></html>", SwingConstants.CENTER);
        cells[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
        cells[i][j].setOpaque(true);
        cells[i][j].setBackground(Color.GRAY);
        ePJ2.frame.add(cells[i][j]);
      }
    }
  }

  public JLabel[][] getGridMap() {
    return cells;
  }

  public void emptyGrid() {
    for (int i = 0; i < gridSize; i++) {
      for (int j = 0; j < gridSize; j++) {
        cells[i][j].setText("<html></html>");
        cells[i][j].setBackground(Color.GRAY);
      }
    }
  }
}
