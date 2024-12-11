package mk.ukim.finki.av2;

import java.util.*;

class PlayingCard {
    public enum TYPE {hearts, diamond, spade, clubs}

    private TYPE type;
    private int value;

    public PlayingCard(TYPE type, int value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Type: " + type.toString() + " Value: " + value;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayingCard that = (PlayingCard) o;
        return value == that.value && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}

class Deck {
    private PlayingCard[] cards;
    private boolean[] isDealt;
    private int dealtTotal;

    public Deck() {
        cards = new PlayingCard[52];
        isDealt = new boolean[52];
        dealtTotal = 0;
        for (int i = 0; i < PlayingCard.TYPE.values().length; i++) {
            for (int j = 0; j < 13; j++) {
                cards[i * 13 + j] = new PlayingCard(PlayingCard.TYPE.values()[i], j + 2);
            }
        }
    }

    public PlayingCard[] getCards() {
        return cards;
    }

    public void setCards(PlayingCard[] cards) {
        this.cards = cards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deck deck = (Deck) o;
        return Objects.deepEquals(cards, deck.cards);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(cards);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (PlayingCard card : cards) {
            s.append(card).append("\n");
        }
        return s.toString();
    }

    public boolean hasCardsLeft() {
        return (cards.length - dealtTotal > 0);
    }

    public PlayingCard[] shuffle() {
        List<PlayingCard> playingCardList = Arrays.asList(cards);
        Collections.shuffle(playingCardList);
        return cards;
    }

    public PlayingCard dealCard() {
        if (!hasCardsLeft()) {
            return null;
        }
        int card = (int) (Math.random() * 52);
        if (!isDealt[card]) {
            isDealt[card] = true;
            dealtTotal++;
            return cards[card];
        }
        return dealCard();
    }

    public void dealCards() {
        shuffle();
        for (PlayingCard card : cards) {
            System.out.println(card);
        }
    }


}

class MultipleDecks {
    private Deck[] decks;

    public MultipleDecks(int numOfDecks) {
        this.decks = new Deck[numOfDecks];
        for (int i = 0; i < numOfDecks; i++) {
            decks[i] = new Deck();
        }
    }

    @Override
    public String toString() {
       StringBuilder s = new StringBuilder();
       for(Deck deck : decks) {
           s.append(deck).append("\n");
       }
       return s.toString();
    }
}

public class PlayingCardsTest {
    public static void main(String[] args) {
        Deck deck1 = new Deck();
        System.out.println(deck1);

        deck1.shuffle();
        System.out.println(deck1);

        PlayingCard card;
        while ((card = deck1.dealCard()) != null) {
            System.out.println(card);
        }
        Deck deck2 = new Deck();
        System.out.println(deck2);
        deck2.dealCards();

        MultipleDecks multipleDecks = new MultipleDecks(2);
        System.out.println(multipleDecks);
    }
}
