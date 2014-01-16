package org.smartgrid.test;


import org.evaluation.SmartGrid;
import org.evaluation.SmartMeter;
import org.evaluation.impl.DefaultEvaluationFactory;
import org.kevoree.modeling.api.persistence.DataStore;
import org.kevoree.modeling.api.persistence.MemoryDataStore;
import org.kevoree.modeling.api.time.TimeAwareKMFContainer;
import org.kevoree.modeling.api.time.TimePoint;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;

/**
 * Created with IntelliJ IDEA.
 * User: duke
 * Date: 14/11/2013
 * Time: 20:31
 */
public class SimpleTimeDistortionTest {

    public static final int NODES_PER_GRID = 100;
    private static DataStore datastore = new LevelDbDataStore("/Users/duke/Documents/dev/dukeboard/kevoree-modeling-framework/metamodel/smartgrid/smartgrid.tests/tempTimeDistorted");

    private static void populate(DefaultEvaluationFactory factory) {
        SmartGrid smartgrid = factory.createSmartGrid();
        for (int i = 0; i < NODES_PER_GRID; i++) {
            SmartMeter node = factory.createSmartMeter();
            node.setName("meter_" + i);
            smartgrid.addSmartmeters(node);
            node.setElectricLoad(100000l);
            if (i >= 2) {
                node.addNeighbors(smartgrid.getSmartmeters().get(i - 2));
                node.addNeighbors(smartgrid.getSmartmeters().get(i - 1));
            }
        }
        System.out.println("Persist everything...");
        factory.persistBatch(factory.createBatch().addElementAndReachable(smartgrid));
        factory.commit();
    }

    public static void main(String[] args) {
        DefaultEvaluationFactory factory = new DefaultEvaluationFactory();
        //MemoryDataStore datastore = new MemoryDataStore();
        factory.setDatastore(datastore);
        long startPersist = System.currentTimeMillis();
        populate(factory);
        factory.clearCache();
        SmartMeter meter5 = (SmartMeter) factory.lookup("smartmeters[meter_5]");
        System.out.println(meter5.getNeighbors().size());
        SmartGrid grid = (SmartGrid) factory.lookup("/");
        System.out.println(grid.getSmartmeters().size());
        for (SmartMeter meter : grid.getSmartmeters()) {
            TimeAwareKMFContainer tmeter = (TimeAwareKMFContainer) meter;
            for (int i = 0; i < 10000; i++) {
                tmeter.shift(tmeter.getNow().shift(1));
                meter.setElectricLoad(200000l);
                factory.persist(meter);
            }
        }
        factory.commit();
        long endPersist = System.currentTimeMillis();
        System.out.println("Persisted in " + (endPersist - startPersist) + " ms");


        //datastore.dump();
        long before = System.currentTimeMillis();
        computeLast(factory, 90, 100);
        computeLast(factory, 20, 30);
        System.out.println("lookup two elements in " + (System.currentTimeMillis() - before) + "ms");
    }


    public static void computeLast(DefaultEvaluationFactory factory, int begin, int end) {
        factory.clearCache();
        SmartMeter meter5 = (SmartMeter) factory.lookup("smartmeters[meter_5]");
        //System.out.println(meter5.getNeighbors().size());
        //factory.setRelativityStrategy(RelativeTimeStrategy.RELATIVE_FIRST);
        for (int i = end; i > begin; i--) {
            factory.setRelativeTime(new TimePoint(i, 0));
            //System.out.println(factory.getRelativeTime());
            //SmartGrid grid = (SmartGrid) factory.lookup("/");
            //System.out.println(((TimeAwareKMFContainer) grid.getSmartmeters().get(0)).getNow());
            SmartMeter meter = (SmartMeter) factory.lookup("smartmeters[meter_5]");
            //System.out.println(((TimeAwareKMFContainer) meter).getNow());
            //System.out.println(meter.getNeighbors().size());
        }
    }


}
