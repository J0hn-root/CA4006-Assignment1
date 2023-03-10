import javax.swing.*;
import java.awt.*;

public class MenuGUI {

    private JFrame frame = new JFrame();
    private JLabel tickDurationLabel;
    private JTextField tickDurationField;
    private JLabel bookDeliveryIntervalLabel;
    private JTextField bookDeliveryIntervalField;
    private JLabel clientIntervalGenerationLabel;
    private JTextField clientIntervalGenerationField;
    private JLabel numberOfAssistantsLabel;
    private JTextField numberOfAssistantsField;
    private JLabel shelfCapacityLabel;
    private JTextField shelfCapacityField;
    private JLabel customerPurchaseBehaviourLabel;
    private JTextField customerPurchaseBehaviourField;
    private JLabel instructionBehaviourLabel;
    private JLabel instructionCategoriesBehaviourLabel;
    private JLabel bookCategoryDeliveryBehaviourLabel;
    private JTextField bookCategoryDeliveryBehaviourField;

    public MenuGUI (Main main) {
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setSize(900, 500);
        this.frame.setLayout(new GridLayout(9, 2));

        tickDurationLabel = new JLabel("Tick duration (in milliseconds - 1000 -> 1s): (Default 200)");
        tickDurationField = new JTextField();

        bookDeliveryIntervalLabel = new JLabel("Tick duration between deliveries (in Ticks - on average): (Default 90)");
        bookDeliveryIntervalField = new JTextField();

        clientIntervalGenerationLabel = new JLabel("Client generation interval (in Ticks): (Default 10)");
        clientIntervalGenerationField = new JTextField();

        numberOfAssistantsLabel = new JLabel("Number of assistants: (Default 2)");
        numberOfAssistantsField = new JTextField();

        shelfCapacityLabel = new JLabel("Shelf capacity: (Default 10)");
        shelfCapacityField = new JTextField();

        instructionBehaviourLabel = new JLabel("Behaviour categories order (fields below), 6 values required =>");
        instructionCategoriesBehaviourLabel = new JLabel("FANTASY; FICTION; HISTORY; HORROR; POETRY; ROMANCE ");
        bookCategoryDeliveryBehaviourLabel = new JLabel("Book categories delivery behaviour (e.g. -> 50;20;10;70;90;100 ):");
        bookCategoryDeliveryBehaviourField = new JTextField();

        customerPurchaseBehaviourLabel = new JLabel("Probabilities of customer purchase behaviour (e.g. -> 50;20;10;70;90;100 ):");
        customerPurchaseBehaviourField = new JTextField();

        JButton button = new JButton("Start");
        button.addActionListener(e -> {
            frame.dispose();
            main.start(tickDurationField.getText(), bookDeliveryIntervalField.getText(),
                    clientIntervalGenerationField.getText(), numberOfAssistantsField.getText(), shelfCapacityField.getText(),
                    bookCategoryDeliveryBehaviourField.getText(), customerPurchaseBehaviourField.getText());
        });


        this.frame.add(tickDurationLabel);
        this.frame.add(tickDurationField);

        this.frame.add(bookDeliveryIntervalLabel);
        this.frame.add(bookDeliveryIntervalField);

        this.frame.add(clientIntervalGenerationLabel);
        this.frame.add(clientIntervalGenerationField);

        this.frame.add(numberOfAssistantsLabel);
        this.frame.add(numberOfAssistantsField);

        this.frame.add(shelfCapacityLabel);
        this.frame.add(shelfCapacityField);

        this.frame.add(instructionBehaviourLabel);
        this.frame.add(instructionCategoriesBehaviourLabel);

        this.frame.add(bookCategoryDeliveryBehaviourLabel);
        this.frame.add(bookCategoryDeliveryBehaviourField);

        this.frame.add(customerPurchaseBehaviourLabel);
        this.frame.add(customerPurchaseBehaviourField);

        this.frame.add(button);

        this.frame.setVisible(true);
    }
}
