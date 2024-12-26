package mk.ukim.finki.kolokviumski2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Team{
    public String name;
    public int playedGames;
    public int numWins;
    public int numDraws;
    public int goalsScored;
    public int goalsConceded;


    public Team(String name) {
        this.name = name;
        this.playedGames = 0;
        this.numWins = 0;
        this.numDraws = 0;
        this.goalsScored = 0;
        this.goalsConceded = 0;
    }

    public int totalPoints(){
        return this.numWins*3 + this.numDraws;
    }
    public int goalDifference(){
        return this.goalsScored - this.goalsConceded;
    }

    @Override
    public String toString() {
        return String.format("%-15s%5d%5d%5d%5d%5d", name, playedGames, numWins, numDraws, playedGames-numWins-numDraws, totalPoints());
    }

    public String getName() {
        return name;
    }

    public int getPlayedGames() {
        return playedGames;
    }

    public int getNumWins() {
        return numWins;
    }

    public int getNumDraws() {
        return numDraws;
    }

    public int getGoalsScored() {
        return goalsScored;
    }

    public int getGoalsConceded() {
        return goalsConceded;
    }
}

class FootballTable {

    public Map<String, Team> mapTeams;

    public FootballTable() {
        mapTeams = new HashMap<>();
    }

    public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals){
        mapTeams.putIfAbsent(homeTeam, new Team(homeTeam));
        mapTeams.putIfAbsent(awayTeam, new Team(awayTeam));
        Team home = mapTeams.get(homeTeam);
        Team away = mapTeams.get(awayTeam);
        home.goalsScored += homeGoals;
        home.goalsConceded += awayGoals;

        away.goalsScored += awayGoals;
        away.goalsConceded += homeGoals;

        if(homeGoals > awayGoals){
            home.numWins++;
        }else if(homeGoals < awayGoals){
            away.numWins++;
        }else {
            home.numDraws++;
            away.numDraws++;
        }
        home.playedGames++;
        away.playedGames++;

        mapTeams.put(homeTeam, home);
        mapTeams.put(awayTeam, away);
    }
    public void printTable(){
        Comparator<Team> comparator=Comparator.comparing(Team::totalPoints, Comparator.reverseOrder())
                .thenComparing(Team::goalDifference, Comparator.reverseOrder())
                .thenComparing(Team::getName);

        List<Team> teamList= new ArrayList<>(mapTeams.values());
        teamList.sort(comparator);
        for(int i=0;i<teamList.size();i++){
            System.out.printf("%2d. %s\n", i+1, teamList.get(i));
        }
    }


}

public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}

// Your code here

