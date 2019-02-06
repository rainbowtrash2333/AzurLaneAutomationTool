package unit;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * @author Twikura
 * @create 2019/2/2 11:38
 */
public class Location {
    private Point point;
    public int x;
    public int y;
    private Mat template;
    private Mat result;
    private String name;
    private Core.MinMaxLocResult minMaxLocResult;

    public Location (Mat source, String name) {
        template = Imgcodecs.imread("templateImage/" + name + ".png", Imgcodecs.IMREAD_GRAYSCALE);// 4.0.1 改为IMREAD_GRAYSCALE
        if (template.empty()) {
            System.out.println("无法打开" + name);
            System.exit(1);
        }
        result = Mat.zeros(source.rows(), source.cols(), CvType.CV_32FC1);
        this.name = name;

        Imgproc.matchTemplate(source, template, result, Imgproc.TM_CCOEFF_NORMED);
        minMaxLocResult = Core.minMaxLoc(result);
        if (exist()) {
            x = (int) (minMaxLocResult.maxLoc.x + (template.width() >> 1));
            y = (int) (minMaxLocResult.maxLoc.y + (template.height() >> 1));
            point = new Point(x, y);
        }
    }

    // 0.78以下 4会匹配为1
    // 0.85以下 3会匹配为2
    public boolean exist () {
        return (minMaxLocResult.maxVal > 0.85);
    }

    public Mat getTemplate () {
        return template;
    }

    public Mat getResult () {
        return result;
    }

    public String getName () {
        return name;
    }

    public Point getPoint () {
        return point;
    }

    @Override
    public String toString () {
        return name + ": (" + x + ", " + y + ")";
    }
}
