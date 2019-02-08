package game;

import image.ScreenShot;
import org.opencv.core.Mat;
import org.opencv.core.*;


/**
 * @author Twikura
 * @create 2019/2/2 11:35
 */
public class Screen {
    private static Screen instance = new Screen();
    public int x;
    public int y;
    private Point startPoint;
    private Point endPoint;
    private final int height = 720;
    private final int width = 1280;
    private boolean exist;

    private Screen () {
    }

    public int getHeight () {
        return height;
    }

    public int getWidth () {
        return width;
    }

    public static Screen getInstance () {
        return instance;
    }

    public void init () {
        Mat src = new ScreenShot().screenShotAsMat(0, 0, 1920, 1080);
        Point p = matchTemplates.INSTANCE.matchObject(src, "attack", 0.6);
        if (p != null) {
            exist=true;
            x = (int) (p.x - 1012);
            y = (int) (p.y - 476);
            startPoint = new Point(x, y);
            endPoint = new Point(x + height, y + width);
        }else exist=false;
    }

    public boolean exist () {
        return exist;
    }

    @Override
    public String toString () {
        return "(" + x + ", " + y + ")";
    }

    public Point getStartPoint () {
        return startPoint;
    }

    public Point getEndPoint () {
        return endPoint;
    }
}
