package dino.test2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import org.neat.activationFunction.ActivationFunction;
import org.neat.calculate.Calculator;
import org.neat.gene.ConnectionGene;
import org.neat.gene.NodeGene;
import org.neat.genome.Genome;
import org.neat.neat.*;

import snake.test3.NetCanvas;

public class ApplicationDino2 {
	
	int generation = 0;
	
	int w0 = 60;
	int w1 = 120;
	int h0 = 80;
	int h1 = 160;
	
	public float speed = 100;
	Image cactusSmall = new ImageIcon("./resources/dino/cactusSmall0000.png").getImage().getScaledInstance(w0, h0, Image.SCALE_SMOOTH);
	Image cactusBig = new ImageIcon("./resources/dino/cactusBig0000.png").getImage().getScaledInstance(w0, h1, Image.SCALE_SMOOTH);
	Image cactusMany = new ImageIcon("./resources/dino/cactusSmallMany0000.png").getImage().getScaledInstance(w1, h0, Image.SCALE_SMOOTH);
	Image bird1 = new ImageIcon("./resources/dino/berd.png").getImage();
	Image bird2 = new ImageIcon("./resources/dino/berd2.png").getImage();
	Image dino1 = new ImageIcon("./resources/dino/dinorun0000.png").getImage();//.getScaledInstance(50, 110, Image.SCALE_SMOOTH);
	Image dino2 = new ImageIcon("./resources/dino/dinorun0001.png").getImage();//.getScaledInstance(50, 110, Image.SCALE_SMOOTH);
	Image duck1 = new ImageIcon("./resources/dino/dinoduck0000.png").getImage();//.getScaledInstance(80, 60, Image.SCALE_SMOOTH);
	Image duck2 = new ImageIcon("./resources/dino/dinoduck0001.png").getImage();//.getScaledInstance(80, 60, Image.SCALE_SMOOTH);
	Image jump1 = new ImageIcon("./resources/dino/dinoJump0000.png").getImage();//.getScaledInstance(50, 110, Image.SCALE_SMOOTH);
	int ticks = 0;
	
	int refreshGameEveryNTicks = 3;
	int refreshNetEveryNTicks = 100;
	
	boolean show = true;
	boolean showNet = true;
	Game game = null;
	Canvas canvas;
	
	Neat neat;
	
	
	public static void main(String[] args) {
		ApplicationDino2 applicationDino2 = new ApplicationDino2();
	}
	
