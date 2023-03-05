/*
    - Only one person (e.g. assistant) can take books from the box at the same time.
    - Once they are finished another assistant can take books from the box.
    - Each assistant can carry up to 10 books at once.

    - It takes an assistant 10 ticks to walk from where the deliveries arrive to a particular section
    - and 1 tick extra for every book they are carrying to that section.
    - Additionally, for every book they put on the shelf, it takes 1 tick.

 */

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

    public Assistant (int bookCarryingTime, int sectionCarryingTimePerBook, Timer timer, Box box, BookStore bookStore, String name) {
        this.timer = timer;
        this.box = box;
        this.bookCarryingTime = bookCarryingTime;
        this.bookStore = bookStore;
        this.sectionCarryingTimePerBook = sectionCarryingTimePerBook;
        this.name = name;
        this.status = AssistantStatus.WAITING;
    }

    public synchronized AssistantStatus getStatus() {
        return this.status;
    }

    public synchronized void setStatus (AssistantStatus status) {
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public void run () {
        try {
            while (true) {
                this.setStatus(AssistantStatus.WAITING);
                this.carriedBooks = box.retrieveBooks(this.name);
                this.setStatus(AssistantStatus.IN_TRANSIT);

                // walk from where the deliveries arrive to a particular section
                timer.waitTicks(this.bookCarryingTime);

                List<BookCategory> categories = new ArrayList<>(carriedBooks.keySet());

                // sort categories based on the queue size
                // section with bigger queues will be stocked first
                Collections.sort(categories, new Comparator<BookCategory>() {
                    @Override
                    public int compare(BookCategory category1, BookCategory category2) {
                        int queue1 = bookStore.getSectionQueue(category1);
                        int queue2 = bookStore.getSectionQueue(category2);
                        return Integer.compare(queue2, queue1);
                    }
                });

                Integer nBooks = 10;
                while (categories.size() > 0) {
                    this.setStatus(AssistantStatus.STOCKING);
                    List<Book> categoryBooks = carriedBooks.get(categories.get(0));

                    while(categoryBooks.size() != 0) {
                        //System.out.println(i + ": " + carriedBooks.get(i).getCategory());
                        this.bookStore.stockBooks(categories.get(0), categoryBooks.get(0), this.name);
                        // for every book they put on the shelf, it takes 1 tick
                        timer.waitTicks(1);
                        categoryBooks.remove(0);
                        nBooks = nBooks - 1;
                    }

                    categories.remove(0);

                    if (nBooks == 0) {
                        carriedBooks = null;
                    } else {
                        //1 tick extra for every book they are carrying to that section.
                        System.out.println(this.name + ": Waiting for " + (this.bookCarryingTime + nBooks) + " Ticks.");
                        this.setStatus(AssistantStatus.SWITCHING_SECTION);
                        timer.waitTicks(this.bookCarryingTime + nBooks); // wait 10 ticks + number of books left
                    }
                }

                this.setStatus(AssistantStatus.RETURNING);
                timer.waitTicks(this.bookCarryingTime);

            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
