package com.whr.jhttp.response.download;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.whr.jhttp.helper.IoUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

class DownloadCallback implements Callback {
	private static final int PROGRESS_MESSAGE = 0x01;
	private Handler mDeliveryHandler;
	private DownloadListener mListener;
	private String filName;
	private static String DEFAULT_DOWNLOAD_DIR = "/ZLife/apk";
	private String mDownloadDir = DEFAULT_DOWNLOAD_DIR;
	private boolean isDownloading=false;
	private static class InnerHolder{
		private static DownloadCallback instance=new DownloadCallback();
	}
	public static DownloadCallback getInstance(){
		if(InnerHolder.instance.isDownloading){
			return null;
		}else{
			return InnerHolder.instance;
		}
	}
	void init(DownloadListener listener, String fileName) {
		init(listener, null, fileName);
	}
	void init(DownloadListener listener, String downloadDir, String fileName){
		this.mListener = listener;
		if (TextUtils.isEmpty(fileName)) {
			this.filName = "update.apk";
		} else {
			this.filName = fileName;
		}
		if (!TextUtils.isEmpty(downloadDir)) {
			this.mDownloadDir = downloadDir;
		}
		this.mDeliveryHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case PROGRESS_MESSAGE:
						mListener.onProgress((int) msg.obj);
						break;
				}
			}
		};
	}
	@Override
	public void onFailure(final Call call, final IOException ioexception) {
		postFailure(ioexception.getMessage());
	}

	@Override
	public void onResponse(Call call, Response response) throws IOException {
		final File file = handleResponse(response);
		if (file == null) {
			postFailure("下载失败");
		} else {
			mDeliveryHandler.post(new Runnable() {
				@Override
				public void run() {
					mListener.onSuccess(file);
				}
			});
		}
	}

	/**
	 * 此时还在子线程中，不则调用回调接口
	 *
	 * @param response
	 * @return
	 */
	private File handleResponse(Response response) {
		isDownloading=true;
		if (response == null) {
			return null;
		}
		InputStream inputStream = null;
		File file = null;
		FileOutputStream fos = null;
		byte[] buffer = new byte[2048];
		int length = -1;
		int currentLength = 0;
		double sumLength = 0;
		try {
			File fileDir = null;
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
				fileDir = new File(Environment.getExternalStorageDirectory(), mDownloadDir);
			} else {
				postFailure("SD卡未挂载");
			}
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			file = new File(fileDir, filName);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			inputStream = response.body().byteStream();
			sumLength = (double) response.body().contentLength();
			int postPersent = 0;
			while ((length = inputStream.read(buffer)) != -1) {
				fos.write(buffer, 0, length);
				currentLength += length;
				final double mProgress = currentLength / sumLength * 100;
				if (mProgress > postPersent) {
					postPersent = (int) Math.floor(mProgress);
					postPersent++;
					mDeliveryHandler.obtainMessage(PROGRESS_MESSAGE, postPersent).sendToTarget();
				}
			}
			fos.flush();
		} catch (final Exception e) {
			file = null;
			postFailure(e.getMessage());
		} finally {
			IoUtil.close(fos);
			IoUtil.close(inputStream);
			isDownloading=false;
		}
		return file;
	}

	private void postFailure(final String msg) {
		mDeliveryHandler.post(new Runnable() {
			@Override
			public void run() {
				mListener.onFailure(msg);
			}
		});
	}
}