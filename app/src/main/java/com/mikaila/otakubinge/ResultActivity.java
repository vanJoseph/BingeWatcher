package com.mikaila.otakubinge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

/**
 * <h1>ResultActivity</h1>
 * Result activity to show anime details and allow user to calculate binge time
 *
 * @author  Donovan Wilder (dwilder1181)
 * @version 1.0
 * @since   2017-06-05
 */
public class ResultActivity extends AppCompatActivity {
    private ImageView img;
    private TextView title;
    private TextView episodes;
    private TextView duration;
    private TextView description;
    private TextView rawBingeTimeTextView;
    private TextView completionDateTextView;
    private Spinner frequencySpinner;
    private String frequency="day"; //default value;
    private EditText numOfEpisodesEditText;
    private Anime mAnime;
    private int mDuration;
    private int mNumberOfEpisodes;

    /**
     * Executes when activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        mAnime = (Anime) intent.getSerializableExtra("anime");

        img = (ImageView) findViewById(R.id.result_image_view);
        title = (TextView) findViewById(R.id.result_title_text_view);
        title.setText(mAnime.get_title_english());
        episodes = (TextView) findViewById(R.id.results_episodes_text_view);
        episodes.setText(mAnime.get_total_episodes());
        duration = (TextView) findViewById(R.id.result_runtime_text_view);
        duration.setText(mAnime.get_duration() + " mins");
        mDuration = Integer.valueOf(mAnime.get_duration());
        mNumberOfEpisodes = Integer.valueOf(mAnime.get_total_episodes());

        description = (TextView) findViewById(R.id.result_synopsis_text_view);
        description.setMovementMethod(new ScrollingMovementMethod());

        // Sets views on UI
        description.setText(mAnime.get_description());
        numOfEpisodesEditText = (EditText) findViewById(R.id.result_number_of_episodes_edit_text);
        numOfEpisodesEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                calculateCompletionDate();
                return false;
            }
        });
        frequencySpinner = (Spinner) findViewById(R.id.result_frequency_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ResultActivity.this, R.array.time_frequency, android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);
        frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String frequency = (String) parent.getItemAtPosition(position);
                ResultActivity.this.frequency = frequency;
                calculateCompletionDate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        completionDateTextView = (TextView) findViewById(R.id.result_completion_date_text_view);
        rawBingeTimeTextView = (TextView) findViewById(R.id.result_raw_binge_time_text_view);
        rawBingeTimeTextView.setText(TimeCalculator.calculateRawBingeTime(mDuration*mNumberOfEpisodes));
        completionDateTextView = (TextView) findViewById(R.id.result_completion_date_text_view);

        new LoadImageFromUrl(img).execute(mAnime.get_image_url());
    }

    /**
     * Calculates completion date and sets it text view
     */
    public void calculateCompletionDate() {
        mNumberOfEpisodes = Integer.valueOf(numOfEpisodesEditText.getText().toString());

        if (mNumberOfEpisodes > 0) {
            Date completionDate = TimeCalculator.getFrequencyEndDate(ResultActivity.this.mAnime, new Date(), frequency, mNumberOfEpisodes);
            completionDateTextView.setText(TimeCalculator.converDateToFormatedString(completionDate));
        }
        else {
            Toast.makeText(getApplicationContext(), "Enter a value greater than 0.", Toast.LENGTH_LONG).show();
        }
    }
}