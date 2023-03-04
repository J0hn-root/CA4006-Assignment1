import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookStore {
    private static Timer timer;

    private BookStoreSection firstSectonInChain;


    public BookStore (Timer timer) {
        BookStoreSection firstSectioninChain = new FictionBookStoreSection(timer);

        firstSectioninChain
                .setNextBookStoreSectionInChain(new HorrorBookStoreSection(timer))
                .setNextBookStoreSectionInChain(new RomanceBookStoreSection(timer))
                .setNextBookStoreSectionInChain(new FantasyBookStoreSection(timer))
                .setNextBookStoreSectionInChain(new PoetryBookStoreSection(timer))
                .setNextBookStoreSectionInChain(new HistoryBookStoreSection(timer));

        this.firstSectonInChain = firstSectioninChain;
        this.timer = timer;
    }

    public void buyBook(BookCategory category) {
        this.firstSectonInChain.getSectionAndBuyBook(category);
    }

    public void stockBooks(BookCategory category, Book book, String name) {
        this.firstSectonInChain.getSectionAndStockBooks(category, book, name);
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

}
