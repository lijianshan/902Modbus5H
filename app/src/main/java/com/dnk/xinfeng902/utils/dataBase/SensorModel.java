package com.dnk.xinfeng902.utils.dataBase;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SensorModel extends RealmObject {
    @PrimaryKey
    public String time;
    public int pm25;
    public int co2;
    public int tvoc;
    public int temp;
    public int humidity;
}
