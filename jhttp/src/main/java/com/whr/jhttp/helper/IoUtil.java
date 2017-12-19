package com.whr.jhttp.helper;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by whrr5 on 2017/6/28.
 */
 public final class IoUtil {
	public static void close(Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
				closeable=null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
