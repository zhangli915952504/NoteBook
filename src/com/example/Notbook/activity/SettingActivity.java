package com.example.Notbook.activity;

import com.example.Notbook.Utils.Constants;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import zhangli.noteBook.R;

public class SettingActivity extends PreferenceActivity {

	

	private EditTextPreference mEditTextPreference;

	private OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new MySharedPreferenceChangeListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 通用的读取设置的某个值的方法
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		String s = sharedPreferences.getString(Constants.KEY_LISTVIEW_TITLE_TEXT_SIZE, "15");

		addPreferencesFromResource(R.xml.preferences);

		mEditTextPreference = (EditTextPreference) findPreference(Constants.KEY_LISTVIEW_TITLE_TEXT_SIZE);
		mEditTextPreference.setSummary(s);

		// 注册添加设置的项数据发生变化时候的监听
		sharedPreferences.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
	}

	private class MySharedPreferenceChangeListener implements OnSharedPreferenceChangeListener {

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Log.d(this.getClass().getName(), key);

			String value = sharedPreferences.getString(key, "15");
			mEditTextPreference.setSummary(value);
		}
	}
}
