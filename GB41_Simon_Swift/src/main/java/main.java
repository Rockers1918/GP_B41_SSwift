import java.util.*;

import swiftbot.Button;
import swiftbot.SwiftBotAPI;
import swiftbot.Underlight;
public class main {
	static SwiftBotAPI swiftBot;
	// main resizable colours array
	static ArrayList<Integer> colours = new ArrayList<Integer>();
	static Random random = new Random();
	static int level = 1;
	static volatile boolean play = true;
	static volatile boolean incomplete;

	static int[] red = {255, 0, 0};
	static int[] green = {0, 255, 0};
	static int[] blue = {0, 0, 255};
	static int[] yellow = {255, 255, 0};	
	static int[] blank = {0, 0, 0};

	public static void main(String[] args) throws InterruptedException {
		swiftBot = SwiftBotAPI.INSTANCE;
		System.out.println("Simon Says - Get Ready!");
		swiftBot.fillUnderlights(blank);

		GameLogic();

		swiftBot.fillUnderlights(blank);
		System.out.println("Final Score"+(level-1));
	}


	public static void GameLogic() throws InterruptedException {
		incomplete = false;
		while (play) {
			DisplayLevel();
			colours.add(random.nextInt(4));
			DisplaySequence();
			swiftBot.fillUnderlights(blank);
			Scanner reader = new Scanner(System.in); // Reading from System.in
			for (int i=0; i<level; ++i) {
				if (!play) break;

				final int currentIndex = i;  // Fix lambda capture
				final int expectedColor = colours.get(currentIndex);
				incomplete = true;
				long endtime = System.currentTimeMillis()+10_000;
				// 1. ENABLE BUTTONS
				swiftBot.enableButton(Button.A, () -> handleInput(0, expectedColor));
				swiftBot.enableButton(Button.B, () -> handleInput(1, expectedColor));
				swiftBot.enableButton(Button.X, () -> handleInput(2, expectedColor));
				swiftBot.enableButton(Button.Y, () -> handleInput(3, expectedColor));

				// 2. waiting while time is left AND input is incomplete
				while (System.currentTimeMillis()<endtime && incomplete) {
					Thread.sleep(10);				
				}
				swiftBot.disableAllButtons();
				if (incomplete && play) {
					System.out.println("Too Slow!");
					play = false;
				}
			}

			//When sequence survived


			if (level%5==0 && level>0) {
				System.out.println("Would you like to continue? (y/n): ");
				String ans = reader.next();
				if (ans.equals("y")) {
					break;
				}
				else if (ans.equals("n")) {	
					System.exit(5);
				}
			}
			if (play) {
				level++;
				System.out.println("Correct! Next Round");
				Thread.sleep(1000);
			}

		}
		System.exit(5);
	}

	public static void handleInput(int pressedColor, int expectedColor) {
		if (!incomplete) return; // Ignore extra clicks if already processed

		if (pressedColor == expectedColor) {
			// Correct button
			incomplete = false; // This breaks the waiting loop
		} else {
			// Wrong button
			System.out.println("Wrong button!");
			play = false;
			incomplete = false; // Break the loop to end game
		}
	}

	public static void DisplaySequence() throws InterruptedException{
		for (int i = 0; i < colours.size(); ++i) {
			switch (colours.get(i)) {
			case 0:
				swiftBot.setButtonLight(Button.A, true);
				lightUp(Underlight.MIDDLE_LEFT, red);
				swiftBot.setButtonLight(Button.A, false);
				break;
			case 1:
				swiftBot.setButtonLight(Button.B, true);
				lightUp(Underlight.BACK_LEFT, blue);
				swiftBot.setButtonLight(Button.B, false);
				break;
			case 2:             
				swiftBot.setButtonLight(Button.X, true);
				lightUp(Underlight.MIDDLE_RIGHT, green);
				swiftBot.setButtonLight(Button.X, false);
				break;
			case 3:
				swiftBot.setButtonLight(Button.Y, true);
				lightUp(Underlight.BACK_RIGHT, yellow);
				swiftBot.setButtonLight(Button.Y, false);
				break;
			}
		}
	}

	public static void lightUp(Underlight u, int[] color) throws InterruptedException {
		swiftBot.setUnderlight(u, color);
		Thread.sleep(700); // Light on time
		swiftBot.setUnderlight(u, blank);
		Thread.sleep(200); // Gap between lights
	}

	public static void DisplayLevel() {
		System.out.println("Level "+level);
	}
	
	public static void celebrationDive(int score) throws InterruptedException { // input score
		int speedPercent;
		if (score < 5) speedPercent = 40;
		else if (score >= 10) speedPercent = 100;
		else speedPercent = score * 10;

		Underlight[] leds = {
			Underlight.FRONT_LEFT,
			Underlight.FRONT_RIGHT,
			Underlight.MIDDLE_LEFT,
			Underlight.MIDDLE_RIGHT,
		};

		blinkRandom(leds);

		int armLength = 30; // cm
		
		swiftBot.move(armLength, 30, speedPercent);  // right arm
		swiftBot.turn(45, speedPercent);
		swiftBot.move(armLength, 30, speedPercent);
		swiftBot.turn(-45, speedPercent);
		swiftBot.turn(-45, speedPercent);
		swiftBot.move(armLength, 30, speedPercent);  // left arm
		swiftBot.turn(45, speedPercent);
		swiftBot.move(armLength, 30, speedPercent);

		blinkRandom(leds);
	}

	public static void blinkRandom(Underlight[] leds) {
		List<int[]> colours = new ArrayList<>(Arrays.asList(yellow, red, green, blue));
		Collections.shuffle(colours); 

		for (int[] c: colours) {
			for (Underlight led: leds) {
				swiftBot.setUnderlight(led, c);
			}
			Thread.sleep(500);
		}

		swiftBot.fillUnderlights(blank);
	}
}




