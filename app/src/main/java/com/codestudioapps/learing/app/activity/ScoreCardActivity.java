package com.codestudioapps.learing.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdView;
import com.codestudioapps.learing.app.R;
import com.codestudioapps.learing.app.adapters.ResultAdapter;
import com.codestudioapps.learing.app.data.constant.AppConstant;
import com.codestudioapps.learing.app.models.quiz.ResultModel;
import com.codestudioapps.learing.app.utility.ActivityUtilities;
import com.codestudioapps.learing.app.utility.AdsUtilities;

import java.util.ArrayList;

public class ScoreCardActivity extends BaseActivity implements OnChartValueSelectedListener {

    private Activity mActivity;
    private Context mContext;
    private Button mBtnShare, mBtnPlayAgain;
    private TextView mScoreTextView, mWrongAnsTextView, mSkipTextView, mGreetingTextView;
    private int mScore, mWrongAns, mSkip;
    private ArrayList<ResultModel> mResultList;

    private ResultAdapter mAdapter = null;
    private RecyclerView mRecyclerResult;

    private PieChart mPieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mActivity = ScoreCardActivity.this;
        mContext = mActivity.getApplicationContext();

        Intent intent = getIntent();
        if (intent != null) {
            mScore = intent.getIntExtra(AppConstant.BUNDLE_KEY_SCORE, 0);
            mWrongAns = intent.getIntExtra(AppConstant.BUNDLE_KEY_WRONG_ANS, 0);
            mSkip = intent.getIntExtra(AppConstant.BUNDLE_KEY_SKIP, 0);
            mResultList = intent.getParcelableArrayListExtra(AppConstant.BUNDLE_KEY_ITEM);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_score_card);

        mRecyclerResult = (RecyclerView) findViewById(R.id.rvContent);
        mRecyclerResult.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        mBtnShare = (Button) findViewById(R.id.btn_share);
        mBtnPlayAgain = (Button) findViewById(R.id.btn_play_again);

        mScoreTextView = (TextView) findViewById(R.id.txt_score);
        mWrongAnsTextView = (TextView) findViewById(R.id.txt_wrong);
        mSkipTextView = (TextView) findViewById(R.id.txt_skip);
        mGreetingTextView = (TextView) findViewById(R.id.greeting_text);

        initToolbar(true);
        setToolbarTitle(getResources().getString(R.string.score_card));
        enableUpButton();

    }

    public void initFunctionality() {

        mScoreTextView.setText(String.valueOf(mScore));
        mWrongAnsTextView.setText(String.valueOf(mWrongAns));
        mSkipTextView.setText(String.valueOf(mSkip));

        float actualScore = ((float) mScore / (float) (mScore + mWrongAns + mSkip)) * AppConstant.MULTIPLIER_GRADE;
        switch (Math.round(actualScore)) {
            case 10:
            case 9:
            case 8:
                mGreetingTextView.setText(Html.fromHtml(getResources().getString(R.string.greeting_text3)));
                break;
            case 7:
            case 6:
            case 5:
                mGreetingTextView.setText(Html.fromHtml(getResources().getString(R.string.greeting_text2)));
                break;
            default:
                mGreetingTextView.setText(Html.fromHtml(getResources().getString(R.string.greeting_text1)));
                break;

        }

        showPieChart();

        mAdapter = new ResultAdapter(mContext, mActivity, mResultList);
        mRecyclerResult.setAdapter(mAdapter);

        // show full-screen ads
        AdsUtilities.getInstance(mContext).showFullScreenAd();
        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    public void initListener() {
        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.sharing_text, mScore) + " https://play.google.com/store/apps/details?id=" + mActivity.getPackageName());
                ;
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
            }
        });
        mBtnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, QuizPromptActivity.class, true);
            }
        });
    }

    public void showPieChart() {
        mPieChart = (PieChart) findViewById(R.id.piechart);
        mPieChart.setUsePercentValues(true);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setTransparentCircleRadius(AppConstant.TRANSPARENT_CIRCLE_RADIUS);
        mPieChart.setHoleRadius(AppConstant.TRANSPARENT_CIRCLE_RADIUS);
        mPieChart.setDescription(getString(R.string.score_card));
        mPieChart.animateXY(AppConstant.ANIMATION_VALUE, AppConstant.ANIMATION_VALUE);

        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        yvalues.add(new Entry(mScore, AppConstant.BUNDLE_KEY_ZERO_INDEX));
        yvalues.add(new Entry(mWrongAns, AppConstant.BUNDLE_KEY_SECOND_INDEX));
        yvalues.add(new Entry(mSkip, AppConstant.BUNDLE_KEY_FIRST_INDEX));
        PieDataSet dataSet = new PieDataSet(yvalues, AppConstant.EMPTY_STRING);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add(getString(R.string.score));
        xVals.add(getString(R.string.wrong));
        xVals.add(getString(R.string.skipped));
        PieData data = new PieData(xVals, dataSet);

        // In percentage Term
        data.setValueFormatter(new PercentFormatter());
        mPieChart.setData(data);


    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        if (e == null)
            return;
    }

    @Override
    public void onNothingSelected() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
    }

}
