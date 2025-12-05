import java.util.*;
import java.io.*;
import swiftbot.Button;
import swiftbot.SwiftBotAPI;
import swiftbot.Underlight;
import java.util.Scanner;


// =========================
// MAIN CLASS
// =========================
public class main {

	public static SwiftBotAPI swiftBot = SwiftBotAPI.INSTANCE; // Creating Instance
	public static Scoreboard scoreboard = new Scoreboard(); // Creating Object

	public static void main(String[] args) throws InterruptedException {

		System.setProperty("file.encoding", "UTF-8");
		CLI ui = new CLI(); // Intialising CLI Object

		boolean running = true;
		int score;

		while (running) { // Main Program Loop
			int choice = ui.showMenu();

			switch (choice) {
			case 1: // Play
				swiftBot.fillUnderlights(Display.blank);

				GameLogic game = new GameLogic(swiftBot, ui);
				game.start();

				swiftBot.fillUnderlights(Display.blank);
				score = game.getLevel() - 1;
				game.celebrationDive(score);
				ui.finalScore(score);
				System.out.print("Enter your name: ");
				Scanner sc = new Scanner(System.in);
				String name = sc.nextLine();

				main.scoreboard.saveScore(name, game.getLevel() - 1);
				ui.askContinue();
				Utility.clearConsole();
				break;

			case 2: // Scoreboard
				Utility.clearConsole();
				ui.showTitle();
			    main.scoreboard.show();
				Thread.sleep(1000);
				ui.askContinue();
				Utility.clearConsole();
				break;

			case 3: // Settings
				Utility.clearConsole();
				ui.showTitle();
				ui.settingsNotImplemented();
				Thread.sleep(1000);
				ui.askContinue();
				Utility.clearConsole();
				break;

			case 4: // Quit
				ui.goodbye();
				running = false;
				System.exit(5);
				break;

			default:
				ui.invalidOption();
				Thread.sleep(1000);
				Utility.clearConsole();
			}
		}
	}
}



// =========================
// GAME LOGIC CLASS
// =========================
class GameLogic {

	private SwiftBotAPI swiftBot;
	private CLI ui;

	private ArrayList<Integer> colours = new ArrayList<>();
	private Random random = new Random();
	private int lives = 3;

	private int level = 1;
	private volatile boolean play = true;
	private volatile boolean incomplete;

	// Constructor
	public GameLogic(SwiftBotAPI bot, CLI cli) {
		this.swiftBot = bot;
		this.ui = cli;
	}

	public int getLevel() { return level; }

	public void start() throws InterruptedException { // Starts the game loop

		incomplete = false;

		while (play) { // Main Game Loop
			
			Utility.clearConsole();
			ui.showTitle();
			ui.showLevelandLives(level,lives);

			colours.add(random.nextInt(4));
			Display.showSequence(swiftBot, colours);

			for (int i = 0; i < level; i++) {

				int expected = colours.get(i);
				incomplete = true;

				long end = System.currentTimeMillis() + 10_000;

				// Enable Buttons (Linked Actions)
				swiftBot.enableButton(Button.A, () -> handleInput(0, expected));
				swiftBot.enableButton(Button.B, () -> handleInput(1, expected));
				swiftBot.enableButton(Button.X, () -> handleInput(2, expected));
				swiftBot.enableButton(Button.Y, () -> handleInput(3, expected));

				// Timer for Input
				while (System.currentTimeMillis() < end && incomplete) {
					Thread.sleep(10);
				}

				swiftBot.disableAllButtons();

				if (incomplete && play) {
					ui.tooSlow();
					play = false;
				}
			}

			if (level%5==0 && level>0) { // Eve
				if (!ui.askContinue()) {
					play = false;
				}
			}

			if (play) {
				level++;
				ui.correctRound();
				Thread.sleep(1000);
			}

		}
		if (play) {
			level++;
			System.out.println("Correct! Next Round");
			Thread.sleep(1000);
		}


		if (level % 5 == 0) {
			lives++;
			ui.extralife(lives);
		}
	}


