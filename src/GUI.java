/*
    A bookstore is open 24 hours a day, 365 days a year.
    The bookstore has multiple sections such as fiction, horror, romance, fantasy, poetry, and history.

    Time in the bookstore is measured in ticks.
    There are 1000 ticks in a day.
    Every 100 ticks (on average) a delivery is made of 10 books,
    with a random number of books for each of the above categories.

*/

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GUI implements Runnable {

    private BookStore bookStore;
    private Timer timer;
    private Box box;
    private List<Assistant> assistantList;
    private BookCategory[] categories;
    private AssistantStatus[] assistantStatuses;
    private JFrame frame;
    private Map<BookCategory, JPanel> panels;
    private Map<BookCategory, Map<String, JLabel>> labels;
    private JLabel ticksLabel;
    private JLabel jobsLabel;
    private JLabel deliveryBoxLabel;
    private JLabel customerWaitingMeanLabel;
    private ScheduledExecutorService scheduler;
    private Integer tickDuration;
    private JButton resumetButton;
    private JButton stopResumeButton;
    private Map<Assistant, JPanel> assistantStatusPanels;
    private Map<Assistant, Map<String, JLabel>> assistantStatusLabel;
    private Map<BookCategory, JLabel> deliveryBoxLabels;
    private Map<Assistant, Map<BookCategory, JLabel>> assistantBooksRetrievedLabel;

    public GUI(BookStore bookStore, Timer timer, Box box, List<Assistant> assistantList, Integer ticksDuration) {
        this.bookStore = bookStore;
        this.categories = BookCategory.values();
        this.assistantStatuses = AssistantStatus.values();
        this.timer = timer;
        this.box = box;
        this.assistantList = assistantList;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.tickDuration = ticksDuration;

        this.labels = new HashMap<>();
        this.panels = new HashMap<>();
        Integer panelY = 0;

        this.frame = new JFrame();

        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.setTitle("CA4006 BookStore");
        this.frame.setLayout(null);
        this.frame.setSize(1000, 810);
        this.frame.setVisible(true);

        for(BookCategory category : this.categories) {
            Map<String, JLabel> sectionLabels = new HashMap<>();

            JPanel sectionPanel = new JPanel();
            sectionPanel.setBounds(0,panelY,250,130);
            sectionPanel.setBorder(BorderFactory.createLineBorder(Color.black));
            sectionPanel.setLayout(null);
            panelY = panelY + 130;

            JLabel titleLabel = new JLabel("BookStore Section: " + category);
            titleLabel.setBounds(10, 0, 200, 40);
            sectionPanel.add(titleLabel);
            sectionLabels.put("Title", titleLabel);

            JLabel queueLabel = new JLabel("Queue: " + this.bookStore.getSectionQueue(category));
            queueLabel.setBounds(10, 40, 200, 20);
            sectionPanel.add(queueLabel);
            sectionLabels.put("Queue", queueLabel);

            JLabel booksLabel = new JLabel("Books in stock: " + this.bookStore.getSectionBooks(category));
            booksLabel.setBounds(10, 60, 200, 20);
            sectionPanel.add(booksLabel);
            sectionLabels.put("Books", booksLabel);

            JLabel booksSoldLabel = new JLabel("Books sold: " + this.bookStore.getSoldSectionBooks(category));
            booksSoldLabel.setBounds(10, 80, 200, 20);
            sectionPanel.add(booksSoldLabel);
            sectionLabels.put("BooksSold", booksSoldLabel);

            double sectionMean = this.bookStore.getSectionCustomerWaitingTime(category);
            String sectionMeanString = String.format("%.2f", sectionMean);
            JLabel sectionQueueWaitingTimeLabel = new JLabel("Queue waiting time: " + sectionMeanString);
            sectionQueueWaitingTimeLabel.setBounds(10, 100, 200, 20);
            sectionPanel.add(sectionQueueWaitingTimeLabel);
            sectionLabels.put("QueueStatistics", sectionQueueWaitingTimeLabel);

            this.frame.add(sectionPanel);
            this.labels.put(category, sectionLabels);
            this.panels.put(category, sectionPanel);
        }

        // CLOCK (in ticks)
        JPanel tickPanel = new JPanel();
        tickPanel.setBounds(650,0,350,120);
        tickPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        tickPanel.setLayout(null);

        this.ticksLabel = new JLabel("Clock(in ticks): 0");
        this.ticksLabel.setBounds(10, 0, 150, 30);
        tickPanel.add(this.ticksLabel);

        this.jobsLabel = new JLabel("Jobs running: " + this.timer.getNumberOfJobs());
        this.jobsLabel.setBounds(10, 30, 150, 30);
        tickPanel.add(this.jobsLabel);

        this.deliveryBoxLabel = new JLabel("Delivery Box books: " + this.box.getBoxBooks());
        this.deliveryBoxLabel.setBounds(10, 60, 150, 30);
        tickPanel.add(this.deliveryBoxLabel);

        this.customerWaitingMeanLabel = new JLabel("Customer waiting mean time: " + this.bookStore.getCustomerWaitingTimeMean());
        this.customerWaitingMeanLabel.setBounds(10, 90, 250, 30);
        tickPanel.add(this.customerWaitingMeanLabel);

        frame.add(tickPanel);

        // BOX books info

        JPanel boxPanel = new JPanel();
        boxPanel.setBounds(300,0,300,200);
        boxPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        boxPanel.setLayout(new GridLayout(4, 2));

        JLabel titleLabel = new JLabel("Delivery Box: ");
        titleLabel.setBounds(10, 0, 300, 40);
        boxPanel.add(titleLabel);
        JLabel emptyLabel = new JLabel("");
        emptyLabel.setBounds(10, 0, 300, 40);
        boxPanel.add(emptyLabel);

        this.deliveryBoxLabels = new HashMap<>();
        Integer deliveryBoxLabelY = 0;
        for(BookCategory category : this.categories) {
            Integer stock = this.box.getBookByCategory(category);

            JLabel categoryBoxLabel = new JLabel(category + ": " + stock);
            categoryBoxLabel.setBounds(10, deliveryBoxLabelY, 150, 40);
            boxPanel.add(categoryBoxLabel);
            this.deliveryBoxLabels.put(category, categoryBoxLabel);
            deliveryBoxLabelY = deliveryBoxLabelY + 40;
        }

        frame.add(boxPanel);

        // PAUSE/RESUME BUTTONS
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.GRAY);
        buttonPanel.setBounds(250,650,750,150);
        buttonPanel.setLayout(null);

        stopResumeButton = new JButton();
        stopResumeButton.setBounds(250,35,200,50);
        stopResumeButton.addActionListener(e -> this.timer.pauseResumeTimer());
        stopResumeButton.setText("Pause/Resume Clock");
        stopResumeButton.setFocusable(false);

        buttonPanel.add(stopResumeButton);

        frame.add(buttonPanel);

        // ASSISTANT

        this.assistantStatusLabel = new HashMap<>();
        this.assistantStatusPanels = new HashMap<>();



        Integer assistantStatusPanelY = 150;
        for (Assistant assistant : assistantList) {
            Map<String, JLabel> assistantPanelLabels = new HashMap<>();

            JPanel assistantPanel = new JPanel();
            assistantPanel.setBounds(650,assistantStatusPanelY,350,50);
            assistantPanel.setBorder(BorderFactory.createLineBorder(Color.black));
            assistantPanel.setLayout(null);

            AssistantStatus status = assistant.getStatus();
            BookCategory section = assistant.getSection();

            JLabel assistantTitleLabel = new JLabel(assistant.getName() + ": ");
            assistantTitleLabel.setBounds(10,0,100,20);
            assistantPanel.add(assistantTitleLabel);
            assistantPanelLabels.put("Title", assistantTitleLabel);

            JLabel assistantStatusLabel = new JLabel("" + (section != null ? section : "") + status);
            assistantStatusLabel.setBounds(110,0,250,20);
            assistantPanel.add(assistantStatusLabel);
            assistantPanelLabels.put("Status", assistantStatusLabel);

            JLabel assistantBreakLabel = new JLabel("Time on break: " + assistant.getBreakTime());
            assistantBreakLabel.setBounds(10,20,250,20);
            assistantPanel.add(assistantBreakLabel);
            assistantPanelLabels.put("Break", assistantBreakLabel);

            this.assistantStatusLabel.put(assistant, assistantPanelLabels);
            this.assistantStatusPanels.put(assistant, assistantPanel);

            frame.add(assistantPanel);
            assistantStatusPanelY = assistantStatusPanelY + 50;
        }

    }

    public void run () {
        this.scheduler.scheduleAtFixedRate(() -> {
            for(BookCategory category : this.categories) {
                Map<String, JLabel> sectionLabel = labels.get(category);
                sectionLabel.get("Queue").setText("Queue: " + this.bookStore.getSectionQueue(category));
                sectionLabel.get("Books").setText("Books in stock: " + this.bookStore.getSectionBooks(category));
                sectionLabel.get("BooksSold").setText("Books sold: " + this.bookStore.getSoldSectionBooks(category));

                double sectionMean = this.bookStore.getSectionCustomerWaitingTime(category);
                String sectionMeanString = String.format("%.2f", sectionMean);
                sectionLabel.get("QueueStatistics").setText("Queue waiting time: " + sectionMeanString);

                // delivery box
                Integer stock = this.box.getBookByCategory(category);
                this.deliveryBoxLabels .get(category).setText(category + ": " + stock);
            }

            for (Assistant assistant : assistantList) {
                AssistantStatus status = assistant.getStatus();
                BookCategory section = assistant.getSection();

                Map<String, JLabel> assistantPanelLabels = this.assistantStatusLabel.get(assistant);
                assistantPanelLabels.get("Status").setText("" + (section != null ? section : "") + " " + status);
                assistantPanelLabels.get("Break").setText("Time on break: " + assistant.getBreakTime());
            }

            this.ticksLabel.setText("Clock(in ticks): " + this.timer.getTicks());
            this.jobsLabel.setText("Jobs running: " + this.timer.getNumberOfJobs());
            this.deliveryBoxLabel.setText("Delivery Box books: " + this.box.getBoxBooks());

            double mean = this.bookStore.getCustomerWaitingTimeMean();
            String meanString = String.format("%.2f", mean);
            this.customerWaitingMeanLabel.setText("Customer waiting mean time: " + meanString);
        }, this.tickDuration, this.tickDuration, TimeUnit.MILLISECONDS); //delay, time, time unit
    }

}


