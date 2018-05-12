package org.cra.contextrecognition.services;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.internal.bind.TimeTypeAdapter;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReadingsSaverService {
    private final String filePrefix = "readings";
    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");


    public void saveReadings(Context context ,List readings) {

        FileOutputStream f = null;
        try {
            f = new FileOutputStream(new File(context.getFilesDir() + "/" + filePrefix + "-" + sdf.format(new Date())));
            ObjectOutputStream o = new ObjectOutputStream(f);

            o.writeObject(readings);

            o.close();
            f.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public <T> T getReadings(Context context, String fileName){
        FileInputStream fi = null;
        try {
            fi = new FileInputStream(new File(context.getFilesDir() + "/" +fileName));
            ObjectInputStream oi = new ObjectInputStream(fi);

            // Read objects
            T o = (T) oi.readObject();

            oi.close();
            fi.close();
            return o;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> listReadings(Context context) {

        File directory = new File(context.getFilesDir().toString());
        List<String> fileNames = new ArrayList<>();
        for (File f : directory.listFiles()) {
            fileNames.add(f.getName());
        }
        return  fileNames;
    }


}
