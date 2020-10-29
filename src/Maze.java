
import java.util.Random;
import java.util.Vector;

public class Maze 
{
	//-Finals----------------------------------------------------------------------------------------------------------
	private final String MAZE = new String(
								"|_|_|_|_|_|_| | \n"
							  + "|______ _   | |0\n"
						      + "|  _____| | | |1\n"
						      + "|  ___  | | | |2\n"
						      + "|_____| | |_  |3\n"
						      + "| ______|   | |4\n"
						      + "| | _   | | | |5\n"
						      + "| __|___|_|___|6\n"
						      + "| |_|_|_|_|_|_|7\n"
						      + "000000000111111 \n"
						      + "123456789012345 \n");
	//                           123456789012345678
	//							 000000000111111111
	
	private final int LINE_CHAR_OFFSET = MAZE.substring(0, MAZE.indexOf("\n")).length() + 1; // Add this to the position to move to next line
	private final static int RIGHT = 0;
	private final static int LEFT = 1;
	private final static int DOWN = 2;
	private final static int UP = 3;
	
	//-Attributes-----------------------------------------------------------------------------------------------------
	private int characterX;
	private int characterY;
	private int farmerX;
	private int farmerY;
	private Vector<int[]> traps;
	private String infoLog; // Stores the info for whether the player has hit a wall, terminated the game, etc.
	
	//-Constructor----------------------------------------------------------------------------------------------------
	public Maze()
	{
		Random rand = new Random();
		traps = new Vector<int[]>();
		
		// Spawn in a Random Position that is reasonably far away from the exits
		do
		{
			characterX = rand.nextInt(6) + 5;
			characterY = rand.nextInt(7) + 1;
		} while (getMazeSpotAtLocation(characterX, characterY) == '|');
		
		do
		{
			farmerX = rand.nextInt(6) + 5;
			farmerY = rand.nextInt(7) + 1;
		} while (getMazeSpotAtLocation(farmerX, farmerY) == '|' && (farmerX == characterX && farmerY == characterY));
	}

	//-Get the character at a position in the maze--------------------------------------------------------------------
	private char getMazeSpotAtLocation(int LocX, int LocY)
	{
		if ((LocX - 1) + (LINE_CHAR_OFFSET * LocY - 1) <= MAZE.length())
			return MAZE.charAt((LocX) + (LINE_CHAR_OFFSET * LocY)); 
		
		return '_';
	}
	
	//-Check if a move is possible------------------------------------------------------------------------------------
	private boolean isMovePossible(int currentX, int currentY, int dir)
	{
		char wallD = getMazeSpotAtLocation(currentX, currentY);
		char wallD2 = getMazeSpotAtLocation(currentX, currentY + 1);
		char wallU = getMazeSpotAtLocation(currentX, currentY - 1);
		char wallR = getMazeSpotAtLocation(currentX + 1, currentY);
		char wallL = getMazeSpotAtLocation(currentX - 1, currentY);
		if (wallD == '_' && dir == DOWN)
		{
			infoLog = "You hit a wall . . . ";
			return false;
		}
		if (wallU == '_' && dir == UP)
		{
			infoLog = "You hit a wall . . . ";
			return false;
		}
		if (wallU == '|' && dir == UP)
		{
			infoLog = "You hit a wall . . . ";
			return false;
		}
		if (wallD2 == '|' && dir == DOWN)
		{
			infoLog = "You hit a wall . . . ";
			return false;
		}
		if (wallR == '|' && dir == RIGHT)
		{
			infoLog = "You hit a wall . . . ";
			return false;
		}
		if (wallL == '|' && dir == LEFT)
		{
			infoLog = "You hit a wall . . . ";
			return false;
		}
		if (dir != DOWN && dir != UP && dir != RIGHT && dir != LEFT)
		{
			infoLog = "You hit a wall . . . ";
			return false;
		}
		infoLog = "";
		return true;
	}
	
	//-Move-----------------------------------------------------------------------------------------------------------
	public boolean move(int dir)
	{
		if (isMovePossible(characterX, characterY, dir))
		{
			if (dir == RIGHT)
			{
				characterX++;
				return true;
			}
			else if (dir == LEFT)
			{
				characterX--;
				return true;
			}
			else if (dir == UP)
			{
				characterY--;
				return true;
			}
			else if (dir == DOWN)
			{
				characterY++;
				return true;
			}
		}
		return false;
	}
	
	//-Move the farmer------------------------------------------------------------------------------------------------
	public boolean moveF(int dir) // For the farmer
	{
		if (isMovePossible(farmerX, farmerY, dir))
		{
			if (dir == RIGHT)
			{
				farmerX++;
				return true;
			}
			else if (dir == LEFT)
			{
				farmerX--;
				return true;
			}
			else if (dir == UP && farmerY != -1)
			{
				farmerY--;
				return true;
			}
			else if (dir == DOWN && farmerY != 7)
			{
				farmerY++;
				return true;
			}
		}
		return false;
	}
	
	//-Replace a character in the string ----------------------------------------------------------------------------
	static String replaceChar(String old, int x, String newChar)
	{
		return (old.substring(0, x) + newChar) + old.substring(x+1);
	}
	
	//-Take a "Snapshot" of the maze from above----------------------------------------------------------------------
	public String getSnapshot()
	{
		String Snapshot = new String(MAZE);
		Snapshot = replaceChar(Snapshot, characterX + (LINE_CHAR_OFFSET * characterY), "X");
		Snapshot = replaceChar(Snapshot, farmerX + (LINE_CHAR_OFFSET * farmerY), "Y");
		for (int i = 0; i < traps.size(); i++)
		{
			Snapshot = replaceChar(Snapshot, traps.elementAt(i)[0] + (LINE_CHAR_OFFSET * traps.elementAt(i)[0]), "O");
		}
		return Snapshot;
	}
	
