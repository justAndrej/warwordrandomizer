package com.justandrej.fun.wordrandomizer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ActivityRW extends AppCompatActivity {

    private static TextFileHelper mTextFileHelper;
    private TextView mWordView;
    private static final int TOTAL_WORDS = 362632;
    private static final String TAG = "MainActivity";
    private static final String SAVED_WORDS = "SavedWords";
    private List<String> mWordList;
    private fillWordList mFillWordList;
    private setRandomWord mSetRandomWord;
    private boolean mIsFillingWordList = false;
    private boolean mIsWordViewSetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rw);
        mTextFileHelper = new TextFileHelper();
        mWordList = new ArrayList<>();
        mFillWordList = null;
        mSetRandomWord = null;
        Button newWordButton = (Button) findViewById(R.id.button);
        mWordView = (TextView)findViewById(R.id.textView);

        String s = PreferenceManager.getDefaultSharedPreferences(this).getString(SAVED_WORDS, null);
        if(s != null){
            String[] arr = s.split(" ");
            mWordList.addAll(Arrays.asList(arr));
        }

        newWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    fillWordList fillWordList = getFillWordListInstance();
                    if (fillWordList != null) {
                        fillWordList.execute();
                    }
                    if (mWordList.size() == 0){
                        mSetRandomWord = getSetRandomWordInstance();
                        if(mSetRandomWord != null){
                            mSetRandomWord.execute();
                        }
                    } else {
                        setFirstWordInList();
                    }

                Log.i(TAG, "Word list is: " + getWordListAsString());
            }
        });
    }

    private void saveWordList(){
//        try {
//            StringBuilder stringBuilder = new StringBuilder();
//            if(mWordList.size() != 0) {
//                for (String str : mWordList) {
//                    stringBuilder.append(str);
//                    stringBuilder.append(" ");
//                }
//                PreferenceManager.getDefaultSharedPreferences(this).edit().putString(SAVED_WORDS, stringBuilder.toString()).apply();
//            }
//        } catch (NullPointerException e) {
//            Log.e(TAG, "Failed to save words " + e);
//            e.printStackTrace();
//        }

        String str = getWordListAsString();

        if(str != null)
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(SAVED_WORDS, str).apply();
    }

    private String getWordListAsString(){
        StringBuilder stringBuilder = new StringBuilder();
        if(mWordList.size() != 0) {
            for (String str : mWordList) {
                stringBuilder.append(str);
                stringBuilder.append(" ");
            }
            return stringBuilder.toString();
        }
        return null;
    }

    private void setFirstWordInList() {
        mWordView.setText(mWordList.get(0));
        mWordList.remove(0);
        saveWordList();
    }

    private fillWordList getFillWordListInstance(){
        if(!mIsFillingWordList){
            mFillWordList = new fillWordList(this);
            return mFillWordList;
        }
        return null;
    }

    private setRandomWord getSetRandomWordInstance(){
        if(!mIsWordViewSetting){
            mSetRandomWord = new setRandomWord(this);
            return mSetRandomWord;
        }
        return null;
    }




    private static class fillWordList extends AsyncTask<Void, Void, Void>{

        private WeakReference <ActivityRW> mActivityRWWeakReference;

        fillWordList(ActivityRW context){
            mActivityRWWeakReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mActivityRWWeakReference.get().mIsFillingWordList = true;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i(TAG, "Word-filling executing...");

            ActivityRW activity = mActivityRWWeakReference.get();

            Random rand = new Random();

            while(activity.mWordList.size() < 21){
                activity.mWordList.add(mTextFileHelper.getWord(rand.nextInt(TOTAL_WORDS) + 1, activity));
                activity.saveWordList();
                Log.i(TAG, "Word list is: " + activity.getWordListAsString());
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            Log.i(TAG, "Word-filling finished!");
            super.onPostExecute(aVoid);
            ActivityRW activity = mActivityRWWeakReference.get();
            activity.mIsFillingWordList = false;
            mActivityRWWeakReference.clear();
        }
    }
    
    
    private static class setRandomWord extends AsyncTask <Void, Void, String>{

        private WeakReference <ActivityRW> mActivityRWWeakReference;

        setRandomWord(ActivityRW context){
            mActivityRWWeakReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mActivityRWWeakReference.get().mIsWordViewSetting = true;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Log.i(TAG, "Random-word setter executing...");

            ActivityRW activity = mActivityRWWeakReference.get();

            Random rand = new Random();
            return mTextFileHelper.getWord(rand.nextInt(TOTAL_WORDS) + 1, activity);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ActivityRW activity = mActivityRWWeakReference.get();
            activity.mIsWordViewSetting = false;
            activity.mWordView.setText(s);
            Log.i(TAG, "Random word setted: " + s);
        }
    }
    
    

}
