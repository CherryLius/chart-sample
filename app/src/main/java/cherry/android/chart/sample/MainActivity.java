package cherry.android.chart.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cherry.android.chart.PieChartView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    PieChartView pieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pieChartView = (PieChartView) findViewById(R.id.pieChartView);
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.button2).setOnClickListener(this);
        pieChartView.setOnPieSelectedListener(new PieChartView.OnPieSelectedListener() {
            @Override
            public void onPieSelected(int position, float value, String title) {
                Toast.makeText(MainActivity.this, "select pie " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                pieChartView.random();
                break;
            case R.id.button2:
                startActivity(new Intent(this, PathActivity.class));
                break;
        }
    }
}
