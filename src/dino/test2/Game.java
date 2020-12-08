package dino.test2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {
	
	Random rand;
	
	List<Player> players;
	List<Obstacle> obstacles;
	
	int dist = 600;
	
	int ticks = 0;
	int score = 0;
	ApplicationDino2 applicationDino2;
	
	public Game(ApplicationDino2 applicationDino2, long seed) {
		obstacles = new ArrayList<>();
		players = new ArrayList<>();
		this.applicationDino2 = applicationDino2;
		obstacles.add(new Obstacle(true, false, false, 0));
		//obstacles.add(new Obstacle(false, false, true, 2));
		obstacles.get(0).posX-=400;
		obstacles.add(new Obstacle(false, true, false, 0));
		//obstacles.add(new Obstacle(false, false, true, 1));
		//players.add(new Player(null, this));
		//players.get(0).velY = 1;
		rand = new Random(seed);
	}
	
	public void tick(float dt) {
		ticks++;
		float speed = 4+Math.min((float)ticks/100000, 0.5f);
		for (Obstacle obstacle : obstacles) {
			obstacle.posX -= speed*dt*100;
		}
		for (Player player : players) {
			player.tick(dt);
		}
		Obstacle lastObstacle = obstacles.get(obstacles.size()-1);
		if (lastObstacle.posX+(lastObstacle.thick?applicationDino2.w1: applicationDino2.w0)<1200-dist) {
			if (rand.nextDouble()<0.35*Math.min(1, (float)ticks/1000)) {
				obstacles.add(new Obstacle(false, false, true, (int)(rand.nextDouble()*3)));
			}
			else {
				double val = rand.nextDouble();
				boolean thick = Math.floorMod((int) (val*3), 2)==1;
				boolean tall = (int) (val*3)==0;
				//boolean thick = rand.nextDouble()<0.5;
				obstacles.add(new Obstacle(tall, thick, false, 0));
				dist = (int) Math.min(500, Math.max(450, rand.nextDouble()*Math.exp(10-ticks/1000)*5));
			}
			//obstacles.add(new Obstacle(true, true, false, 0));
		}
		if (obstacles.get(0).posX+(obstacles.get(0).thick?100: 10)<-50) {
			obstacles.remove(0);
			score++;
		}
	}
	
}
