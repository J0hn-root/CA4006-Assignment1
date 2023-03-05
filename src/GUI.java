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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GUI implements Runnable {

    private BookStore bookStore;
    private Timer timer;
    private List<Assistant> assistantList;
    private BookCategory[] categories;
    private AssistantStatus[] assistantStatuses;
    private JFrame frame;
    private Map<BookCategory, JPanel> panels;
    private Map<BookCategory, Map<String, JLabel>> labels;
    private JLabel ticksLabel;
    private ScheduledExecutorService scheduler;
    private Integer tickDuration;
    private JButton resumetButton;
    private JButton stopButton;
    private JPanel assistantStatusPanels;
    private Map<Assistant, JLabel> assistantStatusLabel;

    public GUI(BookStore bookStore, Timer timer, List<Assistant> assistantList, Integer ticksDuration) {
        this.bookStore = bookStore;
        this.categories = BookCategory.values();
        this.assistantStatuses = AssistantStatus.values();
        this.timer = timer;
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
            sectionPanel.setBounds(0,panelY,250,110);
            sectionPanel.setBorder(BorderFactory.createLineBorder(Color.black));
            sectionPanel.setLayout(null);
            panelY = panelY + 110;

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

            this.frame.add(sectionPanel);
            this.labels.put(category, sectionLabels);
            this.panels.put(category, sectionPanel);
        }

        // CLOCK (in ticks)
        JPanel tickPanel = new JPanel();
        tickPanel.setBounds(750,0,250,100);
        tickPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        tickPanel.setLayout(null);

        this.ticksLabel = new JLabel("Clock(in ticks): 0");
        this.ticksLabel.setBounds(10, 0, 150, 40);
        tickPanel.add(this.ticksLabel);

        frame.add(tickPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.GRAY);
        buttonPanel.setBounds(0,660,1000,150);
        buttonPanel.setLayout(null);

        stopButton = new JButton();
        stopButton.setBounds(150,35,150,50);
        stopButton.addActionListener(e -> this.timer.pauseTimer());
        stopButton.setText("Pause Clock");
        stopButton.setFocusable(false);

        buttonPanel.add(stopButton);

        resumetButton = new JButton();
        resumetButton.setBounds(450,35,150,50);
        resumetButton.addActionListener(e -> this.timer.resumeTimer());
        resumetButton.setText("Resume Clock");
        resumetButton.setFocusable(false);

        buttonPanel.add(resumetButton);

        frame.add(buttonPanel);

        // ASSISTANT

        this.assistantStatusLabel = new HashMap<>();

        JPanel assistantPanel = new JPanel();
        assistantPanel.setBounds(650,110,350,330);
        assistantPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        assistantPanel.setLayout(null);

        frame.add(assistantPanel);
        this.assistantStatusPanels = assistantPanel;

        Integer assistantStatusPanelY = 0;
        for (Assistant assistant : assistantList) {
            AssistantStatus status = assistant.getStatus();

            JLabel assistantStatusLabel = new JLabel(assistant.getName() + ": " + status);
            assistantStatusLabel.setBounds(10,assistantStatusPanelY,250,30);
            assistantPanel.add(assistantStatusLabel);
            this.assistantStatusLabel.put(assistant, assistantStatusLabel);

            assistantStatusPanelY = assistantStatusPanelY + 30;
        }

    }

    public void run () {
        this.scheduler.scheduleAtFixedRate(() -> {
            for(BookCategory category : this.categories) {
                Map<String, JLabel> sectionLabel = labels.get(category);
                sectionLabel.get("Queue").setText("Queue: " + this.bookStore.getSectionQueue(category));
                sectionLabel.get("Books").setText("Books in stock: " + this.bookStore.getSectionBooks(category));
                sectionLabel.get("BooksSold").setText("Books sold: " + this.bookStore.getSoldSectionBooks(category));
            }

            for (Assistant assistant : assistantList) {
                AssistantStatus status = assistant.getStatus();

                JLabel assistantStatusLabel = this.assistantStatusLabel.get(assistant);
                assistantStatusLabel.setText(assistant.getName() + ": " + status);
            }

            this.ticksLabel.setText("Clock(in ticks): " + this.timer.getTicks());
        }, this.tickDuration, this.tickDuration, TimeUnit.MILLISECONDS); //delay, time, time unit
    }

}


