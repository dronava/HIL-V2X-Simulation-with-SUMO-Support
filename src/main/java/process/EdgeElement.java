package process;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.jsi.Rectangle;


/**
 * Created by szezso on 2017.04.01..
 */
public class EdgeElement implements Serializable{
    static final long SerialVersionUID = -4862926644813433708L;
    private String id;
    double width;

    String allow;
    String disallow;

    double length;

    String type;
    List<List<Point2D>> shapes = new ArrayList<List<Point2D>>();

    Rectangle rectangle;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = Double.parseDouble(width);
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = Double.parseDouble(length);
    }

    public String getAllow() { return allow; }

    public void setAllow(String allow) { this.allow = allow; }

    public String getDisallow() { return disallow; }

    public void setDisallow(String disallow) { this.disallow = disallow; }

    public  void  setRectangle(Rectangle rect){this.rectangle = rect;}

    public Rectangle getRectangle(){return  this.rectangle;}

    public EdgeElement() {
        //http://sumo.dlr.de/wiki/Simulation/SublaneModel Default width = 3.2m
        width = 3.2;
        type = "";
        allow = "";
        disallow = "";
    }
}
