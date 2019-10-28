package com.example.smd_a3;
import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.List;

public class DBmanager {
    private final String TAG = DBmanager.this.getClass().getSimpleName();
    private static Context mContext;
    private static DBmanager INSTANCE;
    private DBhelper databaseHelper;

    private Dao<ContactDB, Long> userItemDao;
    private static String INDEX = "index";
    private static String NAME = "name";
    private static String AGE = "age";
    private static String ID = "id";


    public DBmanager(Context mContext) {
        Log.i(TAG, "DatabaseManager");
        this.mContext = mContext;
        databaseHelper = OpenHelperManager.getHelper(mContext, DBhelper.class);

        try {
            userItemDao = databaseHelper.getUserItemDao();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static DBmanager getInstance(Context context){
        if(INSTANCE == null) INSTANCE = new DBmanager(context);
        return INSTANCE;
    }

    public void releaseDB(){
        if (databaseHelper != null){
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
            INSTANCE = null;
        }
    }

    public int clearAllData(){
        try {
            if (databaseHelper == null) return -1;
            databaseHelper.clearTable();
            return 0;
        }catch (SQLException e){
            e.printStackTrace();
            return -1;
        }
    }

    public boolean isUserExisting(String index){
        QueryBuilder queryBuilder = userItemDao.queryBuilder();
        boolean flag = false;
        try {
            if(queryBuilder.where().eq(INDEX,index).countOf()>0){
                flag = true;
            }else {
                flag = false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return flag;
    }

    public int insertUserItem(ContactDB userItemDB, boolean isEdit){
        int count = 0;
        try {
            UpdateBuilder updateBuilder = userItemDao.updateBuilder();
            String contact = userItemDB.getContact() != null ? userItemDB.getContact() : "";
            String name = userItemDB.getName() != null ? userItemDB.getName(): "";
            String email = userItemDB.getEmail() != null ? userItemDB.getEmail(): "";

            if(userItemDao == null) return -1;

            if(isUserExisting(email)){
                Log.i(TAG,"this user exist");
                count = 1;
                if(isEdit){
                    deleteUser(email);
                    userItemDao.create(userItemDB);

                }


            }else {
                count = 0;
                userItemDao.create(userItemDB);
            }
            return count;
        }catch (SQLException e){
            e.printStackTrace();
            return  -1;
        }
    }

    public int deleteUser(String index){
        try {
            if(userItemDao == null) return -1;
            DeleteBuilder deleteBuilder = userItemDao.deleteBuilder();
            if(index != null || !index.isEmpty()) deleteBuilder.where().eq(INDEX,index);
            deleteBuilder.delete();
            Log.i(TAG,"user deleted");
            return 0;
        }catch (SQLException e){
            e.printStackTrace();
            return  -1;
        }
    }

    public List<ContactDB> getAllUsers(){
        try {
            if (userItemDao == null)return null;
            return userItemDao.queryForAll();
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

}
