package mk.ukim.finki.labs.lab7;

import java.util.ArrayList;
import java.util.List;

class Song{
    public String title;
    public String artist;

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

}

class MP3Player{
    public List<Song> songs;
    public int currentSong;
    public boolean isPlaying;
    public boolean isStopped;

    public MP3Player(List<Song> songs) {
        this.songs = songs;
        this.currentSong = 0;
        this.isPlaying = false;
        this.isStopped = false;
    }

    public void pressPlay() {
        if(isPlaying)
        {
            System.out.println("Song is already playing");
        }else if(isStopped){
            isPlaying = true;
            isStopped = false;
            System.out.printf("Song %d is playing%n", currentSong);
        }else{
            isPlaying = true;
            System.out.printf("Song %d is playing%n", currentSong);
        }
    }

    public void printCurrentSong() {
        Song song = songs.get(currentSong);
        System.out.printf("Song{title=%s, artist=%s}%n", song.title, song.artist);
    }

    public void pressStop() {
        if(isStopped)
        {
            System.out.println("Songs are already stopped");
        }else if(isPlaying){
            isStopped = true;
            isPlaying = false;
            System.out.printf("Song %d is paused%n", currentSong);
        }else{
            isStopped = true;
            currentSong = 0;
            System.out.println("Songs are stopped");
        }
    }

    public void pressFWD() {
        isPlaying = false;
        isStopped = false;
        currentSong = (currentSong + 1) % songs.size();
        System.out.println("Forward...");
    }

    public void pressREW() {
        isPlaying = false;
        isStopped = false;
        currentSong = (currentSong - 1 + songs.size()) % songs.size();
        System.out.println("Reward...");
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MP3Player{currentSong = ").append(currentSong).append(", songList = [");
        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            sb.append("Song{title=").append(song.title).append(", artist=").append(song.artist).append("}");
            if (i < songs.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}

public class PatternTest {
    public static void main(String args[]) {
        List<Song> listSongs = new ArrayList<Song>();
        listSongs.add(new Song("first-title", "first-artist"));
        listSongs.add(new Song("second-title", "second-artist"));
        listSongs.add(new Song("third-title", "third-artist"));
        listSongs.add(new Song("fourth-title", "fourth-artist"));
        listSongs.add(new Song("fifth-title", "fifth-artist"));
        MP3Player player = new MP3Player(listSongs);


        System.out.println(player.toString());
        System.out.println("First test");


        player.pressPlay();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Second test");


        player.pressStop();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Third test");


        player.pressFWD();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
    }
}

//Vasiot kod ovde