package game;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * @author Twikura
 * @create 2019/2/3 15:13
 */
public class FindShipsThread implements Callable<ArrayList<Point>> {

    private Mat tpl;
    private Mat img;
    private String name;
    private ArrayList<Point> ships = new ArrayList<>(5);

    public FindShipsThread (Mat src, String templateName) {
        name = templateName;
        tpl = Imgcodecs.imread("templateImage/" + templateName + ".png",Imgcodecs.IMREAD_GRAYSCALE);
        if (tpl.empty()) {
            System.out.println("找不到" + templateName);
            System.exit(1);
        }
        img = src.clone();
    }

    @Override
    public ArrayList<Point> call () {
  //      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat result = new Mat();
        double threshold = 0.6;
        double maxval;


        while (true) {
            Imgproc.matchTemplate(img, tpl, result, Imgproc.TM_CCOEFF_NORMED);//Template Matching
            Core.MinMaxLocResult maxr = Core.minMaxLoc(result);
            maxval = maxr.maxVal;
            Point maxp = maxr.maxLoc;
            if (maxval >= threshold) {
                ships.add(new Point(
                        maxp.x + (tpl.height() >> 1),
                        maxp.y+20 + (tpl.width() >> 1)));
                Imgproc.rectangle(img, maxp, new Point(
                        maxp.x + tpl.cols(),
                        maxp.y + tpl.rows()), new Scalar(0, 255, 0), 5);
                Imgproc.rectangle(img, maxp, new Point(
                        maxp.x + tpl.cols(),
                        maxp.y + tpl.rows()), new Scalar(0, 0, 0), -1);
                System.out.println("Template Matches with image of "+name);
            } else break;
        }
     //   ImageHelper.displayImage(img);
        return ships;
    }
}
