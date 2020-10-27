package ace.infosolutions.guruprasadhotelapp.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GenerateNumber {
    private String completed_date;
    private String Bill_NO;

    public String generateCompletedDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Date date = new Date();
        String datetoday = format.format(date);
        completed_date = datetoday.replaceAll("/", "-");
        return completed_date;
    }

  /* public String generateTime(){
       SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");

   }
    */


    public String generateBillNo() {
        Random r = new Random();
        Date date = new Date();
        char a = (char) (r.nextInt(26) + 'a');
        char b = (char) (r.nextInt(26) + 'a');
        long timelilli = date.getTime();
        String timeString = String.valueOf(timelilli);
        String randomMilli = timeString.substring(timeString.length() - 5);
        Bill_NO = String.valueOf(a).concat(String.valueOf(b)).toUpperCase().concat(randomMilli);
        return Bill_NO;
    }
}
