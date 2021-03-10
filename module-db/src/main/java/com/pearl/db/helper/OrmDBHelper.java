package com.pearl.db.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

import com.ab.util.ArraysAndLists;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.pearl.db.DBModule;

import java.sql.SQLException;
import java.util.List;

/**
 * 
 * @author shuwoom
 * @email 294299195@qq.com
 * @date 2015-9-2
 * @update 2015-9-2
 * @des OrmDBHelper
 */
public class OrmDBHelper extends OrmLiteSqliteOpenHelper {
	public static final String DB_NAME = "db_main";
	public static final int DB_VERSION = DBModule.getIns().getCurrentDBVersion();

	public OrmDBHelper(Context context, String databaseName, CursorFactory factory, int databaseVersion) {
		super(context, DB_NAME, factory, DB_VERSION);
	}

	public OrmDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
            List<Class> tables = DBModule.getIns().getTables();
            for (int i = 0; i < tables.size(); i++) {
                TableUtils.createTableIfNotExists(connectionSource, tables.get(i));
            }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
    
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int currVersion) {
        for (int i = oldVersion; i < currVersion; i++) {
            List<String> states = DBModule.getIns().getUpdateStatement(i, i + 1);
            if(ArraysAndLists.isNotEmpty(states)){
                for (int j = 0; j < states.size(); j++) {
                    sqLiteDatabase.execSQL(states.get(j));
                }
            }
        }
    }
}