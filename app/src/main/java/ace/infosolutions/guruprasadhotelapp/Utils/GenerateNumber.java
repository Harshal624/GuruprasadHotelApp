package ace.infosolutions.guruprasadhotelapp.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GenerateNumber {
    private String completed_date;
    private String Bill_NO;

    public String generateDateOnly() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();
        String dateCurrent = format.format(date);
        return dateCurrent.replaceAll("/", "-");
    }

    public String generateTimeOnly() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String timeCurrent = format.format(date);
        return timeCurrent;
    }
    


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
