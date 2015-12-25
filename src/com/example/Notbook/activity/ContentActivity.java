package com.example.Notbook.activity;

import java.sql.SQLException;

import com.example.Notbook.database.ORMLiteDatabaseHelper;
import com.example.Notbook.database.Record;

import com.gitonway.lee.niftynotification.lib.Configuration;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import com.j256.ormlite.dao.Dao;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import zhangli.noteBook.R;

public class ContentActivity extends Activity {

	private int SAVE = 0xa01;

	private EditText title_EditText;
	private EditText content_EditText;

	private Record record;
	Activity activity = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;

		setContentView(R.layout.content_activity);

		title_EditText = (EditText) findViewById(R.id.title_EditText);
		content_EditText = (EditText) findViewById(R.id.content_EditText);
		TextView title_TextView = (TextView) findViewById(R.id.title_TextView);
		title_TextView.setText("新笔记");
		// RelativeLayout relativeLayout=(RelativeLayout)
		// findViewById(R.id.title_layout_xml);
		// relativeLayout.setBackgroundColor(127);
		String title = "", content = "";

		Intent intent = this.getIntent();
		long record_id = intent.getIntExtra(Record.RECORD_ID, -1);
		if (record_id != -1) {
//			Log.d(this.getClass().getName(), "从ListView点击进来,record_id=" + record_id);

			// 从ListView点击某一条item进来的，注意!intent里面包含有record id,解析出来。
			Dao<Record, Integer> mDao = getDao();

			try {
				record = mDao.queryForId((int) record_id);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			title = record.title;
			content = record.content;
		} else {
//			Log.d(this.getClass().getName(), "从创建新记事的那个菜单进来的");
			record = new Record();
		}
		title_EditText.setText(title);
		content_EditText.setText(content);

		// 菜单键
		findViewById(R.id.title_ImageView).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				activity.openOptionsMenu();
				showPopupMenu(v);
			}
		});
		// 返回键
		findViewById(R.id.back_ImageView).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.finish();
			}
		});
	}

	private Dao<Record, Integer> getDao() {
		ORMLiteDatabaseHelper mOrmLiteDatabaseHelper = ORMLiteDatabaseHelper.getInstance(this);

		Dao<Record, Integer> mDao = null;
		try {
			mDao = mOrmLiteDatabaseHelper.getDao(Record.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mDao;
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		menu.add(0, SAVE, 80, "保存");
//
//		return super.onCreateOptionsMenu(menu);
//	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.content_save) {
			String title = title_EditText.getText() + "";
			String content = content_EditText.getText() + "";

			// 保存数据逻辑
			// Log.d(title_EditText.getText()+"",content_EditText.getText()+"");
			saveDataToDatabase(title, content);
			// Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
			// Intent intent = new Intent(this, MainActivity.class);
			// startActivity(intent);

			Configuration cfg = new Configuration.Builder().setAnimDuration(500).setDispalyDuration(2000)
					.setBackgroundColor("#FFBDC3C7").setTextColor("#FF444444").setIconBackgroundColor("#FFFFFFFF")
					.setTextPadding(5) // dp
					.setViewHeight(48) // dp
					.setTextLines(2) // You had better use setViewHeight and
										// setTextLines together
					.setTextGravity(Gravity.CENTER) // only text def
													// Gravity.CENTER,contain
													// icon
													// Gravity.CENTER_VERTICAL
					.build();

			Effects effect = Effects.standard;
			String s = "虽然没有奖励，但还是保存成功了...";
			NiftyNotificationView.build(this, s, effect, R.id.content_activity, cfg).setIcon(R.drawable.title_image)
					.show();

			// finish();
		}

		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * PopupView
	 */
	@SuppressLint("NewApi")
	private void showPopupMenu(View view) {
		// View当前PopupMenu显示的相对View的位置
		PopupMenu popupMenu = new PopupMenu(this, view);

		// menu布局
		popupMenu.getMenuInflater().inflate(R.menu.content_main, popupMenu.getMenu());

		// menu的item点击事件
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// 点击
				onMenuItemSelected(R.id.content_save, item);
				// Toast.makeText(getApplicationContext(), item.getTitle(),
				// Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		// PopupMenu关闭事件
		popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override
			public void onDismiss(PopupMenu menu) {
				// activity.finish();
				// Toast.makeText(getApplicationContext(), "关闭PopupMenu",
				// Toast.LENGTH_SHORT).show();
			}
		});

		popupMenu.show();
	}

	private void saveDataToDatabase(String title, String content) {
		Dao<Record, Integer> mDao = getDao();

		record.title = title;
		record.content = content;
		record.create_at = System.currentTimeMillis();

		try {
			mDao.createOrUpdate(record);
			// Toast.makeText(this, " 已经保存", Toast.LENGTH_SHORT).show();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
