
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class Game 
{

	//-Private Attributes----------------------------------------------------------------------------------------------
	private Maze maze;
	private long sTime, eTime;
	private long timeFromSave = 0;
	private int winMethod; // Can -1, 0, or 1 depending on whether the person exited through the wrong end, exited through the right end, or befriended the farmer.
	private boolean isDone;
	
	//-Game Constructor-----------------------------------------------------------------------------------------------
	public Game()
	{
		maze = new Maze();
		sTime = System.currentTimeMillis();
		
		isDone = true;
	}
	
	//-Move the Character---------------------------------------------------------------------------------------------
	public void moveCharacter(String input)
	{
		maze.move(Maze.getDir(input));
	}
	
	//-Get an overhead view of the maze-------------------------------------------------------------------------------
	public String getSnapshot()
	{
		return maze.getSnapshot();
	}
	
	//-Get A Move-----------------------------------------------------------------------------------------------------
	public void getMove()
	{
		//System.out.println(getSnapshot());
		moveCharacter(JOptionPane.showInputDialog("Which way do you want to move?"));
	}
	
	//-Convert WASD to a direction------------------------------------------------------------------------------------
	private static String WASDtoULDR(String WASD)
	{
		if (WASD.toLowerCase().charAt(0) == 'w')
			return "UP";
		if (WASD.toLowerCase().charAt(0) == 's')
			return "DOWN";
		if (WASD.toLowerCase().charAt(0) == 'd')
			return "RIGHT";
		if (WASD.toLowerCase().charAt(0) == 'a')
			return "LEFT";
		
		return "[ERROR] (Method: WASDtoRLUD) :: Input not W, A, S, or D";
	}
	
	//-Get a move in WASD format--------------------------------------------------------------------------------------
	public String getWASDMove()
	{
		String input = showMazeDB(maze.getCPos());
		if (input != "")
		{
			String actualChar = WASDtoULDR(input);
			if (!actualChar.contains("[ERROR]"))
			{
				moveCharacter(actualChar);
				return "";
			}
			else 
				return input;
		}
		return input;
	}
	
	//-Get the Maze formated for a dialog box-------------------------------------------------------------------------
	public static String getDBMaze(String Maze, String addition)
	{
		String temp = Maze;
		temp = temp.replaceAll("  ", " &nbsp;");
		temp = temp.replaceAll("\n", "<br>");
		temp = "<HTML>" + temp;
		temp += addition;
		temp += "</HTML>";
		return temp;
	}

	//-Show the maze in a dialog box---------------------------------------------------------------------------------
	public String showMazeDB(String message)
	{
		JLabel Label = new JLabel(getDBMaze(maze.getMAZE(), "Please enter in a direction . . ."));
		Label.setFont(new Font("Courier New", Font.PLAIN, 14));
		
		return JOptionPane.showInputDialog( null, Label, "The Maze Escape", JOptionPane.PLAIN_MESSAGE );
	}
	
	//-Check if the game is over-------------------------------------------------------------------------------------
	public boolean isDone()
	{
		if (maze.isDone() && !isDone)
		{
			eTime = System.currentTimeMillis();
			isDone = true;
		}
		return maze.isDone();
	}
	
	//-Get the info that is currently stored about the game----------------------------------------------------------
	public String getInfoLog()
	{
		return maze.getInfoLog();
	}
	
	//-Refresh the maze's data---------------------------------------------------------------------------------------
	public void Refresh()
	{
		maze.setInfoLog("");
		maze.Update();
	}
	
	//-Put a string into a file without overriding it----------------------------------------------------------------
	public static void saveData(String filepath, String str) throws IOException
	{
		FileOutputStream outFile = new FileOutputStream(filepath, true);
		PrintStream outStream = new PrintStream(outFile);
		outStream.append(str);
		outStream.close();
		outFile.close();
	}
	
	//-Check if a save exists under a certain name-------------------------------------------------------------------
	public static boolean isSaveExisting(String name) throws FileNotFoundException
	{
		String dir = System.getProperty("user.dir") + "\\";
		Scanner scanF = new Scanner(new File(dir + "TMEDataFile.txt"));
		while (scanF.hasNextLine())
		{
			if (scanF.nextLine().equals(name))
			{
				scanF.close();
				return true;
			}
		}
		scanF.close();
		return false;
	}
	
	//-Save current data under a name--------------------------------------------------------------------------------
	public void save(String name) throws IOException
	{
		if (isSaveExisting(name))
			removeSave(name);
		String dir = System.getProperty("user.dir") + "\\";
		eTime = System.currentTimeMillis();
		saveData(dir + "TMEDataFile.txt", maze.getDataString(name, (eTime - sTime) + timeFromSave));
		eTime = 0;
	}
	
	//-Remove a Save-------------------------------------------------------------------------------------------------
	public static void removeSave(String name) throws FileNotFoundException
	{
		String file = "";
		String dir = System.getProperty("user.dir") + "\\";
		String temp;
		Scanner scanF = new Scanner(new File(dir + "TMEDataFile.txt"));
		while (scanF.hasNextLine())
		{
			temp = scanF.nextLine();
			if (temp.equals(name))
			{
				do
				{
					temp = scanF.nextLine();
				} while (!temp.contains("-"));
				if (scanF.hasNextLine())
				{
					temp = scanF.nextLine();
					temp = scanF.nextLine();
				}
			}
			file += temp + "\n";
		}
		scanF.close();
		PrintStream outStream = new PrintStream(dir + "TMEDataFile.txt");
		outStream.append(file);
		outStream.close();
	}
	
	//-Load From Saved Data-------------------------------------------------------------------------------------------
	public void loadFromSaved(String name) throws FileNotFoundException
	{
		String dir = System.getProperty("user.dir") + "\\";
		String oldtemp = "", temp = "", data = "";
		File f = new File(dir + "TMEDataFile.txt");
		Scanner scanF = new Scanner(f);
		while (!temp.equals(name) && scanF.hasNextLine())
		{
			oldtemp = temp;
			temp = scanF.nextLine();
		}
		data += oldtemp + "\n";
		data += temp + "\n";
		
		if (scanF.hasNextLine())
		{
			do
			{
				temp = scanF.nextLine();
				data += temp + "\n";
				if (temp.charAt(0) == 'T')
				{
					String Ntemp = temp.substring(4, temp.length());
					timeFromSave = Long.parseLong(Ntemp);
				}
			} while (!temp.contains("-"));
		}
		maze.setDataStr(data);
		removeSave(name);
		scanF.close();
	}
	
	//-Get the maze object--------------------------------------------------------------------------------------------
	public Maze getMazeObj()
	{
		return maze;
	}
	
	//-Set the win method--------------------------------------------------------------------------------------------
	public void setWinMethod()
	{
		String log = maze.getInfoLog();
		if (log.contains("wrong")) // Left through long method
			winMethod = -1;
		else if (log.contains("friend")) // Became farmer's friend
			winMethod = 1;
		else // Left through the right method
			winMethod = 0;
	}
	
	//-Get the time taken--------------------------------------------------------------------------------------------
	public long getTime()
	{
		if (eTime == 0)
			eTime = System.currentTimeMillis();
		return (eTime - sTime + timeFromSave) / 1000;
	}
	
	//-Get the score-------------------------------------------------------------------------------------------------
	public int getScore()
	{
		if (isDone())
		{
			if (getInfoLog().equals("L"))
				return 0;
			int diffInSecs = (int) getTime();
			double score = Math.sqrt(diffInSecs);
			score = 100000 / score;
			score -= 50 * winMethod;
			if (score < 0)
				score = 0;
			return (int) score;
		}
		else
		{
			return -1;
		}
	}
	
	// LEADERBOARD STUFF BELOW
	/* LAYOUT:
    Leaderboard
---------------------------------------------
|       Name       |   Score   |    Time    |
1.
2.
3.
4.
5.
6.
7.
8.
9.
10.

Previous leaderboard data is stored in the Leaderboard.txt file, in the following format:
N:
S:
T:
N:
S:
T:
N:
S:
T:
...
	*/
	
	//-Function for putting a string into another string at a certain location but overwrites the string--------------
	public static String replaceAtChars(String template, String charSequence, int location)
	{
		String New = template;
		for (int i = 0; i < charSequence.length(); i++)
			New = Maze.replaceChar(New, location + i, "" + charSequence.charAt(i));
	
		return New;
	}
	
	//-push a name, score, and time to the leaderboard----------------------------------------------------------------
	public static String pushToLeaderboard(String name, int score, long l) throws FileNotFoundException // The leaderboard data should always be in order
	{
		Vector<String> Names = new Vector<String>();
		Vector<Integer> S = new Vector<Integer>();
		Vector<Long> T = new Vector<Long>();
		
		String dir = System.getProperty("user.dir") + "\\";
		String temp;
		
		Scanner scanF = new Scanner(new File(dir + "Leaderboard.txt"));
		while (scanF.hasNextLine())
		{
			temp = scanF.nextLine();
			if (temp.charAt(0) == 'N')
			{
				Names.add(temp.substring(3));
			}
			else if (temp.charAt(0) == 'S')
			{
				S.add(Integer.parseInt(temp.substring(3)));
			}
			else if (temp.charAt(0) == 'T')
			{
				T.add(Long.parseLong(temp.substring(3)));
			}
		}
		scanF.close();
		
		int place = 1; // They get bumped down to their place.
		for (int i = 0; i < S.size(); i++)
		{
			if (score < S.elementAt(i))
				place++;
		}
		
		// If the place that they are at is less than or equal to ten, modify the Leaderboard:
		if (place <= 10)
		{
			Names.add(place - 1, name);
			S.add(place - 1, score);
			T.add(place - 1, l);
			
			// Put the leaderboard data into the file
			PrintStream outStream = new PrintStream(dir + "Leaderboard.txt");
			for (int i = 0; i < 10 && i < Names.size(); i++)
			{
				outStream.println("N: " + Names.elementAt(i));
				outStream.println("S: " + S.elementAt(i));
				outStream.println("T: " + T.elementAt(i));
			}
			outStream.close();
		}	
		final String template = "|------------------|-----------|------------|";
		String result = "<HTML>Leaderboard<br>" + 
			    "---------------------------------------------<br>" +
			    "|-------Name-------|---Score---|----Time----|<br>";
		int i = 0;
		while (i < 10 && i < Names.size())
		{
			result += replaceAtChars(replaceAtChars(replaceAtChars(template, Names.elementAt(i), 1), ""+S.elementAt(i), 20), ""+T.elementAt(i), 32) + "<br>";
			i++;
		}
		for (int j = i; j < 10; j++)
			result += "|------------------|-----------|------------|<br>";
			    
		result += "</HTML>";
		return result;
	}
	
	//-Get the leaderboard as a formatted String---------------------------------------------------------------------
	public static String getLeaderboardStr() throws FileNotFoundException // Testing
	{
		Vector<String> Names = new Vector<String>();
		Vector<Integer> S = new Vector<Integer>();
		Vector<Long> T = new Vector<Long>();
		
		String dir = System.getProperty("user.dir") + "\\";
		String temp;
		
		Scanner scanF = new Scanner(new File(dir + "Leaderboard.txt"));
		while (scanF.hasNextLine())
		{
			temp = scanF.nextLine();
			if (temp.charAt(0) == 'N')
			{
				Names.add(temp.substring(3));
			}
			else if (temp.charAt(0) == 'S')
			{
				S.add(Integer.parseInt(temp.substring(3)));
			}
			else if (temp.charAt(0) == 'T')
			{
				T.add(Long.parseLong(temp.substring(3)));
			}
		}
		scanF.close();
		
		final String template = "|------------------|-----------|------------|";
	    String result = "<HTML>Leaderboard<br>" + 
	    "---------------------------------------------<br>" +
	    "|-------Name-------|---Score---|----Time----|<br>";
	    int i = 0;
	    while (i < 10 && i < Names.size())
	    {
	    	result += replaceAtChars(replaceAtChars(replaceAtChars(template, Names.elementAt(i), 1), ""+S.elementAt(i), 20), ""+T.elementAt(i), 32) + "<br>";
	    	i++;
	    }
	    for (int j = i; j < 10; j++)
	    	result += "|------------------|-----------|------------|<br>";
	    
	    result+="</HTML>";
	    
	    return result;
	}
	
}
