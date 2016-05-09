package com.my.instantmessag.mydb;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.my.instantmessag.base.BaseApplication;
import com.my.instantmessag.entity.ContactsBean;
import com.my.instantmessag.entity.FriendCircleBean;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by dllo onDetailClick 16/3/4.
 */
public class DBHelper implements PersonInfo, FriendCircleInfo {
    private SQLiteDatabase db;
    private DaoMaster.DevOpenHelper helper;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;
    private MyDataDao myDataDao;
    private Context context = BaseApplication.getContext();
    private static ParseObject parseObject;

    //单例
    private static DBHelper dbHelper;

    public static DBHelper getInstance() {
        if (null == dbHelper) {
            dbHelper = new DBHelper();
        }
        return dbHelper;
    }


    private DBHelper() {

    }

    public void saveMessage(String user, String name, String body, String type, String time) {
        helper = new DaoMaster.DevOpenHelper(context, "MyDB.db", null);
        db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        myDataDao = mDaoSession.getMyDataDao();

        MyData myData = new MyData(user, name, body, type, time);
        myDataDao.insertOrReplace(myData);
    }

    public ArrayList<MyData> getMessage(String name) {
        ArrayList<MyData> beans = new ArrayList();
        helper = new DaoMaster.DevOpenHelper(context, "MyDB.db", null);
        db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        QueryBuilder queryBuilder = mDaoSession.getMyDataDao().queryBuilder();
        queryBuilder.where(MyDataDao.Properties.Name.eq(name));

        List<MyData> data = queryBuilder.list();
        if (null != data) {
            for (int i = 0; i < data.size(); i++) {
                beans.add(data.get(i));
            }
        }


        return beans;
    }


    public void deleteMessage(String name) {
        helper = new DaoMaster.DevOpenHelper(context, "MyDB.db", null);
        db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        myDataDao = mDaoSession.getMyDataDao();
        QueryBuilder qb = mDaoSession.getMyDataDao().queryBuilder();
        DeleteQuery bd = qb.where(MyDataDao.Properties.Name.eq(name)).buildDelete();
        bd.executeDeleteWithoutDetachingEntities();

    }

    public ArrayList<MyData> getContactsBeans() {
        ArrayList<MyData> data = new ArrayList<>();
        List<String> body = new ArrayList<>();
        List<String> time = new ArrayList<>();
        List<String> name = new ArrayList<>();
        helper = new DaoMaster.DevOpenHelper(context, "MyDB.db", null);
        db = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
        QueryBuilder queryBuilder = mDaoSession.getMyDataDao().queryBuilder();
        queryBuilder.where(MyDataDao.Properties.User.eq(EMChatManager.getInstance().getCurrentUser()));
        List<MyData> allData = queryBuilder.list();
        int j = 0;
        for (int i = 0; i < allData.size(); i++) {
            if (!name.contains(allData.get(i).getName())) {
                j++;
                name.add(allData.get(i).getName());
                body.add(allData.get(i).getBody());
                time.add(allData.get(i).getTime());
            } else {
                body.set(j - 1, allData.get(i).getBody());
                time.set(j - 1, allData.get(i).getTime());
            }
        }
        for (int i = 0; i < name.size(); i++) {
            MyData bean = new MyData(name.get(i), body.get(i), time.get(i));
            data.add(bean);
        }
        return data;
    }

    /**
     * 格式化时间,将毫秒转换为分秒:格式
     *
     * @param time
     * @return
     */
    public static String formatTime(long time) {
        Date nowTime = new Date(time);
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String retStrFormatNowDate = sdFormatter.format(nowTime);

        return retStrFormatNowDate;
    }

    public static Bitmap bytesToBitmap(byte[] b) {
        if (b.length != 0) {

            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static void getFriendCircleInfo(List<String> friends, final FriendCircleInfoCallBack callBack) {
        final List<FriendCircleBean> data = new ArrayList<>();
        parseObject = new ParseObject(FRIEND_TABLE_NAME);
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(FRIEND_TABLE_NAME);
        parseQuery.addDescendingOrder(SUBMIT_TIME);
        parseQuery.whereContainedIn(USER_NAME, friends);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (null == e) {
                    if (0 != objects.size()) {
                        for (int i = 0; i < objects.size(); i++) {
                            String username = objects.get(i).getString(USER_NAME);
                            List<List<String>> comment = objects.get(i).getList(COMMENT);
                            long time = objects.get(i).getLong(SUBMIT_TIME);
                            int good = objects.get(i).getInt(GOOD);
                            Bitmap img = null;
                            try {
                                if (null != objects.get(i).getParseFile(CONTENT_IMG)) {
                                    img = bytesToBitmap(objects.get(i).getParseFile(CONTENT_IMG).getData());
                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            List<String> friends = objects.get(i).getList(FRIEND_NAME);
                            String content = objects.get(i).getString(CONTENT);
                            FriendCircleBean bean = new FriendCircleBean(content, good, comment, img, time, username, friends);
                            data.add(bean);
                        }
                        callBack.getBean(data);
                    }
                } else {
                    Toast.makeText(BaseApplication.getContext(), "失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void getPersonInfo(final String username, final PersonInfoCallBack callBack) {
        parseObject = new ParseObject(PERSON_TABLE_NAME);
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(PERSON_TABLE_NAME);
        parseQuery.whereEqualTo(USER, username);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Bitmap headImg = null, coverImg = null;
                try {
                    if (null != objects.get(0).getParseFile(HEAD_IMG)) {
                        headImg = bytesToBitmap(objects.get(0).getParseFile(HEAD_IMG).getData());
                    }
                    if (null != objects.get(0).getParseFile(COVER_IMG)) {
                        coverImg = bytesToBitmap(objects.get(0).getParseFile(COVER_IMG).getData());
                    }
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                String nickName = objects.get(0).getString(NICK_NAME);
                String sex = objects.get(0).getString(SEX);
                ContactsBean bean = new ContactsBean(username, headImg, sex, nickName, coverImg);
                callBack.getBean(bean);
            }
        });
    }

    public static void getPersonInfoList( List<String> names, final PersonInfoCallBack callBack, final String username) {
        parseObject = new ParseObject(PERSON_TABLE_NAME);
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery(PERSON_TABLE_NAME);
        parseQuery.whereContainedIn(USER, names);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    List<ContactsBean> beans = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        Bitmap headImg = null, coverImg = null;
                        try {
                            if (null != objects.get(i).getParseFile(HEAD_IMG)) {
                                headImg = bytesToBitmap(objects.get(i).getParseFile(HEAD_IMG).getData());
                            }
                            if (null != objects.get(i).getParseFile(COVER_IMG)) {
                                coverImg = bytesToBitmap(objects.get(i).getParseFile(COVER_IMG).getData());
                            }
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        String name=objects.get(i).getString(USER);
                        String nickName = objects.get(i).getString(NICK_NAME);
                        String sex = objects.get(i).getString(SEX);
                        ContactsBean bean = new ContactsBean(name, headImg, sex, nickName, coverImg);
                        beans.add(bean);
                        if (name.equals(username)){
                            callBack.getBean(bean);
                        }
                    }
                    callBack.getBeans(beans);
                }
            }
        });
    }

}
