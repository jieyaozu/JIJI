package com.jiji.iready.httpmanager;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.jiji.iready.httpmanager.ParamList.Parameter;
import com.jiji.iready.utils.Logger;
import com.jiji.iready.volley.RequestQueue;
import com.jiji.iready.volley.Response;
import com.jiji.iready.volley.VolleyError;
import com.jiji.iready.volley.toolbox.StringRequest;
import com.jiji.iready.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求网络帮助类
 * @author jieyaozu
 *
 */
public class RequestManager {

	public interface OnResponseListener {
		public void onSuccess(Object object, int code, String message);

		public void onFailure(int code, String message);
	}

	private static final String TAG = "RequestManager";
	private static final String NETWORK_MESSAGE_ANALYSIS = "解析数据失败";
	private static final String NETWORK_MESSAGE_HINT = "获取数据失败";
	private static RequestManager mInstance;
	private RequestQueue mRequestQueue;
	private Gson mGson;

	/**
	 * 构造函数
	 */
	private RequestManager() {

	}

	/**
	 * 单例
	 * 
	 * @return
	 */
	public static RequestManager getInstance() {
		if (mInstance == null) {
			mInstance = new RequestManager();
		}
		return mInstance;
	}

	/**
	 * 获取volley请求调度队列
	 * 
	 * @param context
	 * @return
	 */
	private RequestQueue getRequestQueue(Context context) {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(context);
		}
		return mRequestQueue;
	}
	
	/**
	 * 基于Http的Get请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 */
	public void getRequest(Context context, String url) {
		getRequest(context, url, String.class, null, null);
	}

	/**
	 * 基于Http的Get请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param tag
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void getRequest(Context context, String url, Object tag,
						   OnResponseListener onResponseListener) {
		getRequest(context, url, String.class, tag, onResponseListener);
	}
	
	
	/**
	 * 基于Http的Get请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param params
	 *            请求参数
	 * @param onResponseListener
	 *            回调监听
	 */
	public void getRequest(Context context, String url, ParamList params,
						   OnResponseListener onResponseListener) {
		getRequest(context, url, params, null, onResponseListener);
	}
	
	/**
	 * 基于Http的Get请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param mClass
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void getRequest(Context context, String url, Class<?> mClass,
						   OnResponseListener onResponseListener) {
		getRequest(context, url, mClass, null, onResponseListener);
	}

	/**
	 * 基于Http的Get请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param params
	 *            请求参数
	 * @param tag
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void getRequest(Context context, String url, ParamList params,
						   Object tag, OnResponseListener onResponseListener) {
		getRequest(context, url, params, String.class, 0, tag,
				onResponseListener);
	}	
	
	
	/**
	 * 基于Http的Get请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param mClass
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void getRequest(Context context, String url, Class<?> mClass, Object tag,
						   OnResponseListener onResponseListener) {
		getRequest(context, url, null, mClass, 0, tag, onResponseListener);
	}

	/**
	 * 基于Http的Get请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param params
	 *            请求参数
	 * @param mClass
	 * @param tag
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void getRequest(Context context, String url, ParamList params,
						   Class<?> mClass, Object tag, OnResponseListener onResponseListener) {
		getRequest(context, url, params, mClass, 0, tag, onResponseListener);
	}
	
	/**
	 * 基于Http的Get请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param params
	 *            请求参数
	 * @param mClass
	 * @param code
	 *            请求码
	 * @param tag
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void getRequest(Context context, String url, ParamList params,
						   Class<?> mClass, int code, Object tag,
						   OnResponseListener onResponseListener) {
		http_Request(context, url, params, mClass, code, tag,
				com.jiji.iready.volley.Request.Method.GET, onResponseListener);
	}

	/**
	 * 基于Http的Post请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param tag
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void postRequest(Context context, String url, Object tag,
							OnResponseListener onResponseListener) {
		postRequest(context, url, null, String.class, 0, tag,
				onResponseListener);
	}

	/**
	 * 基于Http的Post请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param params
	 * @param tag
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void postRequest(Context context, String url, ParamList params,
							Object tag, OnResponseListener onResponseListener) {
		postRequest(context, url, params, String.class, 0, tag,
				onResponseListener);
	}
	
	
	/**
	 * 基于Http的Post请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param mClass
	 * @param tag
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void postRequest(Context context, String url, Class<?> mClass,
							Object tag, OnResponseListener onResponseListener) {
		postRequest(context, url, null, mClass, 0, tag, onResponseListener);
	}
	
	/**
	 * 基于Http的Post请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param params
	 *            请求参数
	 * @param mClass
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void postRequest(Context context, String url, ParamList params,
							Class<?> mClass, OnResponseListener onResponseListener) {
		postRequest(context, url, params, mClass, 0, null, onResponseListener);
	}

	/**
	 * 基于Http的Post请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param params
	 *            请求参数
	 * @param mClass
	 * @param tag
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void postRequest(Context context, String url, ParamList params,
							Class<?> mClass, Object tag, OnResponseListener onResponseListener) {
		postRequest(context, url, params, mClass, 0, tag, onResponseListener);
	}

	/**
	 * 基于Http的Post请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param params
	 *            请求参数
	 * @param mClass
	 * @param code
	 *            请求码
	 * @param tag
	 *            请求Tag
	 * @param onResponseListener
	 *            回调监听
	 */
	public void postRequest(Context context, String url, ParamList params,
							Class<?> mClass, int code, Object tag,
							OnResponseListener onResponseListener) {
		http_Request(context, url, params, mClass, code, tag,
				com.jiji.iready.volley.Request.Method.POST, onResponseListener);
	}

	/**
	 * 基于Http的取消请求
	 * 
	 * @param context
	 * @param tag
	 *            请求Tag
	 */
	public void requestCancel(Context context, Object tag) {
		getRequestQueue(context).cancelAll(tag);
	}

	/**
	 * 基于Http的请求
	 * 
	 * @param context
	 * @param url
	 *            地址
	 * @param params
	 *            请求参数
	 * @param mClass
	 * @param code
	 *            请求码
	 * @param tag
	 *            请求Tag
	 * @param method
	 * @param onResponseListener
	 *            回调监听
	 */
	private void http_Request(Context context, String url,
							  final ParamList params, final Class<?> mClass, final int code,
							  Object tag, int method, final OnResponseListener onResponseListener) {
		if (method == com.jiji.iready.volley.Request.Method.GET) {
			if (params != null) {
				url += params.getUrlEncode();
			}
		}
		Logger.i(TAG, "http_Request Url--> " + url);
		StringRequest stringRequest = new StringRequest(method, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Logger.i(TAG, "onResponse Json--> " + response.trim());
						sendSuccessResultCallback(response, mClass,
								onResponseListener, code);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Logger.i(TAG, "onResponse--> " + NETWORK_MESSAGE_HINT);
						sendFailureResultCallback(NETWORK_MESSAGE_HINT,
								onResponseListener, code);
					}
				}) {
			@Override
			protected Map<String, String> getParams() {
				// 在这里设置需要post的参数
				Map<String, String> map = new HashMap<String, String>();
				for (int i = 0; i < params.size(); i++) {
					Parameter param = params.get(i);
					map.put(param.getName(), param.getValue().toString());
				}
				return map;
			}
		};
		if (tag != null) {
			stringRequest.setTag(tag);
		}
		stringRequest.setShouldCache(false);
		getRequestQueue(context).add(stringRequest);
	}

	/**
	 * 拼接请求参数串
	 * 
	 * @param params
	 *            请求参数
	 * @return
	 */
	private String getParamsToString(ParamList params) {
		if (params != null && params.size() > 0) {
			StringBuilder sbuilder = new StringBuilder();
			for (int i = 0; i < params.size(); i++) {
				Parameter param = params.get(i);
				if (sbuilder.length() > 0) {
					sbuilder.append("&");
				}
				if (param.getValue() != null) {
					sbuilder.append(param.getName() + "="
							+ param.getValue().toString());
				} else {
					sbuilder.append(param.getName() + "=");
				}
			}
			return "?" + sbuilder.toString();
		}
		return null;
	}

	/**
	 * 发送成功请求回调
	 * 
	 * @param jsonStr
	 * @param mClass
	 * @param onResponseListener
	 * @param code
	 */
	private void sendSuccessResultCallback(String jsonStr, Class<?> mClass,
										   OnResponseListener onResponseListener, int code) {
		if (!TextUtils.isEmpty(jsonStr)) {
			try {
				if (String.class.getName().equals(mClass.getName())) {
					if (onResponseListener != null) {
						onResponseListener
								.onSuccess(jsonStr, code, null);
					}
				} else {
					if (mGson == null) {
						mGson = new Gson();
					}
					Object object = mGson.fromJson(jsonStr, mClass);
					if (onResponseListener != null) {
						onResponseListener.onSuccess(object, code, null);
					}
				}
			} catch (Exception e) {
				if (e != null) {
					e.printStackTrace();
				}
				Logger.i(TAG, "sendSuccessResultCallback--> " + NETWORK_MESSAGE_ANALYSIS);
				sendFailureResultCallback(NETWORK_MESSAGE_ANALYSIS,
						onResponseListener, code);
			}
		}
	}

	/**
	 * 发送失败请求回调
	 * 
	 * @param message
	 * @param onResponseListener
	 * @param code
	 */
	private void sendFailureResultCallback(String message,
			OnResponseListener onResponseListener, int code) {
		try {
			if (onResponseListener != null) {
				onResponseListener.onFailure(code, message);
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}
}
