package process;

import net.sf.jsi.Rectangle;
import net.sf.jsi.SpatialIndex;
import net.sf.jsi.rtree.RTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by szezso on 2017.04.30..
 */
public class MyRTree {
    static SpatialIndex rTree;
    static List<EdgeElement> rTreeNodes;

    public EdgeElement getNode(int i){
        return rTreeNodes.get(i);
    }

    public void addNode(EdgeElement edge){
        rTreeNodes.add(edge);
    }

    public void addRTreeElement(Rectangle rectangle){
        rTree.add(rectangle, rTreeNodes.size()-1);
    }

    public SpatialIndex getRTree(){
        return rTree;
    }

    public void add(EdgeElement edge, Rectangle rectangle){
        rTreeNodes.add(edge);
        rTree.add(rectangle, rTreeNodes.size()-1);
    }

    public MyRTree(){
        rTree = new RTree();
        rTreeNodes = new ArrayList<>();
        rTree.init(null);
    }
}
