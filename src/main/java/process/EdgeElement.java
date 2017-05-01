package process;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.jsi.Rectangle;


/**
 * Created by szezso on 2017.04.01..
 */
public class EdgeElement {
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

    public void setShapes(String shapes) {
        String[] tmp = shapes.split(" ");
        List<Point2D> lane = new ArrayList<>();
        for (int i = 0; i < tmp.length; i++) {
            double x = Double.parseDouble(tmp[i].split(",")[0]);
            double y = Double.parseDouble(tmp[i].split(",")[1]);
            lane.add(new Point2D.Double(x, y));
        }
        this.shapes.add(lane);

    }

    public boolean contains(Point2D point) {
        for (int i = 0; i < shapes.size(); i++) {
            Iterator<Point2D> iterator = shapes.get(i).iterator();
            Point2D act = iterator.next();
            while (iterator.hasNext()) {
                List<Point2D> nodes = new ArrayList<Point2D>();
                Point2D next = iterator.next();

                for (int j = 0; j < 4; j++) {
                    if (j < 2)
                        nodes.add(newPoint(act, next, ((j % 2) == 0 ? -1 : 1)));
                    else
                        nodes.add(newPoint(next, act, ((j % 2) == 0 ? -1 : 1)));
                }

               /* 0---1
                  | p |
                  |   |
                  3---2*/
                if (line(nodes.get(0), nodes.get(1), point) <= 0.0 && line(nodes.get(3), nodes.get(2), point) >= 0.0 &&
                        line(nodes.get(0), nodes.get(3), point) >= 0.0 && line(nodes.get(1), nodes.get(2), point) <= 0.0)
                    return true;

                act = next;
            }
        }
        return false;
    }

    public double angle(Point2D point1, Point2D point2) {
        double xdiff = point1.getX() - point2.getX();
        double ydiff = point1.getY() - point2.getY();
        double atan = Math.atan2(ydiff, xdiff);
        return atan;
    }

    public Point2D newPoint(Point2D a, Point2D b, int heading) {

        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double dist = Math.sqrt(dx * dx + dy * dy);
        dx /= dist;
        dy /= dist;
        double x = a.getX() + (width) * dy * heading;
        double y = a.getY() + (width) * dx * -1.0 * heading;


        //double x = refpoint.getX() + Math.sin(angle) * heading * width;
        //double y = refpoint.getY() + Math.cos(angle) * heading * width;
        return new Point2D.Double(x, y);
    }


    /**
     * Két pontra (a, b) állított egyeneshez viszonyítva vissza adja, hogy p hogy helyezkedik el
     *
     * @param a
     * @param b
     * @param p
     * @return >0 felette, <0 alatta, ==0 az egyenesen van
     */
    private static double line(Point2D a, Point2D b, Point2D p) {
        return ((b.getX() - a.getX()) * (p.getY() - a.getY())
                - (b.getY() - a.getY()) * (p.getX() - a.getX()));
    }
}