	private void handleInput(int pressed, int expected) { //Input Handler : Wrong,Correct, Inform, End game,
		if (!incomplete) return;

		if (pressed == expected) {
			incomplete = false;
		} else {
			lives--;
			ui.wrongButton(lives);

			if (lives <= 0) {
				play = false;
			}
			incomplete = false;
		}
	}

	public void celebrationDive(int score) throws InterruptedException {
		int speed;
		if (score < 5) {
			speed = 40;
		} else if (score >= 10) {
			speed = 100;
		} else {
			speed = score * 10;
		}

		// 30cm untested
		int moveTime = (int)(30.0 / speed * 1000);

		int[][] colors = { Display.red, Display.green, Display.blue, Display.yellow };
		Random r = new Random();

		for (int i = 0; i < 4; i++) {
			int[] c = colors[r.nextInt(4)];
			swiftBot.fillUnderlights(c);
			Thread.sleep(300);
			swiftBot.fillUnderlights(Display.blank);
			Thread.sleep(200);
		}

		// Celebration V
		swiftBot.move(speed, speed / 2, moveTime);
		Thread.sleep(200);

		swiftBot.move(speed / 2, speed, moveTime);
		Thread.sleep(200);

		swiftBot.stopMove();

		for (int i = 0; i < 4; i++) {
			int[] c = colors[r.nextInt(4)];
			swiftBot.fillUnderlights(c);
			Thread.sleep(300);
			swiftBot.fillUnderlights(blank);
			Thread.sleep(200);
		}

		swiftBot.fillUnderlights(blank);
	}

}



// =========================
// DISPLAY CLASS
// =========================
class Display {
	
	//Predefined RGB color arrays for each colour

	public static final int[] red = {255, 0, 0};
	public static final int[] green = {0, 255, 0};
	public static final int[] blue = {0, 0, 255};
	public static final int[] yellow = {255, 255, 0};
	public static final int[] blank = {0, 0, 0};

	public static void showSequence(SwiftBotAPI swiftBot, ArrayList<Integer> colours)
			throws InterruptedException {

		// Loops through each colour within the sequence
		for (int c : colours) {
			switch (c) {
			case 0:
				swiftBot.setButtonLight(Button.A, true);
				light(swiftBot, Underlight.MIDDLE_LEFT, red);
				swiftBot.setButtonLight(Button.A, false);
				break;

			case 1:
				swiftBot.setButtonLight(Button.B, true);
				light(swiftBot, Underlight.BACK_LEFT, blue);
				swiftBot.setButtonLight(Button.B, false);
				break;

			case 2:
				swiftBot.setButtonLight(Button.X, true);
				light(swiftBot, Underlight.MIDDLE_RIGHT, green);
				swiftBot.setButtonLight(Button.X, false);
				break;

			case 3:
				swiftBot.setButtonLight(Button.Y, true);
				light(swiftBot, Underlight.BACK_RIGHT, yellow);
				swiftBot.setButtonLight(Button.Y, false);
				break;
			}
		}
	}

	private static void light(SwiftBotAPI bot, Underlight u, int[] col)
			throws InterruptedException {

		bot.setUnderlight(u, col); // Lights up in 700ms
		Thread.sleep(700);
		bot.setUnderlight(u, blank);// Lights off in 200ms
		Thread.sleep(200);
	}
}



// =========================
// CLI CLASS
// =========================
class CLI {

	private Scanner scanner = new Scanner(System.in);

	public int showMenu() {
	    this.showTitle();
	    System.out.println("              1) Play");
	    System.out.println("              2) Scoreboard");
	    System.out.println("              3) Settings");
	    System.out.println("              4) Quit");
	    System.out.println("========================================");
	    System.out.print("Select an option: ");

	    int choice = -1;
	    if (scanner.hasNextInt()) {
	        choice = scanner.nextInt();
	        scanner.nextLine(); // consume the newline
	    } else {
	        scanner.nextLine(); // **consume invalid input**
	    }
	    return choice;
	}

	
	public void showTitle() { // Title
		System.out.println("========================================");
		System.out.println("         ____  _                         ");
		System.out.println("        / ___|(_)_ __ ___   ___ _ __     ");
		System.out.println("        \\___ \\| | '_ ` _ \\ /   \\ '_ \\ ");
		System.out.println("         ___) | | | | | | |  |  || | |   ");
		System.out.println("        |____/|_|_| |_| |_|\\___/_| |_|   ");
		System.out.println();
		System.out.println("           S I M O N   S W I F T          ");
		System.out.println("========================================");
	}

