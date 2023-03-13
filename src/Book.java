import java.security.SecureRandom;

public class Book {
    private BookCategory category;

    public Book(BookCategory category) {
        this.category = category;
    }

    public Book() {
        BookCategory[] categories = BookCategory.values();
        SecureRandom secureRandom = new SecureRandom();
        BookCategory randomCategory = categories[secureRandom.nextInt(categories.length)];


        this.category = randomCategory;
    }

    public BookCategory getCategory(){
        return this.category;
    }
}