	public ApplicationDino2() {
		long seed = (new Random()).nextLong();
		System.out.println("Create KeyListener");
		KeyListener keyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode()==KeyEvent.VK_PLUS) {
					speed+=10;
				}
				if (e.getKeyCode()==KeyEvent.VK_MINUS) {
					if (speed<=10) {
						return;
					}
					speed-=10;
				}
				if (e.getKeyCode()==KeyEvent.VK_N) {
					show = !show;
				}
				if (e.getKeyCode()==KeyEvent.VK_G) {
					showNet = !showNet;
				}
				if (e.getKeyCode()==KeyEvent.VK_R) {
					if (canvas==null) {
						return;
					}
					canvas.repaint();
				}
				if (e.getKeyCode()==KeyEvent.VK_DOWN) {
					if (refreshGameEveryNTicks>100) {
						return;
					}
					refreshGameEveryNTicks++;
				}
				if (e.getKeyCode()==KeyEvent.VK_UP) {
					if (refreshGameEveryNTicks<2) {
						return;
					}
					refreshGameEveryNTicks--;
				}
				if (e.getKeyCode()==KeyEvent.VK_LEFT) {
					if (refreshNetEveryNTicks>100) {
						return;
					}
					refreshNetEveryNTicks++;
				}
				if (e.getKeyCode()==KeyEvent.VK_RIGHT) {
					if (refreshNetEveryNTicks<2) {
						return;
					}
					refreshNetEveryNTicks--;
				}
			}
		};
		float mul = 0.25f;
		System.out.println("Create Neat");
		neat = new Neat(7, 2, true);
		System.out.println("Config Neat");
		neat.config.initGenome.fullMesh = false;
		neat.config.initGenome.meshPercentage = 0.2f;
		neat.config.crossover.takeSecondNode = 0.2f;
		neat.config.crossover.takeSecondConnection = 0.2f;
		neat.config.crossover.thinConnections = false;
		neat.config.crossover.thinViaPercentage = true;
		neat.config.crossover.thinProbOrPerc = 0.02f;
		neat.config.mutate.addExistingNode = true;
		neat.config.mutate.averageAddNodesPerMutate = 0.1f*mul;
		neat.config.mutate.averageAddExistingNodesPerMutate = 0.075f*mul;
		neat.config.mutate.averageAddConnectionsPerMutate = 0.25f*mul;
		neat.config.mutate.averageToggleNodesPerMutate = 0.1f*mul;
		neat.config.mutate.averageToggleConnectionsPerMutate = 0.15f*mul;
		neat.config.mutate.averageCombineInConnectionsPerMutate = 0.1f*mul;
		neat.config.mutate.averageCombineOutConnectionsPerMutate = 0.1f*mul;
		neat.config.mutate.nodeActivation = true;
		neat.config.mutate.genomeActivation = true;
		neat.config.mutate.averageNewNodesActivationPerMutate = 0.05f*mul;
		neat.config.mutate.genomeActivationProb = 0.025f*mul;
		neat.config.mutate.hasWeightBorders = true;
		neat.config.mutate.weightBorders = 1;
		neat.config.species.stagnationDuration = 10;
		//neat.activationFunctions.add(ActivationFunction.Clamped);
		neat.activationFunctions.add(ActivationFunction.Gauss);
		neat.activationFunctions.add(ActivationFunction.Step);
		//neat.activationFunctions.add(ActivationFunction.Hat);
		//neat.activationFunctions.add(ActivationFunction.ArcTan);
		//neat.activationFunctions.add(ActivationFunction.SiLU);
		//neat.activationFunctions.add(ActivationFunction.Sin);
		//neat.activationFunctions.add(ActivationFunction.Softplus);
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setBounds(50, 50, 1200, 800);
		frame.setUndecorated(true);
		frame.setVisible(true);
		canvas = new Canvas();
		canvas.setBackground(Color.white);
		canvas.setSize(frame.getWidth(), frame.getHeight());
		frame.add(canvas);
		canvas.setVisible(true);
		JFrame netFrame1 = new JFrame("Last");
		netFrame1.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		netFrame1.setBounds(0, 0, 1920, 1080);
		netFrame1.setUndecorated(true);
		netFrame1.setVisible(true);
		Canvas netCanvas1 = new Canvas();
		netCanvas1.setSize(netFrame1.getWidth(), netFrame1.getHeight());
		netFrame1.add(netCanvas1);
		netCanvas1.setVisible(true);
		JFrame netFrame2 = new JFrame("Curr");
		netFrame2.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		netFrame2.setBounds(0, 0, 1920, 1080);
		netFrame2.setUndecorated(true);
		netFrame2.setVisible(true);
		Canvas netCanvas2 = new Canvas();
		netCanvas2.setSize(netFrame1.getWidth(), netFrame1.getHeight());
		netFrame2.add(netCanvas2);
		netCanvas2.setVisible(true);
		
		System.out.println("add Listener");
		canvas.addKeyListener(keyListener);
		frame.addKeyListener(keyListener);
		netCanvas1.addKeyListener(keyListener);
		netFrame1.addKeyListener(keyListener);
		netCanvas2.addKeyListener(keyListener);
		netFrame2.addKeyListener(keyListener);
		
		Map<String, Image> activationIcons = initIcons();
		
		System.out.println("Init Neat");
		neat.initialize();
		neat.nodes.get(0L).title = "Dino Y";
		neat.nodes.get(1L).title = "Obstacle X";
		neat.nodes.get(2L).title = "Bird/Cactus";
		neat.nodes.get(3L).title = "Bird Y";
		neat.nodes.get(4L).title = "Height";
		neat.nodes.get(5L).title = "Width";
		neat.nodes.get(8L).title = "Bias";
		System.out.println("Create 10 Genomes");
		neat.createGenomes(10);
		System.out.println("Mutate");
		neat.mutate();
		System.out.println("Randomly breed 290 Genomes");
		neat.breedrand(290);
		System.out.println("Mutate");
		neat.mutate();
		System.out.println("Specicate0");
		neat.initSpecicate();
		neat.specicate();
		
		//canvas.repaint();
		while (true) {
			canvas.repaint();
			game = new Game(this, seed);
			List<Calculator> calculators = neat.createCalculators();
			for (Calculator calculator : calculators) {
				Player player = new Player(calculator, game, canvas);
				game.players.add(player);
			}
			boolean run = true;
			ticks = 0;
			while (run) {
				ticks++;
				run = false;
				for (Player player : game.players) {
					if (player.alive) {
						run = true;
						break;
					}
				}
				if (!run) {
					break;
				}
				if (show) {
					if (Math.floorMod(ticks, refreshGameEveryNTicks)==0) {
						drawGame(game, canvas);
					}
				}
				game.tick(0.01f);
				if (showNet) {
					if (Math.floorMod(ticks, refreshNetEveryNTicks)==1) {
						neat.sortGenomes();
						drawNet(neat.genomes.getList().get(0), netCanvas2, netFrame2, activationIcons);
					}
				}
				for (Player player : game.players) {
					player.test();
					player.think();
				}
				if (show) {
					try {
						Thread.sleep((int)(1000/speed));
					}
					catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
			generation++;
			neat.clearSavedGenomes();
			neat.calcSpeciesFitness();
			neat.sortGenomes();
			Genome saved = neat.genomes.getList().get(0).copyNew();
			drawNet(neat.genomes.getList().get(0), netCanvas1, netFrame1, activationIcons);
			System.out.println(game.score);
			neat.sortSpecies();
			neat.addSpeciesFitnessToHist();
			System.out.println("Cut Species");
			neat.cutGenomesInSpecies(0.7f);
			System.out.println("Stagnate");
			neat.stagnate(0.1f, 0.1f);
			/*neat.saveGenomesCopy();
			int count = Math.max(0, 10-neat.savedGenomes.size());
			for (int k = 0; k < count; k++) {
				neat.savedGenomes.remove(neat.savedGenomes.size()-1);
			}*/
			neat.removeEmptySpecies();
			System.out.println("Breed");
			neat.breedSpeciesUpTo(400);
			System.out.println("Mutate");
			neat.mutate();
			neat.genomes.add(saved);
			//neat.loadGenomesCopyNew();
			System.out.println("Specicate");
			neat.clearGenomesOfSpecies();
			neat.specicate();
			neat.removeEmptySpecies();
		}
	}
	
	public void drawGame(Game game, Canvas canvas) {
		Graphics2D g = (Graphics2D) canvas.getGraphics();
		g.setColor(Color.white);
		//g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.fillRect(95, canvas.getHeight()-400, 140, 400);
		for (Obstacle obstacle : game.obstacles) {
			if (obstacle.bird) {
				if (obstacle.height==0) {
					g.fillRect(obstacle.posX, canvas.getHeight()-60, 120, 80);
				}
				if (obstacle.height==1) {
					g.fillRect(obstacle.posX, canvas.getHeight()-140, 120, 80);
				}
				if (obstacle.height==2) {
					g.fillRect(obstacle.posX, canvas.getHeight()-240, 120, 80);
				}
			}
			else {
				int w = w0;
				if (obstacle.thick) {
					w = w1;
				}
				int h = h0;
				if (obstacle.tall) {
					h = h1;
				}
				g.fillRect(obstacle.posX, canvas.getHeight()-h, w+20, h);
			}
		}
		g.setColor(Color.black);
		for (Player player : game.players) {
			if (!player.alive) {
				continue;
			}
			if (player.duck) {
				g.drawRect(100, (int)(canvas.getHeight()-player.posY*100)-50, 120, 50);
				g.drawImage((Math.floorMod(ticks/2, 2)==0?duck1:duck2), 100, (int)(canvas.getHeight()-player.posY*100)-60, null);
				continue;
			}
			g.drawRect(100, (int)(canvas.getHeight()-player.posY*100)-100, 90, 100);
			if (player.posY>0) {
				g.drawImage((Math.floorMod(ticks/2, 2)==0?jump1:jump1), 100, (int)(canvas.getHeight()-player.posY*100)-110, null);
				continue;
			}
			g.drawImage((Math.floorMod(ticks/2, 2)==0?dino1:dino2), 100, (int)(canvas.getHeight()-player.posY*100)-110, null);
		}
		for (Obstacle obstacle : game.obstacles) {
			if (obstacle.bird) {
				if (obstacle.height==0) {
					g.drawImage((Math.floorMod(ticks/8, 2)==0?bird1:bird2), obstacle.posX, canvas.getHeight()-60, null);
					g.drawRect(obstacle.posX, canvas.getHeight()-40, 80, 40);
				}
				if (obstacle.height==1) {
					g.drawImage((Math.floorMod(ticks/8, 2)==0?bird1:bird2), obstacle.posX, canvas.getHeight()-140, null);
					g.drawRect(obstacle.posX, canvas.getHeight()-120, 80, 40);
				}
				if (obstacle.height==2) {
					g.drawImage((Math.floorMod(ticks/8, 2)==0?bird1:bird2), obstacle.posX, canvas.getHeight()-240, null);
					g.drawRect(obstacle.posX, canvas.getHeight()-220, 80, 40);
				}
			}
			else {
				int w = w0;
				if (obstacle.thick) {
					w = w1;
				}
				int h = h0;
				if (obstacle.tall) {
					h = h1;
				}
				g.drawRect(obstacle.posX, canvas.getHeight()-h, w, h);
				if (obstacle.tall) {
					g.drawImage(cactusBig, obstacle.posX, canvas.getHeight()-h1, null);
				}
				else {
					if (obstacle.thick) {
						g.drawImage(cactusMany, obstacle.posX, canvas.getHeight()-h0, null);
					}
					else {
						g.drawImage(cactusSmall, obstacle.posX, canvas.getHeight()-h0, null);
					}
				}
			}
		}
		g.setColor(Color.white);
		g.fillRect(canvas.getWidth()-210, 80, 210, 60);
		g.setFont(new Font("Times New Roman", Font.BOLD, 30));
		g.setColor(Color.red);
		g.drawString("Generation: "+generation, canvas.getWidth()-280, 80);
		if (game!=null) {
			g.drawString("Score: "+game.score, canvas.getWidth()-280, 120);
		}
		g.drawString("Population Size: "+neat.genomes.size(), canvas.getWidth()-280, 160);
	}
	private static Map<String, Image> initIcons() {
		Map<String, Image> activationIcons = new HashMap<>();
		addToMap("Abs", activationIcons);
		addToMap("ArcTan", activationIcons);
		addToMap("Clamped", activationIcons);
		addToMap("Cube", activationIcons);
		addToMap("ELU", activationIcons);
		addToMap("Exp", activationIcons);
		addToMap("Gauss", activationIcons);
		addToMap("Hat", activationIcons);
		addToMap("Identity", activationIcons);
		addToMap("Inverted", activationIcons);
		addToMap("LeakyReLU", activationIcons);
		addToMap("Log", activationIcons);
		addToMap("reLU", activationIcons);
		addToMap("Sigmoid", activationIcons);
		addToMap("SiLU", activationIcons);
		addToMap("Sin", activationIcons);
		addToMap("Softplus", activationIcons);
		addToMap("Square", activationIcons);
		addToMap("Step", activationIcons);
		addToMap("Tanh", activationIcons);
		return activationIcons;
	}
	private static void addToMap(String name, Map<String, Image> activationIcons) {
		activationIcons.put("Activation"+name, (new ImageIcon("./resources/Activations/"+name+".png")).getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH));
	}
	private void drawNet(Genome genome, Canvas netCanvas, JFrame netFrame, Map<String, Image> activationIcons) {
		//netCanvas.repaint();
		/*File file = new File("C:\\Users\\quent.DESKTOP-L0VD1RP\\Desktop\\XXX.csv");
		String string = "";
		try {
			FileOutputStream fos = new FileOutputStream(file);
			string += "Perfect Dino";
			string += "\n\n";
			string += "Nodes\n";
			string += "Inno; X; Act; Aggr; Enabled\n";
			for (NodeGene node : genome.nodes.getList()) {
				string += node.getInnovationNumber()+"; "+Float.toString(node.x).replace(".", ",")+"; "+node.activation.getClass().getSimpleName()+"; "+node.aggregation.getClass().getSimpleName()+"; "+node.enabled+"\n";
			}
			string += "\nConnections\n";
			string += "Inno; From; To; Weight; Enabled\n";
			for (ConnectionGene connection : genome.connections.getList()) {
				string += connection.getInnovationNumber()+"; "+connection.fromGene.getInnovationNumber()+"; "+connection.toGene.getInnovationNumber()+"; "+Float.toString(connection.weight).replace(".", ",")+"; "+connection.enabled+"\n";
			}
			fos.write(string.getBytes());
			fos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}*/
		int dist = 200;
		int sizeNode = 40;
		float start = 175;
		float stop = 150;
		//System.out.print(ActivationArcTan.class.getSimpleName());
		Graphics2D g = (Graphics2D) netCanvas.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, netCanvas.getWidth(), netCanvas.getHeight());
		if (genome == null) {
			return;
		}
		Map<Float, List<NodeGene>> nodesByX = genome.nodes.getNodesByX();
		List<ConnectionGene> connections = genome.connections.getList();
		g.setStroke(new BasicStroke(4));
		for (Entry<Float, List<NodeGene>> layerEntry : nodesByX.entrySet()) {
			for (NodeGene node : layerEntry.getValue()) {
				if (node.enabled) {
					g.setColor(Color.black);
				}
				else {
					g.setColor(Color.gray);
				}
				Image icon = activationIcons.get(node.activation.getClass().getSimpleName());
				//icon = icon.getScaledInstance(sizeNode, sizeNode, Image.SCALE_SMOOTH);
				int posX = (int) ((float)netCanvas.getWidth()*((float)layerEntry.getKey()*(1-(start+stop)/(float)netCanvas.getWidth()))+start);
				int posY = (int) ((float)netCanvas.getHeight()*((float)layerEntry.getValue().indexOf(node)/(float)layerEntry.getValue().size()*0.85+0.05))+0;
				//g.fillOval(posX-10, posY-10, 20, 20);
				g.drawImage(icon, posX-sizeNode/2, (int)(posY-(sizeNode/2-dist*Math.pow(layerEntry.getKey(), 2))), null);
				g.drawImage(icon, posX-sizeNode/2, (int)(posY-(sizeNode/2-dist*Math.pow(layerEntry.getKey(), 2))), null);
				g.drawOval(posX-sizeNode/2, (int)(posY-sizeNode/2+dist*Math.pow(layerEntry.getKey(), 2)), sizeNode, sizeNode);
				//System.out.println(node);
			}
		}
		for (ConnectionGene connectionGene : connections) {
			if (connectionGene.weight>0) {
				g.setColor(Color.blue);
				if (!connectionGene.enabled) {
					g.setColor(new Color(0, 255, 255));
				}
			}
			else {
				g.setColor(Color.red);
				if (!connectionGene.enabled) {
					g.setColor(new Color(255, 255, 0));
				}
			}
			g.setStroke(new BasicStroke(2*(float) Math.ceil(10*Math.abs(connectionGene.weight*4))/10));
			if (connectionGene.weight==0) {
				if (connectionGene.enabled) {
					g.setColor(Color.black);
				}
				else {
					g.setColor(Color.gray);
				}
				g.setStroke(new BasicStroke(1));
			}
			NodeGene from = connectionGene.fromGene;
			NodeGene to = connectionGene.toGene;
			List<NodeGene> fromLayer = nodesByX.get(from.x);
			List<NodeGene> toLayer = nodesByX.get(to.x);
			int posX1 = (int) ((float)netCanvas.getWidth()*(from.x*(1-(start+stop)/(float)netCanvas.getWidth()))+start);
			int posX2 = (int) ((float)netCanvas.getWidth()*(to.x*(1-(start+stop)/(float)netCanvas.getWidth()))+start);
			int posY1 = (int) ((float)netCanvas.getHeight()*((float)fromLayer.indexOf(from)/(float)fromLayer.size()*0.85+0.05))+0;
			int posY2 = (int) ((float)netCanvas.getHeight()*((float)toLayer.indexOf(to)/(float)toLayer.size()*0.85+0.05))+0;
			g.drawLine(posX1+sizeNode/2, (int)(posY1+dist*Math.pow(from.x, 2)), posX2-sizeNode/2, (int)(posY2+dist*Math.pow(to.x, 2)));
			//System.out.println(connectionGene);
		}
		g.setStroke(new BasicStroke(4));
		for (Entry<Float, List<NodeGene>> layerEntry : nodesByX.entrySet()) {
			for (NodeGene node : layerEntry.getValue()) {
				if (node.enabled) {
					g.setColor(Color.black);
				}
				else {
					g.setColor(Color.gray);
				}
				Image icon = activationIcons.get(node.activation.getClass().getSimpleName());
				//icon = icon.getScaledInstance(sizeNode, sizeNode, Image.SCALE_SMOOTH);
				int posX = (int) ((float)netCanvas.getWidth()*((float)layerEntry.getKey()*(1-(start+stop)/(float)netCanvas.getWidth()))+start);
				int posY = (int) ((float)netCanvas.getHeight()*((float)layerEntry.getValue().indexOf(node)/(float)layerEntry.getValue().size()*0.85+0.05))+0;
				//g.fillOval(posX-10, posY-10, 20, 20);
				g.drawImage(icon, posX-sizeNode/2, (int)(posY-(sizeNode/2-dist*Math.pow(layerEntry.getKey(), 2))), null);
				g.drawImage(icon, posX-sizeNode/2, (int)(posY-(sizeNode/2-dist*Math.pow(layerEntry.getKey(), 2))), null);
				g.drawOval(posX-sizeNode/2, (int)(posY-sizeNode/2+dist*Math.pow(layerEntry.getKey(), 2)), sizeNode, sizeNode);
				//System.out.println(node);
			}
		}
		Font normalFont = new Font("Times New Roman", Font.PLAIN, 23);
		Font subFont = new Font("Times New Roman", Font.PLAIN, 15);
		g.setColor(Color.black);
		g.setFont(normalFont);
		g.drawString("Duck", netCanvas.getWidth()-100, 260);
		g.drawString("Jump", netCanvas.getWidth()-100, 720);
		
		g.drawString("Dino Y", 30, 55);
		
		g.drawString("Obstacle X", 30, 170);
		
		g.drawString("Bird/Cactus", 30, 285);
		g.setFont(subFont);
		g.drawString("(B:1, C:0)", 30, 305);
		
		g.setFont(normalFont);
		g.drawString("Bird Y", 30, 400);
		g.setFont(subFont);
		g.drawString("B: (-1) - 1", 30, 420);
		g.drawString("C: (-1)", 30, 440);
		
		g.setFont(normalFont);
		g.drawString("Tall", 30, 515);
		g.setFont(subFont);
		g.drawString("B: 0", 30, 535);
		
		g.setFont(normalFont);
		g.drawString("Thick", 30, 630);
		g.setFont(subFont);
		g.drawString("B: 0", 30, 650);
		
		g.setFont(normalFont);
		g.drawString("Speed", 30, 745);
		
		g.setFont(new Font("Times New Roman", Font.BOLD, 25));
		g.drawString("Bias", 30, 860);
		//g.setFont(new Font("Times New Roman", Font.BOLD, 15));
		g.drawString("1", 30, 890);
		
		g.setFont(new Font("Times New Roman", Font.BOLD, 30));
		g.setColor(Color.red);
		g.drawString("Generation: "+generation, netCanvas.getWidth()-280, 80);
		if (game!=null) {
			g.drawString("Score: "+game.score, netCanvas.getWidth()-280, 120);
		}
		g.drawString("Population Size: "+neat.genomes.size(), netCanvas.getWidth()-280, 160);
		
	}
	
}
