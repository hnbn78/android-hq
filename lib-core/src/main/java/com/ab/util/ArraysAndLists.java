package com.ab.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArraysAndLists {

	@SuppressWarnings("unchecked")
	public static <T> ArrayList<T> cast(List<Object> list, Class<T> clazz){
		ArrayList<T> newList = null;
		newList = new ArrayList<T>(cast(list.toArray(), clazz));
		return newList;
	}
    
    public static <T> ArrayList<T> cast(Object [] list, Class<T> clazz){
        ArrayList<T> newList = null;
        newList = new ArrayList<T>();
        for (int i = 0; i < list.length; i++) {
            newList.add((T) list[i]);
        }
        return newList;
    }
	
	public static ArrayList<String> trimEmptyString(ArrayList<String> list){
		ArrayList<String> res = new  ArrayList<String> ();
		for(String str: list){
			if(!Strs.isEmpty(str)){
				res.add(str);
			}
		}
		return res;
	}
    
    /**
     * 查找第一个. 适用于初级类型和引用判断.
     * @param arr
     * 
     * @return
     */
    public static <T extends Object> int findIndexWithValue(T v, T ... arr){
        int index = -1;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] == v){
                index = i;
                break;
            }
        }
        return index;
    }
    
    /**
     * 查找第一个. 适用于初级类型和引用判断.
     * @param arr
     * 
     * @return
     */
    public static <T extends Object> int findIndexWithValueOfArray(T v, T [] arr){
        int index = -1;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] == v){
                index = i;
                break;
            }
        }
        return index;
    }
    
    /**
     * 查找第一个. 适用于初级类型和重载equals的类型
     * @param arr
     * 
     * @return
     */
    public static <T extends Object> int findIndexWithEquals(T v, T ... arr){
        int index = -1;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i].equals(v)){
                index = i;
                break;
            }
        }
        return index;
    }
    
    /**
     * 查找第一个. 适用于初级类型和重载equals的类型
     * @param arr
     *
     * @return
     */
    public static <T extends Object> int findIndexWithEqualsOfList(T v, List<T> arr){
        int index = -1;
        for (int i = 0; i < arr.size(); i++) {
            if(arr.get(i).equals(v)){
                index = i;
                break;
            }
        }
        return index;
    }
    
    /**
     * 查找第一个. 适用于初级类型和重载equals的类型
     * @param arr
     * 
     * @return
     */
    public static <T extends Object> int findIndexWithEqualsOfArray(T v, T [] arr){
        int index = -1;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i].equals(v)){
                index = i;
                break;
            }
        }
        return index;
    }
    
    /**
     * 查找第一个. 适用于初级类型和重载equals的类型
     * @param arr
     *
     * @return
     */
    public static <T extends Object> boolean containsWithEqualsOfArray(T v, T [] arr){
        return findIndexWithEqualsOfArray(v, arr) > -1;
    }
    
    /**
     * 查找字符串包含
     * @param arr
     * 
     * @return
     */
    public static int findIndexWithContains(String v, String ... arr){
        int index = -1;
        if(Strs.isEmpty(v)){
            return index;
        }
        for (int i = 0; i < arr.length; i++) {
            if(arr[i].contains(v)){
                index = i;
                break;
            }
        }
        return index;
    }
    
    public static <T> ArrayList<T> asList(T [] arr) {
        ArrayList<T> list = new ArrayList<>();
        if(arr != null){
            for (int i = 0; i < arr.length; i++) {
                list.add(arr[i]);
            }
        }
        return list;
    }
    
    public static <T> T lastObject(T [] arr) {
        if (arr == null || arr.length == 0){
            return null;
        }
        return arr[arr.length - 1];
    }
    
    public static <T> T lastObject(List<T> list) {
        if (list == null || list.size() == 0){
            return null;
        }
        return list.get(list.size() - 1);
    }
    
    public static String [] toStringArray(List<String> listNum) {
        String [] arr = new String [listNum.size()];
        listNum.toArray(arr);
        return arr;
    }
    
    public static HashMap<String, Object>[] toMapArray(List<HashMap<String, Object>> listNum) {
        HashMap[] arr = new HashMap [listNum.size()];
        listNum.toArray(arr);
        return arr;
    }
    
    public static boolean isEmpty(List list) {
        return list == null || list.size() == 0;
    }
    
    public static boolean isNotEmpty(List list) {
        return !isEmpty(list);
    }
    
    
}
