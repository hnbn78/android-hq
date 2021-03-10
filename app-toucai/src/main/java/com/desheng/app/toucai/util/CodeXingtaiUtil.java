package com.desheng.app.toucai.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CodeXingtaiUtil {

    public static String getXingtai(String code) {

        String[] codes = code.split(",");

        List<Integer> integers = new ArrayList<>();

        for (String s : codes) {
            integers.add(Integer.parseInt(s));
        }

        if (integers.size() < 5) {
            return null;
        }

        //前三
        List<Integer> integers1 = integers.subList(0, 3);
        //中三
        List<Integer> integers2 = integers.subList(1, 4);
        //后三
        List<Integer> integers3 = integers.subList(2, 5);
        //万个龙虎
        int wan = integers.get(0);
        int ge = integers.get(integers.size() - 1);

        HashMap<Integer, Integer> map1 = new HashMap<>();
        HashMap<Integer, Integer> map2 = new HashMap<>();
        HashMap<Integer, Integer> map3 = new HashMap<>();

        for (Integer i : integers1) {
            if (map1.containsKey(i)) {
                map1.put(i, map1.get(i) + 1);
            } else {
                map1.put(i, 1);
            }
        }

        for (Integer i : integers2) {
            if (map2.containsKey(i)) {
                map2.put(i, map2.get(i) + 1);
            } else {
                map2.put(i, 1);
            }
        }

        for (Integer i : integers3) {
            if (map3.containsKey(i)) {
                map3.put(i, map3.get(i) + 1);
            } else {
                map3.put(i, 1);
            }
        }

        List<Integer> list1 = new ArrayList<>();
        List<Integer> list2 = new ArrayList<>();
        List<Integer> list3 = new ArrayList<>();

        for (int key : map1.keySet()) {
            list1.add(map1.get(key));
        }

        for (int key : map2.keySet()) {
            list2.add(map2.get(key));
        }

        for (int key : map3.keySet()) {
            list3.add(map3.get(key));
        }

        System.out.println(" max1: " + Collections.max(list1));//求最大值：如果：1表示组六，2表示组三，3表示豹子

        Log.d("CodeXingtaiUtil", " 号码形态: " + sun(Collections.max(list1)) + " | " +
                sun(Collections.max(list2)) + " | " + sun(Collections.max(list3)) + " | " + (wan == ge ? "和" : (wan > ge ? "龙" : "虎")));


        return sun(Collections.max(list1)) + "|" +
                sun(Collections.max(list2)) + "|" +
                sun(Collections.max(list3)) + "|" +
                (wan == ge ? "和" : (wan > ge ? "龙" : "虎"));
    }


    private static String sun(int a) {
        switch (a) {
            case 1:
                return "组六";
            case 2:
                return "组三";
            case 3:
                return "豹子";
            default:
                return "";
        }
    }
}
