package mk.ukim.finki.kolokviumski2;

import java.util.*;
import java.util.stream.Collectors;

class CosineSimilarityCalculator2 {

    public static double cosineSimilarity(Map<String, Integer> c1, Map<String, Integer> c2) {
        return cosineSimilarity(c1.values(), c2.values());
    }

    public static double cosineSimilarity(Collection<Integer> c1, Collection<Integer> c2) {
        int[] array1;
        int[] array2;
        array1 = c1.stream().mapToInt(i -> i).toArray();
        array2 = c2.stream().mapToInt(i -> i).toArray();
        double up = 0.0;
        double down1 = 0, down2 = 0;

        for (int i = 0; i < c1.size(); i++) {
            up += (array1[i] * array2[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down1 += (array1[i] * array1[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down2 += (array2[i] * array2[i]);
        }

        return up / (Math.sqrt(down1) * Math.sqrt(down2));
    }
}


abstract class ObjectConstruct{
    public String id;
    public String name;

    public ObjectConstruct(String id, String name) {
        this.id = id;
        this.name = name;
    }
}


class StreamingUser extends ObjectConstruct {

    public Map<String, Integer> ratings;
    public List<String> ratedMovies;
    public TreeMap<Integer, Movie> topMovies;


    public StreamingUser(String id, String name) {
        super(id, name);
        this.ratings=new HashMap<>();
        this.ratedMovies=new ArrayList<>();
        this.topMovies=new TreeMap<>();
    }
    public void addRatedMovie(String movieId, int rating, String movieName)
    {
        Movie movie=new Movie(movieId, movieName);
        topMovies.put(rating, movie);
    }



    @Override
    public String toString() {
       return String.format("User ID: %s Name: %s", id, name);
    }
}

class Movie extends ObjectConstruct {
    public List<Integer> ratings;
    public Map<StreamingUser, Integer> ratingsByUser;

    public Movie(String id, String name) {
        super(id, name);
        this.ratings=new ArrayList<>();
        this.ratingsByUser=new HashMap<>();
    }
    public void addRating(int rating) {
        ratings.add(rating);
    }
    public double getAverageRating() {
        return ratings.stream().mapToInt(i->i).average().orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("Movie ID: %s Title: %s Rating: %.2f", id, name, getAverageRating());
    }
}

class StreamingPlatform{

    public Map<String, StreamingUser> mapUsers;
    public Map<String, Movie> mapMovies;
    public StreamingPlatform() {
        this.mapMovies=new HashMap<>();
        this.mapUsers = new HashMap<>();
    }
    public void addMovie (String id, String name){
        Movie movie=new Movie(id, name);
        mapMovies.put(id, movie);
    }
    public void addUser (String id, String username){
        StreamingUser user=new StreamingUser(id, username);
        mapUsers.put(id, user);
    }
    public void addRating (String userId, String movieId, int rating){
        Movie movie=mapMovies.get(movieId);
        StreamingUser user=mapUsers.get(userId);
        if(movie!=null)
        {
            movie.addRating(rating);
            user.ratedMovies.add(movieId);
            user.addRatedMovie(movieId, rating, movie.name);
            movie.ratingsByUser.put(user, rating);
        }

    }
    public void  topNMovies (int n){
        Comparator<Movie> comparator = Comparator.comparing(Movie::getAverageRating).reversed();
        mapMovies.values().stream().sorted(comparator).limit(n).forEach(System.out::println);

    }
    public void favouriteMoviesForUsers(List<String> userIds){
        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.get(i);
            StreamingUser currentUser = mapUsers.get(userId);

            int maxRatingByUser = currentUser.topMovies.lastKey();

            // Print user details
            System.out.println(currentUser);

            // Collect and sort favorite movies
            List<Movie> tempMovie = new ArrayList<>();
            for (String ratedMovie : currentUser.ratedMovies) {
                Movie currentMovie = mapMovies.get(ratedMovie);
                int rating = currentMovie.ratingsByUser.get(currentUser);
                if (rating == maxRatingByUser) {
                    tempMovie.add(currentMovie);
                }
            }

            tempMovie.sort(Comparator.comparing(Movie::getAverageRating).reversed());
            tempMovie.forEach(System.out::println);

            // Print one extra newline after each user except the last one
            if (i != userIds.size() - 1) {
                System.out.println(); // Adds exactly one newline
            }
        }
    }
    public void similarUsers(String userId){
        for (StreamingUser user : mapUsers.values()) {
            for (String movieId : mapMovies.keySet()) {
                user.ratings.putIfAbsent(movieId, 0); // Add missing movies with a default rating of 0
            }
        }

        StreamingUser targetUser = mapUsers.get(userId);
        if (targetUser == null) {
            System.out.println("User not found!");
            return;
        }

        double maxSimilarity = 0;
        StreamingUser similarUser = null;

        for (StreamingUser otherUser : mapUsers.values()) {
            if (!otherUser.id.equals(userId)) { // Skip the target user
                double similarity = CosineSimilarityCalculator2.cosineSimilarity(targetUser.ratings, otherUser.ratings);

                // Update the most similar user if necessary
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    similarUser = otherUser;
                }
            }
        }


        System.out.println(similarUser);
    }



}

public class StreamingPlatform2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StreamingPlatform sp = new StreamingPlatform();

        while (sc.hasNextLine()){
            String line = sc.nextLine();
            String [] parts = line.split("\\s+");

            if (parts[0].equals("addMovie")) {
                String id = parts[1];
                String name = Arrays.stream(parts).skip(2).collect(Collectors.joining(" "));
                sp.addMovie(id ,name);
            } else if (parts[0].equals("addUser")){
                String id = parts[1];
                String name = parts[2];
                sp.addUser(id ,name);
            } else if (parts[0].equals("addRating")){
                //String userId, String movieId, int rating
                String userId = parts[1];
                String movieId = parts[2];
                int rating = Integer.parseInt(parts[3]);
                sp.addRating(userId, movieId, rating);
            } else if (parts[0].equals("topNMovies")){
                int n = Integer.parseInt(parts[1]);
                System.out.println("TOP " + n + " MOVIES:");
                sp.topNMovies(n);
            } else if (parts[0].equals("favouriteMoviesForUsers")) {
                List<String> users = Arrays.stream(parts).skip(1).collect(Collectors.toList());
                System.out.println("FAVOURITE MOVIES FOR USERS WITH IDS: " + users.stream().collect(Collectors.joining(", ")));
                sp.favouriteMoviesForUsers(users);
            } else if (parts[0].equals("similarUsers")) {
                String userId = parts[1];
                System.out.println("SIMILAR USERS TO USER WITH ID: " + userId);
                sp.similarUsers(userId);
            }
        }
    }
}


