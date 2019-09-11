/**EnemyEntity
 * - extends Entity class
 * - this class adds enemies to the game
 * - defines the type of enemy
 * - each type of enemy has different speeds and healths
 * - set the path of enemies
 * - tells the game when to turn an enemy
 * - change sprite when enemy is turned
 * - moves the enemy
 */
public class EnemyEntity extends Entity {

    private Game game; // the game this enemy is in
    private char enemyType; // type of enemy
    private double speed; // speed of enemy
    int health; // current health of enemy (accessible from main)
    int totalHealth; // total health of enemy (accessible from main)

    /**constructor
     * @param g - the game
     * @param r - sprite location reference
     * @param type - the type of enemy
     */
    EnemyEntity(Game g, String r, char type) {

        // set initial location of enemy
        super(r, 610, -30);

        // set speeds
        if (type == 's') speed = 30;
        if (type == 'm') speed = 50;
        if (type == 'f') speed = 70;
        if (type == 'b') speed = 20;

        // set healths
        if (type == 's') totalHealth = health = 3;
        if (type == 'm') totalHealth = health = 5;
        if (type == 'f') totalHealth = health = 8;
        if (type == 'b') totalHealth = health = 150;

        // initialize class variables
        enemyType = type;
        game = g;
        dx = 0;
        dy = speed;
    } // EnemyEntity

    /**get the type of enemy from main
     * @return - the type of enemy as char
     */
    char getEnemyType() {
        return enemyType;
    } // getEnemyType

    /**update enemy location based on the set path
     * @param delta - frame interval
     */
    public void move (long delta) {
        if(x == 610 && y > 60 && y < 70) {
            y = 60;
            goLeft();
        } else if(x > 350 && x < 360 && y == 60) {
            x = 360;
            goDown();
        } else if(x == 360 && y > 510 && y < 520) {
            y = 510;
            goRight();
        } else if(x > 660 && x < 670 && y == 510) {
            x = 660;
            goUp();
        } else if(x == 660 && y < 260 && y > 250) {
            y = 260;
            goLeft();
        } else if(x < 560 && x > 550 && y == 260) {
            x = 560;
            goDown();
        } else if(x == 560 && y > 410 && y < 420) {
            y = 410;
            goLeft();
        } else if(x < 460 && x > 450 && y == 410) {
            x = 460;
            goUp();
        } else if(x == 460 && y < 160 && y > 150) {
            y = 160;
            goRight();
        } else if(x > 810 && x < 820 && y == 160) {
            x = 810;
            goDown();
        } // if else
        if(y > 600) game.notifyDeath();
        super.move(delta);
    } // move

    /**change enemy direction */
    private void goUp() {
        dx = 0;
        dy = -speed;
        if (enemyType == 's') setEnemyOrientation("images/enemy-1-1.png");
        if (enemyType == 'm') setEnemyOrientation("images/enemy-2-1.png");
        if (enemyType == 'f') setEnemyOrientation("images/enemy-3-1.png");
        if (enemyType == 'b') setEnemyOrientation("images/enemy-4-1.png");
    } // goUp

    /**change enemy direction */
    private void goRight() {
        dx = speed;
        dy = 0;
        if (enemyType == 's') setEnemyOrientation("images/enemy-1-2.png");
        if (enemyType == 'm') setEnemyOrientation("images/enemy-2-2.png");
        if (enemyType == 'f') setEnemyOrientation("images/enemy-3-2.png");
        if (enemyType == 'b') setEnemyOrientation("images/enemy-4-2.png");
    } // goRight

    /**change enemy direction */
    private void goDown() {
        dx = 0;
        dy = speed;
        if (enemyType == 's') setEnemyOrientation("images/enemy-1-3.png");
        if (enemyType == 'm') setEnemyOrientation("images/enemy-2-3.png");
        if (enemyType == 'f') setEnemyOrientation("images/enemy-3-3.png");
        if (enemyType == 'b') setEnemyOrientation("images/enemy-4-3.png");
    } // goDown

    /**change enemy direction */
    private void goLeft() {
        dx = -speed;
        dy = 0;
        if (enemyType == 's') setEnemyOrientation("images/enemy-1-4.png");
        if (enemyType == 'm') setEnemyOrientation("images/enemy-2-4.png");
        if (enemyType == 'f') setEnemyOrientation("images/enemy-3-4.png");
        if (enemyType == 'b') setEnemyOrientation("images/enemy-4-4.png");
    } // goLeft
} // EnemyEntity class
