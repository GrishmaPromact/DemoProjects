package com.promact.videodemo;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class VideoActivity extends AppCompatActivity {
	public static final int code = 1111;
	Button start_video;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_video);

			start_video = (Button) findViewById(R.id.start_video);
			start_video.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					startActivityForResult(new Intent(
							MediaStore.ACTION_VIDEO_CAPTURE), code);
				}
			});
		} catch (Exception e) {
			Log.v("Camera Exception ", Log.getStackTraceString(e));
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == code) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "Video Recorded", Toast.LENGTH_LONG)
						.show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video, menu);
		return true;
	}

}
