import java.util.List;

public class PoetryBookStoreSection extends BookStoreSection{

    public PoetryBookStoreSection (Timer timer) {
        super(timer);
    }

    @Override
    public BookCategory getBookStoreSectionCategory() {
        return BookCategory.POETRY;
    }

    @Override
    public void getSectionAndBuyBook (BookCategory category) {
        if(!this.getBookStoreSectionCategory().equals(category)){
            super.getSectionAndBuyBook(category);
            return;
        }

        this.buyBook();
    }

    @Override
    public void getSectionAndStockBooks (BookCategory category, Book book, String name) {
        if(!this.getBookStoreSectionCategory().equals(category)){
            super.getSectionAndStockBooks(category, book, name);
            return;
        }

        this.stockBook(book, name);
    }

    @Override
    public Integer getSectionQueue (BookCategory category) {
        if(!this.getBookStoreSectionCategory().equals(category)){
            return super.getSectionQueue(category);
        }

        return this.getQueue();
    }

    @Override
    public Integer getSectionBooks (BookCategory category) {
        if(!this.getBookStoreSectionCategory().equals(category)){
            return super.getSectionBooks(category);
        }

        return this.getStock();
    }

    @Override
    public Integer getSoldSectionBooks (BookCategory category) {
        if(!this.getBookStoreSectionCategory().equals(category)){
            return super.getSoldSectionBooks(category);
        }

        return this.getSoldBooks();
    }
}
