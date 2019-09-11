import java.awt.Graphics;
import java.awt.Image;

/**Sprite
 * - adds a new sprite
 * - draws the sprite
 */
class Sprite {
	private Image image; // image object

	/**create new sprite
	 * @param i - sprite object
	 */
	Sprite(Image i) {
		image = i;
	} // Sprite

	/**draw the sprite in the graphics object
	 * @param g - graphics object
	 * @param x - x coordinate of sprite
	 * @param y - y coordinate of sprite
	 */
	void draw(Graphics g, int x, int y){
		g.drawImage(image, x, y, null);
	} // draw
} // Sprite class
