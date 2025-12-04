import java.util.*;
import swiftbot.Button;
import swiftbot.SwiftBotAPI;
import swiftbot.Underlight;
import java.util.Scanner;


// =========================
// MAIN CLASS
// =========================
public class main {

	public static SwiftBotAPI swiftBot = SwiftBotAPI.INSTANCE;

	public static void main(String[] args) throws InterruptedException {

		System.setProperty("file.encoding", "UTF-8");
		CLI ui = new CLI();

		boolean running = true;

		while (running) {
			int choice = ui.showMenu();

			switch (choice) {
			case 1: // Play
				swiftBot.fillUnderlights(Display.blank);

				GameLogic game = new GameLogic(swiftBot, ui);
				game.start();

				swiftBot.fillUnderlights(Display.blank);
				ui.finalScore(game.getLevel() - 1);
				break;

			case 2: // Scoreboard
				ui.scoreboardNotImplemented();
				Thread.sleep(1000);
				break;

			case 3: // Settings
				ui.settingsNotImplemented();
				Thread.sleep(1000);
				break;

			case 4: // Quit
				ui.goodbye();
				running = false;
				System.exit(5);
				break;

			default:
				ui.invalidOption();
				Thread.sleep(1000);
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

	public GameLogic(SwiftBotAPI bot, CLI cli) {
		this.swiftBot = bot;
		this.ui = cli;
	}

	public int getLevel() { return level; }

	public void start() throws InterruptedException {

		incomplete = false;

		while (play) {

			ui.showLevelandLives(level,lives);

			colours.add(random.nextInt(4));
			Display.showSequence(swiftBot, colours);

			for (int i = 0; i < level; i++) {

				int expected = colours.get(i);
				incomplete = true;

				long end = System.currentTimeMillis() + 10_000;

				swiftBot.enableButton(Button.A, () -> handleInput(0, expected));
				swiftBot.enableButton(Button.B, () -> handleInput(1, expected));
				swiftBot.enableButton(Button.X, () -> handleInput(2, expected));
				swiftBot.enableButton(Button.Y, () -> handleInput(3, expected));

				while (System.currentTimeMillis() < end && incomplete) {
					Thread.sleep(10);
				}

				swiftBot.disableAllButtons();

				if (incomplete && play) {
					ui.tooSlow();
					play = false;
				}
			}

			if (level%5==0 && level>0) {
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


	private void handleInput(int pressed, int expected) {
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
}



// =========================
// DISPLAY CLASS
// =========================
class Display {

	public static final int[] red = {255, 0, 0};
	public static final int[] green = {0, 255, 0};
	public static final int[] blue = {0, 0, 255};
	public static final int[] yellow = {255, 255, 0};
	public static final int[] blank = {0, 0, 0};

	public static void showSequence(SwiftBotAPI swiftBot, ArrayList<Integer> colours)
			throws InterruptedException {

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

		bot.setUnderlight(u, col);
		Thread.sleep(700);
		bot.setUnderlight(u, blank);
		Thread.sleep(200);
	}
}



// =========================
// CLI CLASS
// =========================
class CLI {

	private Scanner scanner = new Scanner(System.in);

	public int showMenu() {
		System.out.println("========================================");
		System.out.println("         ____  _                         ");
		System.out.println("        / ___|(_)_ __ ___   ___ _ __     ");
		System.out.println("        \\___ \\| | '_ ` _ \\ /   \\ '_ \\ ");
		System.out.println("         ___) | | | | | | |  |  || | |   ");
		System.out.println("        |____/|_|_| |_| |_|\\___/_| |_|   ");
		System.out.println();
		System.out.println("           S I M O N   S W I F T          ");
		System.out.println("========================================");
		System.out.println("              1) Play");
		System.out.println("              2) Scoreboard");
		System.out.println("              3) Settings");
		System.out.println("              4) Quit");
		System.out.println("========================================");
		System.out.print("Select an option: ");

		int choice = -1;
		if (scanner.hasNextInt()) {
			choice = scanner.nextInt();
			scanner.nextLine();
		}
		return choice;
	}

	public void showWelcome() {
		System.out.println("\n========================================");
		System.out.println("         SWIFTBOT SIMON SAYS            ");
		System.out.println("========================================");
		System.out.println("Press ENTER to begin...");
		scanner.nextLine();
	}

	public void showLevelandLives(int level, int lives) {
		System.out.println("\n========================================");
		System.out.println("    LEVEL " + level + "          LIVES: " + lives);
		System.out.println("========================================");
	}


	public void extralife(int currentlives) {
		System.out.println("\n========================================");
		System.out.println("EXTRA LIFE!");
		System.out.println("Lives remaining: " + currentlives);
		System.out.println("========================================");
	}

	public void correctRound() {
		System.out.println("Correct! Moving to next round...");
	}

	public void tooSlow() {
		System.out.println("Too slow! Game over.");
	}

	public void wrongButton(int remainingLives) {
		System.out.println("Wrong button! Lives remaining: " + remainingLives);
	}

	public boolean askContinue() {
		System.out.print("Continue? (y/n): ");
		String ans = scanner.nextLine();
		return ans.equalsIgnoreCase("y");
	}

	public void finalScore(int score) {
		System.out.println("\n========================================");
		System.out.println("               GAME OVER                ");
		System.out.println("           Final Score: " + score + "           ");
		System.out.println("========================================\n");
	}

	public void settingsNotImplemented() {
		System.out.println("Settings not implemented yet!");
	}

	public void scoreboardNotImplemented() {
		System.out.println("Scoreboard not implemented yet!");
	}

	public void goodbye() {
		System.out.println("Goodbye!");
	}

	public void invalidOption() {
		System.out.println("Invalid option! Try again.");
	}
}
