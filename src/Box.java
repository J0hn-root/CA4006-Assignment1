import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Box {
    private LinkedList<List<Book>> box;

    public Box () {
        this.box = new LinkedList<>();
    }

    public synchronized void deliverBooks (List<Book> booksDelivered){
        this.box.add(booksDelivered);
        notify();

        System.out.println("Books delivered! " + this.box.size());
    }

    public synchronized List<Book> retrieveBooks (String assistantName ){
        try {
            if(box.size() == 0){
                wait();
            }

            System.out.println(assistantName + ": Books read! " + (this.box.size() - 1));
            return box.removeFirst();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
