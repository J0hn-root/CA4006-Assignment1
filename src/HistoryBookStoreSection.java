public class HistoryBookStoreSection extends BookStoreSection{

    public HistoryBookStoreSection (Timer timer, Box box, Integer shelfCapacity) {
        super(timer, box, shelfCapacity);
    }

    @Override
    public BookCategory getBookStoreSectionCategory() {
        return BookCategory.HISTORY;
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
    public void getSectionAndStockBooks (BookCategory category, Book book, Assistant assistant) {
        if(!this.getBookStoreSectionCategory().equals(category)){
            super.getSectionAndStockBooks(category, book, assistant);
            return;
        }

        this.stockBook(book, assistant);
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

    @Override
    public void setSectionCustomerWaitingTime (BookCategory category, Integer waitingTime) {
        if(!this.getBookStoreSectionCategory().equals(category)){
            super.setSectionCustomerWaitingTime(category, waitingTime);
            return;
        }

        this.setCustomerWaitingTime(waitingTime);
    }

    @Override
    public double getSectionCustomerWaitingTime (BookCategory category) {
        if(!this.getBookStoreSectionCategory().equals(category)){
            return super.getSectionCustomerWaitingTime(category);
        }

        return this.getCustomerWaitingTime();
    }

}
