package weka.clusterers;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests BANGFile. Run from the command line with:<p/>
 * java weka.clusterers.BANGFileTest
 *
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public class BANGFileTest extends AbstractClustererTest {

    public BANGFileTest(String name) {
        super(name);
    }

    /** Creates a default BANGFile */
    public Clusterer getClusterer() {
        return new BANGFile();
    }

    public static Test suite() {
        return new TestSuite(SimpleKMeansTest.class);
    }

    public static void main(String[] args){
        junit.textui.TestRunner.run(suite());
    }
}
