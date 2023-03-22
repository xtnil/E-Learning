package com.codestudioapps.learing.app.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.codestudioapps.learing.app.R;
import com.codestudioapps.learing.app.adapters.QuizAdapter;
import com.codestudioapps.learing.app.data.constant.AppConstant;
import com.codestudioapps.learing.app.data.preference.AppPreference;
import com.codestudioapps.learing.app.data.preference.PrefKey;
import com.codestudioapps.learing.app.listeners.ListItemClickListener;
import com.codestudioapps.learing.app.models.quiz.QuizModel;
import com.codestudioapps.learing.app.models.quiz.ResultModel;
import com.codestudioapps.learing.app.utility.ActivityUtilities;
import com.codestudioapps.learing.app.utility.AdsUtilities;
import com.codestudioapps.learing.app.utility.BeatBox;
import com.codestudioapps.learing.app.utility.DialogUtilities;
import com.codestudioapps.learing.app.utility.SoundUtilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class QuizActivity extends BaseActivity implements RewardedVideoAdListener, DialogUtilities.OnCompleteListener {

    private Activity mActivity;
    private Context mContext;
    private ImageButton mBtnSpeaker;
    private Button mBtnNext;
    private ImageView mImgFirstLife, mImgSecondLife, mImgThirdLife, mImgFourthLife, mImgFifthLife;

    private List<QuizModel> mItemList;
    ArrayList<String> mOptionList;
    ArrayList<String> mBackgroundColorList;

    private RecyclerView mRecyclerQuiz;
    private QuizAdapter mAdapter = null;
    private TextView mQuestionTextView;

    private int mQuestionPosition = 0;
    private int mScore = 0, mWrongAns = 0, mSkip = 0;
    private int mLifeCounter = 5;
    private boolean mUserHasPressed = false;
    private boolean mIsSkipped = false, mIsCorrect = false;
    private String mQuestionText, mGivenAnsText, mCorrectAnsText;
    private ArrayList<ResultModel> mResultList;

    private RewardedVideoAd mRewardedVideoAd;

    private BeatBox mBeatBox;
    private List<SoundUtilities> mSounds;
    private boolean isSoundOn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeRewardedAds();
        loadRewardedVideoAds();

        initVar();
        initView();
        initFunctionality();
        initListener();
    }

    private void initVar() {
        mActivity = QuizActivity.this;
        mContext = mActivity.getApplicationContext();

        mItemList = new ArrayList<>();
        mOptionList = new ArrayList<>();
        mBackgroundColorList = new ArrayList<>();
        mResultList = new ArrayList<>();

        mBeatBox = new BeatBox(mActivity);
        mSounds = mBeatBox.getSounds();
    }

    private void initView() {
        setContentView(R.layout.activity_quiz);

        mImgFirstLife = (ImageView) findViewById(R.id.first_life);
        mImgSecondLife = (ImageView) findViewById(R.id.second_life);
        mImgThirdLife = (ImageView) findViewById(R.id.third_life);
        mImgFourthLife = (ImageView) findViewById(R.id.fourth_life);
        mImgFifthLife = (ImageView) findViewById(R.id.fifth_life);
        mBtnSpeaker = (ImageButton) findViewById(R.id.btn_speaker);
        mBtnNext = (Button) findViewById(R.id.btn_next);

        mQuestionTextView = (TextView) findViewById(R.id.question_text);

        mRecyclerQuiz = (RecyclerView) findViewById(R.id.rvQuiz);
        mRecyclerQuiz.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter = new QuizAdapter(mContext, mActivity, mOptionList, mBackgroundColorList);
        mRecyclerQuiz.setAdapter(mAdapter);

        initToolbar(true);
        setToolbarTitle(getString(R.string.site_menu_quiz));
        enableUpButton();
        initLoader();

    }

    private void initFunctionality() {

        loadData();

        // show banner ads
        AdsUtilities.getInstance(mContext).showBannerAd((AdView) findViewById(R.id.adsView));
    }

    private void loadData() {
        showLoader();

        isSoundOn = AppPreference.getInstance(mActivity).getBoolean(PrefKey.KEY_SOUND, true);
        setSpeakerImage();

        loadJson();
    }

    private void setSpeakerImage() {
        if (isSoundOn) {
            mBtnSpeaker.setImageResource(R.drawable.ic_speaker);
        } else {
            mBtnSpeaker.setImageResource(R.drawable.ic_speaker_not);
        }
    }


    public void initListener() {
        mBtnSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSoundOn = !isSoundOn;
                AppPreference.getInstance(mActivity).setBoolean(PrefKey.KEY_SOUND, isSoundOn);
                setSpeakerImage();
            }
        });
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mUserHasPressed) {
                    FragmentManager manager = getSupportFragmentManager();
                    DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.skip_text), getString(R.string.skip_prompt), getString(R.string.yes), getString(R.string.no), AppConstant.BUNDLE_KEY_SKIP_OPTION);
                    dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
                } else {
                    updateResultSet();
                    setNextQuestion();
                }
            }
        });

        mAdapter.setItemClickListener(new ListItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                if (!mUserHasPressed) {
                    int clickedAnswerIndex = position;
                    if (mItemList.get(mQuestionPosition).getCorrectAnswer() != -1) {
                        for (int currentItemIndex = 0; currentItemIndex < mOptionList.size(); currentItemIndex++) {
                            if (currentItemIndex == clickedAnswerIndex && currentItemIndex == mItemList.get(mQuestionPosition).getCorrectAnswer()) {
                                mBackgroundColorList.set(currentItemIndex, AppConstant.COLOR_GREEN);
                                mScore++;
                                mIsCorrect = true;
                                if (isSoundOn) {
                                    mBeatBox.play(mSounds.get(AppConstant.BUNDLE_KEY_ZERO_INDEX));
                                }
                            } else if (currentItemIndex == clickedAnswerIndex && !(currentItemIndex == mItemList.get(mQuestionPosition).getCorrectAnswer())) {
                                mBackgroundColorList.set(currentItemIndex, AppConstant.COLOR_RED);
                                mWrongAns++;
                                if (isSoundOn) {
                                    mBeatBox.play(mSounds.get(AppConstant.BUNDLE_KEY_SECOND_INDEX));
                                }
                                decreaseLifeAndStatus();
                            } else if (currentItemIndex == mItemList.get(mQuestionPosition).getCorrectAnswer()) {
                                mBackgroundColorList.set(currentItemIndex, AppConstant.COLOR_GREEN);
                                ((LinearLayoutManager) mRecyclerQuiz.getLayoutManager()).scrollToPosition(currentItemIndex);
                            }
                        }
                    } else {
                        mBackgroundColorList.set(clickedAnswerIndex, AppConstant.COLOR_GREEN);
                        mScore++;
                        mIsCorrect = true;
                    }

                    mGivenAnsText = mItemList.get(mQuestionPosition).getAnswers().get(clickedAnswerIndex);
                    mCorrectAnsText = mItemList.get(mQuestionPosition).getAnswers().get(mItemList.get(mQuestionPosition).getCorrectAnswer());

                    mUserHasPressed = true;
                    mAdapter.notifyDataSetChanged();
                }
            }

        });

    }

    public void initializeRewardedAds() {
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.app_ad_id));

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
    }

    private void loadRewardedVideoAds() {
        mRewardedVideoAd.loadAd(getResources().getString(R.string.rewarded_ad_unit_id),
                new AdRequest.Builder().build());
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAds();
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        increaseLifeAndStatus();
        updateResultSet();
        setNextQuestion();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
    }

    @Override
    public void onRewardedVideoCompleted() {

    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }

    public void decreaseLifeAndStatus() {
        mLifeCounter--;
        setLifeStatus();
    }

    void increaseLifeAndStatus() {
        if (mLifeCounter < AppConstant.BUNDLE_KEY_MAX_LIFE) {
            mLifeCounter = AppConstant.BUNDLE_KEY_MAX_LIFE;
            setLifeStatus();
        }
    }

    public void setLifeStatus() {
        switch (mLifeCounter) {
            case 1:
                mImgFirstLife.setVisibility(View.VISIBLE);
                mImgSecondLife.setVisibility(View.GONE);
                mImgThirdLife.setVisibility(View.GONE);
                mImgFourthLife.setVisibility(View.GONE);
                mImgFifthLife.setVisibility(View.GONE);
                break;
            case 2:
                mImgFirstLife.setVisibility(View.VISIBLE);
                mImgSecondLife.setVisibility(View.VISIBLE);
                mImgThirdLife.setVisibility(View.GONE);
                mImgFourthLife.setVisibility(View.GONE);
                mImgFifthLife.setVisibility(View.GONE);
                break;
            case 3:
                mImgFirstLife.setVisibility(View.VISIBLE);
                mImgSecondLife.setVisibility(View.VISIBLE);
                mImgThirdLife.setVisibility(View.VISIBLE);
                mImgFourthLife.setVisibility(View.GONE);
                mImgFifthLife.setVisibility(View.GONE);
                break;
            case 4:
                mImgFirstLife.setVisibility(View.VISIBLE);
                mImgSecondLife.setVisibility(View.VISIBLE);
                mImgThirdLife.setVisibility(View.VISIBLE);
                mImgFourthLife.setVisibility(View.VISIBLE);
                mImgFifthLife.setVisibility(View.GONE);
                break;
            case 5:
                mImgFirstLife.setVisibility(View.VISIBLE);
                mImgSecondLife.setVisibility(View.VISIBLE);
                mImgThirdLife.setVisibility(View.VISIBLE);
                mImgFourthLife.setVisibility(View.VISIBLE);
                mImgFifthLife.setVisibility(View.VISIBLE);
                break;
            default:
                mImgFirstLife.setVisibility(View.GONE);
                mImgSecondLife.setVisibility(View.GONE);
                mImgThirdLife.setVisibility(View.GONE);
                mImgFourthLife.setVisibility(View.GONE);
                mImgFifthLife.setVisibility(View.GONE);
                break;
        }
    }

    public void updateResultSet() {
        mResultList.add(new ResultModel(mQuestionText, mGivenAnsText, mCorrectAnsText, mIsCorrect, mIsSkipped));
        mIsCorrect = false;
        mIsSkipped = false;
    }

    public void setNextQuestion() {
        if (isSoundOn) {
            mBeatBox.play(mSounds.get(AppConstant.BUNDLE_KEY_FIRST_INDEX));
        }
        mUserHasPressed = false;
        if (mQuestionPosition < mItemList.size() - 1 && mLifeCounter > 0) {
            mQuestionPosition++;
            // load full screen ad
            AdsUtilities.getInstance(mContext).loadFullScreenAd(mActivity);
            if (mQuestionPosition % AppConstant.ADS_INTERVAL == 0) {
                // show full-screen ads
                AdsUtilities.getInstance(mContext).showFullScreenAd();
            }
            updateQuestionsAndAnswers();
        } else if (mQuestionPosition < mItemList.size() - 1 && mLifeCounter == 0 && mRewardedVideoAd.isLoaded()) {
            FragmentManager manager = getSupportFragmentManager();
            DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.reward_dialog_title), getString(R.string.reward_dialog_message), getString(R.string.yes), getString(R.string.no), AppConstant.BUNDLE_KEY_REWARD_OPTION);
            dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
        } else {
            ActivityUtilities.getInstance().invokeScoreCardActivity(mActivity, ScoreCardActivity.class, mScore, mWrongAns, mSkip, mResultList, true);
        }
    }

    public void updateQuestionsAndAnswers() {
        mOptionList.clear();
        mBackgroundColorList.clear();
        ((LinearLayoutManager) mRecyclerQuiz.getLayoutManager()).scrollToPosition(AppConstant.BUNDLE_KEY_ZERO_INDEX);

        mOptionList.addAll(mItemList.get(mQuestionPosition).getAnswers());
        mBackgroundColorList.addAll(mItemList.get(mQuestionPosition).getBackgroundColors());
        mAdapter.notifyDataSetChanged();

        mQuestionTextView.setText(Html.fromHtml(mItemList.get(mQuestionPosition).getQuestion()));
        mQuestionText = mItemList.get(mQuestionPosition).getQuestion();
    }

    public void quizActivityClosePrompt() {
        FragmentManager manager = getSupportFragmentManager();
        DialogUtilities dialog = DialogUtilities.newInstance(getString(R.string.exit), getString(R.string.quiz_close_prompt), getString(R.string.yes), getString(R.string.no), AppConstant.BUNDLE_KEY_CLOSE_OPTION);
        dialog.show(manager, AppConstant.BUNDLE_KEY_DIALOG_FRAGMENT);
    }

    private void loadJson() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(AppConstant.QUESTION_FILE)));
            String temp;
            while ((temp = br.readLine()) != null)
                sb.append(temp);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parseJson(sb.toString());
    }

    public void parseJson(String jsonData) {
        try {

            JSONObject jsonObjMain = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObjMain.getJSONArray(AppConstant.JSON_KEY_QUESTIONNAIRY);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObj = jsonArray.getJSONObject(i);

                String question = jsonObj.getString(AppConstant.JSON_KEY_QUESTION);
                int correctAnswer = Integer.parseInt(jsonObj.getString(AppConstant.JSON_KEY_CORRECT_ANS));

                JSONArray jsonArray2 = jsonObj.getJSONArray(AppConstant.JSON_KEY_ANSWERS);
                ArrayList<String> contents = new ArrayList<>();
                ArrayList<String> backgroundColors = new ArrayList<>();
                for (int j = 0; j < jsonArray2.length(); j++) {
                    String item_title = jsonArray2.get(j).toString();
                    contents.add(item_title);
                    backgroundColors.add(AppConstant.COLOR_WHITE);
                }
                mItemList.add(new QuizModel(question, contents, correctAnswer, backgroundColors));
                Collections.shuffle(mItemList);
            }

            hideLoader();
            updateQuestionsAndAnswers();

        } catch (JSONException e) {
            e.printStackTrace();
            showEmptyView();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                quizActivityClosePrompt();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        quizActivityClosePrompt();
    }

    @Override
    public void onComplete(Boolean isOkPressed, String viewIdText) {
        if (isOkPressed) {
            if (viewIdText.equals(AppConstant.BUNDLE_KEY_CLOSE_OPTION)) {
                ActivityUtilities.getInstance().invokeNewActivity(mActivity, MainActivity.class, true);
            } else if (viewIdText.equals(AppConstant.BUNDLE_KEY_SKIP_OPTION)) {
                mSkip++;
                mIsSkipped = true;
                mGivenAnsText = getResources().getString(R.string.skipped_text);
                mCorrectAnsText = mItemList.get(mQuestionPosition).getAnswers().get(mItemList.get(mQuestionPosition).getCorrectAnswer());
                updateResultSet();
                setNextQuestion();
            } else if (viewIdText.equals(AppConstant.BUNDLE_KEY_REWARD_OPTION)) {
                mRewardedVideoAd.show();
            }
        } else if (!isOkPressed && viewIdText.equals(AppConstant.BUNDLE_KEY_REWARD_OPTION)) {
            ActivityUtilities.getInstance().invokeScoreCardActivity(mActivity, ScoreCardActivity.class, mScore, mWrongAns, mSkip, mResultList, true);
        }
    }
}
