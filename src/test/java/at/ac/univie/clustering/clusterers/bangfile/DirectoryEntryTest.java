package at.ac.univie.clustering.clusterers.bangfile;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public class DirectoryEntryTest extends TestCase {

	public void testCreateBuddySplitRoot() {
		DirectoryEntry dirEntry = new DirectoryEntry();

		dirEntry.setRegion(new GridRegion(0, 0));

		dirEntry.createBuddySplit();

		assertEquals(0, dirEntry.getLeft().getRegion().getRegion());
		assertEquals(1, dirEntry.getLeft().getRegion().getLevel());

		assertEquals(1, dirEntry.getRight().getRegion().getRegion());
		assertEquals(1, dirEntry.getRight().getRegion().getLevel());
	}

	public void testDoBuddySplitSub() {
		DirectoryEntry dirEntry = new DirectoryEntry();

		dirEntry.setRegion(new GridRegion(3, 2));

		dirEntry.createBuddySplit();

		assertEquals(3, dirEntry.getLeft().getRegion().getRegion());
		assertEquals(3, dirEntry.getLeft().getRegion().getLevel());

		assertEquals(7, dirEntry.getRight().getRegion().getRegion());
		assertEquals(3, dirEntry.getRight().getRegion().getLevel());
	}

	public void testClearBuddySplit(){
		DirectoryEntry dirEntry = new DirectoryEntry();

		dirEntry.setRegion(new GridRegion(0, 0));

		dirEntry.createBuddySplit();
		
		dirEntry.clearBuddySplit();
		
		assertEquals(null, dirEntry.getLeft());
		assertEquals(null, dirEntry.getRight());
	}

	public void testMoveToRight() {
		DirectoryEntry dirEntry = new DirectoryEntry();
		dirEntry.setRegion(new GridRegion(3, 2));

		dirEntry.getRegion().insertTuple(new double[] { 0.1, 0.1 });
		dirEntry.getRegion().insertTuple(new double[] { 0.2, 0.2 });

		dirEntry.moveToRight();

		assertEquals(null, dirEntry.getRegion());
		assertEquals(null, dirEntry.getLeft());

		assertEquals(7, dirEntry.getRight().getRegion().getRegion());
		assertEquals(3, dirEntry.getRight().getRegion().getLevel());
		assertEquals(2, dirEntry.getRight().getRegion().getPopulation());
	}

	public void testMoveToLeft() {
		DirectoryEntry dirEntry = new DirectoryEntry();
		dirEntry.setRegion(new GridRegion(3, 2));

		dirEntry.getRegion().insertTuple(new double[] { 0.1, 0.1 });
		dirEntry.getRegion().insertTuple(new double[] { 0.2, 0.2 });

		dirEntry.moveToLeft();

		assertEquals(null, dirEntry.getRegion());
		assertEquals(null, dirEntry.getRight());

		assertEquals(3, dirEntry.getLeft().getRegion().getRegion());
		assertEquals(3, dirEntry.getLeft().getRegion().getLevel());
		assertEquals(2, dirEntry.getLeft().getRegion().getPopulation());
	}

	public void testGetSparseEntry() {
		DirectoryEntry dirEntry = new DirectoryEntry();
		dirEntry.setRegion(new GridRegion(0, 0));
		dirEntry.createBuddySplit();

		dirEntry.getLeft().getRegion().insertTuple(new double[] { 0.1, 0.1 });

		assertEquals(dirEntry.getRight(), dirEntry.getSparseEntry());

		dirEntry.getRight().getRegion().insertTuple(new double[] { 0.1, 0.1 });
		dirEntry.getRight().getRegion().insertTuple(new double[] { 0.1, 0.1 });

		assertEquals(dirEntry.getLeft(), dirEntry.getSparseEntry());
	}

	public void testGetDenseEntry() {
		DirectoryEntry dirEntry = new DirectoryEntry();
		dirEntry.setRegion(new GridRegion(0, 0));
		dirEntry.createBuddySplit();

		dirEntry.getLeft().getRegion().insertTuple(new double[] { 0.1, 0.1 });

		assertEquals(dirEntry.getLeft(), dirEntry.getDenseEntry());

		dirEntry.getRight().getRegion().insertTuple(new double[] { 0.1, 0.1 });
		dirEntry.getRight().getRegion().insertTuple(new double[] { 0.1, 0.1 });

		assertEquals(dirEntry.getRight(), dirEntry.getDenseEntry());
	}

	public void testClearSucceedingEntry() {
		DirectoryEntry dirEntry = new DirectoryEntry();
		dirEntry.setRegion(new GridRegion(0, 0));
		dirEntry.createBuddySplit();

		dirEntry.getLeft().getRegion().insertTuple(new double[] { 0.1, 0.1 });

		dirEntry.clearSucceedingEntry(dirEntry.getRight());

		assertEquals(null, dirEntry.getRight());
	}

	public void testCalculateDensity() {
        DirectoryEntry dirEntry = new DirectoryEntry();
        dirEntry.setRegion(new GridRegion(0, 0));

        dirEntry.setLeft(new DirectoryEntry());
        dirEntry.getLeft().setLeft(new DirectoryEntry());

        dirEntry.getLeft().getLeft().setRegion(new GridRegion(0, 2));

        dirEntry.getRegion().insertTuple(new double[] { 0.1, 0.1 });

        dirEntry.getLeft().getLeft().getRegion().insertTuple(new double[] { 0.1, 0.1 });
        dirEntry.getLeft().getLeft().getRegion().insertTuple(new double[] { 0.1, 0.1 });

        dirEntry.calculateDensity();
        //main is size 0,75; main->left->left is size 0,25

        assertEquals(1.333, dirEntry.getRegion().getDensity(), 0.001);

        assertEquals(8, dirEntry.getLeft().getLeft().getRegion().getDensity(), 0);
	}

    public void testGetRegionSize() {
        DirectoryEntry dirEntry = new DirectoryEntry();
        dirEntry.setRegion(new GridRegion(0, 0));

        dirEntry.setLeft(new DirectoryEntry());
        dirEntry.getLeft().setLeft(new DirectoryEntry());

        dirEntry.getLeft().getLeft().setRegion(new GridRegion(0, 2));

        dirEntry.getRegion().insertTuple(new double[] { 0.1, 0.1 });

        dirEntry.getLeft().getLeft().getRegion().insertTuple(new double[] { 0.1, 0.1 });
        dirEntry.getLeft().getLeft().getRegion().insertTuple(new double[] { 0.1, 0.1 });

        assertEquals(1, dirEntry.getRegionSize(), 0);
        assertEquals(0.25, dirEntry.getLeft().getLeft().getRegionSize(), 0);
    }

    public void testCollectRegions(){
        DirectoryEntry dirEntry = new DirectoryEntry();
        dirEntry.setRegion(new GridRegion(0, 0));
        dirEntry.createBuddySplit();

        dirEntry.getLeft().createBuddySplit();

        List<GridRegion> regionArray = new ArrayList<GridRegion>();
        dirEntry.collectRegions(regionArray);

        assertEquals(5, regionArray.size());

        assertTrue(regionArray.contains( dirEntry.getRegion()));

        assertTrue(regionArray.contains( dirEntry.getLeft().getRegion()));
        assertTrue(regionArray.contains( dirEntry.getRight().getRegion()));

        assertTrue(regionArray.contains( dirEntry.getLeft().getLeft().getRegion()));
        assertTrue(regionArray.contains( dirEntry.getLeft().getRight().getRegion()));
    }

}
