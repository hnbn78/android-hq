package com.desheng.base.action;

import com.ab.callback.AbCallback;
import com.ab.module.MM;
import com.ab.util.ArraysAndLists;
import com.desheng.base.model.UserFavoriteLottery;
import com.desheng.base.model.UserPlayedLottery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 数据库访问对象
 * Created by lee on 2017/10/3.
 */
public class DBAction {
    public static void clearUserInfo(){
        MM.db.deleteAllArray(UserFavoriteLottery.class, UserPlayedLottery.class);
    }
    
    public static int countUserFavoriteLottery() {
        return (int) MM.db.count(UserFavoriteLottery.class);
    }
    
    public static void addUserFavoriteLottery(int id) {
        UserFavoriteLottery fav = new UserFavoriteLottery();
        fav.lotteryId = id;
        MM.db.create(UserFavoriteLottery.class, fav);
    }
    
    public static void removeAllUserFavoriteLottery() {
        MM.db.deleteAll(UserFavoriteLottery.class);
    }
    
    public static List<UserFavoriteLottery> queryAllUserFevoriteLottery() {
        return MM.db.queryAll(UserFavoriteLottery.class);
    }
    
    public static List<UserPlayedLottery> queryAllUserPlayedLottery() {
        return MM.db.queryAll(UserPlayedLottery.class);
    }
    
    public static void addOrUpdateUserPlayedLottery(final int lotteryId) {
        List<UserPlayedLottery> samePlayedList = MM.db.queryListByBuilder(UserPlayedLottery.class, new AbCallback<QueryBuilder>() {
            @Override
            public boolean callback(QueryBuilder obj) {
                QueryBuilder builder = (QueryBuilder)obj;
                Where<UserPlayedLottery, Integer> where = builder.where();
                try {
                    where.eq("lotteryId", lotteryId);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                builder.orderBy("createTime", false);
                return true;
            }
        });
        if (ArraysAndLists.isEmpty(samePlayedList)) {
            List<UserPlayedLottery> allPlayedList = queryAllUserPlayedLottery();
            if (allPlayedList == null) {
                allPlayedList = new ArrayList<>();
            }
            UserPlayedLottery played = new UserPlayedLottery();
            played.lotteryId = lotteryId;
            played.createTime = new Date();
            if(allPlayedList.size() < 8){
                allPlayedList.add(played);
            }else  {
                allPlayedList.remove(0);
                allPlayedList.add(played);
            }
            
            MM.db.createOrUpdateList(UserPlayedLottery.class, allPlayedList);
        }
    }
}
