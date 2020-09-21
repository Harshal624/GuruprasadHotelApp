package ace.infosolutions.guruprasadhotelapp;

import android.util.Log;

import java.util.Date;
import java.util.Random;

/*public class GenerateBill {
    public static String KOT_NO;
    public static String Bill_NO;

    //Logic of generating KOT number
    public void generateKOT() {
        Date date = new Date();
        Random r = new Random();
        long timelilli = date.getTime();
        String timeString = String.valueOf(timelilli);
        String randomMilli = timeString.substring(timeString.length() - 3);
        char c = (char)(r.nextInt(26) + 'a');
        String c1 = String.valueOf(c).toUpperCase();
        KOT_NO = c1.concat(randomMilli);
        Log.e("random", KOT_NO);

    }
    //Logic of generating Bill no of the customer
    public void generateBillNO(){
        Random r = new Random();
        Date date = new Date();
        char a = (char)(r.nextInt(26) + 'a');
        char b = (char)(r.nextInt(26) + 'a');
        long timelilli = date.getTime();
        String timeString = String.valueOf(timelilli);
        String randomMilli = timeString.substring(timeString.length() - 5);
        Bill_NO = String.valueOf(a).concat(String.valueOf(b)).toUpperCase().concat(randomMilli);
        Log.e("billno", Bill_NO);
    }
}*/
