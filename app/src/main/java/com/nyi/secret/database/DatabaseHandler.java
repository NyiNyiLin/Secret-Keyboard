package com.nyi.secret.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "KeyBoardKey";

	// Contacts table name
	private static final String TABLE_CONTACTS = "Key";

	// Contacts Table Columns names
	private static final String KEY_CODE = "code";
	private static final String KEY_CUSTOM_CODE= "custom_code";
	private static final String KEY_NAME = "keyName";
	private static final String KEY_WORD= "text";
	private static final String KEY_ENCRYPT= "encrypt";
	private static final String KEY_PASSWORD= "password";



	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
				+ KEY_CODE + " INTEGER PRIMARY KEY,"
				+ KEY_CUSTOM_CODE + " INTEGER,"
				+ KEY_NAME + " TEXT,"
				+ KEY_WORD + " TEXT,"
				+ KEY_ENCRYPT + " TEXT,"
				+ KEY_PASSWORD +" INTEGER"
				+ ")";
		db.execSQL(CREATE_CONTACTS_TABLE);

		//This is for inserting data in database
		for(int code=97; code<=122; code++){
			char key = (char)code;
			String st="INSERT INTO Key VALUES ("+code+",-"+code+",'"+key+"','','"+eneryppt(key)+"',0);";
			db.execSQL(st);
		}

		//This is for key press sound enables or not
		db.execSQL("INSERT INTO Key VALUES (-999,0,'Sound','','',0);");
		db.execSQL("INSERT INTO Key VALUES (-998,0,'Vibrate','','',0);");
		db.execSQL("INSERT INTO Key VALUES (-997,0,'first_time_or_not','','',0);");
		
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */
	//Adding new data
	public void addData(int key_code,String key,String text){
		SQLiteDatabase db=this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_CODE, key_code);
		values.put(KEY_NAME, key); // Key Name
		values.put(KEY_WORD, text); // Key Text
		
		db.insert(TABLE_CONTACTS, null, values);
		db.close(); // Closing database connection
		
		
	}
	// Updating single key
	public int updateKeyText(CustomKey key) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_CODE, key.getKEY_CODE());
		values.put(KEY_CUSTOM_CODE, key.getKEY_CUSTOM_CODE());
		values.put(KEY_NAME, key.getKEY_NAME()); // Key Name
		values.put(KEY_WORD, key.getKEY_WORD()); // Key Text
		values.put(KEY_ENCRYPT, key.getKEY_ENCRYPT()); // Key Encrypt
		values.put(KEY_PASSWORD, key.getKEY_PASSWORD());

		Log.i("Save", key.getKEY_WORD());
		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_CODE + " = ?",new String[] { String.valueOf(key.getKEY_CODE())});
	}

	//Get Custom Key By Name
	public CustomKey getCustomKeyByName(String key_name){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_CODE,KEY_CUSTOM_CODE,
						KEY_NAME, KEY_WORD,KEY_ENCRYPT, KEY_PASSWORD }, KEY_NAME + "=?",
				new String[] { key_name }, null, null, null,null);
		if (cursor != null)
			cursor.moveToFirst();

		CustomKey customKey=new CustomKey(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),cursor.getString(2),cursor.getString(3),cursor.getString(4),Integer.parseInt(cursor.getString(5)));
		return customKey;

	}
	
	//Get Text by Key Name
	public String getTextByKeyName(String key_name){
		SQLiteDatabase db = this.getReadableDatabase();
		
		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_CODE,
						KEY_CUSTOM_CODE,
						KEY_NAME,
						KEY_WORD,
						KEY_ENCRYPT,
						KEY_PASSWORD }, KEY_NAME + "=?",
				new String[] { key_name }, null, null, null,null);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor.getString(3);

	}
	//Get Text by Key Custom id
	public String getTextByKeyCustomID(int customID){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_CODE,KEY_CUSTOM_CODE,
						KEY_NAME, KEY_WORD,KEY_ENCRYPT, KEY_PASSWORD }, KEY_CUSTOM_CODE + "=?",
				new String[] { String.valueOf(customID) }, null, null, null,null);
		if (cursor != null) cursor.moveToFirst();

		return cursor.getString(3);

	}
	//Get Encrypt Text by Key id
	public String getEncryptTextByKeyID(int id){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_CODE,KEY_CUSTOM_CODE,
						KEY_NAME, KEY_WORD,KEY_ENCRYPT, KEY_PASSWORD }, KEY_CODE + "=?",
				new String[] { String.valueOf(id) }, null, null, null,null);
		if (cursor != null) cursor.moveToFirst();

		return cursor.getString(4);

	}
	//Get Custom code by Key code
	public int getCustomCodebyKeyCode(int code){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_CODE,KEY_CUSTOM_CODE,
						KEY_NAME, KEY_WORD,KEY_ENCRYPT, KEY_PASSWORD }, KEY_CODE + "=?",
				new String[] { String.valueOf(code) }, null, null, null,null);
		if (cursor != null) cursor.moveToFirst();

		return Integer.parseInt(cursor.getString(1));

	}

	// Getting All Contacts
	public ArrayList<CustomKey> getAllKey() {
		ArrayList<CustomKey> keyList = new ArrayList<CustomKey>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				if(!cursor.getString(3).isEmpty()) {
					CustomKey customKey = new CustomKey();
					customKey.setKEY_CODE(Integer.parseInt(cursor.getString(0)));
					customKey.setKEY_CUSTOM_CODE(Integer.parseInt(cursor.getString(1)));
					customKey.setKEY_NAME(cursor.getString(2));
					customKey.setKEY_WORD(cursor.getString(3));
					customKey.setKEY_ENCRYPT(cursor.getString(4));
					customKey.setKEY_PASSWORD(Integer.parseInt(cursor.getString(5)));
					// Adding contact to list
					keyList.add(customKey);
				}
			} while (cursor.moveToNext());
		}

		// return contact list
		return keyList;
	}

	/*
	Get Real word by encrypt word
	Decryption
	 */
	public String getRealWordByEncryptWord(char world){
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_CODE, KEY_CUSTOM_CODE,
						KEY_NAME, KEY_WORD, KEY_ENCRYPT, KEY_PASSWORD}, KEY_ENCRYPT + "=?",
				new String[]{String.valueOf(world)}, null, null, null, null);
		if (cursor != null) cursor.moveToFirst();

		return cursor.getString(2);

	}


	public char eneryppt(char name){
		char convert='a';
		switch (name){
			case 's':convert='m';break;
			case 'm':convert='g';break;
			case 'g':convert='a';break;
			case 'a':convert='t';break;
			case 't':convert='n';break;
			case 'n':convert='h';break;
			case 'h':convert='b';break;
			case 'b':convert='y';break;
			case 'y':convert='u';break;
			case 'u':convert='o';break;
			case 'o':convert='i';break;
			case 'i':convert='c';break;
			case 'c':convert='z';break;
			case 'z':convert='v';break;
			case 'v':convert='p';break;
			case 'p':convert='j';break;
			case 'j':convert='d';break;
			case 'd':convert='w';break;
			case 'w':convert='q';break;
			case 'q':convert='k';break;
			case 'k':convert='e';break;
			case 'e':convert='x';break;
			case 'x':convert='r';break;
			case 'r':convert='l';break;
			case 'l':convert='f';break;
			case 'f':convert='s';break;
			default:convert='a';
		}
		return convert;

	}

}
