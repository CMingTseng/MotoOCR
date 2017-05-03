package me.image.util;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import me.image.domain.CharaterImage;
import me.image.domain.LabelingBean;
import me.image.domain.Pixel;

public class ImageUtil {

    /**
     * 轉變為2維灰度圖像
     */
    public static int[][] toGrays(int[] pixels, int width, int height) {
        int[][] grayPixels = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = pixels[y * width + x];
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = (pixel) & 0xff;

                int gray = new Double(0.299 * r + 0.587 * g + 0.114 * b).intValue();
                //Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue
                grayPixels[x][y] = 255 << 24 | gray << 16 | gray << 8 | gray;
            }
        }
        return grayPixels;
    }

    /**
     * 轉變為2維灰度圖像
     */
    public static int[][] toGrays(int[][] pixels, int width, int height) {
        int[][] grayPixels = new int[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int pixel = pixels[x][y];
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = (pixel) & 0xff;

                int gray = new Double(0.299 * r + 0.587 * g + 0.114 * b).intValue();
                //Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue
                grayPixels[x][y] = 255 << 24 | gray << 16 | gray << 8 | gray;
            }
        }
        return grayPixels;
    }

    /**
     *
     *
     * */
    public static int otsuThreshs(int[][] pix, int width, int height) {

        int wh = width * height;

        int i, j, t;
        int L = 256;
        int[][] tempPix = new int[width][height];
        double[] p = new double[L];

        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                tempPix[i][j] = pix[i][j] & 0xff; //取出RGB
            }
        }

        for (i = 0; i < L; i++) {
            p[i] = 0;
        }

        //計算各灰階像素出現次數
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                p[tempPix[i][j]]++;//計算RGB於256之直方圖
            }
        }
        //計算各灰階像素出現機率
        for (int m = 0; m < L; m++) {
            p[m] = p[m] / wh;
        }

        double[] sigma = new double[L];
        for (t = 0; t < L; t++) {
            double w0 = 0;
            //影像灰度值小於等於t值的機率
            for (int m = 0; m < t + 1; m++) {
                w0 += p[m];
            }
            //影像灰度值大於t值的機率
            double w1 = 1 - w0;

            double u0 = 0;

            //影像灰度值小於等於t值的平均值(mean)
            for (int m = 0; m < t + 1; m++) {
                u0 += m * p[m] / w0;
            }
            double u1 = 0;

            //影像灰度值大於等於t值的平均值(mean)
            for (int m = t; m < L; m++) {
                u1 += m * p[m] / w1;
            }
            sigma[t] = w0 * w1 * (u0 - u1) * (u0 - u1);
        }
        double max = 0.0;
        int T = 0;
        for (i = 0; i < L - 1; i++) {
            if (max < sigma[i]) {
                max = sigma[i];
                T = i;
            }
        }
        return T;
    }

    //最佳閾值分割
    public static int bestThresh(int[][] pix, int width, int height) {
        int i;
        int j;
        int thresh;
        int newthresh;
        int gmax;
        int gmin;         //最大,最小灰度值

        double[] p = new double[256];

        int[][] im = new int[width][height];

        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                im[i][j] = pix[i][j] & 0xff;
            }
        }
        for (i = 0; i < 256; i++) {
            p[i] = 0;
        }
        //1.統計各灰度級出現的次數、灰度最大和最小值
        gmax = 0;
        gmin = 255;
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                int g = im[i][j];
                p[g]++;
                if (g > gmax) {
                    gmax = g;
                }
                if (g < gmin) {
                    gmin = g;
                }
            }
        }

        thresh = 0;
        newthresh = (gmax + gmin) / 2;

        int meangray1, meangray2;
        long p1, p2, s1, s2;
        for (i = 0; (thresh != newthresh) && (i < 100); i++) {
            thresh = newthresh;
            p1 = 0;
            p2 = 0;
            s1 = 0;
            s2 = 0;

            //2. 求兩個區域的灰度平均值
            for (j = gmin; j < thresh; j++) {
                p1 += p[j] * j;
                s1 += p[j];
            }
            meangray1 = (int) (p1 / s1);

            for (j = thresh + 1; j < gmax; j++) {
                p2 += p[j] * j;
                s2 += p[j];
            }
            meangray2 = (int) (p2 / s2);
            //3. 計算新閾值
            newthresh = (meangray1 + meangray2) / 2;
        }
        return newthresh;
    }

    /**
     * Sobel對灰階圖片運算
     *
     * thresh閥值-1代表不需要
     */
    public static int[][] doSobelGray(int[][] gray, int iw, int ih, int thresh) {


        byte[][] sx = {{1, 0, -1},
                {2, 0, -2},
                {1, 0, -1}};

        byte[][] sy = {{1, 2, 1},
                {0, 0, 0},
                {-1, -2, -1}};


        int[][] edger1 = edge(gray, sx, iw, ih);
        int[][] edger2 = edge(gray, sy, iw, ih);

        int[][] sobels = new int[iw][ih];


        int g = 0;

        //File fileTxt=new File(FileOperator.IMAGE_FILE_PATH+"/doSobelGray.txt");
        //FileOutputStream fileOutputStream = new FileOutputStream(fileTxt);
        //OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream,"utf-8");


        for (int j = 0; j < ih; j++) {
            //outputStreamWriter.write("\r\n");
            for (int i = 0; i < iw; i++) {
                if (thresh > 0) {
                    if (Math.max(edger1[i][j], edger2[i][j]) > thresh) {
                        g = 0;
                    } else {
                        g = 255;
                    }
                }
//	        	else{
//	        		//System.out.println(edger1[i][j]+"|"+edger2[i][j]);
//	        	   g=Math.max(edger1[i][j],edger2[i][j]);//若上下左右相同顏色則值為0，故為黑色
//	        	}
                //System.out.println(g);
                //outputStreamWriter.write(g+" ");
                //px[i+j*iw] = (255<<24)|(g<<16)|(g<<8)|g;

                sobels[i][j] = (255 << 24) | (g << 16) | (g << 8) | g;
//	        	if(gray[i][j]!=black&&gray[i][j]!=white){
//	        		
//	        		System.out.println("===>g ["+g+"]");
//	        	}
            }
        }
        //outputStreamWriter.flush();
        //outputStreamWriter.close();
        return sobels;
    }

    /**
     * sobel 運算
     */
    public static int[][] edge(int[][] in, byte[][] tmp, int iw, int ih) {
        int[][] ed = new int[iw][ih];
        //int k=0;

        for (int j = 1; j < ih - 1; j++) {
            for (int i = 1; i < iw - 1; i++) {

                ed[i][j] = Math.abs(tmp[0][0] * in[i - 1][j - 1] + tmp[0][1] * in[i - 1][j] +
                        tmp[0][2] * in[i - 1][j + 1] + tmp[1][0] * in[i][j - 1] +
                        tmp[1][1] * in[i][j] + tmp[1][2] * in[i][j + 1] +
                        tmp[2][0] * in[i + 1][j - 1] + tmp[2][1] * in[i + 1][j] +
                        tmp[2][2] * in[i + 1][j + 1]);
                //k+=ed[i][j];
                //System.out.println(ed[i][j]);
            }
        }
        //System.out.println(k/(iw*ih));
        return ed;
    }

    public static LabelingBean labeling(int[][] images, int width, int height) {
        //預設為白色為背景，黑色為前景
        return labeling(images, width, height, Color.WHITE, Color.BLACK);

    }

    /**
     * two pass Labeling
     */
    public static LabelingBean labeling(int[][] images, int width, int height, int background, int foreground) {
        LabelingBean labelingBean = new LabelingBean();
        labelingBean.setWidth(width);
        labelingBean.setHeight(height);

        int[][] rst = new int[width][height];
        // region label starts from 1;
        // this is required as union-find data structure
        int nextLabel = 1;
        Map<Integer, Set<Integer>> linkedLabel = new HashMap<Integer, Set<Integer>>();


        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {

//                if (x==88&&y==14){
//                	System.out.println("--"+images[x][y]);
//                }
                //白色為背景，標為0
                if (images[x][y] == background) {
                    rst[x][y] = 0;
                    continue;
                }

                //若為前景黑色

                boolean leftBlack = false;
                boolean topBlack = false;
                // if  left and top are 黑色
                if (x > 0 && images[x - 1][y] == foreground) {
                    leftBlack = true;
                }

                if (y > 0 && images[x][y - 1] == foreground) {
                    topBlack = true;
                }


                Set<Integer> set = null;
                //若left and top are White
                if (!leftBlack && !topBlack) {
                    //linked[NextLabel] = set containing NextLabel
                    set = new TreeSet<Integer>();
                    set.add(nextLabel);

                    linkedLabel.put(nextLabel, set);

                    rst[x][y] = nextLabel;

                    nextLabel += 1;
                } else {
                    //若只有左其中一個為黑
                    if (leftBlack && !topBlack) {
                        rst[x][y] = rst[x - 1][y];

                    }
                    //若只有上其中一個為黑
                    if (topBlack && !leftBlack) {
                        rst[x][y] = rst[x][y - 1];

                    }

                    //若左及上皆為黑，取得最小的當(x,y)編號
                    if (leftBlack && topBlack) {


                        if (rst[x - 1][y] < rst[x][y - 1]) {
                            rst[x][y] = rst[x - 1][y];
                            //若左及上不相等
                            if (rst[x - 1][y] != rst[x][y - 1]) {

                                Set<Integer> set1 = linkedLabel.get(rst[x][y]);
                                set1.add(rst[x][y - 1]);


                                Set<Integer> set2 = linkedLabel.get(rst[x][y - 1]);
                                //set2.add( rst[x][y]);
                                set1.addAll(set2);

                                linkedLabel.put(rst[x][y], set1);
                                linkedLabel.put(rst[x][y - 1], set1);
                            }


                        } else {
                            rst[x][y] = rst[x][y - 1];
                            //若左及上不相等
                            if (rst[x - 1][y] != rst[x][y - 1]) {

                                Set<Integer> set1 = linkedLabel.get(rst[x][y]);
                                set1.add(rst[x - 1][y]);


                                Set<Integer> set2 = linkedLabel.get(rst[x - 1][y]);
                                //set.add( rst[x][y]);
                                set1.addAll(set2);

                                linkedLabel.put(rst[x][y], set1);
                                linkedLabel.put(rst[x - 1][y], set1);
                            }
                        }


                    }


                }

            }
        }

        //StartLicenseOCR_V2.doWriteHtml(rst, "lable_pass1.html", width, height);

        //System.out.println(" nextLabel :"+nextLabel);
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        int count = 1;
        for (int i = 0; i < linkedLabel.size(); i++) {

            Set<Integer> set = linkedLabel.get(i + 1);
            boolean isAdd = false;
            for (Integer j : set) {


                if (!map.containsKey(j)) {
                    //System.out.println(" j ["+j+"] count["+count+"] list["+set.size()+"]");
                    map.put(j, count);
                    isAdd = true;
                }

            }
            if (isAdd) {
                count++;
            }

        }

        Map<Integer, CharaterImage> labelsCharMap = labelingBean.getLabelsCharMap();

        // Begin the second pass.  Assign the new labels
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                //只要是為黑色，重新標記
                if (images[x][y] == foreground) {
                    Integer z = map.get(rst[x][y]);
                    //System.out.println("x["+x+"] y["+y+"] z["+z+"]");
                    rst[x][y] = z;
                    CharaterImage charaterImage = null;

                    //看是否已經取出此字元陣列
                    if (labelsCharMap.containsKey(z)) {
                        charaterImage = labelsCharMap.get(z);
                        if (!charaterImage.getTop().compareTop(x, y)) {
                            charaterImage.getTop().setXY(x, y);

                        }
                        ;
                        if (charaterImage.getBottom().compareTop(x, y)) {
                            charaterImage.getBottom().setXY(x, y);

                        }
                        if (!charaterImage.getLeft().compareLeft(x, y)) {
                            charaterImage.getLeft().setXY(x, y);

                        }
                        ;
                        if (charaterImage.getRight().compareLeft(x, y)) {
                            charaterImage.getRight().setXY(x, y);

                        }
                        ;

                    } else {
                        charaterImage = new CharaterImage();
                        charaterImage.getTop().setXY(x, y);
                        charaterImage.getBottom().setXY(x, y);
                        charaterImage.getLeft().setXY(x, y);
                        charaterImage.getRight().setXY(x, y);


                    }
                    labelsCharMap.put(z, charaterImage);
                }
            }
        }
        labelingBean.setLabelsImage(rst);
        //System.out.println(next_label+" regions");

        return labelingBean;
    }

    /**
     * 將得到的標記後圖片LabelingBean
     * 做簡單紀錄
     */
    public static int[][] doLabelingBeanMessage(LabelingBean labelingBean) {
        Map<Integer, CharaterImage> map = labelingBean.getLabelsCharMap();
        int[][] datas = labelingBean.getLabelsImage();
        int width = labelingBean.getWidth();
        int height = labelingBean.getHeight();
        int[][] hdatas = new int[width][height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                //只要是為黑色，重新標記
                if (datas[x][y] > 0) {
                    hdatas[x][y] = new Color().BLACK;
                    //System.out.println("--------");
                } else {
                    hdatas[x][y] = new Color().WHITE;

                }
            }
        }
        Set<Map.Entry<Integer, CharaterImage>> set = map.entrySet();
        for (Map.Entry<Integer, CharaterImage> entry : set) {
            CharaterImage charaterImage = entry.getValue();
            Pixel top = charaterImage.getTop();
            Pixel bottom = charaterImage.getBottom();
            Pixel left = charaterImage.getLeft();
            Pixel right = charaterImage.getRight();
            //System.out.println(" top ["+ top.toString()+"] bottom ["+bottom.toString()+"] left["+left.toString()+"] right["+right+"]");
            drawRectangle(charaterImage, hdatas, Color.RED);
        }
        return hdatas;
    }

    //畫出矩型範圍
    public static void drawRectangle(CharaterImage charaterImage, int[][] hdatas, int color) {

        Pixel top = charaterImage.getTop();
        Pixel bottom = charaterImage.getBottom();
        Pixel left = charaterImage.getLeft();
        Pixel right = charaterImage.getRight();

        int topY = top.getY();
        int bottomY = bottom.getY();
        int leftX = left.getX();
        int rightX = right.getX();

        //(左上->右上)畫最上及最下

        for (int x = leftX; x < (rightX + 1); x++) {
            hdatas[x][topY] = color;
            hdatas[x][bottomY] = color;
        }

        //(左下->右下)畫最左及最右
        for (int y = topY; y < (bottomY + 1); y++) {
            hdatas[leftX][y] = color;
            hdatas[rightX][y] = color;
        }

    }
}
