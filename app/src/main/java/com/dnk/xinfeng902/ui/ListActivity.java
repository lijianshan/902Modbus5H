package com.dnk.xinfeng902.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dnk.xinfeng902.R;
import com.dnk.xinfeng902.utils.ChartUtil;
import com.dnk.xinfeng902.utils.dataBase.DataBaseUtil;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends Activity {

    Button pm25Btn;
    Button co2Btn;
    Button tvocBtn;
    Button tempBtn;
    Button humiBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);

        pm25Btn = findViewById(R.id.list_pm25);
        co2Btn = findViewById(R.id.list_co2);
        tvocBtn = findViewById(R.id.list_tvoc);
        tempBtn = findViewById(R.id.list_temp);
        humiBtn = findViewById(R.id.list_humitiy);

        getSensorChar("pm25");
    }
    /**
     * 五大参数选择按键
     */
    public void sensorTypeClk(View view){

        pm25Btn.setBackgroundColor(Color.parseColor("#696969"));
        co2Btn.setBackgroundColor(Color.parseColor("#696969"));
        tvocBtn.setBackgroundColor(Color.parseColor("#696969"));
        tempBtn.setBackgroundColor(Color.parseColor("#696969"));
        humiBtn.setBackgroundColor(Color.parseColor("#696969"));

        switch (view.getId()) {
            case R.id.list_pm25:
                pm25Btn.setBackgroundColor(Color.parseColor("#006400"));
                getSensorChar("pm25");
                break;
            case R.id.list_co2:
                co2Btn.setBackgroundColor(Color.parseColor("#006400"));
                getSensorChar("co2");
                break;
            case R.id.list_tvoc:
                tvocBtn.setBackgroundColor(Color.parseColor("#006400"));
                getSensorChar("tvoc");
                break;
            case R.id.list_temp:
                tempBtn.setBackgroundColor(Color.parseColor("#006400"));
                getSensorChar("temp");
                break;
            case R.id.list_humitiy:
                humiBtn.setBackgroundColor(Color.parseColor("#006400"));
                getSensorChar("humidity");
                break;
        }
    }
    /**
     * 获取对应参数到列表
     */
    public void getSensorChar(String type){

        LineChart lineChart;// 声明图表控件
        List xDataList = new ArrayList<>();// x轴数据源
        List yDataList = new ArrayList<>();// y轴数据数据源

        lineChart = (LineChart) findViewById(R.id.lineChart);//绑定控件
        switch (type) {
            case "pm25":
                xDataList = DataBaseUtil.query("time");
                yDataList =DataBaseUtil.query("pm25");
                //显示图表,参数（ 上下文，图表对象， X轴数据，Y轴数据，图表标题，曲线图例名称，坐标点击弹出提示框中数字单位）
                ChartUtil.showChart(this, lineChart, xDataList, yDataList, "PM2.5趋势图", "PM2.5(0表示没有采集到数据) / 时间", "ug/m3");
                break;
            case "co2":
                xDataList = DataBaseUtil.query("time");
                yDataList =DataBaseUtil.query("co2");
                //显示图表,参数（ 上下文，图表对象， X轴数据，Y轴数据，图表标题，曲线图例名称，坐标点击弹出提示框中数字单位）
                ChartUtil.showChart(this, lineChart, xDataList, yDataList, "CO2趋势图", "CO2/时间", "PPM");
                break;
            case "tvoc":
                xDataList = DataBaseUtil.query("time");
                yDataList =DataBaseUtil.query("tvoc");
                //显示图表,参数（ 上下文，图表对象， X轴数据，Y轴数据，图表标题，曲线图例名称，坐标点击弹出提示框中数字单位）
                ChartUtil.showChart(this, lineChart, xDataList, yDataList, "TVOC趋势图", "TVOC/时间", "");
                break;
            case "temp":
                xDataList = DataBaseUtil.query("time");
                yDataList =DataBaseUtil.query("temp");
                //显示图表,参数（ 上下文，图表对象， X轴数据，Y轴数据，图表标题，曲线图例名称，坐标点击弹出提示框中数字单位）
                ChartUtil.showChart(this, lineChart, xDataList, yDataList, "温度趋势图", "温度/时间", "度");
                break;
            case "humidity":
                xDataList = DataBaseUtil.query("time");
                yDataList =DataBaseUtil.query("humidity");
                //显示图表,参数（ 上下文，图表对象， X轴数据，Y轴数据，图表标题，曲线图例名称，坐标点击弹出提示框中数字单位）
                ChartUtil.showChart(this, lineChart, xDataList, yDataList, "湿度趋势图", "湿度/时间", "RH%");
                break;
        }
    }
}
