package dino.test0;

import java.util.ArrayList;
import java.util.List;

public class Game {
	
	List<Player> players;
	List<Obstacle> obstacles;
	
	public Game() {
		obstacles = new ArrayList<>();
		players = new ArrayList<>();
		obstacles.add(new Obstacle(false, false, false, 0));
		obstacles.get(0).posX-=200;
		obstacles.add(new Obstacle(false, false, false, 0));
		//players.add(new Player(null, this));
		//players.get(0).velY = 1;
	}
	
	public void tick(float dt) {
		float speed = 2;
		for (Obstacle obstacle : obstacles) {
			obstacle.posX -= speed*dt*100;
		}
		for (Player player : players) {
			player.tick(dt);
		}
		Obstacle lastObstacle = obstacles.get(obstacles.size()-1);
		if (lastObstacle.posX+(lastObstacle.thick?100: 10)<1000) {
			if (Math.random()<0.1) {
				obstacles.add(new Obstacle(false, false, true, (int)(Math.random()*3)));
			}
			else {
				boolean tall = Math.random()<0.5;
				//boolean thick = Math.random()<0.5;
				obstacles.add(new Obstacle(tall, !tall, false, 0));
			}
			//obstacles.add(new Obstacle(true, true, false, 0));
		}
		if (obstacles.get(0).posX+(obstacles.get(0).thick?100: 10)<-50) {
			obstacles.remove(0);
		}
	}
	
}
