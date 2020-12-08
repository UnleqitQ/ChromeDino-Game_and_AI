package dino.test2;

import java.awt.Canvas;
import java.util.HashMap;
import java.util.Map;

import org.neat.calculate.Calculator;

public class Player {
	
	Calculator calculator;
	Canvas canvas;
	float posY = 0;
	float velY = 0;
	boolean alive = true;
	Game game;
	boolean duck;
	int ticks = 0;
	
	public Player(Calculator calculator, Game game, Canvas canvas) {
		this.calculator = calculator;
		this.game = game;
		this.canvas = canvas;
	}
	
	public Map<Long, Float> getSightMap() {
		Map<Long, Float> sightMap = new HashMap<>();
		Obstacle obstacle = game.obstacles.get(0);
		if (obstacle.posX<100) {
			obstacle = game.obstacles.get(1);
		}
		sightMap.put(0L, posY);
		sightMap.put(1L, (float)(obstacle.posX-100)/100);
		if (obstacle.bird) {
			sightMap.put(2L, 1f);
			sightMap.put(3L, (float)obstacle.height-1);
		}
		else {
			sightMap.put(2L, 0f);
			sightMap.put(3L, -1f);
		}
		if (obstacle.tall) {
			sightMap.put(4L, 1f);
		}
		else {
			sightMap.put(4L, 0f);
		}
		if (obstacle.thick) {
			sightMap.put(5L, 1f);
		}
		else {
			sightMap.put(5L, 0f);
		}
		sightMap.put(6L, 4+Math.min((float)game.ticks/100000, 0.5f));
		return sightMap;
	}
	
	public void test() {
		if (!alive) {
			return;
		}
		Obstacle first = game.obstacles.get(0);
		Obstacle second = game.obstacles.get(1);
		test(first);
		test(second);
	}
	
	public void test(Obstacle obstacle) {
		if (obstacle==null) {
			return;
		}
		/*if (obstacle.bird) {
			if (obstacle.posX+(obstacle.thick?100:0)>100) {
				if (obstacle.posX<100+(duck?100:50)) {
					if (obstacle.height == 0) {
						if (posY*100<40) {
							alive = false;
						}
					}
					if (obstacle.height == 1) {
						if (!duck) {
							if (posY*100<140) {
								alive = false;
							}
						}
					}
					if (obstacle.height == 2) {
						if (posY*100+100>180) {
							alive = false;
						}
					}
				}
			}
		}
		else {
			if (obstacle.posX+(obstacle.thick?100:0)>=100) {
				if (obstacle.posX<=100+(duck?50:50)) {
					if (posY*100 < (obstacle.tall?100:50)) {
						alive = false;
					}
				}
			}
		}*/
		if (obstacle.bird) {
			if (obstacle.posX<=100+(duck?50:50)) {
				if (obstacle.posX+(obstacle.thick?100:40)>=100) {
					if (obstacle.height == 0) {
						if (posY*100<40) {
							alive = false;
						}
					}
					if (obstacle.height == 1) {
						if (!duck) {
							if (posY*100<140) {
								alive = false;
							}
						}
					}
					if (obstacle.height == 2) {
						if (posY*100+100>180) {
							alive = false;
						}
					}
				}
			}
		}
		else {
			if (obstacle.posX<=100+(duck?90:120)) {
				if (obstacle.posX+(obstacle.thick?game.applicationDino2.w1:game.applicationDino2.w0)>=100) {
					if (posY*100 <= (obstacle.tall?game.applicationDino2.h1:game.applicationDino2.h0)) {
						alive = false;
					}
				}
			}
		}
	}
	
	public void think() {
		if (!alive) {
			return;
		}
		calculator.setInputValues(getSightMap());
		Map<Long, Float> outputMap = calculator.getOutputs();
		duck = false;
		if (outputMap.get(8L)>0.5) {
			if (posY<=0.00) {
				velY = 10;
			}
		}
		else {
			if (posY<=0.00) {
				if (outputMap.get(7L)>0.5) {
					duck = true;
				}
			}
		}
	}
	
	public void tick(float dt) {
		if (!alive) {
			return;
		}
		ticks++;
		float gravity = 20;
		posY -= Math.pow(dt, 2)*gravity/2;
		posY += velY*dt;
		posY = Math.max(0, posY);
		velY -= gravity*dt;
		calculator.genome.fitness = ticks/1000;
	}
	
}
