package com.whr.jhttp.response;

/**
 * Created by whrr5 on 2017/3/23.
 */

public abstract class BaseResponse {
	public String ret_code;
	public String ret_msg;

	@Override
	public String toString() {
		return "BaseResponse{" +
				"ret_code='" + ret_code + '\'' +
				", ret_msg='" + ret_msg + '\'' +
				'}';
	}
	public final boolean isSuccess(){
		return "0".equals(ret_code);
	}
}
