import java.util.*;

public class Delivery implements Runnable {
    private Timer timer;
    private Box box;
    private Integer bookDeliveryInterval;
    private Integer totalProbabilities;
    private  Map<BookCategory, Map<String, Integer>> deliveryBehaviour;

    public Delivery(Integer bookDeliveryInterval, Timer timer, Box box, String deliveryBooksBehaviour) {
        this.bookDeliveryInterval = bookDeliveryInterval;
        this.timer = timer;
        this.box = box;
        this.timer.increaseNumberOfJobs();

        String[] percentageValues = deliveryBooksBehaviour.split(";");

        this.deliveryBehaviour = new HashMap<>();
        this.totalProbabilities = 0;
        Map<String, Integer> probabilities;

        // books delivery behavior probabilities
        for (int i = 0; i < percentageValues.length; i++) {
            switch (i) {
                case 0:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    deliveryBehaviour.put(BookCategory.FANTASY, probabilities);
                    break;
                case 1:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    deliveryBehaviour.put(BookCategory.FICTION, probabilities);
                    break;
                case 2:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    deliveryBehaviour.put(BookCategory.HISTORY, probabilities);
                    break;
                case 3:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    deliveryBehaviour.put(BookCategory.HORROR, probabilities);
                    break;
                case 4:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    deliveryBehaviour.put(BookCategory.POETRY, probabilities);
                    break;
                case 5:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    deliveryBehaviour.put(BookCategory.ROMANCE, probabilities);
                    break;
                default:
                    break;
            }
        }
    }

    public String getThreadName() {
        return  Thread.currentThread().getName();
    }

    public void run() {
        try {
            while (true) {
                Random randomGen = new Random();
                Integer intervalDelivery = 2 * randomGen.nextInt(this.bookDeliveryInterval);
                timer.waitTicks(intervalDelivery);

                List<Book> deliveredBooks = new ArrayList<>();
                BookCategory[] categories = BookCategory.values();
                for(int i = 0; i < 10; i++){

                    Random random = new Random();
                    Integer probability = random.nextInt(totalProbabilities);
                    BookCategory bookCategory = null;

                    // based on the delivery behaviour probabilities assigned create a random category
                    for (BookCategory category : categories) {
                        Integer low = this.deliveryBehaviour.get(category).get("low");
                        Integer high = this.deliveryBehaviour.get(category).get("high");
                        if (probability >= low && probability < high) {
                            bookCategory = category;
                            break;
                        }
                    }

                    Book newBook = new Book(bookCategory);

                    deliveredBooks.add(newBook);
                }

                box.deliverBooks(deliveredBooks);
                //<Tick count> <Thread ID> Deposited a box of books
                System.out.println("<" + this.timer.getTicks() + "> <" + this.getThreadName() + "> Deposited a box of books");
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
