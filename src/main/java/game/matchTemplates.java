package game;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.util.ArrayList;

/**
 * @author Twikura
 * @create 2019/2/7 16:06
 */
public enum matchTemplates {

    INSTANCE;

    // 加载匹配图片
    private Mat loadImage(String templateName) {
        Mat tpl = Imgcodecs.imread("templateImages/" + templateName + ".png", Imgcodecs.IMREAD_GRAYSCALE);
        if (tpl.empty()) {
            System.out.println("找不到" + templateName + "模板，请检查templateImages文件夹下是否有此图片。");
            System.exit(1);
        }
        return tpl;
    }

    /**
     * 匹配多个对象，并返回
     * @param src          - 被匹配的原图
     * @param templateName - 在“templateImages文件夹下的模板的文件名
     * @param threshold    - 模板匹配度的阈值
     */
    public ArrayList<Point> matchMultipleObjects (Mat src, String templateName, double threshold) {
        ArrayList<Point> targets = new ArrayList<>(4);
        Mat tpl = loadImage(templateName);
        Mat result = new Mat();
        double maxval;
        while (true) {
            Imgproc.matchTemplate(src, tpl, result, Imgproc.TM_CCOEFF_NORMED);//Template Matching
            Core.MinMaxLocResult maxr = Core.minMaxLoc(result);
            maxval = maxr.maxVal;
            Point maxp = maxr.maxLoc;
            if (maxval > threshold) {
               Point p = new Point(
                       maxp.x + (tpl.height() >> 1),
                       maxp.y + (tpl.width() >> 1));
                targets.add(p);
                Imgproc.rectangle(src, maxp, new Point(
                        maxp.x + tpl.cols(),
                        maxp.y + tpl.rows()), new Scalar(0, 255, 0), 5);
                Imgproc.rectangle(src, maxp, new Point(
                        maxp.x + tpl.cols(),
                        maxp.y + tpl.rows()), new Scalar(0, 0, 0), -1);
                System.out.println("匹配到图片：" + templateName+"位置为："+p);
            } else break;
        }
        return targets;
    }

    /**
     * 匹配一个对象，并返回
     * @param src          - 被匹配的原图
     * @param templateName - 在“templateImages文件夹下的模板的文件名
     * @param threshold    - 模板匹配度的阈值
     */
    public Point matchObject (Mat src, String templateName, double threshold) {
        Mat tpl = loadImage(templateName);
        Mat result = new Mat();
        double maxval;
        Imgproc.matchTemplate(src, tpl, result, Imgproc.TM_CCOEFF_NORMED);//Template Matching
        Core.MinMaxLocResult maxr = Core.minMaxLoc(result);
        maxval = maxr.maxVal;
        if(maxval>threshold){
            Point p =new Point(
                    maxr.maxLoc.x + (tpl.height() >> 1),
                    maxr.maxLoc.y  + (tpl.width() >> 1));
            System.out.println("匹配到图片：" + templateName+"位置为："+p);
            return p;
        }else
            return null;
    }
}
