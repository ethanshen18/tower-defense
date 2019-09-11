/**ShotEntity
 * - extends Entity class
 * - determines the speed of shot based on the distance between tower and enemy
 * - moves the shot
 * - kills the shot
 * - reduce enemy health
 * - kills enemy if health becomes zero
 */
public class ShotEntity extends Entity{

    private long initTime = System.currentTimeMillis(); // the time when this shot was generated
    private Entity tower; // the parent tower of this shot
    private Entity enemy; // target enemy of this shot
    private Game game; // the game this shot is in

    /**constructor
     * @param g - the game
     * @param r - sprite location reference
     * @param me - the tower which this shot is fired from
     * @param other - the enemy which this shot is going to hit
     */
    ShotEntity(Game g, String r, Entity me, Entity other){
        super(r, (int) me.x + 10, (int) me.y + 10);
        this.game = g;
        tower = me;
        enemy = other;
    } // ShotEntity

    /**calculate shot speed based its distance from enemy
     * moves the shot
     * kills the shot
     * reduce enemy health
     * kills enemy if health becomes zero
     * @param delta - frame interval
     */
    public void move (long delta){
        if (System.currentTimeMillis() - initTime > 120) {

            // kill shot and reduce enemy health after being hit
            ((EnemyEntity) enemy).health -= 1;
            game.removeEntity(this);

            // kill enemy when health becomes 0
            if (((EnemyEntity) enemy).health == 0) game.removeEntity(enemy);
        } // if

        // calculate shot speed based its distance from enemy
        dx = (enemy.x - tower.x - 10) * 5;
        dy = (enemy.y - tower.y - 10) * 5;

        // move shot
        super.move(delta);
    } // move
} // ShotEntity class
