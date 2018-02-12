package weka.clusterers;

import weka.core.AttributeStats;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <!-- globalinfo-start -->
 * The BANG file is a multidimensional structure of the grid file type.<br>
 * It organizes the value space surrounding the data values, instead of comparing the data values themselves.<br>
 * Its tree structured directory partitions the data space into block regions with successive binary divisions on dimensions.<br>
 * The clustering algorithm identifies densely populated regions as cluster centers and expands those with neighboring blocks.<br><br>
 * Inserted data has to be normalized to an interval [0, 1].
 * <p>
 * <!-- globalinfo-end -->
 *
 * <!-- technical-bibtex-start -->
 * <!-- technical-bibtex-end -->
 *
 * <!-- options-start -->
 * Valid options are:
 * <p>
 *
 * -S &lt;num&gt;<br>
 * The max population of a single data bucket can, if smaller, yield more accurate clusters for
 * the cost of performance depending on the size of the dataset
 * (default = 100)
 * <p>
 *
 * -N &lt;num&gt;<br>
 * Provided neighborhood-margin reduces number of touching dimensions required when expanding cluster
 * with adjacent regions; strictest possible value is 1
 * (default = 1)
 * <p>
 *
 * -C &lt;num&gt;<br>
 * Percentage of data and regions to consider when expanding cluster around cluster centers; a value to
 * high may cause clusters to be merged
 * (default = 50)
 * <p>
 *
 * -D <br>
 * If set, clusterer is run in debug mode and may output additional info to
 * the console.
 * <p>
 *
 * -do-not-check-capabilities <br>
 * If set, clusterer capabilities are not checked before clusterer is built
 * (use with caution).
 * <p>
 *
 * <!-- options-end -->
 *
 * @author Florian Fritz (florian.fritzi@gmail.com)
 * @version 1.0
 */
public class BANGFile extends AbstractClusterer implements TechnicalInformationHandler {

    protected Normalize normalize;

    /* Number of dimensions in data-set */
    private int dimensions;
    /* Max number of tuples within a single region */
    private int bucketsize = 100;
    /* Reduces number of touching dimensions required when expanding cluster */
    private int neighborMargin = 1;
    /* Amount of tuples to be included in clusters */
    private int clusterPercent = 50;

    /* BANG file object that manages the BANG-file directory structure and balance instance distribution */
    private at.ac.univie.clustering.clusterers.bangfile.BANGFile bangFile;

    public BANGFile() {
        super();
    }

    /**
     * Get the max population of a single data bucket.
     *
     * @return max population of bucket
     */
    public int getBucketsize() {
        return bucketsize;
    }

