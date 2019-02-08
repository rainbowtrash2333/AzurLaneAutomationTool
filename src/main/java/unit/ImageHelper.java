package unit;

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;

import static org.bytedeco.javacpp.opencv_core.CV_8UC;

/**
 * @author Twikura
 * @create 2019/2/1 15:12
 */
public class ImageHelper {

    //

    public static Mat BufferedImage2Mat (BufferedImage image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", byteArrayOutputStream);
            byteArrayOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
    }

    public static Mat bufferedImageToMat (BufferedImage bi) {
        Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
        byte[] data = (
                (DataBufferByte) bi.getRaster().getDataBuffer()
        ).getData();
        mat.put(0, 0, data);
        return mat;
    }

    //   copy from https://techutils.in/blog/2016/08/02/converting-java-bufferedimage-to-opencv-mat-and-vice-versa/
// 图像失真 垃圾代码
//    public static Mat BufferedImage2Mat(BufferedImage in) {
//        Mat out = new Mat(in.getHeight(), in.getWidth(), CvType.CV_8UC3);
//        byte[] data = new byte[in.getWidth() * in.getHeight() * (int) out.elemSize()];
//        int[] dataBuff = in.getRGB(0, 0, in.getWidth(), in.getHeight(), null, 0, in.getWidth());
//        for (int i = 0; i < dataBuff.length; i++) {
//            data[i * 3] = (byte) ((dataBuff[i]));
//            data[i * 3 + 1] = (byte) ((dataBuff[i]));
//            data[i * 3 + 2] = (byte) ((dataBuff[i]));
//        }
//        out.put(0, 0, data);
//        return out;
//    }

    public static BufferedImage Mat2BufferedImage (Mat matrix) {
        MatOfByte mob = new MatOfByte();
        BufferedImage result = null;
        Imgcodecs.imencode(".jpg", matrix, mob);
        try {
            result = ImageIO.read(new ByteArrayInputStream(mob.toArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void binaryzation (Mat mat) {
        int BLACK = 0;
        int WHITE = 255;
        int ucThre = 0, ucThre_new = 127;
        int nBack_count, nData_count;
        int nBack_sum, nData_sum;
        int nValue;
        int i, j;

        int width = mat.width(), height = mat.height();
        //寻找最佳的阙值
        while (ucThre != ucThre_new) {
            nBack_sum = nData_sum = 0;
            nBack_count = nData_count = 0;

            for (j = 0; j < height; ++j) {
                for (i = 0; i < width; i++) {
                    nValue = (int) mat.get(j, i)[0];

                    if (nValue > ucThre_new) {
                        nBack_sum += nValue;
                        nBack_count++;
                    } else {
                        nData_sum += nValue;
                        nData_count++;
                    }
                }
            }

            nBack_sum = nBack_sum / nBack_count;
            nData_sum = nData_sum / nData_count;
            ucThre = ucThre_new;
            ucThre_new = (nBack_sum + nData_sum) / 2;
        }

        //二值化处理
        int nBlack = 0;
        int nWhite = 0;
        for (j = 0; j < height; ++j) {
            for (i = 0; i < width; ++i) {
                nValue = (int) mat.get(j, i)[0];
                if (nValue > ucThre_new) {
                    mat.put(j, i, WHITE);
                    nWhite++;
                } else {
                    mat.put(j, i, BLACK);
                    nBlack++;
                }
            }
        }

        // 确保白底黑字
        if (nBlack > nWhite) {
            for (j = 0; j < height; ++j) {
                for (i = 0; i < width; ++i) {
                    nValue = (int) (mat.get(j, i)[0]);
                    if (nValue == 0) {
                        mat.put(j, i, WHITE);
                    } else {
                        mat.put(j, i, BLACK);
                    }
                }
            }
        }
    }

    public static void displayImage (Mat mat) {
        ImageHelper.displayImage(
                ImageHelper.Mat2BufferedImage(mat));
    }

    public static void displayImage (Image img2) {

        //BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
        ImageIcon icon = new ImageIcon(img2);
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(img2.getWidth(null) + 50, img2.getHeight(null) + 50);
        JLabel lbl = new JLabel();
        lbl.setIcon(icon);
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
