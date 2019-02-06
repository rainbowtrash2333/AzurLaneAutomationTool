package game;

import image.ScreenShot;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import unit.ImageHelper;
import unit.Location;
import java.awt.image.BufferedImage;
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
        BufferedImage bfimg = new ScreenShot().getScreenShot(0, 0, 1920, 1080);

        Mat source = ImageHelper.BufferedImage2Mat(bfimg);
        Mat grayScouce = new Mat();
        Imgproc.cvtColor(source, grayScouce, Imgproc.COLOR_BGR2GRAY);

        Location attackLocation = new Location(grayScouce, "attack");
        exist = attackLocation.exist();
        x = attackLocation.x - 1093;
        y = attackLocation.y - 402;
        startPoint = new Point(x, y);
        endPoint = new Point(x + height, y + width);
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
