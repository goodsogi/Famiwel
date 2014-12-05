package com.gntsoft.famiwel.gcm;

import java.io.FileInputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gntsoft.famiwel.FWConstants;
import com.gntsoft.famiwel.FWWebViewActivity;
import com.gntsoft.famiwel.R;
import com.gntsoft.famiwel.main.MainActivity;
import com.pluslibrary.PlusConstants;
import com.pluslibrary.utils.PlusOnClickListener;
import com.pluslibrary.utils.PlusTimeFormatter;

/**
 * 푸시 팝업
 * 
 * @author jeff
 */
public class PushPopupActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_push_popup);

		makeLayout();

	}

	private void makeLayout() {
		final String url = getIntent().getStringExtra(PlusConstants.KEY_URL);
		String bitmapFileName = getIntent().getStringExtra(
				FWConstants.KEY_PUSH_BITMAP);
		Bitmap bitmap = getBitmapFromFile(bitmapFileName);
		String title = getIntent().getStringExtra(FWConstants.KEY_PUSH_TITLE);
		String msg = getIntent().getStringExtra(FWConstants.KEY_PUSH_MSG);

		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(title);

		TextView msgView = (TextView) findViewById(R.id.msg);
		msgView.setText(msg);

		TextView timeView = (TextView) findViewById(R.id.time);
		timeView.setText(PlusTimeFormatter.doIt());

		ImageView imageView = (ImageView) findViewById(R.id.image);
		imageView.setImageBitmap(bitmap);

		RelativeLayout container = (RelativeLayout) findViewById(R.id.layout);
		container.setOnClickListener(new PlusOnClickListener() {

			@Override
			protected void doIt() {
				finish();
				Intent notificationIntent = new Intent(PushPopupActivity.this,
						MainActivity.class);
				notificationIntent.putExtra(PlusConstants.KEY_URL,
						 url);
				startActivity(notificationIntent);

			}
		});

	}

	private Bitmap getBitmapFromFile(String bitmapFileName) {
		Bitmap bmp = null;
		try {
		    FileInputStream is = this.openFileInput(bitmapFileName);
		    bmp = BitmapFactory.decodeStream(is);
		    is.close();
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		return bmp;
	}

	/**
	 * 팝업창 닫기
	 * 
	 * @param v
	 */
	public void doClose(View v) {
		finish();
	}

}
