import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;

/**Tower Defense
 *
 * Game class
 * - this is tower defense game
 * - user build towers on the map to defend their home from enemies
 * - includes various mouse events
 * - this class includes the core game algorithms
 * - game loop runs approximately 60 time per second
 * - draws all visual elements and keeps track of everything
 * - calls entity methods to move elements
 *
 * @author Ethan Shen
 * @author Walker Jones
 * @version 2017-04-11
 */
public class Game extends Canvas implements MouseListener, MouseMotionListener {

	/**grids
	 * - stores the availability of each grid
	 * - gets updated when a tower is added or removed
	 * - determines where a tower can be placed
	 * @return - the array of grid availabilities of all grids for reset and initialization purposes
	 */
	private boolean [] setGrids() {
		return new boolean [] {false,
				true, true,  true,  true,  true,  true,  false, true,  true,  true,  true,  true,
				true, false, false, false, false, false, false, true,  true,  true,  true,  true,
				true, false, true,  true,  true,  true,  true,  true,  true,  true,  true,  true,
				true, false, true,  false, false, false, false, false, false, false, false, true,
				true, false, true,  false, true,  true,  true,  true,  true,  true,  false, true,
				true, false, true,  false, true,  false, false, false, true,  true,  false, true,
				true, false, true,  false, true,  false, true,  false, true,  true,  false, true,
				true, false, true,  false, true,  false, true,  false, true,  true,  false, true,
				true, false, true,  false, false, false, true,  false, true,  true,  false, true,
				true, false, true,  true,  true,  true,  true,  false, true,  true,  false, true,
				true, false, false, false, false, false, false, false, true,  true,  false, true,
				true, true,  true,  true,  true,  true,  true,  true,  true,  true,  false, true,};
	} // setGrids
	private boolean grids [] = setGrids(); // initialize grids

	/**time related variables */
	private BufferStrategy strategy; // accelerated graphics
	private long lastLoopTime; // loop time (accessible from addEnemy)
	private long delta; // refresh time (accessible from addEnemy)
	private long startMessage; // start time of the message
	private long startTime; // time when current wave start

	/**boolean variables */
	private boolean timeForNextWave = true; // if user is ready for next wave
	private boolean moneyNotEnough = false; // if user doesn't have enough money
	private boolean startHovered = false; // if the start button is hovered
	private boolean sTowerHovered = false; // if the tower button is hovered
	private boolean mTowerHovered = false; // if the tower button is hovered
	private boolean lTowerHovered = false; // if the tower button is hovered
	private boolean sTowerDragged = false; // if the tower button is dragged
	private boolean mTowerDragged = false; // if the tower button is dragged
	private boolean lTowerDragged = false; // if the tower button is dragged
	private boolean enemyInfo = false; // if enemy info is being displayed
	private boolean lost = false; // if player is dead
	private boolean won = false; // if player won

	/**game logic related variables */
	private int wave = 0; // count waves
	private int money = 600; // player money
	private int dragX = 0; // mouse drag x coordinate
	private int dragY = 0; // mouse drag y coordinate
	private int mX = 0; // mouse move x coordinate
	private int mY = 0; // mouse move y coordinate
	private int enemyCount; // number of enemies on screen
	private Entity sell; // draw sell tower option when hovered
	private Entity sellTower; // the tower ready to be sold

	/**array lists */
	private ArrayList <Entity> entities = new ArrayList <> (); // list of entities in game
	private ArrayList <Entity> removeEntities = new ArrayList <> (); // list of entities to remove this loop
	private ArrayList <Entity> addShots = new ArrayList <> (); // list of entities to add this loop

	/**main */
	public static void main(String[] args) {
		new Game();
	} // main

