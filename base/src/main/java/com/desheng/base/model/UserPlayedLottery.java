package com.desheng.base.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by lee on 2018/3/7.
 */
@DatabaseTable(tableName = "userPlayedLottery")
public class UserPlayedLottery {
    
    @DatabaseField(generatedId = true)
    public int id;
    
    @DatabaseField
    public int lotteryId;
    
    @DatabaseField
    public Date createTime;
    
}
