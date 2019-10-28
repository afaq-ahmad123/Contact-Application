package com.example.smd_a3;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

    public class DBhelper extends OrmLiteSqliteOpenHelper {

        private static final String DATABASE_NAME = "udara";
        private static final int DATABASE_VERSION = 1;

        private Dao<ContactDB, Long> userItemDBS;
        public DBhelper(Context context) {
            super(context, DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {

            try {
                TableUtils.createTable(connectionSource, ContactDB.class);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
            try {
                if(checkTableExist(database,"contacts"))
                    TableUtils.dropTable(connectionSource,ContactDB.class,false);

                onCreate(database,connectionSource);
            }catch (SQLException e){
                e.printStackTrace();
            }
        }

        private boolean checkTableExist(SQLiteDatabase database, String tableName){
            Cursor c = null;
            boolean tableExist = false;
            try {
                c = database.query(tableName, null,null,null,null,null,null);
                tableExist = true;
            }catch (Exception e){

            }
            return tableExist;
        }

        public Dao<ContactDB, Long> getUserItemDao() throws SQLException{
            if(userItemDBS == null){
                userItemDBS = getDao(ContactDB.class);
            }
            return userItemDBS;
        }

        @Override
        public void close() {
            userItemDBS = null;
            super.close();
        }

        public void clearTable() throws SQLException{
            TableUtils.clearTable(getConnectionSource(),ContactDB.class);
        }
}
