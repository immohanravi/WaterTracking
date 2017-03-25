package net.robot_inc.watertracking;

import android.graphics.Bitmap;

/**
 * Created by takaiwa.net on 2016/04/08.
 */
public class Data {
    public Bitmap image;
    public String id;
    public String name;
    public String number;
    public String address;
    public int pendingAmount;
    public Data(Bitmap image,String id,String name, String number, String address,int pendingAmount) {
        this.image = image;
        this.id = id;
        this.name = name;
        this.number = number;
        this.address = address;
        this.pendingAmount = pendingAmount;

    }
}
