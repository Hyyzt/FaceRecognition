package org.opencv.samples.facedetect.nothing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 图片存储数据库类
 * Created by OlAy on 2017/7/20.
 */

public class ImageDB extends SQLiteOpenHelper {
    private final static int Version = 1;
    private final static String DB_NAME = "image_db";

    private String TABLE_IMAGE = "image_data";

    //字段名
    private String T_ID = "_id";
    private String T_NAME = "T_NAME";
    private String T_BLOB = "T_BLOB";
    private Context mContext;
    private String[] col = {T_ID, T_NAME, T_BLOB};

    public ImageDB(Context context) {
        super(context, DB_NAME, null, Version);
        mContext = context;
    }

    public ImageDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private String TABLE_IMAGE_CREATE = "create table " + TABLE_IMAGE + "(" + T_ID + " integer default 1 not null primary key autoincrement,"
            + T_NAME + " char(10) , " + T_BLOB + " blob);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("DB", TABLE_IMAGE_CREATE);
        db.execSQL(TABLE_IMAGE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Long createData(int res, String name) {
        Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), res);
        Long aLong = CreateData(bm, name);
        return aLong;
    }

    public Long CreateData(Bitmap bm, String name) {

        ContentValues initValues = new ContentValues();
        Long id = null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        /**
         *
         * Bitmap.CompressFormat.JPEG 和 Bitmap.CompressFormat.PNG
         * JPEG 与 PNG 的是区别在于 JPEG是有损数据图像，PNG使用从LZ77派生的无损数据压缩算法。
         * 这里建议使用PNG格式保存
         * 100 表示的是质量为100%。当然，也可以改变成你所需要的百分比质量。
         * os 是定义的字节输出流
         *
         * .compress() 方法是将Bitmap压缩成指定格式和质量的输出流
         */
        bm.compress(Bitmap.CompressFormat.PNG, 100, os);
        initValues.put(T_BLOB, os.toByteArray());//以字节形式保存
        initValues.put(T_NAME, name);
        SQLiteDatabase db = getDataBaseWrite();
        id = db.insert(TABLE_IMAGE, null, initValues);
        db.close();
        Log.e("TAG", name + "存入数据库");
        return id;
    }

    public Bitmap getImageById(int id) {
        List<Map<String, Object>> datas = getDatas();
        Map<String, Object> stringObjectMap = datas.get(id);
        Bitmap bitmap = (Bitmap) stringObjectMap.get("T_BLOB");
        return bitmap;
    }

    public List<Map<String, Object>> getDatas() {
        List<Map<String, Object>> list = null;
        SQLiteDatabase db = getDataBaseRead();
        Cursor cursor = db.query(TABLE_IMAGE, col, null, null, null, null, null);
        HashMap<String, Object> bindData = null;
        list = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            bindData = new HashMap<>();
            bindData.put(T_ID, cursor.getLong(0));
            bindData.put(T_NAME, cursor.getString(1));
            byte[] in = cursor.getBlob(2);
            Bitmap bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);
            bindData.put(T_BLOB, bmpout);
            list.add(bindData);
        }
        cursor.close();
        db.close();
        Log.e("DB", "Get the Bitmap");
        return list;
    }


    private SQLiteDatabase getDataBaseWrite() {
        return this.getWritableDatabase();
    }

    private SQLiteDatabase getDataBaseRead() {
        return this.getReadableDatabase();
    }
}
