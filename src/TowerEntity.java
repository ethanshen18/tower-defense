/**TowerEntity
 * - this class adds towers to the game
 * - defines the type of tower
 * - each type of tower has a different firing interval
 */
class TowerEntity extends Entity {

    private int towerType; // type of tower
    long lastFireTime; // time of last fire (accessible from main)

    /**constructor
     * @param r - sprite location reference
     * @param x - tower x coordinate
     * @param y - tower y coordinate
     * @param type - the type of tower
     */
    TowerEntity(String r, int x, int y, char type) {
        super(r, x, y);
        if (type == 's') towerType = 1;
        if (type == 'm') towerType = 2;
        if (type == 'l') towerType = 3;
    } // TowerEntity

    /**make tower fire if time meets minimum firing interval
     * @return - whether the tower is ready to fire
     */
    boolean ready() {
        return towerType == 1 && System.currentTimeMillis() - lastFireTime > 4000
            || towerType == 2 && System.currentTimeMillis() - lastFireTime > 2000
            || towerType == 3 && System.currentTimeMillis() - lastFireTime > 800;
    } // fireWhenReady
} // TowerEntity class
