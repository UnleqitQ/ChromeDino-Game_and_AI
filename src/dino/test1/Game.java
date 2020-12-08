package dino.test1;

import java.util.ArrayList;
import java.util.List;

public class Game {
	
	List<Player> players;
	List<Obstacle> obstacles;
	
	int ticks = 0;
	int score = 0;
	Application application;
	
	public Game(Application application) {
		obstacles = new ArrayList<>();
		players = new ArrayList<>();
		this.application = application;
		obstacles.add(new Obstacle(true, false, false, 0));
		//obstacles.add(new Obstacle(false, false, true, 2));
		obstacles.get(0).posX-=400;
		obstacles.add(new Obstacle(false, true, false, 0));
		//obstacles.add(new Obstacle(false, false, true, 1));
		//players.add(new Player(null, this));
		//players.get(0).velY = 1;
	}
	
	public void tick(float dt) {
		ticks++;
		float speed = 4+Math.min((float)ticks/100000, 1);
		for (Obstacle obstacle : obstacles) {
			obstacle.posX -= speed*dt*100;
		}
		for (Player player : players) {
			player.tick(dt);
		}
		Obstacle lastObstacle = obstacles.get(obstacles.size()-1);
		int dist = (int) Math.min(500, Math.max(350, Math.random()*Math.exp(10-ticks/1000)*5));
		if (lastObstacle.posX+(lastObstacle.thick?application.w1: application.w0)<1200-dist) {
			if (Math.random()<0.35*Math.min(1, (float)ticks/1000)) {
				obstacles.add(new Obstacle(false, false, true, (int)(Math.random()*3)));
			}
			else {
				double val = Math.random();
				boolean thick = Math.floorMod((int) (val*3), 2)==1;
				boolean tall = (int) (val*3)==0;
				//boolean thick = Math.random()<0.5;
				obstacles.add(new Obstacle(tall, thick, false, 0));
			}
			//obstacles.add(new Obstacle(true, true, false, 0));
		}
		if (obstacles.get(0).posX+(obstacles.get(0).thick?100: 10)<-50) {
			obstacles.remove(0);
			score++;
		}
	}
	
}
