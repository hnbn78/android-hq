package com.pearl.act;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ab.debug.AbDebug;
import com.pearl.act.base.AbBaseFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 2017/9/19.
 */

public class AbFragManager {
    //分组fragment保存,  每组仅显示其中一个
    private static Map<String, Map<String, AbBaseFragment>> mapGroups;
    
    //分组fragment一组中正显示的fragment
    private static Map<String, AbBaseFragment> mapShowingFragOfGroups;
    
    //分组fragment一组的container id.
    private static Map<String, Integer> mapContainerIdOfGroups;
    
    
    public static void  init() {
        mapGroups = new HashMap<String, Map<String, AbBaseFragment>>();
        mapShowingFragOfGroups = new HashMap<String, AbBaseFragment>();
        mapContainerIdOfGroups = new HashMap<String, Integer>();
    }
    
    public static void destroy(){
        for (Map.Entry<String, Map<String, AbBaseFragment>> entry:
             mapGroups.entrySet()) {
            entry.getValue().clear();
        }
        mapGroups.clear();
        mapGroups = null;
        mapShowingFragOfGroups.clear();
        mapShowingFragOfGroups = null;
        mapContainerIdOfGroups.clear();
        mapContainerIdOfGroups = null;
    }
    
    public static void createGroup(String groupName, int containerResId){
        mapGroups.put(groupName, new HashMap<String, AbBaseFragment>());
        mapContainerIdOfGroups.put(groupName, containerResId);
    }
    
    
    public  static void putIntoGroup(FragmentManager manager, String groupName,  String fragToken, AbBaseFragment fragment){
        mapGroups.get(groupName).put(fragToken, fragment);
        
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(mapContainerIdOfGroups.get(groupName), fragment, fragToken);
        transaction.hide(fragment);
        transaction.commitAllowingStateLoss();
    }
    
    public  static void putIntoGroup(FragmentManager manager, String groupName, int index, AbBaseFragment fragment){
        putIntoGroup(manager, groupName, String.valueOf(index), fragment);
    }
    
    public static void showOneFromGroup(FragmentManager manager, String groupName, String fragToken){
        FragmentTransaction transaction = manager.beginTransaction();
        AbBaseFragment showingFrag = mapShowingFragOfGroups.get(groupName);
        if (showingFrag != null) {
            transaction.hide(showingFrag);
            if(showingFrag.isShowing){
               showingFrag.isShowing = false;
               AbDebug.log(AbDebug.TAG_UI, "progressFragment:" + showingFrag.getSimpleName() + "#onHide()");
               showingFrag.onHide();
            }
        }
        AbBaseFragment targetFrag = mapGroups.get(groupName).get(fragToken);
        if(targetFrag != null){
            if(!targetFrag.isShowing){
                if(targetFrag.getContentView() != null){ //虽然add过, 但该fragment的onCreateView并未执行.使用setUserHint中的回调来执行onShow
                    targetFrag.isShowing = true;
                    AbDebug.log(AbDebug.TAG_UI, "progressFragment:" + targetFrag.getSimpleName() + "#onShow()");
                    targetFrag.onShow();
                }
            }
            transaction.show(targetFrag);
            mapShowingFragOfGroups.put(groupName, mapGroups.get(groupName).get(fragToken));
        }
        transaction.commit();
    }
    
    public static void showOneFromGroup(FragmentManager manager, String groupName, int index){
        showOneFromGroup(manager, groupName, String.valueOf(index));
    }
}
