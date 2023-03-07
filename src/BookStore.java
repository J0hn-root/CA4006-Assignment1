import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookStore {
    private static Timer timer;

    private BookStoreSection firstSectonInChain;


    public BookStore (Timer timer, Integer shelfCapacity) {
        BookStoreSection firstSectioninChain = new FictionBookStoreSection(timer, shelfCapacity);

        firstSectioninChain
                .setNextBookStoreSectionInChain(new HorrorBookStoreSection(timer, shelfCapacity))
                .setNextBookStoreSectionInChain(new RomanceBookStoreSection(timer, shelfCapacity))
                .setNextBookStoreSectionInChain(new FantasyBookStoreSection(timer, shelfCapacity))
                .setNextBookStoreSectionInChain(new PoetryBookStoreSection(timer, shelfCapacity))
                .setNextBookStoreSectionInChain(new HistoryBookStoreSection(timer, shelfCapacity));

        this.firstSectonInChain = firstSectioninChain;
        this.timer = timer;
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

}
