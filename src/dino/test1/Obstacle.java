package dino.test1;

public class Obstacle {
	
	public int posX = 1200;
	public boolean tall = false;
	public boolean thick = false;
	public boolean bird = false;
	public int height = 0;
	
	public Obstacle(boolean tall, boolean thick, boolean bird, int height) {
		this.tall = tall;
		this.bird = bird;
		this.thick = thick;
		this.height = height;
		if (bird) {
			height = 0;
		}
	}
	
}
