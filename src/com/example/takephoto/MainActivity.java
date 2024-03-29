package com.example.takephoto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Build;
import android.provider.MediaStore;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class MainActivity extends ActionBarActivity {

	private static final int REQUEST_CODE_TAKE_PHOTO = 1;
	private Uri outputFile;

	public static ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Parse.initialize(this, "bz7mnpLieBHrKD0g9ZdrX6SBrTADa6b67kUym4bR",
				"3ESxfneDefBQdari6N2I4wxG4t1SjCMSRKt9cZXw");
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (id == R.id.action_take_photo) {
			Log.d("Debug", "action take photo !");

			Intent intent = new Intent();
			intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

			startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
			Log.d("Debug", "onActivityResult, requestCode=" + requestCode
					+ ", resultCode=" + resultCode);

			if (resultCode == RESULT_OK) {
				Toast.makeText(this, "from camera", Toast.LENGTH_SHORT).show();
				
				Bitmap bitmap = intent.getParcelableExtra("data");
				imageView.setImageBitmap(bitmap);

				// 1-3 saveToParse(bitmap);
				
			
				File file = getTargetFile();
				// Log.d("debug", file.getPath());
				saveToParse(file);
				
				// imageView.setImageURI(outputFile);
			}
		}
	}
	
	private File getTargetFile() {
		File pictureDir = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if (pictureDir.exists() == false) {
			pictureDir.mkdirs();
		}
		return new File(pictureDir, "photo.png");
	}

	private void saveToParse(File file) {
		byte[] data = new byte[(int) file.length()];

		try {
			Log.d("debug", "Star B");
			
			FileInputStream fis = new FileInputStream(file);
			// fis.read(data);

			int offset = 0;
			int numRead = 0;
			Log.d("debug", "Star");
			
			while (true) {
				numRead = fis.read(data, offset, data.length - offset);
				if (numRead == -1) {
					break;
				}
				offset += numRead;
			}
			Log.d("debug", "End");

			/*
			ParseObject testObject = new ParseObject("TestObject");
			testObject.put("foo", "bar");
			testObject.saveInBackground();			
			*/
			
			final ParseFile parsefile = new ParseFile("photo.png", data);
			ParseObject object = new ParseObject("photo");
			object.put("file", parsefile);
			object.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					Log.d("debug", parsefile.getUrl());
				}
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void saveToParse(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] bytes = baos.toByteArray();
		final ParseFile file = new ParseFile("photo.png", bytes);
		ParseObject object = new ParseObject("photo");
		object.put("file", file);
		object.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				Log.d("debug", file.getUrl());
			}
		});

	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			imageView = (ImageView) rootView.findViewById(R.id.imageView1);
			return rootView;
		}
	}

}
