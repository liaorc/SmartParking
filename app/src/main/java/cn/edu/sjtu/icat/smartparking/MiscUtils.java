package cn.edu.sjtu.icat.smartparking;

import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ruochen on 2015/10/13.
 */
public class MiscUtils {
    static private boolean isSameDay(Calendar c1, Calendar c2) {
        boolean sameYear = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
        boolean sameMonth = c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
        boolean sameDay = c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
        return sameDay && sameMonth && sameYear;
    }



    static public CharSequence getTimeDescription(Date d) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d);
        if(isSameDay(c1, c2)) {
            return DateFormat.format("今天HH:mm", d);
        } else {
            c2.add(Calendar.DATE, -1);
            if(isSameDay(c1, c2)) {
                return DateFormat.format("昨天HH:mm", d);
            } else {
                return DateFormat.format("MM月dd日", d);
            }
        }
    }

    static public String getTimeDescription(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        if(isSameDay(c1, c2)) {
            return "今日"+d1.getHours()+":"+d1.getMinutes();
        } else {
            c2.add(Calendar.DATE, -1);
            if(isSameDay(c1, c2)) {
                return "昨日"+d1.getHours()+":"+d1.getMinutes();
            } else {
                c2.add(Calendar.DATE, 2);
                if(isSameDay(c1, c2)) {
                    return "明日"+d1.getHours()+":"+d1.getMinutes();
                }else {
                    return DateFormat.format("MM月dd日", d1).toString();
                }
            }
        }
    }
    static public String getDistanceDescription(int dis) {
        if( dis <= 1000 ) {
            return dis+"米";
        } else {
            double f = 1.0* dis / 1000;
            return (new java.text.DecimalFormat("#.00").format(f)) + "公里";
        }
    }

    static private String getTimeAhead(long diffSec) {
        if( diffSec < 60*60 ) {
            return (diffSec/60) + "分钟";
        } else {
            return diffSec / (3600) + "小时" + ((diffSec % 36000 / 60 > 0) ? ((diffSec % 3600 / 60) + "分钟") : "");
        }
    }

    static public String getTimeDiffDescription(Date d1, Date d2) {

        long millis1 = d1.getTime();
        long millis2 = d2.getTime();
        if ( millis1 > millis2 ) {
            long diffSec = (millis1 - millis2) / 1000;
            //Log.d("MIscUtils", "ahead: " + diffSec);
            if ( diffSec <= 60 * 60 *8 ) {
                return getTimeAhead(diffSec) + "后";
            } else {
                return getTimeDescription(d1, d2);
            }
        } else {
            long diffSec = (millis2 - millis1) / 1000;
            if ( diffSec <= 60 * 60 *8 ) {
                return getTimeAhead(diffSec) + "前";
            } else {
                return getTimeDescription(d1, d2);
            }
        }
    }

    static public Bitmap str2Bitmap(String str, int w, int h) throws WriterException {
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, w, h);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        Log.d("test??test", "h: " + height + ", w:" + width);
        //二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(matrix.get(x, y)){
                    pixels[y * width + x] = 0xff000000;
                }

            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
}