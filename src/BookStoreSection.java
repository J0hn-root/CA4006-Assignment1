import java.util.ArrayList;
import java.util.List;

public class BookStoreSection {
    private Timer timer;
    private List<Book> shelf;
    private Integer shelfCapacity;
    private Integer queue;
    private Integer soldBooks;
    private BookStoreSection nextSection = null;
    private Object assistantLock = new Object();

    public BookStoreSection (Timer timer, Integer shelfCapacity) {
        this.timer = timer;
        this.shelf = new ArrayList<>();
        // each section starts with a book
        this.shelf.add(new Book(this.getBookStoreSectionCategory()));
        this.queue = 0;
        this.soldBooks = 0;
        this.shelfCapacity = shelfCapacity;
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

    public void getSectionAndStockBooks (BookCategory category, Book book, Assistant assistant) {
        if(this.nextSection != null){
            this.nextSection.getSectionAndStockBooks(category, book, assistant);
        }
    }

    public Integer getSectionQueue (BookCategory category) {
        if(this.nextSection != null){
            return this.nextSection.getSectionQueue(category);
        }

        return null;
    }

    public Integer getSectionBooks (BookCategory category) {
        if(this.nextSection != null){
            return this.nextSection.getSectionBooks(category);
        }

        return null;
    }

    public Integer getSoldSectionBooks (BookCategory category) {
        if(this.nextSection != null){
            return this.nextSection.getSoldSectionBooks(category);
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

    public synchronized Integer getSoldBooks() {
        return this.soldBooks;
    }

    public void stockBook (Book bookDelivered, Assistant assistant){
        try {
            System.out.println(this.getBookStoreSectionCategory() + ": Currently Stocking Book.");

            synchronized (assistantLock) {
                while (this.shelf.size() == this.shelfCapacity ) {
                    assistant.setStatus(AssistantStatus.QUEUE_SECTION_FULL);
                    assistantLock.wait();
                }
            }

            synchronized (this) {
                assistant.setStatus(AssistantStatus.STOCKING);
                this.shelf.add(bookDelivered);
                notify();
            }

            System.out.println(assistant.getName() + ": " + this.getBookStoreSectionCategory() + " books stocked! ");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void buyBook ( ){
        System.out.println(this.getBookStoreSectionCategory() + ": Client In!");
        try {
            synchronized (this) {
                if (shelf.size() == 0) {
                    this.increaseQueue();
                    wait();
                    this.decreaseQueue();
                }
            }

            synchronized (this) {
                try {
                    this.shelf.remove(0);
                    this.soldBooks++;
                } catch (Exception e) {
                    System.out.println(shelf.size());
                    throw new RuntimeException(e);
                }
            }

            synchronized (assistantLock){
                assistantLock.notify();
            }

            System.out.println(this.getBookStoreSectionCategory() + ": Books sold! " + this.getStock());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
