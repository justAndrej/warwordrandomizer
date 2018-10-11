package com.justandrej.fun.wordrandomizer;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ActivityRW extends AppCompatActivity {

    private TextFileHelper mTextFileHelper;
    private Button mNewWordButton;
    private TextView mWordView;
    private static final int TOTAL_WORDS = 362632;
    private static final String TAG = "MainActivity";
    private static final String SAVED_WORDS = "SavedWords";
    private List<String> mWordList;
    private boolean mDidntLoadFlag;
    private GetRandomWord mGetRandomWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rw);
        mTextFileHelper = new TextFileHelper();
        mWordList = new ArrayList<>();
        mDidntLoadFlag = false;
        mGetRandomWord = null;
        mNewWordButton = (Button)findViewById(R.id.button);
        mWordView = (TextView)findViewById(R.id.textView);

        String s = PreferenceManager.getDefaultSharedPreferences(this).getString(SAVED_WORDS, null);
        if(s != null){
            String[] arr = s.split(" ");
            mWordList.addAll(Arrays.asList(arr));
        }

        mNewWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mWordList.size() < 8) {
                    GetRandomWord getRandomWord = getRandomWordInstance();
                    if (getRandomWord != null) {
                        getRandomWord.execute();
                        if(mWordList.size() != 0){
                            setFirstWordInList();
                        } else {
                            mDidntLoadFlag = true;
                        }
                    }
                } else {
                    setFirstWordInList();
                }

            }
        });
    }

    private void saveWordList(){
        try {
            StringBuilder stringBuilder = new StringBuilder();
            if(mWordList.size() != 0) {
                for (String str : mWordList) {
                    stringBuilder.append(str);
                    stringBuilder.append(" ");
                }
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString(SAVED_WORDS, stringBuilder.toString()).apply();
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to save words " + e);
            e.printStackTrace();
        }
    }

    private void setFirstWordInList() {
        mWordView.setText(mWordList.get(0));
        mWordList.remove(0);
        saveWordList();
    }

    private GetRandomWord getRandomWordInstance(){
        if(mGetRandomWord == null){
            mGetRandomWord = new GetRandomWord();
            return mGetRandomWord;
        }
        return null;
    }


    private class GetRandomWord extends AsyncTask<Void, Void, String>{


        @Override
        protected String doInBackground(Void... voids) {
            Log.i(TAG, "Task executing...");
            Random rand = new Random();

            while(mWordList.size() <= 15){
                mWordList.add(mTextFileHelper.getWord(rand.nextInt(TOTAL_WORDS) + 1, getApplicationContext()));
            }

            return mWordList.get(0);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mGetRandomWord = null;
            if(mDidntLoadFlag) {
                setFirstWordInList();
                mDidntLoadFlag = false;
            }
        }
    }

}
