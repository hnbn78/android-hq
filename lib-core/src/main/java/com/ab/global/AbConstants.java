/*
 * Copyright (C) 2012 www.amsoft.cn
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ab.global;

// TODO: Auto-generated Javadoc

/**
 * © 2012 amsoft.cn 名称：AbConstant.java 描述：常量.
 * 
 * @author 还如一梦中
 * @version v1.0
 * @date：2013-10-16 下午1:33:39
 */
public class AbConstants {
	public static final String DEBUG_TAG = "pearl_debug";
    /** 主题的key. */
    public static final String THEME_ID = "themeId";
	public static final class HttpControlKey {
		// 使用json为post请求content_type  默认不使用
		public static final String BOOL_USE_JSON_PARAM = "use_json_param";
		/* 上传图片缩放尺寸宽度 */
		public static final String INT_IMAGE_SCALE_TO_WIDTH = "int_image_scale_to_width";
		/* 上传图片缩放尺寸高度 */
		public static final String INT_IMAGE_SCALE_TO_HEIGHT = "int_image_scale_to_height";
		/* 上传图片统一后缀 */
		public static final String STRING_IMAGE_FILE_SUFFIX = "string_image_file_suffix";
		//是否使用公共参数
		public static final String BOOL_USE_AUTO_COMMON_PARAM = "bool_use_auto_common_param";
		//是否使用自动userId
		public static final String BOOL_USE_AUTO_USER_ID = "bool_use_auto_user_id";
		//是否统一处理ResultCode, 如统一则设为结果码的字段名,同时启用message字段。
		//默认不使用, 如需使用结果必须返回json类型
		public static final String STRING_AUTO_CODE = "string_result_code_name";
	}

	public static final class HttpControlValue {
		/* 使用json为post请求content_type */
		public static final String STRING_IMAGE_PARAM = "string_image_param";
		public static final String PNG = "png";
		public static final String JPEG = "jpeg";
		//STRING_AUTO_CODE值 宠托托用
		public static final String RESULTCODE_MESSAGE_DATA = "resultCode_message_data";
		//STRING_AUTO_CODE值 没事找事用
		public static final String STATUS_MESSAGE_DATA_DATAS = "status_message_data_datas";

        public static final String RAW_STRING = "raw_string";
	}

	public static final class ConnectionType {
		public static final int NO_CONNECTION = -1; // 未连接
		public static final int UNKONWN = 0; // UNKONWN为极少情况, 以连接状态处理
		public static final int IS_WIFI = 1;
		public static final int IS_GPRS = 2;
	}

	public static final class DirectoryName{
		public static final String IMAGE_CACHE = "image_cache";
		public static final String DATABASE_DIR = "db";
	}
	
	public static final class SuperActivityListenerTag{
		public static final int TAG_LOCATION = 10;
	}
	
	public static final class ActivityResquestCode{
		public static final int GET_PET_VERIATIES = 1;
		public static final int GET_IMAGE_VIA_SDCARD = 2;
		public static final int GET_IMAGE_VIA_CAMERA = 3;
		public static final int GET_IMAGE_VIA_CROP = 4;
	}
	
	public static final class SettingKey{
		/*设置文件名称*/
		public static final String CONFIG_FILE = "setting_conf";
	}
	
}
