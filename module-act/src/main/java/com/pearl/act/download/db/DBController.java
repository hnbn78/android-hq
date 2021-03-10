package com.pearl.act.download.db;

import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.pearl.act.download.entity.DownloadEntry;
import com.pearl.act.download.utils.Trace;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 
 * @author shuwoom
 * @email 294299195@qq.com
 * @date 2015-9-2
 * @update 2015-9-2
 * @des DBController
 */
public class DBController {
    private static DBController mInstance;
    private OrmDBHelper mDBhelper;

    private DBController(Context context) {
        mDBhelper = new OrmDBHelper(context);
        mDBhelper.getWritableDatabase();
    }

    public static DBController getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DBController(context);
        }
        return mInstance;
    }

    public synchronized void newOrUpdate(DownloadEntry entry) {
        try {
            Dao<DownloadEntry, String> dao = mDBhelper.getDao(DownloadEntry.class);
            dao.createOrUpdate(entry);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized ArrayList<DownloadEntry> queryAll() {
        Dao<DownloadEntry, String> dao;
        try {
            dao = mDBhelper.getDao(DownloadEntry.class);
            return (ArrayList<DownloadEntry>) dao.query(dao.queryBuilder().prepare());
        } catch (SQLException e) {
            Trace.e(e.getMessage());
            return null;
        }
    }

    public synchronized DownloadEntry queryByKey(String key) {
        try {
            Dao<DownloadEntry, String> dao = mDBhelper.getDao(DownloadEntry.class);
            return dao.queryForId(key);
        } catch (SQLException e) {
            Trace.e(e.getMessage());
            return null;
        }
    }
    
    public synchronized void deleteByKey(String key){
    	 try {
			Dao<DownloadEntry, String> dao = mDBhelper.getDao(DownloadEntry.class);
			dao.deleteById(key);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

}
