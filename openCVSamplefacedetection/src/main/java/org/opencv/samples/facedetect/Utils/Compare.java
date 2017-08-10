package org.opencv.samples.facedetect.Utils;

import android.graphics.Bitmap;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 图像匹配的方法类
 * Created by OlAy on 2017/7/25.
 */

public class Compare {

    private static final String TAG = "Compare";

    /**
     * 灰度直方图匹配
     * 匹配程度较高，比较准确
     * 在图片经过压缩存储和提取后，匹配精度有所下降，大约10%左右
     * 对色差，背景颜色，拍摄距离有很大要求
     * 但是与其它的图片差别非常之大
     * 相较于其它方法较为准确
     *
     * @param bitmap1
     * @param bitmap2
     * @return
     */
    public static double Histogram(Bitmap bitmap1, Bitmap bitmap2) {
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        Mat mat11 = new Mat();
        Mat mat22 = new Mat();
        /**
         * 无论是图片转矩阵还是矩阵转图片
         * 图片和矩阵的大小必须一样
         * 否则会报传入非法参数的异常
         */
        Utils.bitmapToMat(bitmap1, mat1);
        Utils.bitmapToMat(bitmap2, mat2);

        //矩阵灰度化
        Imgproc.cvtColor(mat1, mat11, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(mat2, mat22, Imgproc.COLOR_BGR2GRAY);

        //直方图均衡化处理
        Imgproc.equalizeHist(mat11, mat11);
        Imgproc.equalizeHist(mat22, mat22);

        //归一化处理
        //???

        mat11.convertTo(mat11, CvType.CV_32F);
        mat22.convertTo(mat22, CvType.CV_32F);

        //对比图片
        double target = Imgproc.compareHist(mat11, mat22, Imgproc.CV_COMP_CORREL);
        return target;
    }

    /**
     * 基于图像特征点的图像匹配，而非人脸特征点
     * 误差很大，且匹配不准确
     * 失败
     *
     * @param mGray
     * @param rect
     * @param bitmap
     * @return
     */
    public static double FeaturePoint(Mat mGray, Rect rect, Bitmap bitmap) {
        Mat testimage = new Mat();
        Mat grayimage = new Mat();
        MatOfDMatch matches = new MatOfDMatch();
        MatOfKeyPoint keypoint_train = new MatOfKeyPoint();
        MatOfKeyPoint keypoint_test = new MatOfKeyPoint();
        Mat output = new Mat();
        Mat test = new Mat();
        Mat train = new Mat();
        Mat facemat = new Mat(mGray, rect);
        FeatureDetector detector_train = FeatureDetector.create(FeatureDetector.ORB);
        detector_train.detect(facemat, keypoint_train);
        DescriptorExtractor descriptor_train = DescriptorExtractor.create(DescriptorExtractor.ORB);
        descriptor_train.compute(facemat, keypoint_train, train);

        Utils.bitmapToMat(bitmap, testimage);
        Imgproc.cvtColor(testimage, grayimage, Imgproc.COLOR_RGB2GRAY);

        FeatureDetector detector_test = FeatureDetector.create(FeatureDetector.ORB);
        detector_test.detect(grayimage, keypoint_test);
        DescriptorExtractor descriptor_test = DescriptorExtractor.create(DescriptorExtractor.ORB);
        descriptor_test.compute(grayimage, keypoint_test, test);

        DescriptorMatcher descriptormatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        descriptormatcher.match(test, train, matches);
        Features2d.drawMatches(grayimage, keypoint_test, facemat, keypoint_train, matches, output);
        Bitmap matchbitmap = Bitmap.createScaledBitmap(bitmap, output.width(), output.height(), false);
        Utils.matToBitmap(output, matchbitmap);

        double maxDist = Double.MIN_VALUE;
        double minDist = Double.MAX_VALUE;
        DMatch[] mats = matches.toArray();
        int[] qwe = new int[mats.length];
        for (int i = 0; i < mats.length; i++) {
            double dist = mats[i].distance;
            qwe[i] = (int) dist;
            if (dist < minDist) {
                minDist = dist;
            }
            if (dist > maxDist) {
                maxDist = dist;
            }
        }
        int element = majorityElement(qwe);
        int average = average(qwe);
        double anchor = element + average / 2;
        List<DMatch> goodmatch = new LinkedList<>();
        for (int i = 0; i < mats.length; i++) {
            double dist = mats[i].distance;
            if (Math.abs(dist - anchor) < (anchor * 0.75f)) {
                goodmatch.add(mats[i]);
            }
        }
        double targetOfTwo = (double) goodmatch.size() / mats.length;
        return targetOfTwo;
    }

    /**
     * 基于感知Hash算法的快速图像匹配
     * 无论怎么匹配，相似度一直维持在50%左右，且不能识别不同的人
     * 失败
     *
     * @param b1
     * @param b2
     * @return
     */
    public static double HashMatch(Bitmap b1, Bitmap b2) {
        Mat mat1 = new Mat();
        Mat mat2 = new Mat();
        Utils.bitmapToMat(b1, mat1);
        Utils.bitmapToMat(b2, mat2);
        // 缩小尺寸 8*8
        Mat resizedMat1 = new Mat();
        Mat resizedMat2 = new Mat();
        Imgproc.resize(mat1, resizedMat1, new Size(8, 8));
        Imgproc.resize(mat2, resizedMat2, new Size(8, 8));
        // 简化色彩
        Mat resizedGrayMat1 = new Mat();
        Mat resizedGrayMat2 = new Mat();
        Imgproc.cvtColor(resizedMat1, resizedGrayMat1, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(resizedMat2, resizedGrayMat2, Imgproc.COLOR_RGB2GRAY);
        // 计算平均值
        resizedGrayMat1.convertTo(resizedGrayMat1, CvType.CV_32S);
        resizedGrayMat2.convertTo(resizedGrayMat2, CvType.CV_32S);
        int length = (int) (resizedGrayMat1.total() * resizedGrayMat1.channels());
        int[] hashArr1 = new int[length], hashArr2 = new int[length];
        int avg1 = 0, avg2 = 0;
        resizedGrayMat1.get(0, 0, hashArr1);
        resizedGrayMat2.get(0, 0, hashArr2);
        for (int i = 0; i < length; i++) {
            avg1 += hashArr1[i];
            avg2 += hashArr2[i];
        }
        avg1 = avg1 / length;
        avg2 = avg2 / length;
        // 通过比较像素的灰度值和平均灰度值计算哈希值
        for (int i = 0; i < length; i++) {
            hashArr1[i] = (hashArr1[i] >= avg1) ? 1 : 0;
            hashArr2[i] = (hashArr1[i] >= avg2) ? 1 : 0;
        }
        // 计算相似度
        int sameNum = 0;
        for (int i = 0; i < length; i++) {
            if (hashArr1[i] == hashArr2[i]) {
                ++sameNum;
            }
        }

        return (float) sameNum / length;
    }

    /**
     * 将2个图像转换成0和1进行比较
     * 转化后的图像完全不一样，识别率为0，且摄像头帧数爆炸
     * 有特别明显的延迟
     * 失败
     *
     * @param bitmap1
     * @param bitmap2
     * @return
     */
    public static double Zero2One(Bitmap bitmap1, Bitmap bitmap2) {
        String result = Hash(bitmap1);
        String result2 = Hash(bitmap2);
        if (result.length() != result2.length())
            throw new IllegalArgumentException();

        int similarity = 0;
        for (int i = 0; i < result.length(); i++) {
            if (result.charAt(i) == result2.charAt(i)) {
                similarity++;
            }
        }
        return (double) (similarity / result.length());
    }

    public static String Hash(Bitmap bitmap) {
        Byte[] bytes = ReduceColor(bitmap);
        Byte average = CalcAverage(bytes);
        String result = ComputeBits(bytes, average);
        return result;
    }

    private static Byte[] ReduceColor(Bitmap bitmap) {
        Byte[] grayValues = new Byte[bitmap.getHeight() * bitmap.getWidth()];
        for (int x = 0; x < bitmap.getWidth(); x++)
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int pixe = bitmap.getPixel(x, y);
                byte grayValue = (byte) pixe;
                grayValues[x * bitmap.getWidth() + y] = grayValue;
            }
        return grayValues;
    }

    private static Byte CalcAverage(Byte[] values) {
        Integer sum = 0;
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum.byteValue();
    }

    private static String ComputeBits(Byte[] values, byte averageValue) {
        char[] result = new char[values.length];
        for (int i = 0; i < values.length; i++) {
            if (values[i] < averageValue) {
                result[i] = '0';
            } else
                result[i] = '1';
        }
        return new String(result);
    }

    //提取数组中的众数
    public static int majorityElement(int[] nums) {
        Arrays.sort(nums);
        int len = nums.length;
        if (nums[0] == nums[len / 2]) {
            return nums[0];
        } else if (nums[len - 1] == nums[len / 2]) {
            return nums[len - 1];
        } else {
            return nums[len / 2];
        }
    }

    //提取平均数
    public static int average(int[] nums) {
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
        }
        int aver = sum / nums.length;
        return aver;
    }
}
