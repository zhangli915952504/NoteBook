package com.example.Notbook.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "records") 
public class Record {
	public final static String RECORD_ID="recoro_id";
	public final static String TITLE="title";
	public final static String CONTENT="content";
	public final static String CREATE_AT="create_at";
	
	@DatabaseField(generatedId=true,columnName=RECORD_ID)
	public	int record_id;
	
	@DatabaseField(columnName=TITLE)
	public	String title;
	
	@DatabaseField(columnName=CONTENT)
	public	String content;
	
	@DatabaseField(columnName=CREATE_AT)
	public	long create_at;
}
