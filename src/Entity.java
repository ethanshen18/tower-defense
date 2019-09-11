import java.awt.*;

/**Entity
 * - this is a abstract class for all visual elements
 * - moves all visual elements and draw them
 * - checks proximity between towers and enemies
 * - changes enemy sprites and they turn
 */
abstract class Entity {

	private Sprite sprite; // sprite
	double x; // x coordinate
	double y; // y coordinate
	double dx; // horizontal speed
	double dy; // vertical speed

	/**constructor
	 * @param r - sprite location reference
	 * @param newX - x coordinate of element
	 * @param newY - y coordinate of element
	 */
	Entity(String r, int newX, int newY) {
		this.x = newX;
		this.y = newY;
		this.sprite = (SpriteStore.get()).getSprite(r);
	} // Entity

	/**change enemy sprite direction when it turns
	 * @param r - the new enemy sprite
	 */
	void setEnemyOrientation(String r) {
		sprite = (SpriteStore.get()).getSprite(r);
	}// setEnemyOrientation

	/**update movement
	 * @param delta - frame interval
	 */
	public void move (long delta) {
		x += (dx * delta) / 1000;
		y += (dy * delta) / 1000;
	} // move

	/**draw elements at their current location
	 * @param g - graphics object
	 */
	void draw(Graphics g) {
		sprite.draw(g, (int) x, (int) y);
	} // draw

	/**check proximity between tower and enemy
	 * @param other - the enemy
	 * @return - whether the enemy is within the range of the tower
	 */
	boolean proximity(Entity other) {
		double tx = this.x + 25;
		double mx = other.x + 15;
		double ty = this.y + 25;
		double my = other.y + 15;
		double distance = Math.sqrt(((tx - mx) * (tx - mx)) + ((ty - my) * (ty - my)));
		return distance < 80;
	} // proximity
} // Entity class
