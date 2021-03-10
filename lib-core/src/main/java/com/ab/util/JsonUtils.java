package com.ab.util;

import com.ab.debug.AbDebug;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class JsonUtils {
    public static final String TAG = JsonUtils.class.getName();
    
	/**
	 *
	 * 描述：将对象转化为json.
	 * @return
	 */
	public static String toJson(Object src) {
		String json = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			json = gson.toJson(src);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}

	/**
	 *
	 * 描述：将列表转化为json.
	 * @param list
	 * @return
	 */
	public static String toJson(List<?> list) {
		String json = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			json = gson.toJson(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 *
	 * 描述：将json转化为列表.
	 * @param json
	 * @param typeToken new TypeToken<ArrayList<?>>() {};
	 * @return
	 */
	public static List<?> fromJson(String json,TypeToken typeToken) {
		List<?> list = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			Type type = typeToken.getType();
			list = gson.fromJson(json,type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 *
	 * 描述：将json转化为对象.
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static Object fromJson(String json,Class clazz) {
		Object obj = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			obj = gson.fromJson(json,clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	/**
	 * HashMap转json
	 * @param map
	 * @return
	 */
	public String mapToJson(HashMap<String, String> map){
		String json = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			json = gson.toJson(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 * 由初级类型组成的HashMap转JsonObject
	 * @param map
	 * @return
	 */
	public static JsonObject mapToJsonObj(Map<String, Object> map){
		JsonParser parser = new JsonParser();
		JsonObject obj = new JsonObject();
		Map.Entry<String, Object> entry = null;
		String tempKey = null;
		Object tempObj = null;
		//遍历head
		for (Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			entry = (Map.Entry<String, Object>) iterator.next();
			tempKey = entry.getKey();
			tempObj = entry.getValue();
			 if(tempObj == null){
				 obj.add(tempKey,  JsonNull.INSTANCE);
			 }else if(tempObj instanceof Number) {
				 obj.addProperty(tempKey, (Number)tempObj);
			 }else if(tempObj instanceof String){
				 obj.addProperty(tempKey, (String)tempObj);
			 }else if(tempObj instanceof Boolean) {
				 obj.addProperty(tempKey, (Boolean)tempObj);
			 }else if(tempObj instanceof Character) {
				 obj.addProperty(tempKey, (Character)tempObj);
			 }else if(tempObj instanceof JSONArray){
				 JsonArray arr = new JsonArray();
				 for (int i = 0; i < ((JSONArray)tempObj).length(); i++) {
					 try {
						arr.add(parser.parse(((JSONArray)tempObj).getString(i)));
					} catch (Exception e1) {
						try {
							arr.add(new JsonPrimitive(((JSONArray)tempObj).getString(i)));
						} catch (JSONException e2) {
							AbDebug.log(TAG, "mapToJsonObj 转化失败！");
						}
					}
				}
				 obj.add(tempKey, arr);
			 }else if(tempObj instanceof JSONObject){
				 JsonElement ele = parser.parse(((JSONObject)tempObj).toString());
				 obj.add(tempKey, ele);
			 }
		}
		return obj;
	}
	
	/**
	 *
	 * 描述：将json转化为hashmap列表.
	 * @param json
	 * @return
	 */
	public static HashMap<String,String> jsonToMapSS(String json) {
		HashMap<String,String> map = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			Type type = new TypeToken<HashMap<String,String>>() {}.getType();
			map = gson.fromJson(json,type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
    
    /**
     *
     * 描述：将json转化为hashmap列表.
     * @param json
     * @return
     */
    public static HashMap<String,Object> jsonToMapSO(String json) {
        HashMap<String,Object> map = null;
        try {
            GsonBuilder gsonb = new GsonBuilder();
            Gson gson = gsonb.create();
            Type type = new TypeToken<HashMap<String,Object>>() {}.getType();
            map = gson.fromJson(json,type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

	/**
	 *
	 * 描述：将hashMap列表转化为json.
	 * @param list
	 * @return
	 */
	public static String mapListToJson(LinkedList<HashMap<String, String>> list) {
		String json = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			json = gson.toJson(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return json;
	}
	
	/**
	 *
	 * 描述：将hashMap列表转化为json.
	 * @return
	 */
	public static LinkedList<HashMap<String, String>> jsonToMapList(String jsonStr) {
		LinkedList<HashMap<String, String>> list = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			Type type = new TypeToken<LinkedList<HashMap<String, String>>>(){}.getType();
			list = gson.fromJson(jsonStr, type);
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		}
		return list;
	}

	public static LinkedList<String> jsonToStringList(String jsonStr) {
		LinkedList<String> list = null;
		try {
			GsonBuilder gsonb = new GsonBuilder();
			Gson gson = gsonb.create();
			Type type = new TypeToken<LinkedList<String>>(){}.getType();
			list = gson.fromJson(jsonStr, type);
		} catch (Exception e) {
			e.printStackTrace();
			list = null;
		}
		return list;
	}
    
    /**
     * 格式化json字符串
     *
     * @param jsonStr 需要格式化的json串
     * @return 格式化后的json串
     */
    public static String formatJson(String jsonStr) {
        if (null == jsonStr || "".equals(jsonStr)) return "";
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            last = current;
            current = jsonStr.charAt(i);
            //遇到{ [换行，且下一行缩进
            switch (current) {
                case '{':
                case '[':
                    sb.append(current);
                    sb.append('\n');
                    indent++;
                    addIndentBlank(sb, indent);
                    break;
                //遇到} ]换行，当前行缩进
                case '}':
                case ']':
                    sb.append('\n');
                    indent--;
                    addIndentBlank(sb, indent);
                    sb.append(current);
                    break;
                //遇到,换行
                case ',':
                    sb.append(current);
                    if (last != '\\') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;
                default:
                    sb.append(current);
            }
        }
        return sb.toString();
    }
    
    /**
     * 添加space
     *
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append('\t');
        }
    }
    
    /**
     * http 请求数据返回 json 中中文字符为 unicode 编码转汉字转码
     *
     * @param theString
     * @return 转化后的结果.
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                        
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
   
    
}



