package me.image.active;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import me.image.domain.CharaterImage;
import me.image.domain.DrawCG;
import me.image.domain.EPAAirBean;
import me.image.domain.ImageGestureDetector;
import me.image.domain.LabelingBean;
import me.image.domain.Pixel;
import me.image.domain.PixelImage;
import me.image.domain.ServiceCheckBean;
import me.image.domain.ViewPixelBean;
import me.image.service.ImageService;
import me.image.util.FileUtiil;
import me.image.util.ImageUtil;

public class ImageMEActivity extends Activity implements SurfaceHolder.Callback {

    private static final String LOGGER = ImageMEActivity.class.getName();
    /**
     * Called when the activity is first created.
     */
     /*宣告 Button、ImageView物件*/
    private ImageView mImageView01;

    private Button buttonPre;
    private Button buttonBack;
    SurfaceHolder surfaceHolder;
    Camera camera;
    //返回主畫面button
    private Button backbutton;

    private Button buttonExecute;

    private Button buttonExecuteDetail;
    /**
     * 暫存
     */
    private Button buttonSave;
    /**
     * 拍照
     */
    private Button buttonCam;

    private TextView messageText;

    private SurfaceView surfaceView1;

    ViewGroup vg;

    //第幾張圖片
    int picCount = -1;

    private File imgDir = null;

    private GestureDetector gestureScanner;

    public final static String DEFAULT_FOLDER = "/sdcard/DCIM/ImageME";

    //原始影像之資料夾
    public final static String IMAGE_FOLDER = DEFAULT_FOLDER + "/original";

    //影像處理後之資料夾
    public final static String PROCESS_FOLDER = DEFAULT_FOLDER + "/process";

    private int fieldImgXY[] = new int[2];

    private DrawCG mDrawCG;

    /**
     * 紀錄車牌4個角落之點
     */
    private Map<Integer, ViewPixelBean> carPeaks = new HashMap<Integer, ViewPixelBean>();
    /**
     * 代表現在已輪到第幾個角落可選擇
     */
    int count = 0;
    //最多僅可同時存在2點
    int maxCount = 2;

    PixelImage pixelImage = null;

    int[][] grays = null;

    int otsuThreshs = 0;

    float mTop = -1;//上邊界
    float mBottom = -1;//下邊界
    float mLeft = -1;//左邊界
    float mRight = -1;//右邊界
    //車牌切割區域
    int segWidth = -1;
    int segHeight = -1;
    int[][] segment = null;
    //車牌粗定位區域
    int detectWidth = -1;
    int detectHeight = -1;
    int[][] detectSeg = null;
    //Thread 判斷
    int doOnes = 0;

    String fileNamePre = null;

    EPAAirBean epaAirBean = null;

    int surfaceViewVisible = SurfaceView.INVISIBLE;

