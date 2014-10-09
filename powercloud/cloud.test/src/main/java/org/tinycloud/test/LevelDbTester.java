package org.tinycloud.test;

import org.cloud.Cloud;
import org.cloud.Node;
import org.cloud.Software;
import org.cloud.impl.DefaultCloudFactory;
import org.kevoree.modeling.api.KMFContainer;
import org.kevoree.modeling.api.persistence.KMFContainerProxy;
import org.kevoree.modeling.api.util.ModelVisitor;
import org.kevoree.modeling.datastores.leveldb.LevelDbDataStore;

/**
 * Created with IntelliJ IDEA.
 * User: thomas.hartmann
 * Date: 07/11/2013
 * Time: 11:03
 */
public class LevelDbTester {

    private static void populate(DefaultCloudFactory factory) {
        Cloud cloud = factory.createCloud();
        Node node0 = factory.createNode();
        node0.setId("node0");
        cloud.addNodes(node0);
        Software soft0 = factory.createSoftware();
        soft0.setName("soft0");
        node0.addSoftwares(soft0);
        System.out.println("Persist " + cloud + "/" + node0 + soft0);
        factory.persistBatch(factory.createBatch().addElementAndReachable(cloud));
    }

    public static void main(String[] args) {
        DefaultCloudFactory factory = new DefaultCloudFactory();
        LevelDbDataStore datastore = new LevelDbDataStore();
        factory.setDatastore(datastore);

        System.out.println("Populate");
        populate(factory);
        factory.clearCache();
        factory.commit();

        System.out.println("DataStore export");

        System.out.println("Dump Traces");
        for (String key : datastore.keys("trace")) {
            System.out.println(key + "->" + datastore.get("trace", key));
        }
        System.out.println("Dump Types");
        for (String key : datastore.keys("type")) {
            System.out.println(key + "->" + datastore.get("type", key));
        }

        System.out.println("Lookup from DataStore");

        Cloud cloudLazy = (Cloud) factory.lookup("/");
        //System.out.println(cloudLazy);
        System.out.println(cloudLazy.findNodesByID("node0"));



        System.out.println(cloudLazy.getNodes().get(0));
        /*



        System.out.println(cloudLazy.findByPath("nodes[node0]/softwares[soft0]"));
        System.out.println(factory.lookup("nodes[node0]/softwares[soft0]"));



        KMFContainerProxy lazyNode = (KMFContainerProxy) cloudLazy.getNodes().get(0);
        System.out.println(lazyNode.getIsResolved());

        System.out.println(cloudLazy.getNodes().get(0).getSoftwares().get(0));

        System.out.println(lazyNode.getIsResolved());


        KMFContainerProxy cloudLazyProxy = (KMFContainerProxy) cloudLazy;
        System.out.println(cloudLazyProxy.getIsResolved());


        System.out.println(cloudLazy.findByPath("nodes[node0]"));
        System.out.println(cloudLazy.findByPath("nodes[node0]").eContainer());
        System.out.println(cloudLazy.findByPath("nodes[node0]/softwares[soft0]").eContainer());
        System.out.println(cloudLazy.findByPath("nodes[node0]/softwares[soft0]").eContainer().path());

        cloudLazy.visit(new ModelVisitor() {
            @Override
            public void visit(KMFContainer kmfContainer, String s, KMFContainer kmfContainer2) {
                System.out.println("visit=" + kmfContainer);
                KMFContainerProxy proxy = (KMFContainerProxy) kmfContainer;
                System.out.println(proxy.getIsResolved());
            }
        }, true, true, true);
*/

    }

}
