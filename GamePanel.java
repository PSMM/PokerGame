/*
 * GamePanel.java
 *
 * Written by: Pascal Mettes.
 *
 * This file contains the main panel of the poker game, which means that the
 * drawing, mouse events, and game update class are done here.
 */

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import java.io.FileWriter;

/*
 * The primary panel of the poker game. This panel displays the poker cards
 * and game board. Also, it handles the mouse events and contains the game
 * itself in the form of an instance of the class PokerGame.
 */
public class GamePanel extends JPanel {
    /* Instance of the poker game itself. */
    public PokerGame game = new PokerGame();
    
    /* Add a status bar to show the score. */
    public StatusBar status = new StatusBar();
    
    /* Coordinates for the pile of new cards. */
    public final int originalX = 50;
    public final int originalY = 314;
    /* Additional coordinates to help drag new cards to the board. */
    public int diffX = 0;
    public int diffY = 0;
    public int mouseX = originalX;
    public int mouseY = originalY;
    
    /* Coordinates of the 5x5 grid. */
    public int cardboardX = 240;
    public int cardboardY = 28;
    public int offsetX = 100;
    public int offsetY = 140;
    
    /* Boolean to track the use of the mouse click. */
    public boolean pressed = false;

    /*
     * Create the board and set the mouse activities.
     *
     * Input : -
     * Output: -
     */
    public GamePanel() {
        /* Midly dark green as main board color. */
        setBackground(new Color(50,200,50));
        
        /*
         * Mouse listener ofr both mouse clicks and mouse movements.
         */
        addMouseListener(new MouseAdapter() {
            /*
             * Check whether the user attempts to drag a new card from the
             * pile to the board.
             */
            public void mousePressed(MouseEvent e) {
                if (pressed == false && game.state == "play") {
                    if (e.getX() > originalX && e.getX() < originalX +
                            game.cwidth && e.getY() > originalY && e.getY() <
                            originalY + game.cheight) {
                        diffX = e.getX() - originalX;
                        diffY = e.getY() - originalY; 
                        pressed = true; 
                    } 
                }
            }
            
            /*
             * Check whether the user has released a dragged card on a place on
             * the board grid.
             */
            public void mouseReleased(MouseEvent e) {
                int nx = mouseX;
                int ny = mouseY;

                /* Check whether mouse coordinates are on the grid. */
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        int px = cardboardX + i*offsetX;
                        int py = cardboardY + j*offsetY;
                        if (nx > px && nx < px + game.cwidth && ny > py &&
                                ny < py + game.cheight) {
                            /* Only place a new card on an empty spot. */
                            if (game.board[i][j] == -1) {
                                /* Place the card and check for scores. */
                                game.board[i][j] = game.deck.get(0);
                                game.deck.remove(0);
                                game.updateScore(i,j);
                                status.setText("Score: " + game.score);
                                
                                /* Check whether the board is full. */
                                if (game.nr_used == 25) {
                                    game.state = "end";
                                    /* Inform the user on the final score. */
                                    String endstring = "Final score: " + game.score;
                                    if (game.score >= 75) {
                                        endstring += "\nYour have WON!";
                                    }
                                    else {
                                        endstring += "\nYour have LOST!";
                                    }
                                    JOptionPane.showMessageDialog(null, endstring,
                                    "End of the Round", JOptionPane.PLAIN_MESSAGE);
                                    
                                    /* Write the score to file. */
                                    try {
                                        FileWriter writer = new FileWriter("stats.txt", true);
                                        writer.write("" + game.score + "\n");
                                        writer.close();
                                    }
                                    catch (Exception ex) {
                                    
                                    }
                                }
                            }
                        }
                    }
                }
                
                pressed = false;
                mouseX = originalX;
                mouseY = originalY;
                diffX = 0;
                diffY = 0;
                
                repaint();
            }
        });
        
        /*
         * Update the card position when it is dragged.
         */
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (pressed == true) {
                    mouseX = e.getX();
                    mouseY = e.getY(); 
                }
                repaint();
            }
        });
    }
    
    /*
     * Diplay the current state of the game.
     */
    protected void paintComponent(Graphics g) {
        /* Call parent method. */
        super.paintComponent(g);
        /* Set appropriate background color. */
        setBackground(new Color(50,200,50));
        
        /* Draw the board grid and the possible cards on that grid. */
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                g.drawRect(cardboardX-1+i*offsetX,cardboardY-1+j*offsetY,
                        game.cwidth+1, game.cheight+1);
                
                if (game.board[i][j] >= 0) {
                    g.drawImage(game.cards[game.board[i][j]], cardboardX+i*offsetX,
                            cardboardY+j*offsetY, game.cwidth, game.cheight, this);
                }
            }
        }
        
        /* Draw the pile where the new cards emerge. */
        g.drawRect(originalX-1,originalY-1, game.cwidth+1, game.cheight+1);
        if(game.state == "play") {
            g.drawImage(game.cards[game.deck.get(0)], mouseX - diffX, mouseY - diffY,
                    game.cwidth, game.cheight, this);
        }
    }
}

/*
 * This class represents a statusbar to be used for score updates.
 */
class StatusBar extends JLabel {

    /*
     * Creates a new instance of StatusBar.
     *
     * Input : -
     * Output: -
     */
    public StatusBar() {
        super();
        super.setPreferredSize(new Dimension(100, 16));
        setMessage("Score: 0");
    }

    /*
     * Place a string in the statusbar.
     *
     * Input : The message (String).
     * Output: -
     */
    public void setMessage(String message) {
        setText(message);        
    }        
}
