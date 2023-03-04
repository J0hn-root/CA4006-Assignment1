import java.util.List;

public class PoetryBookStoreSection extends BookStoreSection{

    public PoetryBookStoreSection (Timer timer) {
        super(timer);
    }

    public BookCategory getBookStoreSectionCategory() {
        return BookCategory.POETRY;
    }

    public void getSectionAndBuyBook (BookCategory category) {
        if(!this.getBookStoreSectionCategory().equals(category)){
            super.getSectionAndBuyBook(category);
        }

        this.buyBook();
    }

    public void getSectionAndStockBooks (BookCategory category, Book book, String name) {
        if(!this.getBookStoreSectionCategory().equals(category)){
            super.getSectionAndStockBooks(category, book, name);
        }

        this.stockBook(book, name);
    }

    public Integer getSectionQueue (BookCategory category) {
        if(!this.getBookStoreSectionCategory().equals(category)){
            super.getSectionQueue(category);
        }

        return this.getQueue();
    }
}