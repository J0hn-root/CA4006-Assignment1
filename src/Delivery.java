import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Delivery implements Runnable {
    private Timer timer;
    private Box box;
    private static int bookDeliveryInterval;

    public Delivery(Integer bookDeliveryInterval, Timer timer, Box box) {
        this.bookDeliveryInterval = bookDeliveryInterval;
        this.timer = timer;
        this.box = box;
    }

    public void run() {
        try {
            while (true) {
                timer.waitTicks(this.bookDeliveryInterval);

                List<Book> deliveredBooks = new ArrayList<>();
                for(int i = 0; i < 10; i++){
                    Book newBook = new Book();

                    deliveredBooks.add(newBook);
                }

                box.deliverBooks(deliveredBooks);
                //Testing only
//                for(int i = 0; i < 10; i++){
//                    System.out.println(deliveredBooks.get(i).getCategory());
//                }
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
