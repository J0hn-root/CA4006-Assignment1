/*
    A bookstore is open 24 hours a day, 365 days a year.
    The bookstore has multiple sections such as fiction, horror, romance, fantasy, poetry, and history.

    Time in the bookstore is measured in ticks.
    There are 1000 ticks in a day.
    Every 100 ticks (on average) a delivery is made of 10 books,
    with a random number of books for each of the above categories.

*/

public class Main {

    private Timer timer;
    private Box box;
    private BookStore bookStore;
    private static final int TICK_DURATION = 400; // in milliseconds
    private static final int BOOK_DELIVERY_INTERVAL = 20; //in ticks
    private static final int BOOKS_CARRYING_TIME = 10; //in ticks
    private static final int SECTION_CARRYING_TIME_PER_BOOK = 1; //in ticks
    private static final int CLIENT_INTERVAL = 10; //in ticks

    public Main() {
        this.timer = new Timer(this.TICK_DURATION);
        this.box = new Box();
        this.bookStore = new BookStore(this.timer);
    }

    public void start() {
        this.timer.start();

        Runnable delivery = new Delivery(this.BOOK_DELIVERY_INTERVAL, this.timer, this.box);
        Runnable clientGenerator = new ClientGenerator(this.CLIENT_INTERVAL, this.timer, this.bookStore);

        String nameAs1 = "As1";
        Runnable assistant = new Assistant(this.BOOKS_CARRYING_TIME, this.SECTION_CARRYING_TIME_PER_BOOK, this.timer, this.box, this.bookStore, nameAs1);
        Runnable assistant2 = new Assistant(this.BOOKS_CARRYING_TIME, this.SECTION_CARRYING_TIME_PER_BOOK, this.timer, this.box, this.bookStore, "As2");

        new Thread(assistant).start();
        new Thread(assistant2).start();
        new Thread(delivery).start();
        new Thread(clientGenerator).start();


    }

    class myProcess implements Runnable {
        private String processName;
        private Timer timer;

        public myProcess(Timer timer, String processName) {
            this.timer = timer;
            this.processName = processName;
        }

        public void run() {
            try {
                if (this.processName.equals("p")) {
                    this.timer.waitTicks(10);
                } else if (this.processName.equals("d")) {
                    this.timer.waitTicks(6);
                } else if (this.processName.equals("v")) {
                    this.timer.waitTicks(6);
                } else if (this.processName.equals("u")) {
                    this.timer.waitTicks(6);
                } else {
                    this.timer.waitTicks(2);
                }
            } catch (InterruptedException e){
                 e.printStackTrace();
            }
            System.out.println("Hello from " + processName + " thread. Ticks: " + this.timer.getTicks());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Main main = new Main();
        main.start();
    }

}


