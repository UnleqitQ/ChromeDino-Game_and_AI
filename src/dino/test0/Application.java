package dino.test0;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import org.neat.calculate.Calculator;
import org.neat.gene.ConnectionGene;
import org.neat.gene.NodeGene;
import org.neat.genome.Genome;
import org.neat.neat.*;

import snake.test3.NetCanvas;

public class Application {
	
	public float speed = 10;
	float stepSize = 0.002f;
	boolean next = false;
	Canvas canvas;
	Game game;
	Player player;
	long last = 0;
	
	public static void main(String[] args) {
		Application application = new Application();
	}
	
	public Application() {
		last = System.currentTimeMillis();
		KeyListener keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_PLUS) {
					stepSize*=2;
				}
				if (e.getKeyCode()==KeyEvent.VK_MINUS) {
					stepSize/=2;
				}
				if (e.getKeyCode()==KeyEvent.VK_N) {
					game.tick(stepSize);
				}
				if (e.getKeyCode()==KeyEvent.VK_R) {
					canvas.repaint();
					drawGame(game, canvas);
				}
			}
		};
		MouseMotionListener motionListener = new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if (System.currentTimeMillis()-last<100) {
					return;
				}
				player.posX = e.getX();
				player.posY = (float)e.getY()/100;
				player.tick(0.01f);
				player.test();
				//drawGame(game, canvas);
				last = System.currentTimeMillis();
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				if (System.currentTimeMillis()-last<100) {
					return;
				}
				player.posX = e.getX();
				player.posY = (float)e.getY()/100;
				player.tick(0.01f);
				player.test();
				//drawGame(game, canvas);
				last = System.currentTimeMillis();
			}
		};
		
		game = new Game();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setBounds(50, 50, 1200, 800);
		frame.setUndecorated(true);
		frame.setVisible(true);
		canvas = new Canvas();
		canvas.setBackground(Color.white);
		canvas.setSize(frame.getWidth(), frame.getHeight());
		frame.add(canvas);
		player = new Player(game, canvas);
		canvas.setVisible(true);
		
		canvas.addKeyListener(keyListener);
		frame.addKeyListener(keyListener);
		canvas.addMouseMotionListener(motionListener);
		frame.addMouseMotionListener(motionListener);
		
		
		while (true) {
			canvas.repaint();
			game = new Game();
			player.game = game;
			player.alive = true;
			game.players.add(player);
			boolean run = true;
			while (run) {
				run = player.alive;
				if (!run) {
					break;
				}
				drawGame(game, canvas);
				//game.tick(stepSize);
				player.test();
				try {
					Thread.sleep((int)(1000/speed));
				}
				catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				/*while (!next) {
					try {
						Thread.sleep(100);
					}
					catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
				next = false;*/
			}
			System.out.println("Dead");
		}
	}
	
	public static void drawGame(Game game, Canvas canvas) {
		Graphics2D g = (Graphics2D) canvas.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		/*g.fillRect(95, canvas.getHeight()-400, 110, 400);
		for (Obstacle obstacle : game.obstacles) {
			if (obstacle.bird) {
				if (obstacle.height==0) {
					g.fillRect(obstacle.posX+80, canvas.getHeight()-40, 5, 40);
				}
				if (obstacle.height==1) {
					g.fillRect(obstacle.posX+80, canvas.getHeight()-120, 5, 40);
				}
				if (obstacle.height==2) {
					g.fillRect(obstacle.posX+80, canvas.getHeight()-220, 5, 40);
				}
			}
			else {
				int w = 40;
				if (obstacle.thick) {
					w = 100;
				}
				int h = 40;
				if (obstacle.tall) {
					h = 100;
				}
				g.fillRect(obstacle.posX+w, canvas.getHeight()-h, 5, h);
			}
		}*/
		//canvas.repaint();
		g.setColor(Color.black);
		for (Player player : game.players) {
			if (!player.alive) {
				continue;
			}
			if (player.duck) {
				g.drawRect(player.posX, (int)(player.posY*100)-50, 100, 50);
				continue;
			}
			g.drawRect(player.posX, (int)(player.posY*100)-100, 50, 100);
		}
		for (Obstacle obstacle : game.obstacles) {
			if (obstacle.bird) {
				if (obstacle.height==0) {
					g.fillRect(obstacle.posX, canvas.getHeight()-40, 80, 40);
				}
				if (obstacle.height==1) {
					g.fillRect(obstacle.posX, canvas.getHeight()-120, 80, 40);
				}
				if (obstacle.height==2) {
					g.fillRect(obstacle.posX, canvas.getHeight()-220, 80, 40);
				}
			}
			else {
				int w = 40;
				if (obstacle.thick) {
					w = 100;
				}
				int h = 40;
				if (obstacle.tall) {
					h = 100;
				}
				g.fillRect(obstacle.posX, canvas.getHeight()-h, w, h);
			}
		}
	}
	
}
