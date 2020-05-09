import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GUI {

  private static File file1;
  private static File file2;

  public static void main(String[] args) {

    JFrame f = new JFrame("A JFrame");
    f.setSize(500, 500);
    f.setLocation(300, 200);

    JPanel panel1 = new JPanel();
    panel1.setLayout(new BorderLayout());

    // We create a sub-panel. Notice, that we don't use any layout-manager,
    // Because we want it to use the default FlowLayout
    JPanel subPanel = new JPanel();

    // Now we simply add it to your main panel.
    panel1.add(subPanel, BorderLayout.NORTH);
    f.add(panel1, BorderLayout.NORTH);

    JFileChooser chooser1 = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
    chooser1.setFileFilter(filter);

    final JTextArea textArea = new JTextArea(10, 40);
    f.getContentPane().add(BorderLayout.CENTER, textArea);
    final JButton openButton = new JButton("Choose Facility Demands File");
    openButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int returnVal = chooser1.showOpenDialog(f);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
          file1 = chooser1.getSelectedFile();
          // This is where a real application would open the file.
          textArea.append("Facilities Demand: " + file1.getName() + "\n");
          System.out.println("Opening: " + file1.getName() + ".");
        } else {
          System.out.println("Open command cancelled by user.");
        }
      }
    });
    final JButton openButton2 = new JButton("Choose Available Supplies File");
    openButton2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int returnVal = chooser1.showOpenDialog(f);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
          file2 = chooser1.getSelectedFile();
          // This is where a real application would open the file.
          textArea.append("Supplies: " + file2.getName() + "\n");
          System.out.println("Opening: " + file2.getName() + ".");
        } else {
          System.out.println("Open command cancelled by user.");
        }
      }
    });

    subPanel.add(openButton);
    subPanel.add(openButton2);

    final JButton button = new JButton("PROCESS");
    f.getContentPane().add(BorderLayout.SOUTH, button);
    button.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // Handle open button action.
        try {
          textArea.append("Processing...\n");
          Process process = Runtime.getRuntime().exec("ruby script.rb "+ file1.getName() + " "+ file2.getName());
          process.waitFor();

          BufferedReader processIn = new BufferedReader(
                  new InputStreamReader(process.getInputStream()));

          String line;
          while ((line = processIn.readLine()) != null) {
              System.out.println(line);
          } 
          textArea.append("File Created! Look for output.csv file in the directory.\n");
          Desktop.getDesktop().open(new File("output.csv"));
      } 
      catch (Exception err) {
          err.printStackTrace();
      }
      }
    });

    f.setVisible(true);

  }

}