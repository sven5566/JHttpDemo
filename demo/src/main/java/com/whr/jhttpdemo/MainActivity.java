package com.whr.jhttpdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.whr.jhttp.JHttp;
import com.whr.jhttp.response.ResponseCallback;

public class MainActivity extends AppCompatActivity {
	TextView textView;
	Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView = findViewById(R.id.main_tv);
		button = findViewById(R.id.main_btn);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				JHttp.postJson(new MyRequest(), new ResponseCallback<MyResponse>() {
					@Override
					public void onSucess(MyResponse response) {
						textView.setText(response.toString());
					}

					@Override
					public void onFail(String msg) {
						textView.setText(msg);
					}
				});
			}
		});
	}
}
