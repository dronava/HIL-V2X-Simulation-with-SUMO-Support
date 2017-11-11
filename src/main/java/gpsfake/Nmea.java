package gpsfake;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * Created by szzso on 2017. 01. 22..
 */
public class Nmea {

    private double angle;
    private double speed;
    private Point2D point;
    private String date;
    private String time;

    private String sentence;

    private static final double MSTOKNOT = 1.94384449244;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public Point2D getPoint() {
        return point;
    }

    public void setPoint(Point2D point) {
        this.point = point;
    }


    public Nmea(double angle, double speed, Point2D point2D, String date, String time) {
        this.angle = angle;
        this.speed = speed;
        this.point = point2D;
        this.date = date;
        this.time = time;

        sentence = "";
    }

    public String getGGA(){
        sentence = "";
        addParam("GPGGA");
        addParam(time);

        getLatLng();

        // Fix quality
        addParam("1");
        addParam(2);

        addParam(5);
        calculateChecksum();
        return sentence;
    }

    public String getRMC(){
        sentence = "";
        addParam("GPRMC");
        addParam(time);
        addParam("A");

        getLatLng();

        DecimalFormat format = new DecimalFormat("00.00");
        String speedformat = format.format(speed*MSTOKNOT).replace(',', '.');
        addParam(speedformat);
        String angleformat = format.format(angle).replace(',', '.');
        addParam(angleformat);
        addParam(date);
        addParam(1);
        calculateChecksum();
        return sentence;
    }

    private void getLatLng(){
         double lat = point.getY();
        addParam(toNMEA(lat));
        if(lat >=  0){
            addParam("N");
        }else
            addParam("S");

        double lng = point.getX();
        addParam(toNMEA(lng));
        if(lng >=  0){
            addParam("E");
        }else
            addParam("W");
    }

    private  void addParam(String param){
        sentence = sentence.concat(param + ",");
    }

    private void addParam(int space){
        for (int i = 0; i < space; i++) {
            addParam("");
        }
    }

    private String toNMEA(double coord){
        //Fok egész részének kinyerése
        DecimalFormat decimalformat = new DecimalFormat("###");
        double decimal = Double.valueOf(decimalformat.format(coord));
        //Tört rész előállítása
        double tort=coord-decimal;
        decimal*=100;
        //Végeredmény elvárt formátumának létrehozása
        DecimalFormat formater = new DecimalFormat("####.0000");
        return formater.format(decimal+tort*60).replace(',', '.');
    }

    private void calculateChecksum(){
        int checksum=0;
        for (int i = 0; i < sentence.length(); i++) {
            checksum ^=sentence.charAt(i);
        }
        String hexa = Integer.toHexString(checksum);

        if(hexa.length()==1)
            hexa="0"+hexa;
        sentence = sentence.concat("*".concat(hexa.toUpperCase()));
        sentence = "$".concat(sentence);
    }

}
