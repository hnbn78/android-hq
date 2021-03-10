package com.desheng.base.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by lee on 2018/3/7.
 */
@DatabaseTable(tableName = "userFavoriteLottery")
public class UserFavoriteLottery {
    
    @DatabaseField(generatedId = true)
    public int id;
    
    @DatabaseField
    public int lotteryId;
}
