package com.justandrej.fun.warwordrandomizer;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TextFileHelper {
    private static final String TAG = "TextFileHelper";


    public String getWord(int number, Context context) {
        String result = "";
//        String[] list;
//
        try {
//
//            list = context.getAssets().list("/");
//            for(String str:list){
//                Log.i(TAG, str);
//            }

            int countRed = 0;
            List<String> lineWords = new ArrayList<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open("lt1.txt")));

            do{
                String line;

                countRed += lineWords.size();
                lineWords.clear();

                line = br.readLine();
                String[] arrayString = line.split(" ");
                for(String s:arrayString){
                    if(s.length() > 2){
                        lineWords.add(s);
                    }
                }

            } while ((countRed + lineWords.size()) < number);

            result = lineWords.get((number - countRed) - 1);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found " + e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Read file error " + e);
            e.printStackTrace();
        }

        return result;
    }


    public int getWordsCount(Context context) {
        int result = 0;

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open("lt1.txt")));

            String line;

            while((line = br.readLine()) !=null){
                String[] arrayString = line.split(" ");
                for(String s:arrayString){
                    if(s.length() > 2){
                        result++;
                    }
                }

            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found " + e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Read file error " + e);
            e.printStackTrace();
        }

        return result;
    }

}
