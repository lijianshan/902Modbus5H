package com.dnk.xinfeng902.utils.dataBase;

import android.util.Log;

import com.dnk.xinfeng902.utils.Config;
import com.dnk.xinfeng902.utils.DevStateValue;
import com.dnk.xinfeng902.utils.Tools;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class DataBaseUtil {

    //初始化Realm
    //public static Realm realm = Realm.getDefaultInstance(); //默认default.realm
    static RealmConfiguration config = new RealmConfiguration.Builder()
            .name("sensorValue.realm") //文件名
            .schemaVersion(0) //版本号
            .build();
    public static Realm realm = Realm.getInstance(config);

    /**
     * 判断数据库是否是空：
     * 为空的话则初始化数据库
     * 非空的话，则把当前时间之后的数据清空
     */
    public static void checkDatabaseisEmptyAndInit(){
        if(realm.isEmpty()){
            Log.e("数据库","============ 空 =======");
            SensorModel sensorModel = new SensorModel();
            sensorModel.pm25 = 0;
            sensorModel.co2 = 0;
            sensorModel.tvoc = 0;
            sensorModel.temp = 0;
            sensorModel.humidity = 0;

            realm.beginTransaction();
            for(int i=0;i<24;i++){
                for(int j=0;j<6;j++){
                    sensorModel.time = Tools.formatHHMM(i,j*10);
                    realm.copyToRealmOrUpdate(sensorModel);
                }
            }
            realm.commitTransaction();
        }else{
            Log.e("数据库","============ 非空 =======");
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            SensorModel sensorModel = new SensorModel();
            sensorModel.pm25 = 0;
            sensorModel.co2 = 0;
            sensorModel.tvoc = 0;
            sensorModel.temp = 0;
            sensorModel.humidity = 0;

            realm.beginTransaction();
            for(int i=hour;i<24;i++){
                for(int j=((i==hour)?(minute/10):0);j<6;j++){
                    sensorModel.time = Tools.formatHHMM(i,j*10);
                    realm.copyToRealmOrUpdate(sensorModel);
                }
            }
            realm.commitTransaction();
        }
    }

    /**
     * 这是一个添加一条数据的方法
     */
    public static void add() {

        SensorModel sensorModel = new SensorModel();

        sensorModel.time = Tools.getsysHHMM();
        if (DevStateValue.pm25 == Config.NOTHING) sensorModel.pm25 = 0;
        else sensorModel.pm25 = DevStateValue.pm25;
        if (DevStateValue.co2 == Config.NOTHING) sensorModel.co2 = 0;
        else sensorModel.co2 = DevStateValue.co2;
        if (DevStateValue.tvoc == Config.NOTHING) sensorModel.tvoc = 0;
        else sensorModel.tvoc = DevStateValue.tvoc;
        if (DevStateValue.temp == Config.NOTHING) sensorModel.temp = 0;
        else sensorModel.temp = (int) DevStateValue.temp;
        if (DevStateValue.humidity == Config.NOTHING) sensorModel.humidity = 0;
        else sensorModel.humidity = DevStateValue.humidity;

        realm.beginTransaction();
        //realm.createObject(SensorModel.class); // Create a new object
        realm.copyToRealmOrUpdate(sensorModel);
        realm.commitTransaction();
    }

    /**
     * 这是一个查询的方法
     */
    public static List query(String type) {

        List<String> xDataList = new ArrayList<>();// x轴数据源
        List<Entry> yDataList = new ArrayList<>();// y轴数据数据源


        realm.beginTransaction();
        RealmResults<SensorModel> guests = realm.where(SensorModel.class)/*.equalTo("co2", 0)*/.findAll();
        realm.commitTransaction();


        int i = 0;
        if ("time".equals(type)) {
            for (SensorModel guest : guests) {
                xDataList.add(guest.time);
                i++;
            }
            return xDataList;
        } else if ("pm25".equals(type)) {
            for (SensorModel guest : guests) {
                yDataList.add(new Entry(guest.pm25, i));
                i++;
            }
            return yDataList;
        } else if ("co2".equals(type)) {
            for (SensorModel guest : guests) {
                yDataList.add(new Entry(guest.co2, i));
                i++;
            }
            return yDataList;
        } else if ("tvoc".equals(type)) {
            for (SensorModel guest : guests) {
                yDataList.add(new Entry(guest.tvoc, i));
                i++;
            }
            return yDataList;

        } else if ("temp".equals(type)) {

            for (SensorModel guest : guests) {
                yDataList.add(new Entry(guest.temp, i));
                i++;
            }
            return yDataList;
        } else if ("humidity".equals(type)) {
            for (SensorModel guest : guests) {
                yDataList.add(new Entry(guest.humidity, i));
                i++;
            }
            return yDataList;
        }
        return null;
    }

    /**
     * 这是一个删除一条数据的方法
     */
    public static void delete() {
        realm.beginTransaction();
        RealmResults<SensorModel> guests = realm.where(SensorModel.class).equalTo("co2", 0).findAll();
        for (SensorModel guest : guests) {
            if (guest.pm25 > 28) {
                guest.deleteFromRealm();
            }
        }
        realm.commitTransaction();
    }

    /**
     * 这是一条更新的方法
     */
    public static void updata() {
        realm.beginTransaction();
        RealmResults<SensorModel> guests = realm.where(SensorModel.class).equalTo("co2", 0).findAll();
        for (SensorModel guest : guests) {
            guest.pm25 = 48;
        }
        realm.commitTransaction();
    }
}