import org.junit.*;
import static org.junit.Assert.*;

public class RobberTest {

    @Test
    public void testGetLocationSimple(){
        Tile tile = new Tile(10, Resources.BRICK, 8);
        Robber robber = new Robber(tile);
        assertEquals("Simple getRobberLocation test", tile, robber.getLocation());
    }

    @Test
    public void testGetLocationAfterMoved(){
        Tile tile1 = new Tile(10, Resources.WOOD, 9);
        Robber robber = new Robber(tile1);

        Tile tile2 = new Tile(7, Resources.ORE, 3);
        robber.moveRobber(tile2);

        assertEquals("getRobberLocation after moving it", tile2, robber.getLocation());
    }

    @Test
    public void testStealResource(){
        Agent stealer = new ComputerAgent(1, 2);
        Agent victim = new ComputerAgent(4, 3);
        victim.addResource(Resources.ORE, 1);

        Tile tile = new Tile(4, Resources.SHEEP, 10);
        Robber robber = new Robber(tile);
        robber.stealResource(stealer, victim);

        assertEquals("Stealing resources stealer hand size", 0, stealer.handSize());
        assertEquals("Stealing resources victim hand size", 1, victim.handSize());
    }

    @Test
    public void testBlockResource(){
        Tile tile = new Tile(4, Resources.WHEAT, 9);
        Robber robber = new Robber(tile);
        Tile tile2 = new Tile(5, Resources.BRICK, 3);
        assertEquals("Blocking a tile", true, robber.blockResource(tile));
        assertEquals("Not blocking tile", false, robber.blockResource(tile2));
    }
}
