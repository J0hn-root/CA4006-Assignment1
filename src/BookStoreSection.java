import java.util.ArrayList;
import java.util.List;

public class BookStoreSection {
    private Timer timer;
    private List<Book> shelf;
    private Integer queue;
    private BookStoreSection nextSection = null;

    public BookStoreSection (Timer timer) {
        this.timer = timer;
        this.shelf = new ArrayList<>();
        this.queue = 0;
    }

    public BookCategory getBookStoreSectionCategory () {
        return null;
    }

    public BookStoreSection setNextBookStoreSectionInChain(BookStoreSection section) {
        this.nextSection = section;
        return section;
    }

    public void getSectionAndBuyBook (BookCategory category) {
        if(this.nextSection != null) {
            this.nextSection.getSectionAndBuyBook(category);
        }
    }

    public void getSectionAndStockBooks (BookCategory category, Book book, String name) {
        if(this.nextSection != null){
            this.nextSection.getSectionAndStockBooks(category, book, name);
        }
    }

    public Integer getSectionQueue (BookCategory category) {
        if(this.nextSection != null){
            return this.nextSection.getSectionQueue(category);
        }

        return null;
    }

    public synchronized Integer getStock() {
        return this.shelf.size();
    }

    private synchronized void increaseQueue() {
        this.queue = this.queue + 1;
    }

    private synchronized void decreaseQueue() {
        this.queue = this.queue - 1;
    }

    public synchronized Integer getQueue() {
        return this.queue;
    }

    private synchronized void stockBook(Book book) {
        this.shelf.add(book);
    }

    private synchronized void sellBook() {
        this.shelf.remove(0);
    }

    public synchronized void stockBook (Book bookDelivered, String name){
        try {

            System.out.println(this.getBookStoreSectionCategory() + ": Currently Stocking Book.");
            this.stockBook(bookDelivered);
            notify();
            // for every book they put on the shelf, it takes 1 tick
            timer.waitTicks(1);

            System.out.println(name + ": " + this.getBookStoreSectionCategory() + " books stocked! " + this.getStock());
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public synchronized void buyBook ( ){
        System.out.println(this.getBookStoreSectionCategory() + ": Client In!");
        try {
            if(shelf.size() == 0){
                this.increaseQueue();
                System.out.println(this.getBookStoreSectionCategory() + ": Queue " + this.getQueue());
                wait();
            }

            this.sellBook();
            this.decreaseQueue();

            System.out.println(this.getBookStoreSectionCategory() + ": Books sold! " + this.getStock());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
