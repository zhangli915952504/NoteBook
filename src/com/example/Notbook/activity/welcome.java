package com.example.Notbook.activity;

import com.example.Notbook.Utils.Constants;
import com.example.Notbook.Utils.Utils;
import com.example.Notebook.view.MyCircle;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import zhangli.noteBook.R;

public class welcome extends FragmentActivity {

	private MyCircle circle;

	private ViewPager viewpager;

	private Activity activity;
	
	private final static int PAGER_CUONT =4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		

		
		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean isAppFirstRun = sharedPreferences.getBoolean(
				Constants.IS_APP_FIRST_RUN, true);
		
		//APP安装后的第一次启动加载
		if (isAppFirstRun) {
			Editor  editor=sharedPreferences.edit();
			editor.putBoolean(Constants.IS_APP_FIRST_RUN, false);
			editor.commit();
		}
		//不是第一次启动
		else{
			Intent intent=new Intent(this,MainActivity.class);
			startActivity(intent);
			this.finish();
		}
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去标题
		Utils.toggleFullscreen(activity,true);
		
		setContentView(R.layout.activity_welcome);


		viewpager = (ViewPager) findViewById(R.id.viewpager);
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageSelected(int pos) {
				circle.choose(pos);
			}
		});

		viewpager.setAdapter(new MyAdapter(this.getSupportFragmentManager()));

		circle = (MyCircle) findViewById(R.id.cicle);
		circle.setCount(viewpager.getAdapter().getCount());
	}

	private class MyAdapter extends FragmentPagerAdapter {
		int[] imageId={R.drawable.welcome1,R.drawable.welcome2,R.drawable.welcome3};

		public MyAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(final int pos) {

			Fragment f = new Fragment() {
				@Override
				public View onCreateView(LayoutInflater inflater,
						ViewGroup container, Bundle savedInstanceState) {

					if (pos == (getCount()-1)) {
						View view = inflater.inflate(R.layout.itme_main, null);
						Button button=(Button) view.findViewById(R.id.button);
						button.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Intent intent=new Intent(activity,MainActivity.class);
								startActivity(intent);
								
								activity.finish();
							}
						});
							
						return view;
					} else {
						View view = inflater.inflate(R.layout.item, null);
						view.setBackgroundColor(Color.GRAY);
						ImageView image = (ImageView) view
								.findViewById(R.id.imageView);
						image.setImageResource(imageId[pos]);
						return view;
					}
				}
			};

			return f;
		}

		@Override
		public int getCount() {
			return PAGER_CUONT;
		}

	}
}