	/**constructor
	 * - set up game canvas
	 * - add mouse listener
	 * - starts the game
	 */
	private Game() {
		// create a frame to contain game
		JFrame container = new JFrame("Tower Defense"); // title

		// get hold the content of the frame
		JPanel panel = (JPanel) container.getContentPane();

		// set up the resolution of the game
		panel.setPreferredSize(new Dimension(1200, 600)); // width and height
		panel.setLayout(null);

		// set up canvas size (this) and add to frame
		setBounds(0, 0, 1200, 600);
		panel.add(this);

		// add mouse listener to the canvas
		super.addMouseListener(this);
		super.addMouseMotionListener(this);

		// tell AWT not to bother repainting canvas since that will be done using graphics acceleration
		setIgnoreRepaint(true);

		// make the window visible
		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		// if user closes window, shutdown game and jre
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // windowClosing
		});

		// create buffer strategy to take advantage of accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		// start the game
		gameLoop();
	} // Game

	/**gameLoop
	 * - runs the game
	 * - calculate the speed of the game loop
	 * - move the entities
	 * - draw the contents
	 * - updates game events
	 */
	private void gameLoop() {

		// keep loop running until game ends
		while (true) {
			delta = System.currentTimeMillis() - lastLoopTime; // update delta
			lastLoopTime = System.currentTimeMillis(); // update lastLoopTime
			int lastEnemyCount = enemyCount; // record last enemy count
			enemyCount = 0; // reset enemyCount
			for (Entity me : entities) if (me instanceof EnemyEntity) enemyCount++; // enemy update
			if (enemyCount < lastEnemyCount) money += 50; // add money if enemy killed

			// paint background black
			Graphics2D g = (Graphics2D)strategy.getDrawGraphics();
			g.setColor(Color.black);
			g.fillRect(0,0,1200,600);
			g.setColor(Color.white);

			// draw background and instructions
			new ImageEntity("images/background.png", 300, 0).draw(g);
			new ImageEntity("images/instructions.png", 0, 0).draw(g);

			//draw start button or stats bar according to game progress
			if(wave == 0) {
				if(!startHovered) new ImageEntity("images/start.png", 900, 0).draw(g);
				else new ImageEntity("images/start-h.png", 900, 0).draw(g);
			} else {
				new ImageEntity("images/stats.png", 900, 0).draw(g);

				// draw wave number
				g.setFont(new Font ("Courier New", Font.BOLD, 50));
				g.drawString(Integer.toString(wave), 1080, 55);
				g.drawString("5", 1155, 55);

				// draw enemy count
				g.setFont(new Font ("Courier New", Font.BOLD, 100));
				g.drawString(Integer.toString(enemyCount), (enemyCount < 10) ? 1020 : 990, 210);
				if(money < 300) g.setColor(Color.red);

				// draw money
				g.setFont(new Font ("Courier New", Font.PLAIN, 30));
				g.drawString(Integer.toString(money), 1055, 266);
			} // if else

			// grey out tower buttons on start screen
			if (wave == 0) {
				new ImageEntity("images/button-1g.png", 910, 310).draw(g);
				new ImageEntity("images/button-2g.png", 910, 410).draw(g);
				new ImageEntity("images/button-3g.png", 910, 510).draw(g);
			} else {
				new ImageEntity("images/button-1.png", 910, 310).draw(g);
				new ImageEntity("images/button-2.png", 910, 410).draw(g);
				new ImageEntity("images/button-3.png", 910, 510).draw(g);
			} // if else

			// change tower button colors on hover
			if(sTowerHovered) new ImageEntity("images/button-1h.png", 910, 310).draw(g);
			if(mTowerHovered) new ImageEntity("images/button-2h.png", 910, 410).draw(g);
			if(lTowerHovered) new ImageEntity("images/button-3h.png", 910, 510).draw(g);

			// roll in enemies
			if(wave == 1) {
				if(timeForNextWave) newWave(); // 8 s, 1 m, 9 total, 450 money
				addEnemy(3, 's');
				addEnemy(20, 's');
				addEnemy(25, 's');
				addEnemy(30, 's');
				addEnemy(32, 's');
				addEnemy(45, 's');
				addEnemy(55, 's');
				addEnemy(60, 's');
				addEnemy(75, 'm');
				if ((lastLoopTime - startTime) > (76 * 1000) && enemyCount == 0) nextWave();
			} else if(wave == 2) {
				if(timeForNextWave) newWave(); // 12 s, 8 m, 20 total, 1000 money
				addEnemy(3, 'm');
				addEnemy(3, 's');
				addEnemy(5, 's');
				addEnemy(12, 's');
				addEnemy(15, 's');
				addEnemy(20, 'm');
				addEnemy(35, 's');
				addEnemy(38, 's');
				addEnemy(42, 's');
				addEnemy(55, 's');
				addEnemy(60, 's');
				addEnemy(65, 'm');
				addEnemy(70, 's');
				addEnemy(80, 'm');
				addEnemy(80, 's');
				addEnemy(85, 's');
				addEnemy(90, 'm');
				addEnemy(108, 'm');
				addEnemy(110, 'm');
				addEnemy(111, 'm');
				if ((lastLoopTime - startTime) > (112 * 1000) && enemyCount == 0) nextWave();
			} else if(wave == 3) {
				if(timeForNextWave) newWave(); // 17 s, 20 m, 9 f, 46 total, 2300 money
				addEnemy(5, 'f');
				addEnemy(5, 'm');
				addEnemy(5, 's');
				addEnemy(8, 's');
				addEnemy(10, 'm');
				addEnemy(12, 's');
				addEnemy(15, 's');
				addEnemy(18, 's');
				addEnemy(25, 'm');
				addEnemy(26, 's');
				addEnemy(28, 'm');
				addEnemy(35, 's');
				addEnemy(38, 's');
				addEnemy(40, 'm');
				addEnemy(41, 'm');
				addEnemy(42, 's');
				addEnemy(48, 's');
				addEnemy(52, 's');
				addEnemy(55, 'm');
				addEnemy(57, 'm');
				addEnemy(60, 'm');
				addEnemy(60, 's');
				addEnemy(65, 's');
				addEnemy(68, 's');
				addEnemy(75, 'm');
				addEnemy(76, 'm');
				addEnemy(78, 'm');
				addEnemy(90, 'f');
				addEnemy(90, 'm');
				addEnemy(90, 's');
				addEnemy(91, 'm');
				addEnemy(92, 's');
				addEnemy(94, 's');
				addEnemy(110, 'f');
				addEnemy(112, 'm');
				addEnemy(115, 'f');
				addEnemy(115, 'm');
				addEnemy(118, 'f');
				addEnemy(120, 'm');
				addEnemy(125, 'm');
				addEnemy(128, 'm');
				addEnemy(130, 'f');
				addEnemy(132, 'm');
				addEnemy(145, 'f');
				addEnemy(146, 'f');
				addEnemy(148, 'f');
				if ((lastLoopTime - startTime) > (150 * 1000) && enemyCount == 0) nextWave();
			} else if(wave == 4) {
				if(timeForNextWave) newWave(); // 19 s, 27 m, 20 f, 64 total, 3750 money
				addEnemy(3, 's');
				addEnemy(6, 'f');
				addEnemy(8, 'f');
				addEnemy(11, 'm');
				addEnemy(14, 'f');
				addEnemy(17, 'f');
				addEnemy(20, 'm');
				addEnemy(23, 'f');
				addEnemy(25, 's');
				addEnemy(27, 'm');
				addEnemy(30, 'f');
				addEnemy(31, 'm');
				addEnemy(32, 'f');
				addEnemy(36, 'm');
				addEnemy(37, 's');
				addEnemy(39, 'm');
				addEnemy(42, 'f');
				addEnemy(45, 'm');
				addEnemy(46, 'f');
				addEnemy(50, 'm');
				addEnemy(52, 's');
				addEnemy(54, 'f');
				addEnemy(57, 'f');
				addEnemy(65, 'm');
				addEnemy(65, 'f');
				addEnemy(67, 's');
				addEnemy(71, 'm');
				addEnemy(72, 'f');
				addEnemy(73, 'm');
				addEnemy(74, 'f');
				addEnemy(75, 'm');
				addEnemy(82, 'f');
				addEnemy(85, 'f');
				addEnemy(88, 's');
				addEnemy(89, 'f');
				addEnemy(90, 'm');
				addEnemy(96, 'm');
				addEnemy(98, 's');
				addEnemy(98, 'f');
				addEnemy(100, 's');
				addEnemy(104, 'm');
				addEnemy(109, 'm');
				addEnemy(111, 's');
				addEnemy(114, 'f');
				addEnemy(118, 'f');
				addEnemy(119, 'f');
				addEnemy(120, 'f');
				addEnemy(127, 'm');
				addEnemy(132, 's');
				addEnemy(135, 'm');
				addEnemy(137, 'f');
				addEnemy(140, 'm');
				addEnemy(144, 'm');
				addEnemy(147, 'm');
				addEnemy(149, 'f');
				addEnemy(149, 'm');
				addEnemy(150, 'f');
				addEnemy(151, 'f');
				addEnemy(151, 'm');
				addEnemy(152, 'f');
				addEnemy(153, 'f');
				addEnemy(153, 'm');
				addEnemy(153, 'f');
				addEnemy(156, 'f');
				addEnemy(158, 'f');
				addEnemy(163, 's');
				addEnemy(165, 'm');
				addEnemy(168, 'f');
				addEnemy(172, 'm');
				addEnemy(175, 's');
				addEnemy(177, 'm');
				addEnemy(182, 'm');
				addEnemy(187, 'f');
				addEnemy(192, 'f');
				addEnemy(195, 'f');
				addEnemy(198, 'f');
				if ((lastLoopTime - startTime) > (200 * 1000) && enemyCount == 0)nextWave();
			} else if(wave == 5) {
				if(timeForNextWave) newWave(); // 32 s, 43 m, 53 f, 1 b, 128 total, 6400 money
				addEnemy(1, 's');
				addEnemy(1, 'm');
				addEnemy(1, 'f');
				addEnemy(3, 's');
				addEnemy(3, 'm');
				addEnemy(3, 'f');
				addEnemy(5, 's');
				addEnemy(5, 'm');
				addEnemy(5, 'f');
				addEnemy(6, 's');
				addEnemy(6, 'm');
				addEnemy(6, 'f');
				addEnemy(8, 's');
				addEnemy(8, 'm');
				addEnemy(8, 'f');
				addEnemy(10, 's');
				addEnemy(10, 'm');
				addEnemy(10, 'f');
				addEnemy(14, 'f');
				addEnemy(16, 'f');
				addEnemy(18, 'f');
				addEnemy(26, 'f');
				addEnemy(30, 'm');
				addEnemy(31, 'f');
				addEnemy(34, 'm');
				addEnemy(35, 'm');
				addEnemy(36, 'm');
				addEnemy(37, 'm');
				addEnemy(38, 'm');
				addEnemy(39, 'f');
				addEnemy(44, 'f');
				addEnemy(44, 'm');
				addEnemy(45, 'f');
				addEnemy(45, 'm');
				addEnemy(46, 'f');
				addEnemy(46, 'm');
				addEnemy(49, 's');
				addEnemy(50, 'm');
				addEnemy(50, 'f');
				addEnemy(51, 'm');
				addEnemy(51, 'f');
				addEnemy(52, 'm');
				addEnemy(52, 'f');
				addEnemy(56, 'f');
				addEnemy(57, 'f');
				addEnemy(61, 'f');
				addEnemy(62, 'f');
				addEnemy(65, 'f');
				addEnemy(66, 'f');
				addEnemy(68, 'f');
				addEnemy(69, 'f');
				addEnemy(72, 'f');
				addEnemy(73, 'f');
				addEnemy(75, 'f');
				addEnemy(76, 'f');
				addEnemy(79, 'f');
				addEnemy(80, 'f');
				addEnemy(82, 'f');
				addEnemy(83, 'f');
				addEnemy(86, 'f');
				addEnemy(87, 'f');
				addEnemy(89, 'f');
				addEnemy(90, 'm');
				addEnemy(91, 'f');
				addEnemy(92, 'm');
				addEnemy(93, 'f');
				addEnemy(94, 'm');
				addEnemy(95, 'f');
				addEnemy(96, 'm');
				addEnemy(97, 'f');
				addEnemy(98, 'm');
				addEnemy(99, 'f');
				addEnemy(100, 'm');
				addEnemy(101, 'f');
				addEnemy(102, 'm');
				addEnemy(108, 'f');
				addEnemy(109, 'f');
				addEnemy(110, 's');
				addEnemy(111, 'm');
				addEnemy(112, 'm');
				addEnemy(113, 'f');
				addEnemy(114, 'm');
				addEnemy(115, 'f');
				addEnemy(119, 's');
				addEnemy(121, 's');
				addEnemy(122, 's');
				addEnemy(124, 's');
				addEnemy(125, 's');
				addEnemy(126, 's');
				addEnemy(128, 's');
				addEnemy(129, 's');
				addEnemy(130, 's');
				addEnemy(131, 'm');
				addEnemy(133, 's');
				addEnemy(134, 's');
				addEnemy(135, 's');
				addEnemy(136, 'm');
				addEnemy(137, 'm');
				addEnemy(139, 's');
				addEnemy(140, 's');
				addEnemy(141, 's');
				addEnemy(142, 'm');
				addEnemy(143, 'm');
				addEnemy(144, 'm');
				addEnemy(146, 's');
				addEnemy(147, 's');
				addEnemy(148, 's');
				addEnemy(149, 'm');
				addEnemy(150, 'm');
				addEnemy(151, 'm');
				addEnemy(152, 'f');
				addEnemy(154, 's');
				addEnemy(155, 's');
				addEnemy(156, 's');
				addEnemy(157, 'm');
				addEnemy(158, 'm');
				addEnemy(159, 'm');
				addEnemy(160, 'f');
				addEnemy(161, 'f');
				addEnemy(163, 's');
				addEnemy(164, 's');
				addEnemy(165, 's');
				addEnemy(166, 'm');
				addEnemy(167, 'm');
				addEnemy(168, 'm');
				addEnemy(169, 'f');
				addEnemy(170, 'f');
				addEnemy(171, 'f');
				addEnemy(190, 'b');
				if ((lastLoopTime - startTime) > (200 * 1000) && enemyCount == 0) notifyWin();
			} // if

			//check proximity between all towers and enemies
			if (lastLoopTime % 200 < delta) for (Entity element : entities)
				if (element instanceof TowerEntity && ((TowerEntity) element).ready()) checkProximity(element);

			//add shots
			entities.addAll(addShots);
			addShots.clear();

			// move and draw all entities on screen
			for(Entity element : entities) element.move(delta);
			for(Entity element : entities) element.draw(g);

			// draw enemy health
			for (Entity me : entities) if (me instanceof EnemyEntity) {
				double length = (double) ((EnemyEntity) me).health / ((EnemyEntity) me).totalHealth * 40;
				if (length > 30) g.setColor(Color.GREEN);
				else if (length > 15) g.setColor(Color.YELLOW);
				else g.setColor(Color.RED);
				g.fillRect((int) me.x - 5, (int) me.y - 5, (int) length, 5);
			} // for

			// highlight available grids and draw range while dragging towers
			if(sTowerDragged || mTowerDragged || lTowerDragged) {
				if(grids[getGrid(dragX, dragY)])
					new ImageEntity("images/highlight.png", gridX(getGrid(dragX, dragY)),
							gridY(getGrid(dragX, dragY))).draw(g);
				new ImageEntity("images/range.png", dragX - 75, dragY - 75).draw(g);
			} // if

			// draw sell tower option when hovered
			if (sell != null) sell.draw(g);

			// show enemy info when cursor if over enemy
			for (Entity me : entities) if (me instanceof EnemyEntity) {
				if (mX > me.x - 10 && mX < me.x + 40 && mY > me.y - 10 && mY < me.y + 40) {
					int infoX = (int) me.x - 60;
					int infoY = ((int) me.y < 110) ? (int) me.y + 30 : (int) me.y - 110;
					enemyInfo = true;
					char infoType = ((EnemyEntity) me).getEnemyType();
					super.setCursor(new Cursor(Cursor.HAND_CURSOR));
					new ImageEntity("images/info-" + infoType + ".png", infoX, infoY).draw(g);
					break;
				} else enemyInfo = false;
			} // if

			// show message if user doesn't have enough money
			if(moneyNotEnough && System.currentTimeMillis() - startMessage < 2000) {
				g.setColor(Color.red);
				g.setFont(new Font("Courier New", Font.BOLD, 20));
				g.drawString("NOT ENOUGH MONEY", dragX - 90, dragY + 5);
			} // if

			// draw click and drag images
			if(sTowerDragged) new ImageEntity("images/tower-1.png", dragX - 25, dragY - 25).draw(g);
			if(mTowerDragged) new ImageEntity("images/tower-2.png", dragX - 25, dragY - 25).draw(g);
			if(lTowerDragged) new ImageEntity("images/tower-3.png", dragX - 25, dragY - 25).draw(g);

			// remove dead entities
			entities.removeAll(removeEntities);
			removeEntities.clear();

			// display losing screen
			if (lost) new ImageEntity("images/lost.png", 300, 0).draw(g);

			// display winning screen
			if (won) new ImageEntity("images/won.png", 300, 0).draw(g);

			// clear graphics and flip buffer
			g.dispose();
			strategy.show();

			// slow game down to a reasonable refresh rate
			try {
				Thread.sleep(10);
			} catch (Exception ignored){}
		} // while
	} // gameLoop

	/**check proximity between all towers and enemies and attack one enemy
	 * @param me - the selected tower entity
	 */
	private void checkProximity(Entity me) {
		for(Entity element : entities)
			if (element instanceof EnemyEntity && me.proximity(element)) {
				addShots.add(new ShotEntity(this, "images/shot.png", me, element));
				((TowerEntity) me).lastFireTime = System.currentTimeMillis();
				break;
			} // if
	} // checkProximity

	/**time to move on to the next wave */
	private void nextWave() { // maybe add a bonus to money every new wave
		money += 200;
		timeForNextWave = true;
		wave++;
	} // nextWave

	/**add new enemy to game
	 * @param seconds - how many second after the wave starts should this enemy get generated
	 * @param type - type of enemy
	 */
	private void addEnemy(int seconds, char type) {
		if(lastLoopTime - startTime > seconds * 1000 && lastLoopTime - startTime <= seconds * 1000 + delta) {
			if(type == 's') entities.add(new EnemyEntity(this, "images/enemy-1-3.png", type));
			if(type == 'm') entities.add(new EnemyEntity(this, "images/enemy-2-3.png", type));
			if(type == 'f') entities.add(new EnemyEntity(this, "images/enemy-3-3.png", type));
			if(type == 'b') entities.add(new EnemyEntity(this, "images/enemy-4-3.png", type));
		} // if
	} // addEnemy

	/**waves of enemies */
	private void newWave() {
		timeForNextWave = false;
		startTime = System.currentTimeMillis();
	} // firstWave

	/**add new tower to game
	 * @param grid - grid number of where the tower belongs
	 * @param type - type of tower
	 */
	private void addTower(int grid, String type) {
		if(type.equals("s") && money >= 300) {
			entities.add(new TowerEntity("images/tower-1.png", gridX(grid), gridY(grid), 's'));
			grids[grid] = false;
			money -= 300;
		} else if(type.equals("m") && money >= 800) {
			entities.add(new TowerEntity("images/tower-2.png", gridX(grid), gridY(grid), 'm'));
			grids[grid] = false;
			money -= 800;
		} else if(type.equals("l") && money >= 1500) {
			entities.add(new TowerEntity("images/tower-3.png", gridX(grid), gridY(grid), 'l'));
			grids[grid] = false;
			money -= 1500;
		} else { 
			moneyNotEnough = true;
			startMessage = System.currentTimeMillis();
		} // if else
	} // addTower

	/**player has won by killing all enemies */
	private void notifyWin(){
		for (Entity me : entities) removeEntity(me);
		won = true;
		wave = 0;
	} // notifyWin

	/**player has lost because enemies reached the bottom */
	void notifyDeath(){
		for (Entity me : entities) removeEntity(me);	
		lost = true;
		wave = 0;
	} // notifyDeath

	/**remove an entity from the game
	 * @param entity - any entities that needs to be removed immediately
	 */
	void removeEntity(Entity entity){
		removeEntities.add(entity);
	} // removeEntity

	/**getting the grid number of a pair of x and y coordinates
	 * @param x - x coordinate
	 * @param y - y coordinate
	 * @return - the grid number this pair of coordinates belong to
	 */
	private int getGrid(int x, int y) {
		if(y >= 0 && y < 600 && x >= 300 & x < 900) return ((x - 300) / 50) + ((y / 50) * 12) + 1;
		else return 0;
	} // getGrid

	/**getting the x coordinate of a certain grid
	 * @param grid - grid number 1 - 144
	 * @return - the starting x coordinate of this grid
	 */
	private int gridX(int grid) {
		if(grid % 12 == 0) return 850;
		else return (grid % 12) * 50 + 250;
	} // getGridX

	/**getting the y coordinate of a certain grid
	 * @param grid - grid number 1 - 144
	 * @return - the starting y coordinate of this grid
	 */
	private int gridY(int grid) {
		if(grid % 12 == 0) return (grid / 12) * 50 - 50;
		else return (grid / 12) * 50;
	} // getGridY

	/**mouse events handlers
	 * - react to different mouse events
	 * - takes care of drag animation
	 * - takes care of hover animation
	 * - does functions when button is clicked
	 * - tracks cursor location
	 * @param e - mouse event
	 */
	public void mousePressed(MouseEvent e) {
		dragX = e.getX();
		dragY = e.getY();
		moneyNotEnough = false;
		if (wave != 0) {
			if (e.getX() > 910 && e.getX() < 1190 && e.getY() > 310 && e.getY() < 390) sTowerDragged = true;
			if (e.getX() > 910 && e.getX() < 1190 && e.getY() > 410 && e.getY() < 490) mTowerDragged = true;
			if (e.getX() > 910 && e.getX() < 1190 && e.getY() > 510 && e.getY() < 590) lTowerDragged = true;
		} // if
	} // mousePressed

	public void mouseReleased(MouseEvent e) {
		// start game when start button is clicked
		if (wave == 0) {
			if (e.getX() > 910 && e.getX() < 1190 && e.getY() > 10 && e.getY() < 290) {
				wave++;
				if (won || lost) {
					startTime = System.currentTimeMillis();
					lost = false;
					won = false;
					sell = null;
					money = 600;
					grids = setGrids();
				} // restart game
			} // if
		} // if

		// sell tower if button pressed
		if (sell != null) {
			if (e.getX() > sell.x && e.getX() < sell.x + 150 && e.getY() > sell.y && e.getY() < sell.y + 50) {
				money += 100;
				removeEntities.add(sellTower);
				grids [getGrid((int) sell.x + 75, (int) sell.y - 25)] = true;
			} // if
			sell = null;
			sellTower = null;
		} // if

		// highlight available grids while dragging towers
		if(sTowerDragged) if(grids[getGrid(dragX, dragY)]) addTower(getGrid(dragX, dragY), "s");
		if(mTowerDragged) if(grids[getGrid(dragX, dragY)]) addTower(getGrid(dragX, dragY), "m");
		if(lTowerDragged) if(grids[getGrid(dragX, dragY)]) addTower(getGrid(dragX, dragY), "l");

		// reset drag and hover values
		sTowerDragged = false;
		mTowerDragged = false;
		lTowerDragged = false;
		sTowerHovered = false;
		mTowerHovered = false;
		lTowerHovered = false;

		// reset cursor
		super.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	} // mouseReleased

	public void mouseMoved(MouseEvent e) {

		// update mouse move coordinates
		mX = e.getX();
		mY = e.getY();

		// color and cursor change when buttons are hovered
		if (wave == 0) {
			if (mX > 910 && mX < 1190 && mY > 10 && mY < 290) {
				startHovered = true;
				super.setCursor(new Cursor(Cursor.HAND_CURSOR));
			} else {
				startHovered = false;
				super.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			} // if else
		} else {
			if (mX > 910 && mX < 1190 && mY > 310 && mY < 390) {
				sTowerHovered = true;
				super.setCursor(new Cursor(Cursor.HAND_CURSOR));
			} else if (mX > 910 && mX < 1190 && mY > 410 && mY < 490) {
				mTowerHovered = true;
				super.setCursor(new Cursor(Cursor.HAND_CURSOR));
			} else if (mX > 910 && mX < 1190 && mY > 510 && mY < 590) {
				lTowerHovered = true;
				super.setCursor(new Cursor(Cursor.HAND_CURSOR));
			} else {
				sTowerHovered = false;
				mTowerHovered = false;
				lTowerHovered = false;

				// display tower range & show enemy info
				for (Entity me : entities) if (me instanceof TowerEntity) {
					if (mX > me.x && mX < me.x + 50 && mY > me.y && mY < me.y + 50) {
						int sellX = (int) me.x - 50;
						int sellY = ((int) me.y < 550) ? (int) me.y + 50 : (int) me.y - 50;
						sell = new ImageEntity("images/sell.png", sellX, sellY);
						sellTower = me;
						break;
					} // if
				} // if

				// highlight sell tower button when hovered
				if (sell != null) {
					if (mX > sell.x && mX < sell.x + 150 && mY > sell.y && mY < sell.y + 50) {
						sell = new ImageEntity("images/sell-h.png", (int) sell.x, (int) sell.y);
						super.setCursor(new Cursor(Cursor.HAND_CURSOR));
					} else {
						sell = new ImageEntity("images/sell.png", (int) sell.x, (int) sell.y);
						super.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					} // if else

					// clear sell button when cursor moves away
					if (!(mX > sell.x && mX < sell.x + 150 && mY > sell.y - 50 && mY < sell.y + 100)) sell = null;
				} // if

				// reset cursor to default if no button is hovered
				if (sell == null && !enemyInfo) super.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			} // if else
		} // if else
	} // mouseMoved

	public void mouseDragged(MouseEvent e) {
		dragX = e.getX();
		dragY = e.getY();
		startHovered = false;
	} // mouseDragged

	public void mouseExited(MouseEvent e) {
		startHovered = false;
		moneyNotEnough = false;
		if(!sTowerDragged) sTowerHovered = false;
		if(!mTowerDragged) mTowerHovered = false;
		if(!lTowerDragged) lTowerHovered = false;
	} // mouseExited

	public void mouseClicked(MouseEvent e) {} // mouseClicked

	public void mouseEntered(MouseEvent e) {} // mouseEntered
} // Game