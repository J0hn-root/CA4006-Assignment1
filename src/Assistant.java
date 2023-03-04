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

    public Assistant (int bookCarryingTime, int sectionCarryingTimePerBook, Timer timer, Box box, BookStore bookStore, String name) {
        this.timer = timer;
        this.box = box;
        this.bookCarryingTime = bookCarryingTime;
        this.bookStore = bookStore;
        this.sectionCarryingTimePerBook = sectionCarryingTimePerBook;
        this.name = name;
    }

    public void run () {
        try {
            while (true) {
                this.carriedBooks = box.retrieveBooks(this.name);

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

                synchronized (this) {
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                    for (BookCategory value : categories) {
                        System.out.println(value);
                    }
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++");
                }

                Integer nBooks = 10;
                while (categories.size() > 0) {
                    List<Book> categoryBooks = carriedBooks.get(categories.get(0));

                    synchronized (this) {
                        System.out.println("--------------------------------------------------------");
                        System.out.println(timer.getTicks() + " : " + this.name + " : " + categoryBooks.size() + " : " + categories.get(0));
                        for (Book value : categoryBooks) {
                            System.out.println(value.getCategory());
                        }
                        System.out.println("--------------------------------------------------------");
                    }

                    while(categoryBooks.size() != 0) {
                        //System.out.println(i + ": " + carriedBooks.get(i).getCategory());
                        this.bookStore.stockBooks(categories.get(0), categoryBooks.get(0), this.name);
                        categoryBooks.remove(0);
                        nBooks = nBooks - 1;
                    }

                    categories.remove(0);

                    if (nBooks == 0) {
                        carriedBooks = null;
                    } else {
                        //1 tick extra for every book they are carrying to that section.
                        System.out.println(this.name + ": Waiting for " + nBooks + " Ticks.");
                        timer.waitTicks(nBooks);
                    }
                }

                System.out.println(this.name + ": is returning.");
                timer.waitTicks(this.bookCarryingTime);

            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
