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
	static boolean play = true;
	static boolean incomplete;
	static int i;

	static int[] red = {255, 0, 0};
	static int[] green = {0, 255, 0};
	static int[] blue = {0, 0, 255};
	static int[] yellow = {255, 255, 0};	
	static int[] blank = {0, 0, 0};

	public static void main(String[] args) throws InterruptedException {
		swiftBot = SwiftBotAPI.INSTANCE;
		System.out.println("Simon Says");
		swiftBot.fillUnderlights(blank);
		GameLogic();
		swiftBot.fillUnderlights(blank);
		System.out.println("Final Score"+level);
	}


	public static void GameLogic() throws InterruptedException {
		incomplete = false;
		while (play || !incomplete) {
			DisplayLevel();
			colours.add(random.nextInt(0,4));
			DisplaySequence();
			swiftBot.fillUnderlights(blank);

			for (i=0; i<level;++i) {
				try {
			        final int currentIndex = i;  // Fix lambda capture
					incomplete = true;
					play = false;
					long endtime = System.currentTimeMillis()+10_000;
					swiftBot.enableButton(Button.A,() -> {
						if (colours.get(currentIndex)==0) {
							incomplete = false;
							++level;
						}
						else {
							play = false;
						}
					});
					swiftBot.enableButton(Button.B,() -> {
						if (colours.get(currentIndex)==1) {
							incomplete = false;
							++level;
						}
						else {
							play = false;
						}
					});
					swiftBot.enableButton(Button.X,() -> {
						if (colours.get(currentIndex)==2) {
							incomplete = false;
							++level;
						}
						else {
							play = false;
						}
					});
					swiftBot.enableButton(Button.Y,() -> {
						if (colours.get(currentIndex)==3) {
							incomplete = false;
							++level;
						}
						else {
							play = false;
						}
					});
					while (System.currentTimeMillis()<endtime || incomplete) {
						 Thread.sleep(10);					}
				}
				catch (Exception e){
				}
			}
		}
	}

	public static void DisplaySequence() throws InterruptedException{
		for (i=0;i<colours.size();++i) {
			switch (colours.get(i)) {
			case 0:
				swiftBot.setUnderlight(Underlight.MIDDLE_LEFT, red);
				Thread.sleep(1000);
				swiftBot.setUnderlight(Underlight.MIDDLE_LEFT, blank);
				break;
			case 1:
				swiftBot.setUnderlight(Underlight.MIDDLE_RIGHT, green);
				Thread.sleep(1000);
				swiftBot.setUnderlight(Underlight.MIDDLE_RIGHT, blank);

				break;
			case 2:
				swiftBot.setUnderlight(Underlight.BACK_LEFT, blue);
				Thread.sleep(1000);
				swiftBot.setUnderlight(Underlight.BACK_LEFT, blank);

				break;
			case 3:
				swiftBot.setUnderlight(Underlight.BACK_RIGHT, yellow);
				Thread.sleep(1000);
				swiftBot.setUnderlight(Underlight.BACK_RIGHT, blank);

				break;
			}
		}
	}

	public static void CheckButtons() { //Checking and Comparing Inputs
		swiftBot.enableButton(Button.A,() -> {
			if (colours.get(i)==0) {
				incomplete = false;
				++level;
			}
			else {
				play = false;
			}
		});
		swiftBot.enableButton(Button.B,() -> {
			if (colours.get(i)==1) {
				incomplete = false;
				++level;
			}
			else {
				play = false;
			}
		});
		swiftBot.enableButton(Button.X,() -> {
			if (colours.get(i)==2) {
				incomplete = false;
				++level;
			}
			else {
				play = false;
			}
		});
		swiftBot.enableButton(Button.Y,() -> {
			if (colours.get(i)==3) {
				incomplete = false;
				++level;
			}
			else {
				play = false;
			}
		});
	}

	public static void DisplayLevel() {
		System.out.println("Level"+level);
	}
}


