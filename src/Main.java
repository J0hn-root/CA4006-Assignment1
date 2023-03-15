import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private Timer timer;
    private Box box;
    private BookStore bookStore;
    private List<Assistant> assistantList;
    private static int TICK_DURATION = 200; // in milliseconds
    private static int BOOK_DELIVERY_INTERVAL = 90; //in ticks
    private static final int BOOKS_CARRYING_TIME = 10; //in ticks
    private static final int SECTION_CARRYING_TIME_PER_BOOK = 1; //in ticks
    private static int CLIENT_INTERVAL = 10; //in ticks
    private static int NUMBER_OF_ASSISTANTS = 2;
    private static int SHELF_CAPACITY = 10;
    // default all categories equally possible
    private static String CLIENT_PURCHASE_BEHAVIOUR = "1;1;1;1;1;1"; // FANTASY;FICTION;HISTORY;HORROR;POETRY;FANTASY
    private static String BOOK_DELIVERY_BEHAVIOUR = "1;1;1;1;1;1"; // FANTASY;FICTION;HISTORY;HORROR;POETRY;FANTASY

    public Main () {
    }

    //invoked by MenuGUI, initializes GUI, necessary threads and storing object
    public void start(String tickDuration, String bookDeliveryInterval , String clientInterval, String numberOfAssistants,
                      String shelfCapacity, String deliveryBooksBehaviour, String clientBehaviour) {

        // set standard output to file output.dat
        try {
            String fileName = "output.dat";
            PrintStream out = new PrintStream(new FileOutputStream(fileName));
            System.setOut(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // configure main settings
        this.TICK_DURATION = !tickDuration.isEmpty() ? Integer.parseInt(tickDuration) : this.TICK_DURATION;
        this.BOOK_DELIVERY_INTERVAL = !bookDeliveryInterval.isEmpty() ? Integer.parseInt(bookDeliveryInterval) : this.BOOK_DELIVERY_INTERVAL;
        this.CLIENT_INTERVAL = !clientInterval.isEmpty() ? Integer.parseInt(clientInterval) : this.CLIENT_INTERVAL;
        this.NUMBER_OF_ASSISTANTS = !numberOfAssistants.isEmpty() ? Integer.parseInt(numberOfAssistants) : this.NUMBER_OF_ASSISTANTS;
        this.SHELF_CAPACITY = !shelfCapacity.isEmpty() ? Integer.parseInt(shelfCapacity) : this.SHELF_CAPACITY;
        this.CLIENT_PURCHASE_BEHAVIOUR = !clientBehaviour.isEmpty() ? clientBehaviour : this.CLIENT_PURCHASE_BEHAVIOUR;
        this.BOOK_DELIVERY_BEHAVIOUR = !deliveryBooksBehaviour.isEmpty() ? deliveryBooksBehaviour : this.BOOK_DELIVERY_BEHAVIOUR;

        this.timer = new Timer(this.TICK_DURATION);
        this.bookStore = new BookStore(this.timer);
        this.box = new Box(this.bookStore, this.SHELF_CAPACITY);

        // create responsibility chain
        this.bookStore.SetResponsibilityChain(this.box, this.SHELF_CAPACITY);

        Runnable delivery = new Delivery(this.BOOK_DELIVERY_INTERVAL, this.timer, this.box, this.BOOK_DELIVERY_BEHAVIOUR);
        Runnable clientGenerator = new ClientGenerator(this.CLIENT_INTERVAL, this.timer, this.bookStore, this.CLIENT_PURCHASE_BEHAVIOUR);

        // create ExecutorService to manage assistants
        ExecutorService assistantExecutor = Executors.newFixedThreadPool( this.NUMBER_OF_ASSISTANTS );

        this.assistantList = new ArrayList<>();
        for (int i = 0; i < this.NUMBER_OF_ASSISTANTS; i++){
            assistantList.add(new Assistant(this.BOOKS_CARRYING_TIME, this.SECTION_CARRYING_TIME_PER_BOOK, this.timer, this.box, this.bookStore, ("Assistant-" + (i + 1))));
        }
        Runnable gui = new GUI(this.bookStore, this.timer, this.box, this.assistantList, this.TICK_DURATION);

        new Thread(this.timer).start();
        new Thread(gui).start();
        new Thread(delivery).start();
        new Thread(clientGenerator).start();

        for(Assistant assitant : this.assistantList){
            assistantExecutor.execute(assitant);
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        new MenuGUI(main);
    }
}
