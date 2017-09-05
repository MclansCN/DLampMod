package com.mc6m.mod.dlampmod.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Tools {
    public static int scale16To10(String str) {
        return Integer.parseInt(str, 16);
    }

    public static String loadURLJson(String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL urlObject = new URL(url);
            URLConnection uc = urlObject.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static boolean versionCompare(String oldVersion, String newVersion) {
        String[] olds = oldVersion.split("\\.");
        String[] news = newVersion.split("\\.");
        if (olds.length != news.length) {
            return true;
        }
        boolean needUpdate = false;
        for (int i = 0; i < olds.length; i++) {
            int oldInt = Integer.parseInt(olds[i]);
            int newInt = Integer.parseInt(news[i]);
            if (oldInt < newInt) {
                needUpdate = true;
                break;
            }
        }
        return needUpdate;
    }
}
