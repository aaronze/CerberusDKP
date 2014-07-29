package bot;

import games.cards.DeckOfCards.Card;
import java.util.ArrayList;

/**
 * @author Aaron
 */
public class Gambling {
    public final static String[] HEART_GRAPHIC = new String[] {
        ". . . . .",
        "._ . . . _.",
        "|##V##|",
        ".\\####/.",
        ". .\\##/. .",
        ". . .V. . .",
        ". . . . .."
    };
    public final static String[] CLUB_GRAPHIC = new String[] {
        ". . . . . . .",
        ".. . ,- -, . ..",
        ",--,\\ .. /,--,",
        "\\ _ , . , _ /",
        ". . ./ .. \\. . .",
        ". . .'----'. . .",
        ". . . . . . ."
    };
    public final static String[] SPADE_GRAPHIC = new String[] {
        ". . . . . .",
        ". . . /\\ . . .",
        ". . / .. \\ . .",
        ". / .. . . \\ .",
        ". \\_., ,._/ .",
        ". . . /_\\ . . .",
        ". . . . . ."
    };
    public final static String[] DIAMOND_GRAPHIC = new String[] {
        ". . . . .",
        ". . . /\\ . .",
        ". . / .. \\ .",
        ". ./ .... \\.",
        ". . \\ .. / .",
        ". . . \\/ . ..",
        ". . . . ."
    };
    
    public final static int SPADES = 0, HEARTS = 1, CLUBS = 2, DIAMONDS = 3;
    public final static int JACK = 11, QUEEN = 12, KING = 13, ACE = 1;

    
    public static void gamble(String playerName, int platinum) {
    }
    
    public static String[] makeCard(Card card) {
        String[] lines = new String[7];
        if (!card.isFaceUp()) {
            for (int i = 0; i < lines.length; i++)
                lines[i] = "##########";
            return lines;
        }
        
        int rank = card.rank();
        int suit = card.suit();
        
        String strRank = ""+rank;
        if (rank == ACE) strRank = "A";
        else if (rank == JACK) strRank = "J";
        else if (rank == QUEEN) strRank = "Q";
        else if (rank == KING) strRank = "K";
        
        String[] graphic;
        if (suit == SPADES) graphic = SPADE_GRAPHIC;
        else if (suit == HEARTS) graphic = HEART_GRAPHIC;
        else if (suit == CLUBS) graphic = CLUB_GRAPHIC;
        else graphic = DIAMOND_GRAPHIC;
        
        lines[0] = strRank + " " + graphic[0];
        lines[1] = graphic[1];
        lines[2] = graphic[2];
        lines[3] = graphic[3];
        lines[4] = graphic[4];
        lines[5] = graphic[5];
        lines[6] = graphic[6] + " " + strRank;
        
        return lines;
    }
    
    public static String[] makeHand(Card... cards) {
        String[] lines = new String[7];
        
        for (Card card : cards) {
            String[] graphic = makeCard(card);
            
            for (int i = 0; i < 7; i++)
                lines[i] = graphic[i] + " ";
        }
        
        return lines;
    }
    
    public static String[] makeHand(ArrayList<Card> cards) {
        String[] lines = new String[7];
        for (int i = 0; i < 7; i++)
            lines[i] = "";
        
        for (Card card : cards) {
            String[] graphic = makeCard(card);
            
            for (int i = 0; i < 7; i++)
                lines[i] += graphic[i] + " ";
        }
        
        return lines;
    }
}