	//-Get the character position-------------------------------------------------------------------------------------
	public String getCPos()
	{
		return "(" + (characterX - 1) + ", " + (characterY - 1) + ")";
	}
	
	//-Get the direction from a string--------------------------------------------------------------------------------
	public static int getDir(String dir)
	{
		if (dir.toLowerCase().contains("r"))
			return RIGHT;
		else if (dir.toLowerCase().contains("l"))
			return LEFT;
		else if (dir.toLowerCase().contains("u"))
			return UP;
		else if (dir.toLowerCase().contains("d"))
			return DOWN;
		else
			return -1;
	}
	
	//-Check if the game is over--------------------------------------------------------------------------------------
	public boolean isDone()
	{
		// If the character escapes, return true
		if (characterY - 1 == -1)
		{
			infoLog = "You can feel air rush toward you as you escape the maze. Through pure instinct, you run into your house and call the cops . . . you have won.";
			return true;
		}
		if (characterY - 1 == 7)
		{
			infoLog = "You can feel air rush toward you as you escape the maze. Through pure instinct, you run foreward, only to realize that you got out of the wrong end of the maze . . . you will have to try again, but you need a break first.";
			return true;
		}
		
		// If the character is on a trap, return true
		for (int i = 0; i < traps.size(); i++)
		{
			if (characterX == traps.elementAt(i)[0] && characterY == traps.elementAt(i)[1])
			{
				infoLog = "Oof. You have fallen into a trap, and an alarm is blaring. You can hear the footsteps of your enemy rushing toward you.";
				return true;
			}
		}
		
		// If the character has met the farmer, return true
		if (characterX == farmerX && characterY == farmerY)
		{
			infoLog = "Oof. You can hear the farmer's breath right in front of you.";
			return true;
		}
		
		return false;
	}
	
	//-Getter for the infoLog-----------------------------------------------------------------------------------------
	public String getInfoLog()
	{
		return infoLog;
	}
	
	//-Setter for the infoLog-----------------------------------------------------------------------------------------
	public void setInfoLog(String val)
	{
		infoLog = val;
	}
	
	//-Update the game------------------------------------------------------------------------------------------------
	public void Update()
	{
		Random rand = new Random();
		// Drop a trap with probability 0.01:
		if (rand.nextInt(100) == 1)
		{
			int[] loc = {farmerX, farmerY};
			traps.addElement(loc);
		}
			
		// Move the farmer:
		int dir;
		
		do 
		{
			dir = rand.nextInt(4);
		} while (!isMovePossible(farmerX, farmerY, dir));
		
		moveF(dir);
	}
	
	//-Getter for MAZE---------------------------------------------------------------------------------------------------------
	public String getMAZE()
	{
		return MAZE;
	}
	
	//-Method for getting a String with all the data--------------------------------------------------------------------------
	public String getDataString(String UserName, long time)
	{
		String dataStr = "TMED------------------------------------------------------------\n";
		dataStr += UserName + "\n";
		dataStr += "CX: " + characterX + "\n";
		dataStr += "CY: " + characterY + "\n";
		dataStr += "FX: " + farmerX + "\n";
		dataStr += "FY: " + farmerY + "\n";
		for (int i = 0; i < traps.size(); i++)
		{
			dataStr += "TX: " + traps.elementAt(i)[0] + "\n";
			dataStr += "TY: " + traps.elementAt(i)[1] + "\n";
		}
		dataStr += "T : " + time + "\n";
		dataStr += "----------------------------------------------------------------\n";
		return dataStr;
	}
	
	//-Method for setting maze attributes using a data String (created above)-------------------------------------------------
	public void setDataStr(String dataStr)
	{
		int loc = dataStr.indexOf("CX");
		int loc2 = dataStr.indexOf("\n", loc + 1);
		String temp = dataStr.substring(loc+4, loc2);
		characterX = Integer.parseInt(temp);
		dataStr = dataStr.substring(loc2 + 2); // Cut off the useless part of the string
		
		loc = dataStr.indexOf("CY");
		loc2 = dataStr.indexOf("\n");
		temp = dataStr.substring(loc+4, loc2);
		characterY = Integer.parseInt(temp);
		dataStr = dataStr.substring(loc2 + 2); // Cut off the useless part of the string
		
		loc = dataStr.indexOf("FX");
		loc2 = dataStr.indexOf("\n");
		temp = dataStr.substring(loc+4, loc2);
		farmerX = Integer.parseInt(temp);
		dataStr = dataStr.substring(loc2 + 2); // Cut off the useless part of the string
		
		loc = dataStr.indexOf("FY");
		loc2 = dataStr.indexOf("\n");
		temp = dataStr.substring(loc+4, loc2);
		farmerY = Integer.parseInt(temp);
		dataStr = dataStr.substring(loc2 + 2); // Cut off the useless part of the string
		
		traps.clear();
		while (dataStr.contains("TX"))
		{
			loc = dataStr.indexOf("TX");
			loc2 = dataStr.indexOf("\n");
			temp = dataStr.substring(loc+4, loc2);
			int TX = Integer.parseInt(temp);
			dataStr = dataStr.substring(loc2 + 2); // Cut off the useless part of the string
			
			loc = dataStr.indexOf("TY");
			loc2 = dataStr.indexOf("\n");
			temp = dataStr.substring(loc+4, loc2);
			int TY = Integer.parseInt(temp);
			dataStr = dataStr.substring(loc2 + 2); // Cut off the useless part of the string
			
			int[] TXY = { TX, TY };
			traps.addElement(TXY);
		}
	}
	
}