	public void showWelcome() { // Start Of Game
		System.out.println("\n========================================");
		System.out.println("         SWIFTBOT SIMON SAYS            ");
		System.out.println("========================================");
		System.out.println("Press ENTER to begin...");
		scanner.nextLine();
	}

	public void showLevelandLives(int level, int lives) { // Displays Level & Lives
		System.out.println("\n========================================");
		System.out.println("    LEVEL " + level + "          LIVES: " + lives);
		System.out.println("========================================");
	}


	public void extralife(int currentlives) { //Bonus Life with current lives
		System.out.println("\n========================================");
		System.out.println("EXTRA LIFE!");
		System.out.println("Lives remaining: " + currentlives);
		System.out.println("========================================");
	}

	public void correctRound() { // Correct
		System.out.println("Correct! Moving to next round..."); 
	}

	public void tooSlow() { // Too slow
		System.out.println("Too slow! Game over."); 
	}

	public void wrongButton(int remainingLives) { // Wrong with remaining lives
		System.out.println("Wrong button! Lives remaining: " + remainingLives); 
	}

	public boolean askContinue() { // Continuation
		System.out.print("Would You Like To Continue? (Y/N): "); 
		String ans = scanner.nextLine();
		return ans.equalsIgnoreCase("y");
	}

	public void finalScore(int score) { // Game over
		System.out.println("\n========================================");
		System.out.println("               GAME OVER                ");
		System.out.println("           Final Score: " + score + "           ");
		System.out.println("========================================\n");
	}

	public void settingsNotImplemented() {
		System.out.println("Settings not implemented yet!");
	}

	public void goodbye() { // End Screen
		System.out.println("Goodbye!");
	}

	public void invalidOption() { // Invalid Option
		System.out.println("Invalid option! Try again.");
	}
}
	
// =========================
// SCOREBOARD CLASS
// =========================
class Scoreboard {

    private static final String FILE_NAME = "scoreboard.txt"; // File where scores are saved

    // Saves a new score
    public void saveScore(String name, int score) { // Uses parameters such as name and score
        try (FileWriter fw = new FileWriter(FILE_NAME, true)) { // Appending 
            fw.write(name + " - " + score + "\n"); // Stores in Name - Score format
        } catch (IOException e) {
            e.printStackTrace(); // Error Handler
        }
    }

    
    // Displays the scoreboard
    public void show() {
    	
        System.out.println("\n================== SCOREBOARD ==================");

        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No scores yet!"); // If file doesnt exist
            return;
        }

        // Read all scores into a list
        ArrayList<PlayerScore> scores = new ArrayList<>();
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] parts = line.split(" - "); // Reads the name and score which is split by the -
                if (parts.length == 2) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1].trim());
                    scores.add(new PlayerScore(name, score));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Sort scores descending
        scores.sort((a, b) -> b.score - a.score);

        // Print table header
        System.out.printf("%-4s | %-20s | %s%n", "Rank", "Player Name", "Score"); //Uses format string - (Left aligned), Field Size, d = integer s = string and n = new line character
        System.out.println("----------+---------------------------+------------");

        // Print top scores
        int rank = 1;
        for (PlayerScore ps : scores) {
            System.out.printf("%-4d | %-20s | %d%n", rank, ps.name, ps.score);
            rank++;
        }

        System.out.println("==================================\n");
    }

    // Helper class for storing player and score
    private static class PlayerScore {
        String name;
        int score;

        PlayerScore(String name, int score) {
            this.name = name;
            this.score = score;
        }
    }

    
}

//=========================
//SETTINGS CLASS
//=========================

class Settings {
	// WIP - Work In Progress
}


//=========================
//UTILITY CLASS
//=========================

class Utility {
	
	public static void clearConsole() { // One of many ways to clear console
	    System.out.print("\033[H\033[2J"); // ANSI Escape Code - Moves cursor to the top left and clears the screen
	    System.out.flush(); // Imediate Output effect
	}
}
	

