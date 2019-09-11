import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**SpriteStore
 * - stores sprites
 * - draws the sprite
 */
class SpriteStore {

    /**one instance of this class will exist this instance will be accessed by Game.java */
    private static SpriteStore single = new SpriteStore();
    private HashMap <String, Sprite> sprites = new HashMap <>();

    /**returns the single instance of this class */
    static SpriteStore get() {
        return single;
    } // get

    /**to return a specific sprite
     * @param ref - a string specifying which sprite image is required
     * @return - a sprite instance containing an accelerated image of the requested image
     */
    Sprite getSprite(String ref) {

        // return sprite if it is already in the HashMap
        if (sprites.get(ref) != null) {
          return sprites.get(ref);
        } // if

        // else load the image into the HashMap off the hard drive
        BufferedImage sourceImage = null;

        try {
            // get the image location
            URL url = this.getClass().getClassLoader().getResource(ref);
            if (url == null) {
              System.out.println("Failed to load: " + ref);
              System.exit(0); // exit program if file not found
            }
            sourceImage = ImageIO.read(url); // get image
        } catch (IOException e) {
            System.out.println("Failed to load: " + ref);
            System.exit(0); // exit program if file not loaded
        } // catch

        // create an accelerated image (correct size) to store our sprite in
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration();
        Image image = gc.createCompatibleImage(sourceImage.getWidth(), sourceImage.getHeight(), Transparency.BITMASK);

        // draw our source image into the accelerated image
        image.getGraphics().drawImage(sourceImage, 0, 0, null);

        // create a sprite, add it to the cache and return it
        Sprite sprite = new Sprite(image);
        sprites.put(ref, sprite);

        return sprite;
    } // getSprite
} // SpriteStore
