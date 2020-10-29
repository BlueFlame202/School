// OPEN THE BELOW COMMENT TO SEE MY NOTES ON THE PROJECT

/*
_____           _ _  _
  |  |  |___   | | ||_|___ ___   ___ ___ ___  _   _  ___
  |  |__||_	   | | || | _| |_    |_  |__ |   |_| |_| |_
  |  |  ||__   | | || ||__ |__   |__  __||__ | | |   |__
by Aathreya Kadambi

Notes:
Decisions incorporated throughout the project
Mathematical Result: Calculating the Score -- Method in Game.java
String comparison method: Also throughout the code -- Especially in Game.java and Maze.java
Random generator: Random starting position and random CPU moves and randomized trap dropper -- Maze.java

Endings:
1. Quit
2. Save and Quit
3. Meet the farmer and lose
4. Meet the farmer and win
5. Win through the right exit
6. Win through the wrong exit
7. Step on a trap and meet the farmer -> Lose
8. Step on a trap and meet the farmer -> Win
 */


import java.awt.Font;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class GameTester 
{
	private final static String VERSION = "";
	
	// Contains the actual story.
	public static void main(String[] args) throws InterruptedException, HeadlessException, IOException 
	{
		// Create a new Game object
		Game game = new Game();
		
		// Input directions
		JOptionPane.showMessageDialog(null, "NOTE: In the following game, use WASD+ENTER to \nmove and 'q' to quit. If you want to save,\nuse 'p' and if you want to do both, do \"pq\".");
		
		// Display the title
		JOptionPane.showMessageDialog(null, "<HTML><h1>THE MAZE ESCAPE</h1></HTML>", "The Maze Escape", 1);
		
		// Load from a saved file
		int num = JOptionPane.showConfirmDialog(null, "Would you like to load a saved file?");
		if (num == 0)
		{
			String name = JOptionPane.showInputDialog("Please enter the name under which the file was saved . . .");
			game.loadFromSaved(name);
			JOptionPane.showMessageDialog(null, "The game was loaded!");
		}
		// Quit the game if cancel is clicked
		else if (num == 2)
			return;
		// Display the story
		else
		{
			JOptionPane.showMessageDialog(null, "You are a poor farmer who recently built\na maze in order to make some profit.", "The Maze Escape", 1);
			JOptionPane.showMessageDialog(null, "Just as you are about to test it, you get hit from behind.\nYou look up and see your neighbor, who is also poor.", "The Maze Escape", 1);
			JOptionPane.showMessageDialog(null, "[FARMER]: HA HA HA! I like to have fun, so I'm going to\n put you in your maze. If you make it out alive, I'll let you keep your maze . . . ", "The Maze Escape", 1);
			JOptionPane.showMessageDialog(null, "[YOU]: WAIT WHAT?!", "The Maze Escape", 1);
			TimeUnit.SECONDS.sleep(2); // Character got knocked out
			JOptionPane.showMessageDialog(null, "You have just woken up in the middle of your maze.\n You rub your eyes, but it seems that\nyour neighbor has blinded you. You\nneed to get out of the maze . . .\n\nAt least you remember the layout of your\nmaze . . .\n\nIf only you had been your neighbor's friend, maybe he \nwouldn't resort to such methods . . .", "The Maze Escape", 1);
		}
		
		// Game loop
		while(!game.isDone())
		{
			// Get the move
			String input = game.getWASDMove();
			if (!input.equals("")) // If WASD was not entered
			{
				if (input.length() > 1) // Check to make sure the string is more than one character to avoid a runtime error
				{
					if ((input.toLowerCase().charAt(0) == 'q' && input.toLowerCase().charAt(1) == 'p') ||
							(input.toLowerCase().charAt(0) == 'p' && input.toLowerCase().charAt(1) == 'q'))
						// Save and Quit if p and q is entered
					{
						int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to save and quit?");
						if (result == 0)
						{
							game.save(JOptionPane.showInputDialog("What is the name you would like to save it under?"));;
							return;
						}
					}
				}
				else if (input.toLowerCase().charAt(0) == 'q') // Quit if q is entered
				{
					int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?");
					if (result == 0)
						return;
				}
				else if (input.toLowerCase().charAt(0) == 'p') // Save if p is entered
				{
					int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to save?");
					if (result == 0)
						game.save(JOptionPane.showInputDialog("What is the name you would like to save it under?"));;
				}
			}
			
			if (game.getInfoLog() != "" && game.getInfoLog() != " ") // Make sure something is in the infoLog
				JOptionPane.showMessageDialog(null, game.getInfoLog());
			
			// Refresh the game's data
			game.Refresh();
		}
		
		// Game is over so show the info about how the person did.
		JOptionPane.showMessageDialog(null, game.getInfoLog());
		if (game.getInfoLog().contains("Oof")) // If they have oof in their info log, that means they did not win and ran into the farmer
		{
			String Lwords = JOptionPane.showInputDialog(null, "[FARMER] What are your last words?").toLowerCase();
			if (Lwords.contains("friend") && !Lwords.contains("not")) // If you want to become the farmers friend
			{
				JOptionPane.showMessageDialog(null, "[FARMER] (Weeping) Really? You would do that? Thanks so much! Let's be friends forever.");
				JOptionPane.showMessageDialog(null, "You have won and made a friend.");
				game.getMazeObj().setInfoLog("friend");
				game.setWinMethod();
			}
			else // Otherwise you lose
			{
				game.getMazeObj().setInfoLog("L");
				JOptionPane.showMessageDialog(null, "You Lost . . . ");
				return; // End the game (you lost)
			}
		}

		// Report the time and score if you won.
		JOptionPane.showMessageDialog(null, "Your time is " + game.getTime() + " seconds. \nYour score is " + game.getScore() + ".");
		
		JLabel Label;
		
		if (JOptionPane.showInputDialog("Would you like to try to be on the leaderboard?").toLowerCase().charAt(0) == 'y')
		{
			String L = Game.pushToLeaderboard(JOptionPane.showInputDialog("What is you name?") + VERSION, game.getScore(), game.getTime());
			Label = new JLabel(L);
		}
		else
		{
			Label = new JLabel(Game.getLeaderboardStr());
		}
		
		Label.setFont(new Font("Courier New", Font.PLAIN, 14));
		JOptionPane.showMessageDialog(null, Label, "Leaderboard", 1);
	}

}

/*
sg
d

IF YOU WERE SPAMMING, YOUR STUFF WILL OVERFLOW INTO HERE WHEN THE GAME IS DONE :)


*/