import java.util.Random;

public class Book {
    private BookCategory category;

    public Book() {
        BookCategory[] categories = BookCategory.values();
        Random random = new Random();
        BookCategory randomCategory = categories[random.nextInt(categories.length)];

        this.category = randomCategory;
    }

    public BookCategory getCategory(){
        return this.category;
    }
}
