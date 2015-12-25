package com.example.Notbook.database;

import java.sql.SQLException;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class ORMLiteDatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static ORMLiteDatabaseHelper mDatabaseHelper = null;
	private Dao<Record, Integer> mRecordDao = null;

	private final static String DataBase_NAME = "ormlite.db";
	private final static int DataBase_VERSION = 1;

	public ORMLiteDatabaseHelper(Context context, String databaseName,
			CursorFactory factory, int databaseVersion) {
		super(context, DataBase_NAME, factory, DataBase_VERSION);
	}

	public static ORMLiteDatabaseHelper getInstance(Context context) {
		if (mDatabaseHelper == null) {
			mDatabaseHelper = new ORMLiteDatabaseHelper(context, DataBase_NAME,
					null, DataBase_VERSION);
		}

		return mDatabaseHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0, ConnectionSource connectionSource) {

		Log.d(this.getClass().getName(), "ORMLite数据库 -> onCreate");

		try {
			TableUtils.createTableIfNotExists(connectionSource, Record.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource arg1,
			int arg2, int arg3) {

		Log.i(this.getClass().getName(), "数据库 -> onUpgrade");

		try {
			// 删除旧的数据库表。
			TableUtils.dropTable(connectionSource, Record.class, true);

			// 重新创建新版的数据库。
			onCreate(database, connectionSource);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Dao<Record, Integer> getUserDao() {
		if (mRecordDao == null) {
			try {
				mRecordDao = getDao(Record.class);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}

		return mRecordDao;
	}

	@Override
	public void close() {
		super.close();
		mRecordDao = null;
	}
}
