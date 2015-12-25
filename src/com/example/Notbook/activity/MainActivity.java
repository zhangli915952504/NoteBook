package com.example.Notbook.activity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.Notbook.Utils.Constants;
import com.example.Notbook.Utils.Utils;
import com.example.Notbook.database.ORMLiteDatabaseHelper;
import com.example.Notbook.database.Record;

import com.j256.ormlite.dao.Dao;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import zhangli.noteBook.R;

public class MainActivity extends Activity {
	private MyAdapter adapter = null;
	private ArrayList<HashMap<String, Object>> data;
	private static String KEY_LISTVIEW_TITLE_TEXT_SIZE = "title_editText_font_size";

	private Activity activity = null;

	// 默认的字体大小
	private float title_text_size = 18.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;

		setContentView(R.layout.activity_main);
		data = new ArrayList<HashMap<String, Object>>();

		ListView listView = (ListView) findViewById(R.id.listView);

		listView.setOnCreateContextMenuListener(this);

		adapter = new MyAdapter(this, -1);
		listView.setAdapter(adapter);
		final Activity activity = this;
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {

				int record_id = (Integer) data.get(pos).get(Record.RECORD_ID);

				Intent intent = new Intent(activity, ContentActivity.class);
				intent.putExtra(Record.RECORD_ID, record_id);
				activity.startActivity(intent);
			}
		});

		// 菜单键
		findViewById(R.id.title_ImageView).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
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

	/**
	 * PopupView
	 */
	@SuppressLint("NewApi")
	private void showPopupMenu(View view) {
		// View当前PopupMenu显示的相对View的位置
		PopupMenu popupMenu = new PopupMenu(this, view);

		// menu布局
		popupMenu.getMenuInflater().inflate(R.menu.main, popupMenu.getMenu());

		// menu的item点击事件
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				// 点击
				onMenuItemSelected(R.id.create_record, item);
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

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String textSizeString = sharedPreferences.getString(Constants.KEY_LISTVIEW_TITLE_TEXT_SIZE, "15");
		title_text_size = Float.parseFloat(textSizeString);

		new MyTask().execute();
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.create_record) {
			Intent intent = new Intent(this, ContentActivity.class);
			intent.putExtra(Record.RECORD_ID, -1);
			startActivity(intent);
		}
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private class MyTask extends AsyncTask<String, Integer, ArrayList<HashMap<String, Object>>> {

		@Override
		protected ArrayList<HashMap<String, Object>> doInBackground(String... params) {
			return readDataFromDatabase();
		}

		@Override
		protected void onPostExecute(ArrayList<HashMap<String, Object>> result) {
			data.clear();

			for (int i = 0; i < result.size(); i++) {
				HashMap<String, Object> map = result.get(i);
				data.add(map);
			}

			adapter.notifyDataSetChanged();
		}

	}

	// 从数据库读取数据
	private ArrayList<HashMap<String, Object>> readDataFromDatabase() {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		ORMLiteDatabaseHelper mOrmLiteDatabaseHelper = ORMLiteDatabaseHelper.getInstance(this);
		Dao<Record, Integer> mDao = null;
		try {
			mDao = mOrmLiteDatabaseHelper.getDao(Record.class);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			// List<Record> records = mDao.queryForAll();
			// 从头添加笔记。
			List<Record> records = mDao.queryBuilder().orderBy(Record.CREATE_AT, false).query();

			for (int i = 0; i < records.size(); i++) {
				Record r = records.get(i);

				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(Record.TITLE, r.title);
				map.put(Record.CONTENT, r.content);
				map.put(Record.CREATE_AT, r.create_at);
				map.put(Record.RECORD_ID, r.record_id);

				list.add(map);
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return list;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.add(0, 1002, 3, "删除笔记");
		menu.add(0, 1003, 2, "设为置顶");

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final int pos = info.position;

		int id = item.getItemId();
		// 删除
		if (id == 1002) {
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("确认");
			dialog.setMessage("是否删除这条笔记");
			dialog.setButton(dialog.BUTTON_POSITIVE, "是", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					HashMap<String, Object> map = data.get(pos);
					int record_id = (Integer) map.get(Record.RECORD_ID);
					Dao<Record, Integer> mDao = getDao();
					try {
						mDao.deleteById(record_id);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					data.remove(pos);
					adapter.notifyDataSetChanged();
				}
			});
			dialog.setButton(dialog.BUTTON_NEGATIVE, "否", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});

			dialog.show();

		}
		// 置顶
		if (id == 1003) {
			itemToTop(pos);

		}
		return super.onContextItemSelected(item);
	}

	// 删除的方法
	public void deleteItem(int pos) {
		HashMap<String, Object> map = data.get(pos);
		int record_id = (Integer) map.get(Record.RECORD_ID);
		Dao<Record, Integer> mDao = getDao();
		try {
			mDao.deleteById(record_id);
			Toast.makeText(getApplicationContext(), "删除成功", Toast.LENGTH_SHORT).show();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		data.remove(pos);
		adapter.notifyDataSetChanged();
	}

	// 置顶的方法
	public void itemToTop(int pos) {
		long time = System.currentTimeMillis();
		HashMap<String, Object> map = data.get(pos);
		// 替换成新的时间
		map.remove(Record.CREATE_AT);
		map.put(Record.CREATE_AT, time);
		data.remove(pos);
		data.add(0, map);
		adapter.notifyDataSetChanged();
		int record_id = (Integer) map.get(Record.RECORD_ID);
		Dao<Record, Integer> mDao = getDao();
		try {
			Record record = mDao.queryForId(record_id);
			record.create_at = time;
			mDao.createOrUpdate(record);
		} catch (SQLException e) {
			e.printStackTrace();
		}

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

	private class MyAdapter extends ArrayAdapter {

		private LayoutInflater inflater;

		public MyAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// if (convertView == null) {
			convertView = inflater.inflate(R.layout.record_list_item, null);
			// }
			TextView title = (TextView) convertView.findViewById(R.id.title_TextView);
			TextView createAt = (TextView) convertView.findViewById(R.id.create_at_TextView);

			HashMap<String, Object> map = getItem(position);

			title.setText(map.get(Record.TITLE) + "");
			title.setTextSize(title_text_size);

			long create_at_time = (Long) map.get(Record.CREATE_AT);
			// String date = Utils.formatTimeInMillis(create_at_time);
			createAt.setText(Utils.formatTimeInMillis(create_at_time));

			return convertView;
		}

		public HashMap<String, Object> getItem(int position) {

			return data.get(position);
		}

		@Override
		public int getCount() {
			return data.size();
		}
	}

}
