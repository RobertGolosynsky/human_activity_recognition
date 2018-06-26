package org.cra.contextrecognition.services;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatService {
    private static SimpleDateFormat aFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public static String format(Date date){
        return aFormat.format(date);
    }
}
