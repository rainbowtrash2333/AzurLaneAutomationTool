package unit;

import org.opencv.core.Point;

import java.awt.*;
import java.awt.event.InputEvent;

/**
 * @author Twikura
 * @create 2019/2/2 0:56
 */
public class Mouse {
    private static Robot screenWin;

    public static void init (Robot screenWin) {
        Mouse.screenWin = screenWin;
    }

    // windows 10 下，横是X，纵是Y，
    public static void move (int x, int y) {
        for (int count = 0; (MouseInfo.getPointerInfo().getLocation().getX() != x ||
                MouseInfo.getPointerInfo().getLocation().getY() != y) &&
                count < 10; count++) {
            screenWin.mouseMove(x, y);
        }
    }

    public static void move (Point point) {
        move((int) point.x, (int) point.y);
    }

    public static void click () {
        screenWin.mousePress(InputEvent.BUTTON1_MASK);
        screenWin.mouseRelease(InputEvent.BUTTON1_MASK);
    }
    public static void click (int x, int y){
        move(x,y);
        click();
    }

    public static void click (Point point) {
        move(point);
        click();
    }

}
