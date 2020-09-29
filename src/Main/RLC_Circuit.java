/*  Author: Ben Agnes
    Student ID: 301277322
    email: bagnes@sfu.ca

    Runs an RLC circuit with user specified inputs
    - displays GUI for user to configure circuit parameters
    - writes capacitor charge values over time to log file
    - creates, displays, and saves graph of capacitor charge values

*/

package Main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.swing.*;

public class RLC_Circuit extends JFrame implements ActionListener{
    // GUI parameters
    private JLabel labelFilepath = new JLabel("Enter directory to save file in (leave blank for cwd): ");
    private JLabel labelFilename = new JLabel("Enter file name: ");
    private JLabel labelVoltage = new JLabel("Enter Voltage: ");
    private JLabel labelCapacitance = new JLabel("Enter Capacitance: ");
    private JLabel labelInductance = new JLabel(("Enter Inductance: "));
    private JLabel labelResistance = new JLabel(("Enter Resistance"));
    private JLabel labelrunTime = new JLabel(("Enter runtime: "));
    private JLabel labelstepTime = new JLabel(("Enter step time (time increment between points): "));
    private JTextField textFilepath = new JTextField(50);
    private JTextField textFilename = new JTextField(50);
    private JTextField textVoltage = new JTextField(20);
    private JTextField textCapacitance = new JTextField(20);
    private JTextField textInductance = new JTextField(20);
    private JTextField textResistance = new JTextField(20);
    private JTextField textRunTime = new JTextField(20);
    private JTextField textStepTime = new JTextField(20);
    private JButton runButton = new JButton("RUN");

    // Variables for GUI inputs
    private double Voltage;
    private double Capacitance;
    private double Inductance;
    private double Resistance;
    private double runTime;
    private double stepTime;
    private String filepath;
    private String filename;

    // File parameters
    FileWriter logFileWriter;
    private String fullFilePath;

    // Class constructor. Creates and displays GUI for user to enter parameters
    public RLC_Circuit() {

        // Create panel
        JPanel newPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        // add labels and text fields
        constraints.gridx = 0;
        constraints.gridy = 0;
        newPanel.add(labelFilepath, constraints);

        constraints.gridx = 1;
        newPanel.add(textFilepath, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        newPanel.add(labelFilename, constraints);

        constraints.gridx = 1;
        newPanel.add(textFilename, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        newPanel.add(labelVoltage, constraints);

        constraints.gridx = 1;
        newPanel.add(textVoltage, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        newPanel.add(labelCapacitance, constraints);

        constraints.gridx = 1;
        newPanel.add(textCapacitance, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        newPanel.add(labelCapacitance, constraints);

        constraints.gridx = 1;
        newPanel.add(textCapacitance, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        newPanel.add(labelInductance, constraints);

        constraints.gridx = 1;
        newPanel.add(textInductance, constraints);

        constraints.gridx = 0;
        constraints.gridy = 6;
        newPanel.add(labelResistance, constraints);

        constraints.gridx = 1;
        newPanel.add(textResistance, constraints);

        constraints.gridx = 0;
        constraints.gridy = 7;
        newPanel.add(labelrunTime, constraints);

        constraints.gridx = 1;
        newPanel.add(textRunTime, constraints);

        constraints.gridx = 0;
        constraints.gridy = 8;
        newPanel.add(labelstepTime, constraints);

        constraints.gridx = 1;
        newPanel.add(textStepTime, constraints);

        // add RUN button, runs circuit when user clicks button
        constraints.gridx = 0;
        constraints.gridy = 9;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        runButton.addActionListener(this);
        newPanel.add(runButton, constraints);

        // add panel to frame
        add(newPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Enter RLC circuit parameters");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // When 'run' button clicked read in values then run circuit if all within valid ranges
    public void actionPerformed(ActionEvent e) {
        // read in parameters
        filepath = textFilepath.getText();
        filename = textFilename.getText();
        Voltage = Double.parseDouble(textVoltage.getText());
        Capacitance = Double.parseDouble(textCapacitance.getText());
        Inductance = Double.parseDouble(textInductance.getText());
        Resistance = Double.parseDouble(textResistance.getText());
        runTime = Double.parseDouble(textRunTime.getText());
        stepTime = Double.parseDouble(textStepTime.getText());

        // verify inputs
        try {
            if (filepath.isBlank()) {
                fullFilePath = filename;
            }
            else {
                fullFilePath = filepath + "/" + filename;
            }

            String logFileName = fullFilePath + ".log";
            File logFile = new File(logFileName);
            logFileWriter = new FileWriter(logFile);

            if ((Voltage < 4) || (Voltage > 15)) {
                throw new Exception("Invalid voltage range: 4 <= V <= 15");
            }

            if ((Capacitance < 1E-9) || (Capacitance > 1E-7)) {
                throw new Exception("Invalid capacitance range: 1E-9 <= C <= 1E-7");
            }

            if ((Inductance < 1E-3) || (Inductance > 1E-1)) {
                throw new Exception("Invalid inductance range: 1E-3 <= L <= 1E-1");
            }

            if ((Resistance < 5) || (Resistance > 10)) {
                throw new Exception("Invalid resistance range: 5 <= R <= 10");
            }

            if (runTime <= 0) {
                throw new Exception("Invalid runtime entered: Trun > 0");
            }

            if ((stepTime <= 0) || (stepTime > runTime)) {
                throw new Exception("Invalid steptime entered: 0 < Tstep <= Trun");
            }
        }
        catch (Exception exception) {
            displayExceptionasMsg("Invalid Input", exception.getMessage());
            return;
        }

        try {
                runCircuit();
        }
        catch (Exception exception) {
            displayExceptionasMsg("ERROR", exception.getMessage());
        }
    }

    // Runs Circuit with initial conditions; dumps q(t) values to log file and makes graph
    private void runCircuit() throws Exception {

        double Charge;
        double currentTime = 0;
        ArrayList<Double> timeVals = new ArrayList<>();
        ArrayList<Double> chargeVals = new ArrayList<>();

        logFileWriter.write("RLC Circuit Initial Conditions\r\n");
        logFileWriter.write("Run time: " + runTime + " seconds\r\n");
        logFileWriter.write("Step time: " + stepTime + " seconds\r\n");
        logFileWriter.write("Voltage: " + Voltage + " volts\r\n");
        logFileWriter.write("Capacitance: " + Capacitance + " farads\r\n");
        logFileWriter.write("Inductance: " + Inductance + " henrys\r\n");
        logFileWriter.write("Resistance: " + Resistance + " ohms\r\n\r\n");
        logFileWriter.write("Charge q(t) = X at t seconds in C (Coulombs)\r\n");

        // Run circuit and write q(t) to log file
        while (currentTime <= runTime) {
            Charge = Voltage * Capacitance * Math.exp(-1 * (Resistance / (2 * Inductance)) * currentTime)
                    * Math.cos(currentTime * Math.sqrt((1 / (Inductance * Capacitance)) -
                    Math.pow((Resistance / (2 * Inductance)), 2)));

            logFileWriter.write("q(" + currentTime + ") = " + Charge + " C\r\n");

            timeVals.add(currentTime);
            chargeVals.add(Charge);

            if (currentTime == runTime) {
                break;
            }

            currentTime += stepTime;

            // if next iteration exceeds run time, make next iteration end run time
            if (currentTime > runTime) {
                currentTime = runTime;
            }
        }

        logFileWriter.close();

        DrawGraph graph = new DrawGraph(timeVals, chargeVals, fullFilePath);
    }

    private void displayExceptionasMsg(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.WARNING_MESSAGE);
    }
}