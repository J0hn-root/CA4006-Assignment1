import java.util.Random;

public class Client implements Runnable {

    private BookCategory bookToBuy;
    private BookStore bookStore;
    public Client(BookStore bookStore){
        BookCategory[] categories = BookCategory.values();
        Random random = new Random();
        this.bookToBuy = categories[random.nextInt(categories.length)];
        this.bookStore = bookStore;
    }

    public void run(){
        bookStore.buyBook(this.bookToBuy);
    }
}
