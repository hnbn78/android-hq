package com.ab.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.ab.debug.AbDebug;
import com.ab.global.AbConstants;
import com.ab.model.AbKeepBaseEntity;
import com.ab.util.DBUtils;
import com.ab.util.Maps;
import com.ab.util.Nums;
import com.ab.util.Strs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DatabaseDAO {
    public static String TAG = DatabaseDAO.class.getName();
    public static SQLiteOpenHelper helper = null;

    //使用哪个database就换helper即可。
    public static void init(SQLiteOpenHelper helper) {
        AbDebug.setLocalDebug(TAG, AbDebug.TAG_DB);
        DatabaseDAO.helper = helper;
    }


    public static void sql_query(String sql) {
        try {
            sql_log(sql);
            SQLiteDatabase db = helper.getReadableDatabase();
            db.execSQL(sql);
        } catch (Exception e) {
            AbDebug.log(AbConstants.DEBUG_TAG, "数据库操作 sql_query(" + sql + ")失败!");
            e.printStackTrace();
        } finally {
            helper.close();
        }

    }

    ;

    public static void sql_query(String sql, String... args) {
        try {
            String sqlStr = Strs.genFromHolders(sql, "?", args);
            sql_log(sqlStr);
            SQLiteDatabase db = helper.getReadableDatabase();
            db.execSQL(sqlStr);
        } catch (Exception e) {
            AbDebug.log(AbConstants.DEBUG_TAG, "数据库操作 sql_query(" + sql + "," + args.toString() + ") 失败!");
            e.printStackTrace();
        } finally {
            helper.close();
        }
    }

    ;

    public static ArrayList<HashMap<String, String>> sql_fetch_rows(String sql, String... args) {
        Cursor cursor = null;
        ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            cursor = db.rawQuery(sql, args);
            int columnsSize = cursor.getColumnCount();
            String[] columns = cursor.getColumnNames();
            String columnName = "";
            String value = "";
            //获取表的内容
            while (cursor.moveToNext()) {
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < columnsSize; i++) {
                    columnName = columns[i];
                    value = cursor.getString(cursor.getColumnIndex(columnName));
                    map.put(columnName, value);
                }
                mapList.add(map);
            }
        } catch (Exception e) {
            AbDebug.log(AbConstants.DEBUG_TAG, "数据库操作 sql_fetch_rows(" + sql + "," + args.toString() + ") 失败!");
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
            helper.close();
        }
        return mapList;
    }

    public static <T extends Object> ArrayList<T> sql_fetch_rows(Class<? extends AbKeepBaseEntity> clazz, String sql, String... args) {
        Cursor cursor = null;
        ArrayList<Object> objList = new ArrayList<Object>();
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            cursor = db.rawQuery(sql, args);
            DBUtils.fillObjectListByType(objList, clazz, cursor);
        } catch (Exception e) {
            AbDebug.log(AbConstants.DEBUG_TAG, "数据库操作 sql_fetch_rows(" + sql + "," + args.toString() + ") 失败!");
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
            helper.close();
        }
        return (ArrayList<T>) objList;
    }

    public static HashMap<String, String> sql_fetch_one(String sql, String... args) {
        HashMap<String, String> map = new HashMap<String, String>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            cursor = db.rawQuery(sql, args);
            int columnsSize = cursor.getColumnCount();
            String[] columns = cursor.getColumnNames();
            String columnName = "";
            String value = "";
            //获取表的内容
            while (cursor.moveToNext()) {
                for (int i = 0; i < columnsSize; i++) {
                    columnName = columns[i];
                    value = cursor.getString(cursor.getColumnIndex(columnName));
                    map.put(columnName, value);
                }
                break;
            }
        } catch (Exception e) {
            AbDebug.log(AbConstants.DEBUG_TAG, "数据库操作 sql_fetch_one(" + sql + "," + args.toString() + ") 失败!");
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
            helper.close();
        }
        return map;
    }

    public static Object sql_fetch_one(Class<? extends AbKeepBaseEntity> clazz, String sql, String... args) {
        Object obj = null;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
            AbDebug.log(AbConstants.DEBUG_TAG, "数据库操作 sql_fetch_one(" + sql + "," + args.toString() + ") 实例化clazz失败!");
            return null;
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
            AbDebug.log(AbConstants.DEBUG_TAG, "数据库操作 sql_fetch_one(" + sql + "," + args.toString() + ") 实例化clazz失败!");
            return null;
        }
        Cursor cursor = null;
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            cursor = db.rawQuery(sql, args);
            DBUtils.fillObjectByType(obj, cursor);
        } catch (Exception e) {
            AbDebug.log(AbConstants.DEBUG_TAG, "数据库操作 sql_fetch_one(" + sql + "," + args.toString() + ") 失败!");
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
            helper.close();
        }
        return obj;
    }

    @SuppressWarnings("unused")
    public static String sql_fetch_one_cell(String sql, String... args) {
        String res = "";
        Cursor cursor = null;
        try {
            SQLiteDatabase db = helper.getReadableDatabase();
            cursor = db.rawQuery(sql, args);
            int columnsSize = cursor.getColumnCount();
            String[] columns = cursor.getColumnNames();
            String columnName = "";
            String value = "";
            //获取表的内容
            while (cursor.moveToNext()) {
                for (int i = 0; i < columnsSize; i++) {
                    columnName = columns[i];
                    value = cursor.getString(cursor.getColumnIndex(columnName));
                    res = value;
                    break;
                }
                break;
            }
        } catch (Exception e) {
            AbDebug.log(AbConstants.DEBUG_TAG, "数据库操作 sql_fetch_one_cell(" + sql + "," + args.toString() + ") 失败!");
            e.printStackTrace();
        } finally {
            closeCursor(cursor);
            helper.close();
        }


        return res;
    }

    /**
     * @param obj
     * @param tableName
     * @param updateWhereClause 如果重复, 则更新. 如 where user_id=1;
     */
    public static <T extends AbKeepBaseEntity> void sql_insert(T obj, String tableName, String updateWhereClause) {
        try {
            HashMap<String, Object> objmap = Maps.fromObject(obj);
            ContentValues values = new ContentValues();
            Object field = null;
            String key = null;
            for (Iterator<Map.Entry<String, Object>> iterator = objmap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Object> entity = (Map.Entry<String, Object>) iterator.next();
                key = entity.getKey();
                field = entity.getValue();

                //if(key.equals("id") && ((Integer)field).intValue() == -1){
                if (key.equals("id") && Nums.equal(field, -1)) {
                    //如果需要自增长id则本项不加入ContentValues
                } else {
                    DBUtils.fillContentValuesByType(values, key, field);
                }

            }
            SQLiteDatabase db = helper.getWritableDatabase();
            if (!Strs.isEmpty(updateWhereClause)) {
                if (db.update(tableName, values, updateWhereClause, null) == 0) {
                    db.insert(tableName, null, values);
                }
            } else {
                db.insert(tableName, null, values);
            }
        } catch (Exception e) {
            AbDebug.log(AbConstants.DEBUG_TAG, "数据库操作失败! sql_insert(" + obj.toString() + "," + tableName + ")");
            e.printStackTrace();
        } finally {
            helper.close();
        }
    }

    public static <T extends AbKeepBaseEntity> void sql_insert(List<T> list, String tableName, String updateWhereClause, String fieldName) {
        try {
            HashMap<String, Object> objmap = null;
            Object field = null;
            String key = null;
            SQLiteDatabase db = helper.getWritableDatabase();
            for (T item : list) {
                objmap = Maps.fromObject(item);
                ContentValues values = null;
                values = new ContentValues();
                for (Iterator<Map.Entry<String, Object>> iterator = objmap.entrySet().iterator(); iterator.hasNext(); ) {
                    Map.Entry<String, Object> entity = (Map.Entry<String, Object>) iterator.next();
                    key = entity.getKey();
                    field = entity.getValue();
                    DBUtils.fillContentValuesByType(values, key, field);
                }
                if (!Strs.isEmpty(updateWhereClause)) {
                    if (db.update(tableName, values, updateWhereClause, new String[]{String.valueOf(objmap.get(fieldName))}) == 0) {
                        db.insert(tableName, null, values);
                    }
                } else {
                    db.insert(tableName, null, values);
                }
            }
        } catch (Exception e) {
            AbDebug.log(AbConstants.DEBUG_TAG, "数据库操作失败! sql_insert(" + list.toString() + "," + tableName + ")");
            e.printStackTrace();
        } finally {
            helper.close();
        }
    }

    public static boolean sql_check(String sql, String... selectionArgs) {
        boolean isSuccess = false;
        ArrayList<HashMap<String, String>> res = sql_fetch_rows(sql, selectionArgs);
        if (res.size() != 0) {
            isSuccess = true;
        }
        return isSuccess;
    }

    private static void sql_log(String sqlStr) {
        AbDebug.log(AbDebug.TAG_DB, "[SQL]:(" + sqlStr + ")");
    }

    @SuppressWarnings("unused")
    private static void sql_log(String sql, String... args) {
        String sqlStr = Strs.genFromHolders(sql, "?", args);
        AbDebug.log(AbDebug.TAG_DB, "[SQL]:(" + sqlStr + ")");
    }


    public static void closeCursor(Cursor c) {
        if (c != null) {
            c.close();
        }
    }

    public static boolean batchSqlQueryFromFile(InputStream in) {
        BufferedReader bufferedReader = null;
        SQLiteDatabase database = null;
        boolean isSuccess = false;
        SQLiteDatabase db = null;
        try {
            database = helper.getWritableDatabase();
            bufferedReader = new BufferedReader(new InputStreamReader(in));
            String sqlUpdate = null;
            db = helper.getWritableDatabase();
            db.beginTransaction();
            while ((sqlUpdate = bufferedReader.readLine()) != null) {
                if (!TextUtils.isEmpty(sqlUpdate)) {
                    database.execSQL(sqlUpdate);
                }
            }
            isSuccess = true;
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } finally {
            db.endTransaction();
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return isSuccess;
        }
    }
}
//				function  batchSqlQuery($sql,$continue = true){
//					$errorSqls = array ();
//					$lines = split(";", $sql);
//					$correctCount = 0;
//					foreach($lines as $line){
//						$line = trim($line);
//						if(empty($line))
//							continue;
//						try{				
//							$this->sql_query($line);
//							$correctCount++;
//						}catch(Exception $e){
//							if($continue == false)
//								return;
//							$errorSqls[] = $line;
//							echo $e->getMessage().": ".$e->getTraceAsString()."\n";
//						}
//					}
//					return array (count($lines),$correctCount,$errorSqls );
//				}

