package com.desheng.app.toucai.global;

import com.ab.db.AbDBHandler;
import com.desheng.base.model.UserFavoriteLottery;
import com.desheng.base.model.UserPlayedLottery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppDBHandler implements AbDBHandler {
    @Override
    public int getCurrentDBVersion() {
        return 1;
    }
    
    @Override
    public List<Class> getTables() {
        return Arrays.asList(UserFavoriteLottery.class, UserPlayedLottery.class);
    }
    
    @Override
    public List<String> getUpdateStatement(int oldVersion, int newVersion) {
        List<String> list = new ArrayList<>();
        if (oldVersion == 1 && newVersion == 2) {
        
        }
        return list;
    }
}