    /**
     * Set the max population of a single data bucket.
     *
     * @param bucketsize max population of bucket
     */
    public void setBucketsize(int bucketsize) {
        if (bucketsize < 4){
            throw new IllegalArgumentException("Bucketsize may not be lower than 4");
        }
        this.bucketsize = bucketsize;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     *         explorer/experimenter gui
     */
    public String bucketsizeTipText() {
        return "The max population of a single data bucket can, if smaller, yield more accurate clusters for " +
                "the cost of performance depending on the size of the dataset (default = 100)";
    }

    /**
     * Get neighborhood-margin, reducing number of touching dimensions required when expanding cluster.
     *
     * @return  margin when determining neighbors
     */
    public int getNeighborMargin() {
        return neighborMargin;
    }

    /**
     * Set neighborhood-margin, reducing number of touching dimensions required when expanding cluster.
     *
     * @param neighborMargin  margin when determining neighbors
     */
    public void setNeighborMargin(int neighborMargin) {
        if (neighborMargin < 1){
            throw new IllegalArgumentException("Neighborhood-Margin may not be smaller than 1");
        }
        this.neighborMargin = neighborMargin;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     *         explorer/experimenter gui
     */
    public String neighborMarginTipText() {
        return "Provided neighborhood-margin reduces number of touching dimensions required when expanding " +
                "cluster with adjacent regions; strictest possible value is 1 (default = 1)";
    }

    /**
     * Get percentage of tuples to be clustered.
     *
     * @return  percentage of tuples to cluster
     */
    public int getClusterPercent() {
        return clusterPercent;
    }

    /**
     * Set percentage of tuples to be clustered.
     *
     * @param clusterPercent percentage of tuples to cluster
     */
    public void setClusterPercent(int clusterPercent) {
        if (clusterPercent < 0 || clusterPercent > 100){
            throw new IllegalArgumentException("Cluster-Percent must be between 0 and 100");
        }
        this.clusterPercent = clusterPercent;
    }

    /**
     * Returns the tip text for this property
     *
     * @return tip text for this property suitable for displaying in the
     *         explorer/experimenter gui
     */
    public String clusterPercentTipText() {
        return "Percentage of data and regions to consider when expanding cluster around cluster centers; a value to " +
                "high may cause clusters to be merged (default = 50)";
    }

    /**
     * Returns an enumeration describing the available options.
     *
     * @return an enumeration of all the available options.
     */
    @Override
    public Enumeration<Option> listOptions() {
        Vector<Option> result = new Vector<Option>();

        result.addElement(new Option(bucketsizeTipText(), "S", 1, "-S <num>"));

        result.addElement(new Option(neighborMarginTipText(),
                "N", 1, "-N <num>"));

        result.addElement(new Option(clusterPercentTipText(),
                "C", 1, "-C <num>"));

        result.addAll(Collections.list(super.listOptions()));
        return result.elements();
    }

    /**
     * Parses a given list of options.
     * <p>
     *
     * <!-- options-start -->
     * Valid options are:
     * <p>
     *
     * -S &lt;num&gt;<br>
     * The max population of a single data bucket can, if smaller, yield more accurate clusters for
     * the cost of performance depending on the size of the dataset.
     * (default = 10)
     * <p>
     *
     * -N &lt;num&gt;<br>
     * Provided neighborhood-margin reduces number of touching dimensions required when expanding cluster
     * with adjacent regions; strictest possible value is 1
     * (default = 1)
     * <p>
     *
     * -C &lt;num&gt;<br>
     * Percentage of data and regions to consider when expanding cluster around cluster centers; a value to
     * high may cause clusters to be merged.
     * (default = 50)
     * <p>
     *
     * -D <br>
     * If set, clusterer is run in debug mode and may output additional info to
     * the console.
     * <p>
     *
     * -do-not-check-capabilities <br>
     * If set, clusterer capabilities are not checked before clusterer is built
     * (use with caution).
     * <p>
     *
     * <!-- options-end -->
     *
     * @param options the list of options as an array of strings
     * @exception Exception if an option is not supported
     */
    @Override
    public void setOptions(String[] options) throws Exception {
        String optionString = Utils.getOption('S', options);

        if (optionString.length() > 0) {
            setBucketsize(Integer.parseInt(optionString));
        }

        optionString = Utils.getOption('N', options);
        if (optionString.length() > 0) {
            setNeighborMargin(Integer.parseInt(optionString));
        }

        optionString = Utils.getOption('C', options);
        if (optionString.length() > 0) {
            setClusterPercent(Integer.parseInt(optionString));
        }

        super.setOptions(options);
        Utils.checkForRemainingOptions(options);
    }

    /**
     * Gets the current settings of the clusterer.
     *
     * @return an array of strings suitable for passing to setOptions
     */
    @Override
    public String[] getOptions() {
        Vector<String> options = new Vector<String>();

        options.add("-S");
        options.add("" + getBucketsize());
        options.add("-N");
        options.add("" + getNeighborMargin());
        options.add("-C");
        options.add("" + getClusterPercent());

        Collections.addAll(options, super.getOptions());

        return options.toArray(new String[options.size()]);
    }

    /**
     * Returns the Capabilities of this clusterer. Derived clusterers have to
     * override this method to enable capabilities.
     *
     * @return the capabilities of this object
     * @see Capabilities
     */
    @Override
    public Capabilities getCapabilities() {
        //http://weka.sourceforge.net/doc.dev/weka/core/Capabilities.Capability.html
        Capabilities result = super.getCapabilities();
        result.disableAll();
        // since clusterers are unsupervised algorithms,
        result.enable(Capability.NO_CLASS);

        // attributes
        result.enable(Capability.NUMERIC_ATTRIBUTES);

        // other
        // needs to be set to 0 for incremental clusterers
        result.setMinimumNumberInstances(0);

        return result;
    }

    /**
     * Returns an instance of a TechnicalInformation object, containing
     * detailed information about the technical background of this class,
     * e.g., paper reference or book this class is based on.
     *
     * @return the technical information about this class
     */
    @Override
    public TechnicalInformation getTechnicalInformation() {
        TechnicalInformation result;

        result = new TechnicalInformation(Type.INPROCEEDINGS);
        result.setValue(Field.AUTHOR, "Florian Fritz");
        result.setValue(Field.TITLE,
                "Design and Development of a BANG-File Clustering System");
        result.setValue(Field.YEAR, "2017");

        return result;
    }

    /**
     * Returns a string describing this clusterer.
     *
     * @return a description of the evaluator suitable for displaying in the
     *         explorer/experimenter gui
     */
    public String globalInfo() {
        return "The BANG file is a multidimensional structure of the grid file type.\n"
                + "It organizes the value space surrounding the data values, instead of comparing the data values "
                + "themselves.\n"
                + "Its tree structured directory partitions the data space into block regions with successive "
                + "binary divisions on dimensions.\n"
                + "The clustering algorithm identifies densely populated regions as cluster centers and expands "
                + "those with neighboring blocks.\n\n"
                + "Inserted data has to be normalized to an interval [0, 1].\n\n"
                + "For more information see:\n\n" + getTechnicalInformation().toString();
    }

    /**
     * Returns the revision string.
     *
     * @return the revision
     */
    @Override
    public String getRevision() {
        return RevisionUtils.extract("$Revision: TODO $");
    }

    /**
     * Generates a clusterer. Has to initialize all fields of the clusterer that
     * are not being set via options.
     *
     * @param data set of instances serving as training data
     * @exception Exception if the clusterer has not been generated successfully
     */
    @Override
    public void buildClusterer(Instances data) throws Exception {
        getCapabilities().testWithFail(data);

        // If dataset is not normalized to [0, 1], we normalize it
        AttributeStats stats;
        for(int i = 0; i < data.numAttributes(); i++){
            stats = data.attributeStats(i);
            if (stats.numericStats.min < 0 || stats.numericStats.min > 1 || stats.numericStats.max < 0 || stats.numericStats.max > 1){
                normalize = new Normalize();
                normalize.setInputFormat(data);
                data = Filter.useFilter(data, normalize);
                break;
            }
        }

        dimensions = data.numAttributes();

        if (getNeighborMargin() > dimensions){
            throw new Exception("Neighborhood-Margin may not be bigger than amount of dimensions");
        }

        bangFile = new at.ac.univie.clustering.clusterers.bangfile.BANGFile(dimensions, getBucketsize(), getNeighborMargin(), getClusterPercent());

        double[] tuple;
        for (Instance instance : data){
            tuple = new double[dimensions];
            for (int i = 0; i < dimensions; i++){
                tuple[i] = instance.value(i);
            }
            bangFile.insertTuple(tuple);
        }

        bangFile.buildClusters();
    }

    /**
     * Returns the number of clusters.
     *
     * @return the number of clusters generated for a training dataset.
     * @exception Exception if number of clusters could not be returned
     *              successfully
     */
    @Override
    public int numberOfClusters() throws Exception {
        return bangFile.numberOfClusters();
    }

    /**
     * Classifies a given instance. Either this or distributionForInstance() needs
     * to be implemented by subclasses.
     *
     * @param instance the instance to be assigned to a cluster
     * @return the number of the assigned cluster as an integer
     * @exception Exception if instance could not be clustered successfully
     */
    @Override
    public int clusterInstance(Instance instance) throws Exception {
        //Normalize instance if original dataset was normalized
        if (normalize != null){
            normalize.input(instance);
            instance = normalize.output();
        }

        double[] tuple = new double[dimensions];
        for (int i = 0; i < dimensions; i++){
            tuple[i] = instance.value(i);
        }
        return bangFile.clusterTuple(tuple);
    }


    @Override
    public String toString() {
        return bangFile.toString();
    }

    // ============
    // Test method.
    // ============
    public static void main(String[] argv) {
        runClusterer(new BANGFile(), argv);
    }
}
