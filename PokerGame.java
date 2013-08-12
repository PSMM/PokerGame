/*
 * PokerGame.java
 *
 * Written by: Pascal Mettes.
 *
 * This file contains the game essentials.
 */

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;

import java.awt.Image;
import java.awt.Color;
import java.awt.Toolkit;

import java.awt.image.ImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;
import java.awt.image.ImageProducer;


/*
 * The actual poker game essentials. This class contains the playing cards,
 * the deck, the game state, and the current score. Furthermore, the scores
 * are computed here.
 */
public class PokerGame {
    /* The images containing the playing cards. */
    public Image[] cards = new Image[52];
    
    /* Hardcoded values for the card with and height in pixels. */
    public int cwidth = 79;
    public int cheight = 123;
    
    /* The game board and deck. */
    public int[][] board = new int[5][5];
    public ArrayList<Integer> deck = new ArrayList<Integer>();
    public ArrayList<Integer> originaldeck = new ArrayList<Integer>();
    
    /* Game state. */
    public String state = "start";
    
    /* Current score and number of filled places in the grid. */
    public int score = 0;
    public int nr_used = 0;
    
    /*
     * Initialize the game by loading the images, shuffling the deck, and
     * setting the board.
     *
     * Input : -
     * Output: -
     */
    public PokerGame() {
        /* Load the cards and set magenta to invisible. */
        Color bgcolor = new Color(255,0,255);
        for (int i = 0; i < 52; i++) {
            Image image = new ImageIcon("images/" + i + ".png").getImage();
            cards[i] = setColorAlpha(image, bgcolor);
            originaldeck.add(i);
        }
        /* Shuffle the deck to randomize the cards. */
        deck = new ArrayList<Integer>(originaldeck);
        Collections.shuffle(deck);
        
        /* Set the elements of the board to empty. */
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                board[i][j] = -1;
            }
        }
    }
    
    /*
     * Reset the elements of the game to start a new round.
     *
     * Input : -
     * Output: -
     */
    public void reset() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                board[i][j] = -1;
            }
        }
        deck = new ArrayList<Integer>(originaldeck);
        Collections.shuffle(deck);
        state = "play";
        score = 0;
        nr_used = 0;
    }
    
    /*
     * Check whether a new card has created a scoring hand.
     *
     * Input : Updated position (int, int).
     * Output: -
     */
    public void updateScore(int x, int y) {
        int[] listX = new int[5];
        int[] listY = new int[5];
        int[] listD1 = new int[5];
        int[] listD2 = new int[5];
        
        /* Load the affected lines. */
        for (int i = 0; i < 5; i++) {
            listX[i] = board[i][y];
            listY[i] = board[x][i];
            listD1[i] = board[i][i];
            listD2[i] = board[i][4-i];
        }
        
        /* Check for new scores for the horizontal and vertical lines. */
        score += checkScore(listX);
        score += checkScore(listY);
        
        /* Check for new scores for the diagonal lines. */
        if (x == y) {
            score += checkScore(listD1);
        }
        else if (x + y == 4) {
            score += checkScore(listD2);
        }
        
        nr_used += 1;
    }
    
    /*
     * Create a new image from an old image by setting a specific color to
     * transparent (used to remove the corners from the playing cards).
     *
     * Input : The input image (Image) and color (Color).
     * Output: The new image (Image).
     */
    public static Image setColorAlpha(Image image, final Color color) {
        /* Create the filter. */
        ImageFilter filter = new RGBImageFilter() {
            int marker = color.getRGB();
            
            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000 ) == marker) {
                    return 0x00FFFFFF & rgb;
                }
                else {
                    return rgb;
                }
            }
        };
        
        /* Use the filter to create a new image. */
        ImageProducer ip = new FilteredImageSource(image.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);
    }
    
    /*
     * Check the score for a single line.
     *
     * Input : The line (int[]).
     * Output: The score (int).
     */
    public static int checkScore(int[] array) {
        /* Sort the array for efficient score computation. */
        Arrays.sort(array);
        /* If a line is not yet filled, there is no score. */
        if (array[0] == -1) {
            return 0;
        }
        
        /* Check for each possible score. */
        if (Scores.isFlush(array) && Scores.isStraight(array.clone())) {
            return 30;
        }
        else if(Scores.hasFourofaKind(array.clone())) {
            return 16;
        }
        else if(Scores.isStraight(array.clone())) {
            return 12;
        }
        else if(Scores.hasThreeofaKind(array.clone()) && Scores.countPairs(array.clone()) == 2) {
            return 10;
        }
        else if(Scores.hasThreeofaKind(array.clone())) {
            return 6;
        }
        else if(Scores.isFlush(array.clone())) {
            return 5;
        }
        else if(Scores.countPairs(array.clone()) == 2) {
            return 3;
        }
        else if(Scores.countPairs(array.clone()) == 1) {
            return 1;
        }
        return 0;
    }
}

/*
 * This is a helper class which contains a set of static functions to check
 * the presence of certain hands.
 */
class Scores {
    /*
     * Test whether the hand is a straight.
     */
    public static boolean isStraight(int[] array) {
        for (int i = 0; i < 5; i++) {
            array[i] = array[i] % 13;
        }
        Arrays.sort(array);
        
        for (int i = 1; i < 4; i++) {
            if (array[i] - array[i-1] != 1) {
                return false;
            }
        }
        if (array[4] - array[3] == 1 || array[4] - array[0] == 12) {
            return true;
        }

        return false;
    }
    
    /*
     * Test whether the hand is a flush.
     */
    public static boolean isFlush(int[] array) {
        for (int i = 0; i < 4; i++) {
            if (array[0] >= i*13 && array[4] < (i+1)*13) {
                return true;
            }
        }
        return false;
    }
    
    /*
     * Test whether the hand has a 4 of a kind.
     */
    public static boolean hasFourofaKind(int[] array) {
        for (int i = 0; i < 5; i++) {
            array[i] = array[i] % 13;
        }
        Arrays.sort(array);
        
        if ((array[3] - array[0] == 0) || (array[4] - array[1] == 0)) {
            return true;
        }
        return false;
    }
    
    /*
     * Test whether the hand has a three of a kind.
     */
    public static boolean hasThreeofaKind(int[] array) {
        for (int i = 0; i < 5; i++) {
            array[i] = array[i] % 13;
        }
        Arrays.sort(array);
        
        if (array[2] - array[0] == 0 || array[3] - array[1] == 0 || array[4] - array[2] == 0) {
            return true;
        }
        return false;
    }
    
    /*
     * Count the number of pairs in the hand.
     */
    public static int countPairs(int[] array) {
        for (int i = 0; i < 5; i++) {
            array[i] = array[i] % 13;
        }
        Arrays.sort(array);
        
        int nr_pairs = 0;
        for (int i = 1; i < 5; i++) {
            if (array[i] - array[i-1] == 0 && (i == 1 || array[i-1] - array[i-2] != 0)) {
                nr_pairs += 1;
            }
        }
        
        return nr_pairs;
    }
}
