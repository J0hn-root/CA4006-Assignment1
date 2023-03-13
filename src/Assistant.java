/*
    - Only one person (e.g. assistant) can take books from the box at the same time.
    - Once they are finished another assistant can take books from the box.
    - Each assistant can carry up to 10 books at once.

    - It takes an assistant 10 ticks to walk from where the deliveries arrive to a particular section
    - and 1 tick extra for every book they are carrying to that section.
    - Additionally, for every book they put on the shelf, it takes 1 tick.

 */

import java.security.SecureRandom;
import java.util.*;

public class Assistant implements Runnable {

    private Map<BookCategory, List<Book>> carriedBooks;
    private Timer timer;
    private Box box;
    private BookStore bookStore;
    private static int bookCarryingTime;
    private static int sectionCarryingTimePerBook;
    private String name;
    private AssistantStatus status;
    private BookCategory section;
    private Integer breakTime = 0;
    private static final int ASSISTANT_BREAK_TIME = 150; //in ticks
    private static final int BREAK_PROBABILITY = 10; // out of 100

    public Assistant (int bookCarryingTime, int sectionCarryingTimePerBook, Timer timer, Box box, BookStore bookStore, String name) {
        this.timer = timer;
        this.box = box;
        this.bookCarryingTime = bookCarryingTime;
        this.bookStore = bookStore;
        this.sectionCarryingTimePerBook = sectionCarryingTimePerBook;
        this.name = name;
        this.status = AssistantStatus.WAITING_DELIVERY;
        this.timer.increaseNumberOfJobs();
    }

    public synchronized Integer getBreakTime() {
        return this.breakTime;
    }

    public synchronized AssistantStatus getStatus() {
        return this.status;
    }

    public synchronized void setStatus (AssistantStatus status) {
        this.status = status;
    }

    public synchronized BookCategory getSection() {
        return this.section;
    }

    public synchronized void setSection (BookCategory section) {
        this.section = section;
    }

    public String getName() {
        return this.name;
    }

    public String getThreadName() {
        return  Thread.currentThread().getName();
    }

    public void run () {
        try {
            while (true) {
                // 10% probability (by default) that the assistant will go in break for 150 ticks
                SecureRandom secureRandom = new SecureRandom();
                Integer probability = secureRandom.nextInt(100);
                if (probability >= (100 - this.BREAK_PROBABILITY)){
                    this.setStatus(AssistantStatus.ON_BREAK);
                    // <Tick count> <Thread ID> Assistant-3 is taking a break for 150 TICKS
                    System.out.println("<" + this.timer.getTicks() + "> <" + this.getThreadName() + "> " + this.name + " is taking a break for 150 TICKS ");

                    // break time can be accessed from outside the class. (Is a CS)
                    synchronized (this) {
                        this.breakTime += this.ASSISTANT_BREAK_TIME;
                    }

                    timer.waitTicks(this.ASSISTANT_BREAK_TIME);
                    this.breakTime += this.ASSISTANT_BREAK_TIME;
                }

                this.setStatus(AssistantStatus.GETTING_DELIVERY);
                this.carriedBooks = box.retrieveBooks(this);

                // will contain the total number of books retrieved from the box
                Integer nBooks = 0;

                // String builder for log
                StringBuilder stringBuilder = new StringBuilder();
                for (BookCategory category : carriedBooks.keySet()) {
                    Integer categoryBooks = this.carriedBooks.get(category).size();
                    nBooks += categoryBooks;
                    stringBuilder.append(" " + categoryBooks);
                    stringBuilder.append(" " + category + ",");
                }
                // remove last comma
                stringBuilder.substring(0, stringBuilder.length() - 1);
                //<Tick count> <Thread ID> Assistant-1 collected 7 books: 4 POETRY, 3 FICTION
                System.out.println("<" + this.timer.getTicks() + "> <" + this.getThreadName() + "> " + this.name + " collected " + nBooks + " books:" + stringBuilder);


                this.setStatus(AssistantStatus.IN_TRANSIT);

                // walk from where the deliveries arrive to a particular section
                timer.waitTicks(this.bookCarryingTime + nBooks);

                // get all the categories of the book retrieved
                List<BookCategory> categories = new ArrayList<>(carriedBooks.keySet());

                while (categories.size() > 0) {
                    // sort categories based on the queue size
                    // section with bigger queues will be stocked first
                    this.bookStore.prioritiseSectionDelivery(categories);

                    // set section assistant is currently in
                    this.setSection(categories.get(0));
                    this.setStatus(AssistantStatus.STOCKING);

                    // get the list of books retrieved from the box for a single category
                    List<Book> categoryBooks = carriedBooks.get(categories.get(0));
                    Integer categoryBooksSize = categoryBooks.size();

                    //<Tick count> <Thread ID> Assistant-1 began stocking POETRY section with 4 books
                    System.out.println("<" + this.timer.getTicks() + "> <" + this.getThreadName() + "> " + this.name + " began stocking " + categories.get(0) + " section with " + categoryBooksSize + " books");

                    while(categoryBooks.size() != 0) {
                        this.bookStore.stockBooks(categories.get(0), categoryBooks.get(0), this);
                        // for every book they put on the shelf, it takes 1 tick
                        timer.waitTicks(1);
                        categoryBooks.remove(0);
                        nBooks = nBooks - 1;
                    }

                    // update section books in
                    this.bookStore.decreseBooksIssued(categories.get(0), categoryBooksSize);
                    // <Tick count> <Thread ID> Assistant-3 finished stocking FICTION section with 3 books
                    System.out.println("<" + this.timer.getTicks() + "> <" + this.getThreadName() + "> " + this.name + " finished stocking " + categories.get(0) + " section with " + categoryBooksSize + " books");

                    // when no more book are in the list remove it from the list
                    categories.remove(0);

                    if (nBooks == 0) {
                        carriedBooks = null;
                    } else {
                        // set section assistant is currently in
                        this.setSection(categories.get(0));

                        //1 tick extra for every book they are carrying to that section.
                        this.setStatus(AssistantStatus.SWITCHING_SECTION);
                        timer.waitTicks(this.bookCarryingTime + nBooks); // wait 10 ticks + number of books left
                    }
                }
                this.setSection(null);

                this.setStatus(AssistantStatus.RETURNING);
                timer.waitTicks(this.bookCarryingTime);

            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
