/*
    - Every 10 ticks (on average) a customer will buy a book from one of the sections (randomly).
    - If the section is empty, the customer will wait until a book for that section becomes available.
    - This means there may be times where a particular section does not contain any books, and
    the customer will wait until a book for that section is available.


 */

public class ClientGenerator implements Runnable {

    private Timer timer;
    private BookStore bookStore;
    private static int clientGenerationInterval;

    public ClientGenerator(Integer clientGenerationInterval, Timer timer, BookStore bookStore) {
        this.clientGenerationInterval = clientGenerationInterval;
        this.timer = timer;
        this.bookStore = bookStore;
    }

    public void run() {
        try {
            while (true) {
                timer.waitTicks(this.clientGenerationInterval);

                Runnable client = new Client(this.bookStore);
                new Thread(client).start();
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
