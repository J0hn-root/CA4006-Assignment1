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

    private List<Book> carriedBooks;
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

                ArrayList<BookCategory> categories = new ArrayList<>();
                categories.addAll(EnumSet.allOf(BookCategory.class));

                // sort categories based on the queue size
                Collections.sort(categories, new Comparator<BookCategory>() {
                    @Override
                    public int compare(BookCategory category1, BookCategory category2) {
                        int queue1 = bookStore.getSectionQueue(category1);
                        int queue2 = bookStore.getSectionQueue(category2);
                        return Integer.compare(queue1, queue2);
                    }
                });


                // Sort the list of carried books by category
                Collections.sort(carriedBooks, new Comparator<Book>() {
                    @Override
                    public int compare(Book book1, Book book2) {
                        BookCategory categoryBookOne =  book1.getCategory();
                        BookCategory categoryBookTwo =  book2.getCategory();
                        return categoryBookOne.compareTo(categoryBookTwo);
                    }
                });

                Integer nBooks;
                while (categories.size() > 0) {
                    synchronized (this) {
                        System.out.println("--------------------------------------------------------");
                        System.out.println(this.name + " : " + carriedBooks.size() + " : " + categories.get(0));
                        for (Book value : carriedBooks) {
                            System.out.println(value.getCategory());
                        }
                        System.out.println("--------------------------------------------------------");
                    }

                    while(carriedBooks.get(0).getCategory().equals(categories.get(0))) {
                        //System.out.println(i + ": " + carriedBooks.get(i).getCategory());
                        this.bookStore.stockBooks(categories.get(0), carriedBooks.get(0), this.name);
                        carriedBooks.remove(0);

                    }

                    categories.remove(0);

                    //1 tick extra for every book they are carrying to that section.
                    nBooks = carriedBooks.size();
                    System.out.println(this.name + ": Waiting for " + nBooks + " Ticks.");

                    if (carriedBooks.size() == 0) {
                        carriedBooks = null;
                    } else {
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
