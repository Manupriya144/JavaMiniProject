import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BMICalculator extends JFrame implements ActionListener {

    JTextField weightField, heightField;
    JComboBox<String> weightUnitBox, heightUnitBox;
    JButton calculateButton;

    BMICalculator() {

        setTitle("BMI Calculator");
        setSize(420,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("BMI CALCULATOR", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.BLUE);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3,3,10,10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel weightLabel = new JLabel("Weight:");
        weightField = new JTextField();

        String[] weightUnits = {"kg","pounds"};
        weightUnitBox = new JComboBox<>(weightUnits);

        JLabel heightLabel = new JLabel("Height:");
        heightField = new JTextField();

        String[] heightUnits = {"m","cm","inch"};
        heightUnitBox = new JComboBox<>(heightUnits);

        calculateButton = new JButton("Calculate BMI");
        calculateButton.setBackground(new Color(0,153,76));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFont(new Font("Arial", Font.BOLD,14));

        calculateButton.addActionListener(this);

        inputPanel.add(weightLabel);
        inputPanel.add(weightField);
        inputPanel.add(weightUnitBox);

        inputPanel.add(heightLabel);
        inputPanel.add(heightField);
        inputPanel.add(heightUnitBox);

        inputPanel.add(new JLabel(""));
        inputPanel.add(calculateButton);

        add(title, BorderLayout.NORTH);
        add(inputPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        try{

            double weight = Double.parseDouble(weightField.getText());
            double height = Double.parseDouble(heightField.getText());

            if(weight <= 0 || height <= 0){
                JOptionPane.showMessageDialog(this,
                        "Weight and Height must be positive numbers!",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String weightUnit = (String) weightUnitBox.getSelectedItem();
            String heightUnit = (String) heightUnitBox.getSelectedItem();

            if(weightUnit.equals("pounds"))
                weight = weight * 0.453592;

            if(heightUnit.equals("cm"))
                height = height / 100;
            else if(heightUnit.equals("inch"))
                height = height * 0.0254;

            double bmi = weight / (height * height);

            String category;

            if(bmi < 18.5)
                category = "Underweight";
            else if(bmi <= 24.9)
                category = "Normal";
            else if(bmi <= 29.9)
                category = "Overweight";
            else
                category = "Obese";

            showResultWindow(bmi, category);

        }
        catch(NumberFormatException ex){

            JOptionPane.showMessageDialog(this,
                    "Please enter valid numbers!",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showResultWindow(double bmi, String category){

        JFrame resultFrame = new JFrame("BMI Result");
        resultFrame.setSize(450,320);
        resultFrame.setLocationRelativeTo(null);
        resultFrame.setLayout(new BorderLayout());

        JLabel resultLabel = new JLabel(
                "Your BMI: " + String.format("%.2f", bmi) + " (" + category + ")",
                JLabel.CENTER);

        resultLabel.setFont(new Font("Arial", Font.BOLD,18));

        JTextArea infoArea = new JTextArea();
        infoArea.setText(

                "BMI Categories\n\n" +

                        "Underweight (<18.5)\n" +
                        "Body weight is lower than normal.\n\n" +

                        "Normal (18.5 - 24.9)\n" +
                        "Healthy body weight range.\n\n" +

                        "Overweight (25 - 29.9)\n" +
                        "Weight is higher than normal.\n\n" +

                        "Obese (30 or greater)\n" +
                        "Higher health risk due to excess body fat."
        );

        infoArea.setEditable(false);
        infoArea.setFont(new Font("Arial",Font.PLAIN,14));
        infoArea.setBackground(new Color(240,248,255));

        JButton closeButton = new JButton("Close");
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Arial",Font.BOLD,14));

        closeButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                resultFrame.dispose();
            }
        });

        resultFrame.add(resultLabel,BorderLayout.NORTH);
        resultFrame.add(infoArea,BorderLayout.CENTER);
        resultFrame.add(closeButton,BorderLayout.SOUTH);

        resultFrame.setVisible(true);
    }

    public static void main(String[] args) {
        new BMICalculator();
    }
}
