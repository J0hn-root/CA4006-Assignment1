/*
    - Every 10 ticks (on average) a customer will buy a book from one of the sections (randomly).
    - If the section is empty, the customer will wait until a book for that section becomes available.
    - This means there may be times where a particular section does not contain any books, and
    the customer will wait until a book for that section is available.


 */

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClientGenerator implements Runnable {

    private Timer timer;
    private BookStore bookStore;
    private Integer totalProbabilities;
    private int clientGenerationInterval;
    private  Map<BookCategory, Map<String, Integer>> purchaseBehaviour;

    public ClientGenerator(Integer clientGenerationInterval, Timer timer, BookStore bookStore, String clientPurchaseBehaviour) {
        this.clientGenerationInterval = clientGenerationInterval;
        this.timer = timer;
        this.bookStore = bookStore;
        this.timer.increaseNumberOfJobs();

        String[] percentageValues = clientPurchaseBehaviour.split(";");

        this.purchaseBehaviour = new HashMap<>();
        this.totalProbabilities = 0;
        Map<String, Integer> probabilities;

        // store client purchase behavior probabilities
        for (int i = 0; i < percentageValues.length; i++) {
            switch (i) {
                case 0:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    purchaseBehaviour.put(BookCategory.FANTASY, probabilities);
                    break;
                case 1:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    purchaseBehaviour.put(BookCategory.FICTION, probabilities);
                    break;
                case 2:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    purchaseBehaviour.put(BookCategory.HISTORY, probabilities);
                    break;
                case 3:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    purchaseBehaviour.put(BookCategory.HORROR, probabilities);
                    break;
                case 4:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    purchaseBehaviour.put(BookCategory.POETRY, probabilities);
                    break;
                case 5:
                    probabilities = new HashMap<>();
                    probabilities.put("low", totalProbabilities);
                    totalProbabilities += Integer.parseInt(percentageValues[i]);
                    probabilities.put("high", totalProbabilities);
                    purchaseBehaviour.put(BookCategory.ROMANCE, probabilities);
                    break;
                default:
                    break;
            }
        }
    }

    public void run() {
        try {
            while (true) {
                Random randomGen = new Random();
                Integer intervalClientGeneration = 2 * randomGen.nextInt(this.clientGenerationInterval);
                timer.waitTicks(intervalClientGeneration);

                Integer clientNumber = this.bookStore.getClientNumber();
                Runnable client = new Client(this, this.bookStore, this.timer, clientNumber, this.purchaseBehaviour, this.totalProbabilities);
                new Thread(client).start();
            }
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
