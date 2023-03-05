import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private Timer timer;
    private Box box;
    private BookStore bookStore;
    private List<Assistant> assistantList;
    private static int TICK_DURATION = 800; // in milliseconds
    private static int BOOK_DELIVERY_INTERVAL = 100; //in ticks
    private static final int BOOKS_CARRYING_TIME = 10; //in ticks
    private static final int SECTION_CARRYING_TIME_PER_BOOK = 1; //in ticks
    private static int CLIENT_INTERVAL = 10; //in ticks
    private static int NUMBER_OF_ASSISTANTS = 2;

    public Main () {
        this.box = new Box();
    }

    public void start(String tickDuration, String bookDeliveryInterval ,
                      String clientInterval, String numberOfAssistants) {
        this.TICK_DURATION = !tickDuration.isEmpty() ? Integer.parseInt(tickDuration) : this.TICK_DURATION;
        this.BOOK_DELIVERY_INTERVAL = !bookDeliveryInterval.isEmpty() ? Integer.parseInt(bookDeliveryInterval) : this.BOOK_DELIVERY_INTERVAL;
        this.CLIENT_INTERVAL = !clientInterval.isEmpty() ? Integer.parseInt(clientInterval) : this.CLIENT_INTERVAL;
        this.NUMBER_OF_ASSISTANTS = !numberOfAssistants.isEmpty() ? Integer.parseInt(numberOfAssistants) : this.NUMBER_OF_ASSISTANTS;

        this.timer = new Timer(this.TICK_DURATION);
        this.bookStore = new BookStore(this.timer);

        Runnable delivery = new Delivery(this.BOOK_DELIVERY_INTERVAL, this.timer, this.box);
        Runnable clientGenerator = new ClientGenerator(this.CLIENT_INTERVAL, this.timer, this.bookStore);

        // create ExecutorService to manage assistants
        ExecutorService assistantExecutor = Executors.newFixedThreadPool( this.NUMBER_OF_ASSISTANTS );

        this.assistantList = new ArrayList<>();
        for (int i = 0; i < this.NUMBER_OF_ASSISTANTS; i++){
            assistantList.add(new Assistant(this.BOOKS_CARRYING_TIME, this.SECTION_CARRYING_TIME_PER_BOOK, this.timer, this.box, this.bookStore, ("Assistant-" + (i + 1))));
        }
        Runnable gui = new GUI(this.bookStore, this.timer, this.assistantList, this.TICK_DURATION);

        this.timer.start();
        new Thread(gui).start();
        new Thread(delivery).start();
        new Thread(clientGenerator).start();

        for(Assistant assitant : this.assistantList){
            assistantExecutor.execute(assitant);
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        MenuGUI menu = new MenuGUI(main);
    }
}
