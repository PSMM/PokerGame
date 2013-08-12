/*
 * SinglePlayerPoker.java
 *
 * Written by: Pascal Mettes.
 *
 * This file contains the main frame of the single-player poker game. Try to
 * create poker hands for the 5 horizontal, 5 vertical, and 2 diagonal lines.
 * For more info on the game, press help in the menu bar during the game.
 *
 * To compile and run, simple type 'javac *.java' followed by
 * 'java SinglePlayerPoker'.
 */

import java.util.Scanner;
 
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

import java.io.File;

/*
 * This class represents the main window of the Poker game.
 * In here, the main board is initialized and the menubar is created.
 *
 * This class should be called directly in order to function, since it
 * contains the main function.
 */
public class SinglePlayerPoker extends JFrame {
    /* The width and height of the GUI-frame. */
    private final static int width = 800;
    private final static int height = 800;
    
    /* Initialize the game panel, which in turn initializes the game itself. */
    public GamePanel panel = new GamePanel();

    /*
     * Create the panel and menubar, set action listeners, and add everything
     * to the main frame.
     *
     * Input : -
     * Output: -
     */
    public SinglePlayerPoker() {
        /* Create a menu bar. */
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        /* Add menus to the menu bar. */
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        /* Add menu items to menus. */
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem statsItem = new JMenuItem("Game statistics");
        JMenuItem resetItem = new JMenuItem("Reset statistics");
        JMenuItem helpItem = new JMenuItem("Rules of the game");
        fileMenu.add(newGameItem);
        fileMenu.addSeparator();
        fileMenu.add(statsItem);
        fileMenu.add(resetItem);
        helpMenu.add(helpItem);
        
        /*
         * Assign callback on new game.
         */
        newGameItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                panel.game.reset();
            }
        });
        
        /*
         * Assign callback on statistics.
         */
        statsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int won = 0;
                int total = 0;
                int average = 0;
                int best = 0;
                double ratio = 0;
                
                /* Compute the main statistics. */
                try {
                    Scanner scanner = new Scanner(new File("stats.txt"));
                    while (scanner.hasNextLine()) {
                        int v = Integer.parseInt(scanner.nextLine());
                        if (v >= 75) {
                            won += 1;
                        }
                        if (v > best) {
                            best = v;
                        }
                        average += v;
                        total += 1;
                    }
                    if (total == 0) {
                        average = 0;
                        ratio = 0;
                    }
                    else {
                        average /= total;
                        ratio = won / ((double)total);
                    }
                    scanner.close();
                    
                }
                catch (Exception e) {
                }
                /* Display a dialog with information. */
                JOptionPane.showMessageDialog(null,
                "Number of games played: " + total + "\n" +
                "Win ratio: " + ratio + "\n" +
                "Best score: " + best + "\n" +
                "Average score: " + average + "\n",
                "Game statistics", JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        /*
         * Assign callback on reset statistics.
         */
        resetItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    File file = new File("stats.txt");
                    file.delete();
                }
                catch (Exception exc) {
                
                }
            }
        });
        
        /*
         * Assign callback on help.
         */
        helpItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                /* Display a dialog with information. */
                JOptionPane.showMessageDialog(null, "Game rules:\n\n" +
                "Try to fill the 5 by 5 grid with poker hands.\n" +
                "Point can be achieved for each of the 5 horizontal,\n" +
                "5 vertical, and 2 diagonal lines.\n\n" +
                "Score sheet:\n" +
                "Straight flush: 30\n" +
                "Four of a Kind: 16\n" +
                "Straight: 12\n" +
                "Full house: 10\n" +
                "Three of a Kind: 6\n" +
                "Flush: 5\n" +
                "Two pair: 3\n" +
                "One pair: 1\n",
                "Game information", JOptionPane.PLAIN_MESSAGE);
            }
        });
        
        /* Add the panel to the frame. */
        add(panel);
        /* Add the statusbar (initialized in the panel) to the frame. */
        add(panel.status, BorderLayout.SOUTH);
    }
    
    /*
     * Main function. In here, the frame is created.
     *
     * Input : -
     * Output: -
     */
    public static void main(String[] args) {
        /* Initialize a customized frame. */
        JFrame frame = new SinglePlayerPoker();
        frame.setTitle("Single-player Poker Game");
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(false);
    }
}
