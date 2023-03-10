import java.sql.Time;
import java.util.Map;
import java.util.Random;

public class Client implements Runnable {

    private BookCategory category;
    private BookStore bookStore;
    private Timer timer;
    private String name;
    private ClientGenerator clientGenerator;

    public Client(ClientGenerator clientGenerator, BookStore bookStore, Timer timer, Integer customerNumber, Map<BookCategory, Map<String,Integer>> purchaseBehaviour, Integer totalProbabilities){
        this.bookStore = bookStore;
        this.timer = timer;
        this.timer.increaseNumberOfJobs();
        this.name = "Customer-" + customerNumber;
        this.clientGenerator = clientGenerator;

        BookCategory[] categories = BookCategory.values();

        Random random = new Random();
        Integer probability = random.nextInt(totalProbabilities);

        // based on the client behaviour probabilities assigned create a random category
        for (BookCategory category : categories) {
            Integer low = purchaseBehaviour.get(category).get("low");
            Integer high = purchaseBehaviour.get(category).get("high");
            if (probability >= low && probability < high) {
                this.category = category;
                break;
            }
        }

    }

    public void run(){
        //<Tick count> <Thread ID> Customer-7 enters the HISTORY section
        System.out.println("<" + this.timer.getTicks() + "> <" + this.getThreadName() + "> " + this.name + " enters the " + this.category + " section");

        Integer startTime = this.timer.getTicks();
        bookStore.buyBook(this.category);
        Integer endTime = this.timer.getTicks();

        //<Tick count> <Thread ID> Customer-7 collected a HISTORY book having waited 90 TICKS
        System.out.println("<" + this.timer.getTicks() + "> <" + this.getThreadName() + "> " + this.name + " collected a " + this.category + " book having waited " + (endTime - startTime) + " TICKS");


        this.timer.decreaseNumberOfJobs();

        this.bookStore.setTotalCustomerWaitingTime((endTime - startTime));
        this.bookStore.setSectionCustomerWaitingTime(this.category, (endTime - startTime));
    }

    public String getThreadName() {
        return  Thread.currentThread().getName();
    }
}
