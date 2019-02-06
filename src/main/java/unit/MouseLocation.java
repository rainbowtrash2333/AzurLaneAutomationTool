package unit;

import java.awt.*;

/**
 * @author Twikura
 * @create 2019/2/1 13:03
 */
public class MouseLocation {
    public static void main (String[] args) {
        while(true){
            Point point = MouseInfo.getPointerInfo().getLocation();
            point.y-=30;
            System.out.println(point);
            try { Thread.sleep(100); } catch (InterruptedException e) { }
        }
    }
}