    int imageViewVisible = ImageView.VISIBLE;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jumpToPreviw();


    }

    /**
     * APP主畫面
     */
    public void jumpToPreviw() {
        setContentView(R.layout.main);
          /*實體化Button、ImageView物件*/
        mImageView01 = (ImageView) findViewById(R.id.imageview1);
        mImageView01.setVisibility(imageViewVisible);
        System.out.println("==>image view [" + mImageView01.getWidth() + "][" + mImageView01.getHeight() + "]");
        //預設圖片置中
        System.out.println("==>image view getScaleType[" + mImageView01.getScaleType() + "]");
        buttonPre = (Button) findViewById(R.id.buttonPre);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonCam = (Button) findViewById(R.id.buttonCam);

        buttonSave.setVisibility(Button.INVISIBLE);
        buttonExecute = (Button) findViewById(R.id.buttonExecute);
        buttonExecuteDetail = (Button) findViewById(R.id.buttonExecuteDetail);
        buttonExecuteDetail.setVisibility(Button.INVISIBLE);
        surfaceView1 = (SurfaceView) findViewById(R.id.surfaceView1);
        surfaceView1.setVisibility(surfaceViewVisible);
        if (surfaceViewVisible == SurfaceView.VISIBLE) {
            initCamera();
            initSurfaceView();
        }
        //允許
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        messageText = (TextView) findViewById(R.id.messageText);
        mDrawCG = new DrawCG(this);
        gestureScanner = new GestureDetector(this, new ImageGestureDetector());

        /**觸碰選取營幕上之車牌位置*/
        mImageView01.setOnTouchListener(new OnTouchListener() {


                                            public boolean onTouch(View v, MotionEvent event) {
                                                System.out.println("---ImageView on touch---[" + event.getPointerCount() + "]");
                                                int eventaction = event.getAction();
                                                int pointerCount = event.getPointerCount();
                                                int eventX = (int) event.getX();
                                                int eventY = (int) event.getY();
                                                System.out.println("==[" + eventX + "][" + eventY + "]");

                                                switch (pointerCount) {
                                                    case 1: //觸碰選取一點
                                                        System.out.println("===========do one touh [" + eventaction + "]");
                                                        if (eventaction == MotionEvent.ACTION_DOWN) {
                                                            doMark(event, 0);
                                                            doImageProcess();
                                                        }
                                                        //綁定僅處理ImageView
                                                        //gestureScanner.onTouchEvent(event);
                                                        //}
                                                        break;

                                                    case 2://同時觸碰選取兩點
                                                        System.out.println("===========do two touh [" + eventaction + "]");
                                                        //觸碰選取第一點
                                                        if (eventaction == MotionEvent.ACTION_POINTER_1_DOWN) {
                                                            doMark(event, 0);
                                                            doImageProcess();
                                                        }
                                                        //觸碰選取第二點
                                                        if (eventaction == MotionEvent.ACTION_POINTER_2_DOWN) {
                                                            doMark(event, 1);
                                                            doImageProcess();
                                                            //綁定僅處理ImageView
                                                            //gestureScanner.onTouchEvent(event);
                                                        }
                                                        break;

                                                }

                                                return true;
                                            }
                                        }


        );


        //設定讀取資料夾
        imgDir = new File(IMAGE_FOLDER);
        doScreenView(getImage());
        System.out.println("==>image view [" + mImageView01.getWidth() + "][" + mImageView01.getHeight() + "]");
		    /*設定ImageView底圖*/
        //mImageView01.setImageDrawable(getResources().getDrawable(R.drawable.right));
        //mImageView02.setImageDrawable(getResources().getDrawable(R.drawable.irdc));

        /**若按下照相**/
        buttonCam.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                if (surfaceView1.getVisibility() == SurfaceView.INVISIBLE) {
                    System.out.println("=======invisible=====");

                    surfaceViewVisible = SurfaceView.VISIBLE;

                    imageViewVisible = ImageView.INVISIBLE;

                    jumpToPreviw();


                } else {
                    buttonCam.setEnabled(false);
                    camera.autoFocus(afcb);
                    buttonCam.setEnabled(true);

                }
            }
        });

        /**向前一張圖片*/
        buttonPre.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                System.out.println("<---向前一張圖片");
                if (surfaceView1.getVisibility() == SurfaceView.VISIBLE) {
                    System.out.println("=======invisible=====");

                    surfaceViewVisible = SurfaceView.INVISIBLE;

                    imageViewVisible = ImageView.VISIBLE;
                    System.gc();
                    jumpToPreviw();


                } else {
                    picCount--;
                }
                segment = null;
                epaAirBean = null;
                messageText.setText("");
                buttonExecuteDetail.setVisibility(Button.INVISIBLE);
                doScreenView(getImage());
            }
        });
        /**向後一張圖片*/
        buttonBack.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                System.out.println("<---向後一張圖片");
                if (surfaceView1.getVisibility() == SurfaceView.VISIBLE) {
                    System.out.println("=======invisible=====");

                    surfaceViewVisible = SurfaceView.INVISIBLE;

                    imageViewVisible = ImageView.VISIBLE;
                    System.gc();
                    jumpToPreviw();


                } else {
                    picCount++;
                }
                segment = null;
                epaAirBean = null;
                messageText.setText("");
                buttonExecuteDetail.setVisibility(Button.INVISIBLE);
                doScreenView(getImage());
            }
        });

        /**存起來imageView圖片*/
        buttonSave.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                Bitmap savedBitmap = mDrawCG.getmBitmap();
                System.gc();

                String filename = PROCESS_FOLDER + "/" + fileNamePre + new Date().getTime() + ".jpg";
                File dest = new File(filename);

                try {
                    FileOutputStream out = new FileOutputStream(dest);
                    savedBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        buttonExecuteDetail.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View v) {
                jumpToShowDetail();
            }
        });

        /**執行影像處理*/
        buttonExecute.setOnClickListener(new Button.OnClickListener() {

                                             public void onClick(View v) {
                                                 if (doOnes == 0) {
                                                     try {

                                                         if (surfaceView1.getVisibility() == SurfaceView.VISIBLE) {
                                                             doOnes++;
                                                             System.out.println("=======invisible=====");

                                                             surfaceViewVisible = SurfaceView.INVISIBLE;

                                                             imageViewVisible = ImageView.VISIBLE;

                                                             jumpToPreviw();


                                                         } else {

                                                             doOnes++;
                                                             //doImageProcessTest();//做簡單的影像處理
                                                             //doImageProcess();//兩點觸控後車牌粗定位
                                                             if (segment != null) {
                                                                 Bitmap bitmap = null;
                                                                 System.out.println("=======segment [" + detectSeg.length + "]");
                                                                 if (detectSeg.length > 10) {
                                                                     bitmap = FileUtiil.doubleArrayToBitmap(detectSeg, detectWidth, detectHeight);

                                                                 }
//			    			 else{
//			    				 
//			    				 bitmap=FileUtiil.doubleArrayToBitmap(grays,  segWidth, segHeight);
//			    			 }

                                                                 // 設備識別碼
                                                                 String systemID = Build.ID;
                                                                 ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                                 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                                                 byte[] byteArray = stream.toByteArray();
                                                                 Log.i(LOGGER, " byteArray size [" + byteArray.length + "]");
                                                                 ImageService imageService = new ImageService();
                                                                 epaAirBean = imageService.doEPAService(byteArray, systemID);

                                                                 System.gc();
                                                                 setEpaAirBean(epaAirBean);
                                                             } else {
                                                                 messageText.setText("請選取車牌 ");
                                                             }
                                                         }
                                                     } catch (Exception e) {
                                                         // TODO Auto-generated catch block
                                                         e.printStackTrace();
                                                     } finally {
                                                         doOnes--;

                                                     }

                                                 }
                                             }
                                         }
        );


    }

    public void setEpaAirBean(EPAAirBean epaAirBean) {

        if (epaAirBean == null) {
            messageText.setText("網路連線問題");
            buttonExecuteDetail.setVisibility(Button.INVISIBLE);

        } else if (!epaAirBean.getCheckBean().getCheckResult()) {
            // messageText.setText("ttaaaaaaaaaaaaaaaa");
            if (epaAirBean.getCheckBean().getOcrResult() == null) {
                messageText.setText("OCR辨視失敗");

            } else {
                messageText.setText("車牌號碼:" + epaAirBean.getCheckBean().getOcrResult().trim());
            }
            buttonExecuteDetail.setVisibility(Button.INVISIBLE);

        } else {
            messageText.setText("車牌號碼:" + epaAirBean.getCheckBean().getOcrResult().trim());
            buttonExecuteDetail.setVisibility(Button.VISIBLE);
        }
    }


    /**
     * APP主畫面點選詳細內容導到此
     * method jumpToLayout2：將layout由main.xml切換成mylayout.xml
     */
    public void jumpToShowDetail() {
			  /* 將layout改成mylayout.xml */
        setContentView(R.layout.showdetail);
        TextView checkresult = (TextView) findViewById(R.id.checkresult);
        TextView license = (TextView) findViewById(R.id.license);
        TextView brandType = (TextView) findViewById(R.id.brandType);
        TextView usedate = (TextView) findViewById(R.id.usedate);
        TextView message = (TextView) findViewById(R.id.message);
        if (epaAirBean != null) {
            ServiceCheckBean serviceCheckBean = epaAirBean.getCheckBean();
            if (serviceCheckBean.getCheckResult()) {
                checkresult.setText("辨視成功");
                license.setText("車牌號碼:" + epaAirBean.getLicense());
                brandType.setText("廠牌:" + epaAirBean.getBrandType());
                usedate.setText("出廠日期:" + epaAirBean.getUseDate());
                message.setText(epaAirBean.getMessage());
            } else {
                checkresult.setText("辨視失敗");
                license.setText("辨視號碼:" + serviceCheckBean.getOcrResult());
                brandType.setText("");
                usedate.setText("");
                Log.i(LOGGER, " errorMessage :" + epaAirBean.getMessage());
                message.setText(serviceCheckBean.getErrorMessage());
            }
        }


        //imageView1=(ImageView)findViewById(R.id.imageView1);
        backbutton = (Button) findViewById(R.id.button3);
        this.backbutton.setOnClickListener(new Button.OnClickListener() {

            public void onClick(View arg0) {
                jumpToPreviw();
                setEpaAirBean(epaAirBean);
            }
        });
    }

    /*做簡單的影像處理並寫檔案**/
    public void doImageProcessTest() {

        //灰階處理
        grays = ImageUtil.toGrays(pixelImage.getPixels(), pixelImage.getWidth(), pixelImage.getHeight());


        doWriteTemp(((BitmapDrawable) mImageView01.getDrawable()).getBitmap(), fileNamePre + "_seg_red");
        //切割
        segWidth = (int) (mRight - mLeft);
        segHeight = (int) (mBottom - mTop);
        segment = new int[(int) segWidth][(int) segHeight];
        for (int x = (int) mLeft; x < (int) mRight; x++) {
            for (int y = (int) mTop; y < (int) mBottom; y++) {
                segment[(int) (x - mLeft)][(int) (y - mTop)] = grays[x][y];

            }
        }
        //doWriteTemp( FileUtiil.doubleArrayToBitmap(segment,  segWidth, segHeight),fileNamePre+"_00_seg_pure");

        //做sobel
        otsuThreshs = ImageUtil.otsuThreshs(segment, segWidth, segHeight);
        int[][] sobels = ImageUtil.doSobelGray(segment, segWidth, segHeight, otsuThreshs);
        //doWriteTemp( FileUtiil.doubleArrayToBitmap(sobels,  segWidth, segHeight),fileNamePre+"_01_seg_pure");

        doScreenDrawView(FileUtiil.doubleArrayToBitmap(sobels, segWidth, segHeight));


        //轉黑白

        int white = new Color().WHITE;
        int black = new Color().BLACK;
        for (int x = 0; x < segWidth; x++) {
            for (int y = 0; y < segHeight; y++) {
                if (segment[x][y] > otsuThreshs) {

                    segment[x][y] = white;
                } else {

                    segment[x][y] = black;
                }
            }
        }
        //doWriteTemp( FileUtiil.doubleArrayToBitmap(segment,  segWidth, segHeight),fileNamePre+"_02_seg_binary");

        // doScreenDrawView(FileUtiil.doubleArrayToBitmap(segment,  segWidth, segHeight));
        /**連通區域標記**/
        LabelingBean labelingBean = ImageUtil.labeling(segment, segWidth, segHeight);
        segment = ImageUtil.doLabelingBeanMessage(labelingBean);
        //doWriteTemp( FileUtiil.doubleArrayToBitmap(segment,  segWidth, segHeight),fileNamePre+"_03_seg_connectd_binary");

        labelingBean = ImageUtil.labeling(sobels, segWidth, segHeight);
        sobels = ImageUtil.doLabelingBeanMessage(labelingBean);
        //doWriteTemp( FileUtiil.doubleArrayToBitmap(sobels,  segWidth, segHeight),fileNamePre+"_04_seg_connectd_sobel");

        doScreenDrawView(FileUtiil.doubleArrayToBitmap(segment, segWidth, segHeight));

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        System.out.println("====onWindowFocusChanged====");
        super.onWindowFocusChanged(hasFocus);

        // Use onWindowFocusChanged to get the placement of
        // the image because we have to wait until the image
        // has actually been placed on the screen  before we
        // get the coordinates. That makes it impossible to
        // do in onCreate, that would just give us (0, 0).
        mImageView01.getLocationOnScreen(fieldImgXY);
        System.out.println("fieldImage lockation on screen: [" + fieldImgXY[0] + "][" + fieldImgXY[1] + "]");
        System.out.println("top [" + mImageView01.getTop() + "] left[" + mImageView01.getLeft() + "]");
        System.out.println("bottom [" + mImageView01.getBottom() + "] right[" + mImageView01.getRight() + "]");
        //System.out.println("=====取得image padding 之上下左右====");
        ///System.out.println( "padding top  [" +mImageView01.getPaddingTop()+"]padding left ["+ mImageView01.getPaddingLeft()+"]");
        //System.out.println( "padding bottom  [" +mImageView01.getPaddingBottom()+"] padding right ["+ mImageView01.getPaddingRight()+"]");
    }

    /**
     * 做營幕內容顯示
     */
    public void doScreenView(File file) {
        Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        System.out.println("Bitmap height [" + myBitmap.getHeight() + "] width[" + myBitmap.getWidth() + "]");
        mImageView01.setImageBitmap(myBitmap);
        String fileName = file.getName();
        fileNamePre = fileName.substring(0, fileName.lastIndexOf("."));
        //messageText.setText("==檔案名稱:"+file.getName());
        Bitmap viewBitmap = ((BitmapDrawable) mImageView01.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        ;
        pixelImage = new PixelImage(((BitmapDrawable) mImageView01.getDrawable()).getBitmap().copy(Bitmap.Config.ARGB_8888, true));
        ;
        // Drawable bd = new BitmapDrawable(viewBitmap);
        //Canvas canvas = new Canvas(viewBitmap);
        mDrawCG.setmBitmap(viewBitmap);
        System.out.println("viewBitmap height [" + viewBitmap.getHeight() + "] width[" + viewBitmap.getWidth() + "]");
        getBitmapOffset(mImageView01, true);
        Rect r = mImageView01.getDrawable().getBounds();
        int drawLeft = r.left;
        int drawTop = r.top;
        int drawRight = r.right;
        int drawBottom = r.bottom;
        System.out.println("drawLeft [" + drawLeft + "] drawTop[" + drawTop + "]");
        System.out.println("drawRight [" + drawRight + "] drawBottom[" + drawBottom + "]");


    }

    /*點兩下畫圓**/
    public void doScreenDrawView(Bitmap bitmap) {

        mImageView01.setImageBitmap(bitmap);


    }


    public File getImage() {

        File[] files = imgDir.listFiles();
        if (picCount < 0) {
            picCount = 0;

        } else if (picCount > (files.length - 1)) {
            picCount = files.length - 1;

        }
        return files[picCount];

        //ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);


    }


    public static int[] getBitmapOffset(ImageView img, Boolean includeLayout) {
        int[] offset = new int[2];

        //矩陣中的MSCALE用於處理縮放變換，MSKEW用於處理錯切變換，MTRANS用於處理平移變換，MPERSP用於處理透視變換
        //於Matrix依序有9個參數int MPERSP_0，int MPERSP_1 ，int MPERSP_2	
        //  	              int MSCALE_X，int MSCALE_Y，int MSKEW_X ，int MSKEW_Y
        //                    int MTRANS_X， int MTRANS_Y

        float[] values = new float[9];

        Matrix m = img.getImageMatrix();
        m.getValues(values);

        offset[0] = (int) values[5];
        offset[1] = (int) values[2];

        if (includeLayout) {
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) img.getLayoutParams();
            int paddingTop = (int) (img.getPaddingTop());
            int paddingLeft = (int) (img.getPaddingLeft());

            offset[0] += paddingTop + lp.topMargin;
            offset[1] += paddingLeft + lp.leftMargin;
        }
        float xScale = values[Matrix.MSCALE_X];
        System.out.println("============xScale [" + xScale + "]");
        System.out.println("==offset[0] [" + offset[0] + "] offset[1][" + offset[1] + "]");
        return offset;
    }

    final float[] getPointerCoords(ImageView view, MotionEvent e, int pointCount) {
        final int index = e.getActionIndex();
        System.out.println("----->index=" + index);
        final float[] coords = new float[]{e.getX(pointCount), e.getY(pointCount)};
        Matrix matrix = new Matrix();
        view.getImageMatrix().invert(matrix);
        matrix.postTranslate(view.getScrollX(), view.getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }

    public void doImageProcess() {
        //切割
        segWidth = (int) (mRight - mLeft);
        segHeight = (int) (mBottom - mTop);

        //因為為640X480畫面，以1/8當閥值
        int threshWidth = 80;
        int threshHeight = 60;
        if (segWidth >= threshWidth && segHeight >= threshHeight) {
            segment = new int[(int) segWidth][(int) segHeight];
            for (int x = (int) mLeft; x < (int) mRight; x++) {
                for (int y = (int) mTop; y < (int) mBottom; y++) {
                    segment[(int) (x - mLeft)][(int) (y - mTop)] = pixelImage.getPixels()[y * pixelImage.getWidth() + x];

                }
            }
            grays = ImageUtil.toGrays(segment, segWidth, segHeight);
            otsuThreshs = ImageUtil.otsuThreshs(grays, segWidth, segHeight);
            //做sobel
            if (otsuThreshs > 190) {
                otsuThreshs = ImageUtil.bestThresh(grays, segWidth, segHeight);

            }

            //轉黑白

            int white = Color.WHITE;
            int black = Color.BLACK;
            for (int x = 0; x < segWidth; x++) {
                for (int y = 0; y < segHeight; y++) {
                    if ((grays[x][y] & 0xff) > otsuThreshs) {

                        segment[x][y] = white;
                    } else {

                        segment[x][y] = black;
                    }
                }
            }

            /**連通區域標記**/
            //白色為前景，黑色為背景找出車牌

            LabelingBean labelingBean = ImageUtil.labeling(segment, segWidth, segHeight, Color.BLACK, Color.WHITE);

            System.out.println(" getLabelsCharMap size [" + labelingBean.getLabelsCharMap().size() + "]");
            //取出最大的邊界(因白色為前景的最大區域一般為車牌)
            CharaterImage maxCharImage = getLabelingBeanMax(labelingBean);
            if (maxCharImage != null) {


                Pixel top = maxCharImage.getTop();
                Pixel bottom = maxCharImage.getBottom();
                Pixel left = maxCharImage.getLeft();
                Pixel right = maxCharImage.getRight();


                //車牌粗定位
                detectWidth = right.getX() - left.getX() + 1;
                detectHeight = bottom.getY() - top.getY() + 1;
                detectSeg = new int[detectWidth][detectHeight];
                for (int x = left.getX(); x <= right.getX(); x++) {
                    for (int y = top.getY(); y <= bottom.getY(); y++) {
                        detectSeg[x - left.getX()][y - top.getY()] = pixelImage.getPixels()[(y + (int) mTop) * pixelImage.getWidth() + (x + (int) mLeft)];
                    }
                }
                //System.out.println(" top ["+ top.toString()+"] bottom ["+bottom.toString()+"] left["+left.toString()+"] right["+right+"]");
                //System.out.println(" top ["+ (top.getY()+(int)mTop)+"] bottom ["+(bottom.getY()+(int)mBottom)+"] left["+(left.getX()+(int)mLeft)+"] right["+ (right.getX()+(int)mLeft)+"]");
                //System.out.println(" mTop ["+ mTop+"] mBottom ["+mBottom+"] mLeft["+mLeft+"] mRight["+mRight+"]");
                mDrawCG.drawRect(left.getX() + (int) mLeft, top.getY() + (int) mTop, right.getX() + (int) mLeft, bottom.getY() + (int) mTop, Color.GREEN, 1);
                //segment=ImageUtil.doLabelingBeanMessage(labelingBean);
                //doScreenDrawView(FileUtiil.doubleArrayToBitmap(segment,  segWidth, segHeight));
                doScreenDrawView(mDrawCG.getmBitmap());
                //segment=ImageUtil.doLabelingBeanMessage(labelingBean);
                //doScreenDrawView(FileUtiil.doubleArrayToBitmap(segment,  segWidth, segHeight));
            }
        }
    }

    /**
     * 將得到的標記後圖片LabelingBean
     * 取出最大區塊
     */
    public static CharaterImage getLabelingBeanMax(LabelingBean labelingBean) {
        Map<Integer, CharaterImage> map = labelingBean.getLabelsCharMap();

        CharaterImage maxCharImage = null;

        //for(int i=0;i<map.size();i++){計數器有問題改日修正
        System.out.println("===>labeling size [" + map.size() + "]");
        Set<Map.Entry<Integer, CharaterImage>> set = map.entrySet();

        for (Map.Entry<Integer, CharaterImage> entry : set) {
            CharaterImage charaterImage = entry.getValue();
//			int key=i+1;
//			CharaterImage charaterImage=map.get(key);

            if (maxCharImage == null ||
                    (maxCharImage.getHeight() * maxCharImage.getWidth() < (charaterImage.getHeight() * charaterImage.getWidth()))
                    ) {

                maxCharImage = charaterImage;
            }

        }
        //BufferedImage segImageBinary=FileOperator.CreateBufferedImageRGB(datas, widtht, heightt,BufferedImage.TYPE_INT_RGB);
        //FileOperator.writeToFile(segImageBinary, fileName);
        return maxCharImage;
    }

    // 觸發選取車牌點
    public boolean doMark(MotionEvent e, int pointCount) {
        ViewPixelBean viewPixelBean = null;
        System.out.println("---------------onDoubleTap");
        float eventX = e.getX(pointCount);
        float eventY = e.getY(pointCount);
        System.out.println(" int view [" + eventX + "][" + eventY + "]");
//            int xOnField = eventX - fieldImgXY[0];
//            int yOnField = eventY - fieldImgXY[1];
//            System.out.println( "==>["+xOnField+"]["+yOnField+"]");
        float[] fs = getPointerCoords(mImageView01, e, pointCount);
        //System.out.println( "===>fs ["+fs.length+"]");
        System.out.println("in bitmap [" + fs[0] + "][" + fs[1] + "]");

        System.out.println("=========>carPeaks size before[" + carPeaks.size() + "]count [" + count + "]");
        if (carPeaks.containsKey(count)) {
            System.out.println(" get from carPeaks");
            viewPixelBean = carPeaks.get(count);
            System.out.println(" viewPixelBean [" + viewPixelBean + "]");
        } else {
            System.out.println(" ===new carPeaks");
            viewPixelBean = new ViewPixelBean();
            carPeaks.put(count, viewPixelBean);

        }
        System.out.println("=========>carPeaks size end[" + carPeaks.size() + "]count [" + count + "]");

        //設定新的選取點
        viewPixelBean.setViewX(eventX);
        viewPixelBean.setViewY(eventY);
        viewPixelBean.setBitmapX(fs[0]);
        viewPixelBean.setBitmapY(fs[1]);
        carPeaks.put(count, viewPixelBean);
        // Drawable bd = new BitmapDrawable(viewBitmap);
        //Canvas canvas = new Canvas(viewBitmap);
        mDrawCG.setmBitmap(pixelImage.getBitmap().copy(Bitmap.Config.ARGB_8888, true));

        //兩點才可以畫線甚至矩型
        if (carPeaks.size() == 2) {
            System.out.println("-----circle-----");
            ViewPixelBean v1 = carPeaks.get(0);
            ViewPixelBean v2 = carPeaks.get(1);
            float v1x = v1.getBitmapX();
            float v1y = v1.getBitmapY();
            float v2x = v2.getBitmapX();
            float v2y = v2.getBitmapY();
            System.out.println("v1x [" + v1x + "] v1y[" + v1y + "]");
            System.out.println("v2x [" + v2x + "] v2y[" + v2y + "]");
            setMargin(v1, v2);
            mDrawCG.drawCircle(v1.getBitmapX(), v1.getBitmapY(), Color.RED);
            mDrawCG.drawCircle(v2.getBitmapX(), v2.getBitmapY(), Color.RED);
        } else {
            System.out.println("=========~~~~~~~~~~~~~~~~~~~~~~");
            mDrawCG.drawCircle(viewPixelBean.getBitmapX(), viewPixelBean.getBitmapY(), Color.RED);

        }


        if (mTop != -1 && mBottom != -1 && mLeft != -1 && mRight != -1) {
            mDrawCG.drawRect((int) mLeft, (int) mTop, (int) mRight, (int) mBottom, Color.RED, 3);
        }


        doScreenDrawView(mDrawCG.getmBitmap());
        System.out.println("============>count start[" + count + "]");
        if (count == 1) {

            count = 0;
        } else {
            count++;

        }
        System.out.println("============>count end[" + count + "]");
        return false;
    }

    //設定所點選於原圖的上下左右
    public void setMargin(ViewPixelBean v1, ViewPixelBean v2) {

        float v1x = v1.getBitmapX();
        float v1y = v1.getBitmapY();
        float v2x = v2.getBitmapX();
        float v2y = v2.getBitmapY();
        //==========設定上下左右
        if (v1x > v2x) {
            mLeft = v2x;
            mRight = v1x;
        } else {
            mLeft = v1x;
            mRight = v2x;

        }
        if (v1y > v2y) {
            mTop = v2y;
            mBottom = v1y;
        } else {
            mTop = v1y;
            mBottom = v2y;

        }
    }

    /**
     * 將處理過程寫入
     */
    public void doWriteTemp(Bitmap savedBitmap, String fileName) {

        String filename = PROCESS_FOLDER + "/" + fileName + ".jpg";
        File dest = new File(filename);

        try {
            FileOutputStream out = new FileOutputStream(dest);
            savedBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    PictureCallback jpeg = new PictureCallback() {


        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i(LOGGER, " Eneter Method onPictureTaken :");
            //resetCamera();
            camera.stopPreview();
            Log.i(LOGGER, " stopPreview camera");
            String fileName = new Date().getTime() + ".jpeg";
            boolean isOK = false;
            try {
                FileOutputStream outStream = null;


                outStream = new FileOutputStream(IMAGE_FOLDER + "/" + fileName);
                outStream.write(data);
                outStream.close();
                isOK = true;


                //關閉流
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            } finally {
                surfaceViewVisible = SurfaceView.INVISIBLE;

                imageViewVisible = ImageView.VISIBLE;

                File[] files = imgDir.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File file = files[i];
                    if (file.getName().equals(fileName)) {

                        picCount = i;
                        break;
                    }
                    ;
                }

                jumpToPreviw();

            }

            //jumpToPreviw();
            //需要手動重新startPreview，否則停在拍下的瞬間
        }

    };

    private void initSurfaceView() {

        //imageView1=(ImageView)findViewById(R.id.imageView1);
        surfaceHolder = surfaceView1.getHolder();
        surfaceHolder.setFixedSize(getWindow().getWindowManager()
                .getDefaultDisplay().getWidth(), getWindow().getWindowManager()
                .getDefaultDisplay().getHeight());
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);


    }

    /**
     * 初始化照相機
     */
    private void initCamera() {
        int numberOfCameras = Camera.getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                camera = Camera.open(i);
            }
        }

        // 取得螢幕的寬高
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int vWidth = dm.widthPixels;
        int vHeight = dm.heightPixels;
        System.out.println("vWidth [" + vWidth + "]");
        System.out.println("vHeight [" + vHeight + "]");

        // 建立Camera.Parameters物件
        Parameters parameters = camera.getParameters();
        // 指定preview的螢幕大小
        parameters.setPreviewSize(640, 480);
        parameters.setPictureSize(640, 480);
        // 設定圖片解析度大小 ，約100萬畫素(3:2)
        // 超過100萬畫素容易out of memory而crash
        //parameters.setPictureSize(1248, 832);
        camera.setParameters(parameters);
        //攝像頭畫面顯示在Surface上
        camera.startPreview();
    }

    /* 相機重置 */
    private void resetCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();

            //camera=null;
            //System.gc();
            //釋放資源
            //camera.stopPreview();
            //關閉預覽

        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO Auto-generated method stub

    }

    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try {

            //設置參數
            camera.setPreviewDisplay(surfaceHolder);


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        System.out.println("surfaceDestroyed");

        resetCamera();
    }

    AutoFocusCallback afcb = new AutoFocusCallback() {


        public void onAutoFocus(boolean success, Camera camera) {
            // TODO Auto-generated method stub
            if (success) {
                //對焦成功才拍照
                camera.takePicture(null, null, jpeg);
            }
        }


    };
}