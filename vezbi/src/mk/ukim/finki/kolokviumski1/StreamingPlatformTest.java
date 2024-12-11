package mk.ukim.finki.kolokviumski1;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

abstract class Show {
    public String name;
    public List<String> genres;

    public Show(String name, List<String> genres) {
        this.name = name;
        this.genres = genres;
    }

    abstract public double calculate();
}

class Episode {
    public String name;
    public List<Integer> ratings;

    public Episode(String name, List<Integer> ratings) {
        this.name = name;
        this.ratings = ratings;
    }

    public double calculateRating() {
        return (ratings.stream().mapToDouble(Integer::doubleValue).average().getAsDouble() * Math.min(ratings.size() / 20.0, 1.0));
    }
}

class Movie extends Show {
    public List<Integer> ratings;

    public Movie(String name, List<String> genres, List<Integer> ratings) {
        super(name, genres);
        this.ratings = ratings;
    }


    @Override
    public double calculate() {
        return (ratings.stream().mapToDouble(Integer::doubleValue).average().getAsDouble() * Math.min(ratings.size() / 20.0, 1.0));
    }

    @Override
    public String toString() {
        return String.format("Movie %s %.4f", name, calculate());
    }
}

class Series extends Show {
    public List<Episode> episodes;

    public Series(String name, List<String> genres, List<Episode> episodes) {
        super(name, genres);
        this.episodes = episodes;
    }

    @Override
    public double calculate() {
        episodes.sort(Comparator.comparing(Episode::calculateRating).reversed());
        List<Episode> bestThreeEpisodes = episodes.subList(0, 3);
        return bestThreeEpisodes.stream().mapToDouble(Episode::calculateRating).average().getAsDouble();
    }

    @Override
    public String toString() {
        return String.format("TV Show %s %.4f (%d episodes)", name, calculate(), episodes.size());
    }
}

class StreamingPlatform {
    public List<Show> shows;

    public StreamingPlatform() {
        this.shows = new ArrayList<>();
    }

    public void addItem(String data) {
        String[] parts = data.split(";");
        //Spider-Man: No Way Home; <-- parts[0]
        // Action,Adventure,Sci-Fi; <-- parts[1]
        // 8 9 7 9 10 8 10 9 10 8 8 9 9 10 10 8 9 10 8 9 10 8 8 9 10 <-- parts[2]
        //Friends;
        // Comedy,Romance;
        // S1E1 9 9 8 8 10 9 8 9 10 8 10 8 9 10 9 8 9 8 10 9 10 8 9 10 8; parts[2]
        // S1E2 8 9 8 10 9 8 9 10 8 9 7 7 7 7 parts[3]
        String name = parts[0];
        List<String> genres = Arrays.asList(parts[1].split(","));
        if (parts.length <= 3) {
            List<Integer> ratings = new ArrayList<>();
            String[] rats = parts[2].split(" ");
            for (int i = 0; i < rats.length; i++) {
                ratings.add(Integer.parseInt(rats[i]));
            }
            shows.add(new Movie(name, genres, ratings));
        } else {
            List<Episode> episodes=new ArrayList<>();
            for (int i = 2; i < parts.length; i++) {
                String[] lineParts = parts[i].split("\\s+");
                String episodeName = lineParts[0];
                List<Integer> ratings=new ArrayList<>();
                for (int m = 1; m < lineParts.length; m++) {
                    // 9 9 8 8 10 9 8 9 10 8 10 8 9 10 9 8 9 8 10 9 10 8 9 10 8
                    ratings.add(Integer.parseInt(lineParts[m]));
                }
                episodes.add(new Episode(episodeName, ratings));
            }
            shows.add(new Series(name, genres, episodes));
        }
    }

    public void listAllItems(OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        shows.sort(Comparator.comparing(Show::calculate).reversed());
        shows.forEach(show -> pw.println(show.toString()));
        pw.flush();
    }

    public void listFromGenre(String genre, OutputStream os) {
        PrintWriter pw = new PrintWriter(os);
        List<Show> result=new ArrayList<>();
        for (Show show : shows) {
            if(show.genres.contains(genre)) {
                result.add(show);
            }
        }
        result.sort(Comparator.comparing(Show::calculate).reversed());
        result.forEach(show -> pw.println(show.toString()));
        pw.flush();
    }
}

public class StreamingPlatformTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StreamingPlatform sp = new StreamingPlatform();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");
            String method = parts[0];
            String data = Arrays.stream(parts).skip(1).collect(Collectors.joining(" "));
            if (method.equals("addItem")) {
                sp.addItem(data);
            } else if (method.equals("listAllItems")) {
                sp.listAllItems(System.out);
            } else if (method.equals("listFromGenre")) {
                System.out.println(data);
                sp.listFromGenre(data, System.out);
            }
        }

    }
}
