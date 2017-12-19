package com.whr.jhttp.response.download;

import java.io.File;

/**
 * @author vision
 * @function 监听下载进度
 */
public interface DownloadListener{
	void onProgress(int progress);
	void onFailure(String msg);
	void onSuccess(File file);
}
