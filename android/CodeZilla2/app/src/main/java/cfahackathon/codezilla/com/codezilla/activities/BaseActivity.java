package cfahackathon.codezilla.com.codezilla.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cfahackathon.codezilla.com.codezilla.R;


/**
 * Created by ashis_000 on 2/22/2016.
 */
public class BaseActivity extends AppCompatActivity {

    ProgressBar progressBar;
    LinearLayout progressBarContainer;
    Toolbar toolbar;
    int toolbarHeight;

    LinearLayout connectionErrorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    void setProgressLayoutVariablesAndErrorVariables() {
        progressBar = (ProgressBar) findViewById(R.id.progressbarloading);
        progressBarContainer = (LinearLayout) findViewById(R.id.progressbarcontainer);

        connectionErrorLayout = (LinearLayout) findViewById(R.id.connection_error_layout);
    }

    void showLoadingLayout() {
        progressBar.setVisibility(View.VISIBLE);
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    void hideLoadingLayout() {
        progressBar.setVisibility(View.GONE);
        progressBarContainer.setVisibility(View.GONE);
    }

    void showErrorLayout() {
        connectionErrorLayout.setVisibility(View.VISIBLE);
    }

    void hideErrorLayout() {
        connectionErrorLayout.setVisibility(View.GONE);
    }

    void updateProgressText(String text) {
        TextView textView = (TextView) findViewById(R.id.progresstext);
        textView.setText(text);
    }
}
