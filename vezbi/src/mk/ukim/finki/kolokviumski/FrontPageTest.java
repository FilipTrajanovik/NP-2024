package mk.ukim.finki.kolokviumski;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String msg)
    {
        super(msg);
    }

}

class Category{
    public String name;

    public Category(String name) {
        this.name = name;
    }

}

abstract class NewsItem{
    public String title;
    public Date date;
    public Category category;
    public NewsItem(String title, Date date, Category category) {
        this.title = title;
        this.date = date;
        this.category = category;
    }
    abstract public String getTeaser();

}

class TextNewsItem extends NewsItem{
    public String text;
    public TextNewsItem(String title, Date date, Category category, String text) {
        super(title, date, category);
        this.text = text;
    }


    @Override
    public String getTeaser() {
        StringBuilder sb=new StringBuilder();

        long duration=Calendar.getInstance().getTime().getTime()-date.getTime();
        sb.append(title).append("\n");
        sb.append(TimeUnit.MILLISECONDS.toMinutes(duration)).append("\n");
        for(int i=0;i<80 && i<text.length();i++) {
            sb.append(text.charAt(i));
        }
        sb.append("\n");
        return sb.toString();
    }
}

class MediaNewsItem extends NewsItem{
    public String url;
    public int numViews;
    public MediaNewsItem(String title, Date date, Category category, String url, int numViews) {
        super(title, date, category);
        this.url = url;
        this.numViews = numViews;
    }

    @Override
    public String getTeaser() {
        StringBuilder sb=new StringBuilder();
        long duration=Calendar.getInstance().getTime().getTime()-date.getTime();
        sb.append(title).append("\n");
        sb.append(TimeUnit.MILLISECONDS.toMinutes(duration)).append("\n");
        sb.append(url).append("\n");
        sb.append(numViews).append("\n");
        return sb.toString();
    }
}

class FrontPage{
    public List<NewsItem> news;
    public List<Category> categories;
    public FrontPage(Category[] categories){
        this.news=new ArrayList<>();
        this.categories=new ArrayList<>();
        for (Category category : categories) {
            this.categories.add(new Category(category.name));
        }
    }
    public void addNewsItem(NewsItem newsItem){
        news.add(newsItem);
    }
    public List<NewsItem> listByCategory(Category category){
        return news.stream().filter(i->i.category.equals(category)).collect(Collectors.toList());
    }
    public List<NewsItem> listByCategoryName(String category) throws CategoryNotFoundException {
        List<NewsItem> found=news.stream().filter(i->i.category.name.equals(category)).collect(Collectors.toList());
        List<Category> categories1=categories.stream().filter(i->i.name.equals(category)).collect(Collectors.toList());
        if(categories1.isEmpty()){
            throw new CategoryNotFoundException("Category Fun was not found");
        }else{
            return found;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        news.forEach(i->sb.append(i.getTeaser()));
        return sb.toString();
    }
}
public class FrontPageTest {
    public static void main(String[] args) {
        // Reading
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }
        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            cal = Calendar.getInstance();
            int min = scanner.nextInt();
            cal.add(Calendar.MINUTE, -min);
            Date date = cal.getTime();
            scanner.nextLine();
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -min);
            scanner.nextLine();
            Date date = cal.getTime();
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }
        // Execution
        String category = scanner.nextLine();
        System.out.println(frontPage);
        for(Category c : categories) {
            System.out.println(frontPage.listByCategory(c).size());
        }
        try {
            System.out.println(frontPage.listByCategoryName(category).size());
        } catch(CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}


// Vasiot kod ovde