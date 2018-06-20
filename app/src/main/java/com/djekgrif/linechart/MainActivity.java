package com.djekgrif.linechart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        List<String> yLabels = new ArrayList<String>();
        yLabels.add("$0.0");
        yLabels.add("$100");
        yLabels.add("$200");
        yLabels.add("$300");
        yLabels.add("$400");
        List<String> xLabels = new ArrayList<String>();
        xLabels.add("Sep");
        xLabels.add("Oct");
        xLabels.add("Nov");
        xLabels.add("Des");
        xLabels.add("Jan");
        xLabels.add("Feb");
        List<Float> values = new ArrayList<Float>();
        values.add(100f);
        values.add(0f);
        values.add(300f);
        values.add(260f);
        values.add(400f);
        values.add(200f);
        values.add(250f);
        values.add(100f);


        LineChartView chartView = findViewById(R.id.line_chart);
        chartView.setSmoothSize(0.45f);
        chartView.setFillBottom(false);
        chartView.setShowYAxis(false);
        chartView.setShowPoints(false);
        chartView.setAxisTextSize(R.dimen.axis_text_size);
        chartView.setAxisYLabels(yLabels);
        chartView.setAxisXLabels(xLabels);
        chartView.setChartLineData(values);

        LineChartView chartPointView = findViewById(R.id.line_chart_point);
        chartPointView.setFillBottom(false);
        chartPointView.setShowYAxis(false);
        chartPointView.setShowXAxis(false);
        chartPointView.setAxisTextSize(R.dimen.axis_text_size);
        chartPointView.setAxisTextColor(R.color.colorAxisText);
        chartPointView.setAxisYLabels(yLabels);
        chartPointView.setAxisXLabels(xLabels);
        chartPointView.setChartLineData(values);

        LineChartView chartFullView = findViewById(R.id.line_chart_full);
        chartFullView.setSmoothSize(0.3f);
        chartFullView.setAxisTextSize(R.dimen.axis_text_size);
        chartFullView.setAxisTextColor(R.color.colorAxisText);
        chartFullView.setAxisYLabels(yLabels);
        chartFullView.setAxisXLabels(xLabels);
        chartFullView.setChartLineData(values);
    }
}
