package image;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import unit.ImageHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Twikura
 * @create 2019/1/25 16:08
 */
public class ScreenShot {
    /**
     * 指定屏幕区域截图，返回截图的BufferedImage对象
     * @param x - 起始横坐标
     * @param y - 起始纵坐标
     * @param width - 宽
     * @param height - 高
     * @return - 截图
     */
    public BufferedImage getScreenShot(int x, int y, int width, int height) {
        BufferedImage bfImage = null;
        try {
            Robot robot = new Robot();
            bfImage = robot.createScreenCapture(new Rectangle(x, y, width, height));
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return bfImage;
    }

    /**
     * 指定屏幕区域截图，保存到指定目录
     * @param x - 起始横坐标
     * @param y - 起始纵坐标
     * @param width - 宽
     * @param height - 高
     * @param savePath - 文件保存路径
     * @param fileName - 文件保存名称
     * @param format - 文件格式
     */
    public void screenShotAsFile(int x, int y, int width, int height, String savePath, String fileName, String format) {
        try {
            Robot robot = new Robot();
            BufferedImage bfImage = robot.createScreenCapture(new Rectangle(x, y, width, height));
            File path = new File(savePath);
            File file = new File(path, fileName+ "." + format);
            ImageIO.write(bfImage, format, file);
        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main (String[] args) {
        try {
            Robot rb = new Robot();
            rb.mouseMove(0,0);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public Mat screenShotAsMat(int x, int y, int width, int height){
        BufferedImage bfimg = getScreenShot(x,y,width,height);
        Mat mat = ImageHelper.BufferedImage2Mat(bfimg);
        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
        return gray;
    }
}

