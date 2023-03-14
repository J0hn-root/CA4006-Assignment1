import java.util.*;

public class Box {
    private Map<BookCategory, List<Book>> box;
    private BookStore bookStore;
    private List<BookCategory> categories;
    private Integer shelfCapacity;

    public Box (BookStore bookStore, Integer shelfCapacity) {
        this.box = new HashMap<>();
        this.bookStore = bookStore;
        this.shelfCapacity = shelfCapacity;

        for (BookCategory category : BookCategory.values()) {
            List<Book> listBooks = new ArrayList<>();
            this.box.put(category, listBooks);
        }
    }

    public synchronized Integer getBookByCategory (BookCategory category) {
        List<Book> categoryBook = this.box.get(category);
        return categoryBook.size();
    }

    public Integer getBoxBooks (){
        return getBoxTotalBooks();
    }

    public synchronized Integer getBoxTotalBooks (){
        Integer totalBooks = 0;
        for (BookCategory category : BookCategory.values()){
            List<Book> listBooks = this.box.get(category);
            totalBooks = totalBooks + listBooks.size();
        }
        return totalBooks;
    }

    // notify assistants waiting that books have been sold and to check if books must be retrieved
    public synchronized void bookSold (){
        notify();
    }

    public synchronized void deliverBooks (List<Book> booksDelivered){
        for(Book book: booksDelivered){
            List<Book> listBooks = this.box.get(book.getCategory());
            listBooks.add(book);
        }
        notify();
    }

    public Map<BookCategory, List<Book>> retrieveBooks (Assistant assistant){
        try {
            // object to be returned
            Map<BookCategory, List<Book>> retrievedBooks = new HashMap<>();
            Integer bookCollected = 0;

            // if assistant already checked the delivery area for books to stock but found nothing
            // the assistant will go on break until the next delivery to avoid waisting resources
            boolean checkedDelivery = false;

            while (bookCollected == 0) {
                this.categories = Arrays.asList(BookCategory.values());
                synchronized (this) {
                    if (getBoxTotalBooks() == 0 || (bookCollected == 0 && checkedDelivery) ) {
                        assistant.setStatus(AssistantStatus.WAITING_DELIVERY);
                        wait();
                        assistant.setStatus(AssistantStatus.GETTING_DELIVERY);
                    }
                }

                // prioritize retrieval of book on queue length/ stock of books per section category
                this.bookStore.prioritiseSectionDelivery(this.categories);

                for (BookCategory category : this.categories) {
                    // retrieve prioritized category list of books from box
                    synchronized (this) {
                        // get the number of books issued for a certain category
                        Integer stock = this.bookStore.getSectionBooks(category);

                        List<Book> listCategoryBook = this.box.get(category);

                        // for each section retrieve the books, limit is 10 per retrieval
                        List<Book> retrievedCategoryBooks = new ArrayList<>();
                        while ((this.bookStore.getBooksIssued(category) + stock) < this.shelfCapacity && bookCollected < this.shelfCapacity && listCategoryBook.size() != 0) {
                            retrievedCategoryBooks.add(listCategoryBook.get(0));
                            // reduce the box section size
                            listCategoryBook.remove(0);
                            bookCollected++;
                            // for optimisation keep track of books issued
                            this.bookStore.setBooksIssued(category, 1);
                        }

                        if (retrievedCategoryBooks.size() != 0) {
                            retrievedBooks.put(category, retrievedCategoryBooks);
                        }
                    }


                    if (bookCollected == 10) {
                        break;
                    }

                }

                checkedDelivery = true;
            }

            return retrievedBooks;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
