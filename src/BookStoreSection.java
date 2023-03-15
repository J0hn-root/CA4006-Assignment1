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
    private double sectionWaitingTime;
    private Box box;

    public BookStoreSection (Timer timer, Box box, Integer shelfCapacity) {
        this.timer = timer;
        this.shelf = new ArrayList<>();
        // each section starts with a book
        this.shelf.add(new Book(this.getBookStoreSectionCategory()));
        this.queue = 0;
        this.soldBooks = 0;
        this.shelfCapacity = shelfCapacity;
        // base class never initialized and not runnable, but child classes bookstore-sections are runnable
        this.timer.increaseNumberOfJobs();
        this.sectionWaitingTime = 0;
        this.box = box;
    }
    public void setSectionCustomerWaitingTime (BookCategory category ,Integer waitingTime) {
        if(this.nextSection != null) {
            this.nextSection.setSectionCustomerWaitingTime(category, waitingTime);
        }
    }

    public double getSectionCustomerWaitingTime (BookCategory category) {
        if(this.nextSection != null) {
            return this.nextSection.getSectionCustomerWaitingTime(category);
        }

        return 0;
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
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void buyBook ( ){
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

            // notify assistant that books have been sold
            this.box.bookSold();

            synchronized (assistantLock){
                assistantLock.notify();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void setCustomerWaitingTime (Integer waitingTime) {
        this.sectionWaitingTime = this.sectionWaitingTime + waitingTime;
    }

    public synchronized double getCustomerWaitingTime () {
        // sold books represent also the amount of customers by section
        return this.sectionWaitingTime / this.soldBooks;
    }
}
