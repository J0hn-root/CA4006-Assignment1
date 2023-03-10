import java.util.*;

public class BookStore {
    private static Timer timer;
    private BookStoreSection firstSectonInChain;
    private Integer totalCustomers;
    private double totalWaitingTime;
    // takes count of the number of books the assistants are carrying for optimising prioritization
    private Map<BookCategory, Integer> booksIssued;
    private Object booksIssuedLock = new Object();


    public BookStore (Timer timer, Integer shelfCapacity) {
        this.booksIssued = new HashMap<>();

        // initialize
        for (BookCategory category : BookCategory.values()) {
            this.booksIssued.put(category, 0);
        }

        BookStoreSection firstSectioninChain = new FictionBookStoreSection(timer, shelfCapacity);

        firstSectioninChain
                .setNextBookStoreSectionInChain(new HorrorBookStoreSection(timer, shelfCapacity))
                .setNextBookStoreSectionInChain(new RomanceBookStoreSection(timer, shelfCapacity))
                .setNextBookStoreSectionInChain(new FantasyBookStoreSection(timer, shelfCapacity))
                .setNextBookStoreSectionInChain(new PoetryBookStoreSection(timer, shelfCapacity))
                .setNextBookStoreSectionInChain(new HistoryBookStoreSection(timer, shelfCapacity));

        this.firstSectonInChain = firstSectioninChain;
        this.timer = timer;
        this.totalCustomers = 0;
        this.totalWaitingTime = 0;
    }

    public void setBooksIssued (BookCategory category, Integer value) {
        synchronized (this.booksIssuedLock) {
            Integer booksInTransit = this.booksIssued.get(category);
            this.booksIssued.put(category, booksInTransit + value);
        }
    }

    public void decreseBooksIssued (BookCategory category, Integer value) {
        synchronized (this.booksIssuedLock) {
            Integer booksInTransit = this.booksIssued.get(category);
            this.booksIssued.put(category, booksInTransit - value);
        }
    }

    public Integer getBooksIssued (BookCategory category) {
        synchronized (this.booksIssuedLock) {
            return this.booksIssued.get(category);
        }
    }

    public synchronized void setTotalCustomerWaitingTime (Integer waitingTime) {
        this.totalWaitingTime = this.totalWaitingTime + waitingTime;
    }

    public synchronized double getCustomerWaitingTimeMean () {
        return this.totalCustomers != 0 ? this.totalWaitingTime / this.totalCustomers : 0;
    }

    public synchronized Integer getClientNumber () {
        return this.totalCustomers++;
    }

    public void buyBook(BookCategory category) {
        this.firstSectonInChain.getSectionAndBuyBook(category);
    }

    public void stockBooks(BookCategory category, Book book, Assistant assistant) {
        this.firstSectonInChain.getSectionAndStockBooks(category, book, assistant);
    }

    public Integer getSectionQueue(BookCategory category) {
        return this.firstSectonInChain.getSectionQueue(category);
    }

    public Integer getSectionBooks(BookCategory category) {
        return this.firstSectonInChain.getSectionBooks(category);
    }

    public Integer getSoldSectionBooks(BookCategory category) {
        return this.firstSectonInChain.getSoldSectionBooks(category);
    }

    public double getSectionCustomerWaitingTime(BookCategory category) {
        return this.firstSectonInChain.getSectionCustomerWaitingTime(category);
    }

    public void setSectionCustomerWaitingTime (BookCategory category, Integer waitingTime) {
        this.firstSectonInChain.setSectionCustomerWaitingTime(category, waitingTime);
    }

    public void prioritiseSectionDelivery (List<BookCategory> categories) {
        Collections.sort(categories, new Comparator<BookCategory>() {
            @Override
            public int compare(BookCategory category1, BookCategory category2) {

                int queue1 = getSectionQueue(category1);
                int queue2 = getSectionQueue(category2);
                int stock1 = getSectionBooks(category1);
                int stock2 = getSectionBooks(category2);

                int queueCompare = Integer.compare(queue2, queue1);
                if (queueCompare != 0) {
                    // If names are different, sort by name
                    return queueCompare;
                } else {
                    // If names are the same, sort by age
                    return Integer.compare(stock1, stock2);
                }

            }
        });
    }

}
