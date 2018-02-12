package at.ac.univie.clustering.clusterers.bangfile;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public class BANGFileTest extends TestCase {

    public void testMapRegion() {
        BANGFile bangFile = new BANGFile(2, 4, 1, 50);
        bangFile.setDimensionLevels( new int[] {2, 1, 1});
        /*
            ---------
            |2,2 |3,2|
            |----|---|
            |0,2 |1,2|
            ---------
         */

        assertEquals(0, bangFile.mapRegion(new double[] {0.1, 0.1}));
        assertEquals(1, bangFile.mapRegion(new double[] {0.6, 0.1}));
        assertEquals(2, bangFile.mapRegion(new double[] {0.1, 0.6}));
        assertEquals(3, bangFile.mapRegion(new double[] {0.6, 0.6}));

        bangFile.setDimensionLevels( new int[] {3, 2, 1});
        /*
            --------------------
            |2,3 |6,3 |3,3 |7,3 |
            |----|----|----|----|
            |0,3 |4,3 |1,3 |5,3 |
            --------------------
         */

        assertEquals(0, bangFile.mapRegion(new double[] {0.1, 0.1}));
        assertEquals(4, bangFile.mapRegion(new double[] {0.3, 0.1}));
        assertEquals(1, bangFile.mapRegion(new double[] {0.6, 0.1}));
        assertEquals(5, bangFile.mapRegion(new double[] {0.8, 0.1}));
        assertEquals(2, bangFile.mapRegion(new double[] {0.1, 0.6}));
        assertEquals(6, bangFile.mapRegion(new double[] {0.3, 0.6}));
        assertEquals(3, bangFile.mapRegion(new double[] {0.6, 0.6}));
        assertEquals(7, bangFile.mapRegion(new double[] {0.8, 0.6}));
    }

    public void testInsertTuple() {
        ArrayList<double[]> tuples = new ArrayList<>();
        tuples.add(new double[] { 0.1, 0.2 });
        tuples.add(new double[] { 0.2, 0.3 });
        tuples.add(new double[] { 0.3, 0.4 });

        BANGFile bangFile = new BANGFile(2, 4, 1, 50);

        for (double[] tuple : tuples) {
            bangFile.insertTuple(tuple);
        }

        DirectoryEntry file = (DirectoryEntry) bangFile.getRootDirectory();

        assertEquals(3, file.getRegion().getPopulation());
        assertEquals(tuples, file.getRegion().getTupleList());
    }

    public void testNumberOfTuples() {
        BANGFile bangFile = new BANGFile(2, 4, 1, 50);
        assertEquals(0, bangFile.numberOfTuples());
        double[] tuple;
        for(int x = 0; x < 100; x++){
            for(int y = 0; y < 100; y++){
                tuple = new double[] {x/100.0f, y/100.0f};
                bangFile.insertTuple(tuple);
            }
        }
        assertEquals(10000, bangFile.numberOfTuples());
    }

    public void testBuddySplit() {
        ArrayList<double[]> tuples = new ArrayList<>();
        tuples.add(new double[] { 0.1f, 0.1f });
        tuples.add(new double[] { 0.2f, 0.1f });
        tuples.add(new double[] { 0.3f, 0.1f });
        tuples.add(new double[] { 0.4f, 0.1f });
        tuples.add(new double[] { 0.7f, 0.1f });
        tuples.add(new double[] { 0.8f, 0.1f });

        BANGFile bangFile = new BANGFile(2, 4, 1, 50);

        for (double[] tuple : tuples) {
            bangFile.insertTuple(tuple);
        }

        DirectoryEntry file = (DirectoryEntry) bangFile.getRootDirectory();

        assertEquals(4, file.getRegion().getPopulation());
        assertEquals(2, file.getLeft().getLeft().getLeft().getRegion().getPopulation());
    }
}
