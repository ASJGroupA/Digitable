package com.shalvahadebayo.digitable;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

public class AssignmentProvider extends ContentProvider {
	public static final String PROVIDER_NAME = "com.shalvahadebayo.provider.digitable";
	public static final String BASE_PATH = "assignments";
	public static final String URL = "content://" + PROVIDER_NAME + "/" + BASE_PATH;
	public static final Uri CONTENT_URI = Uri.parse(URL);
	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/assignments";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
			"/assignment";
	public static final String BASE_PATH2 = "courses";
	public static final String URL2 = "content://" + PROVIDER_NAME + "/" + BASE_PATH2;
	public static final Uri CONTENT_URI2 = Uri.parse(URL2);
	public static final String CONTENT_TYPE2 = ContentResolver.CURSOR_DIR_BASE_TYPE + "/courses";
	public static final String CONTENT_ITEM_TYPE2 = ContentResolver.CURSOR_ITEM_BASE_TYPE +
			"/course";

	/**
	 * for UriMatcher
	 */
	public static final int ASSIGNMENTS = 1;
	public static final int ASSIGNMENT_ID = 2;
	public static final int COURSES = 100;
	public static final int COURSE_ID = 200;

	private static final UriMatcher aUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static SQLiteDatabase db;

	static {
		aUriMatcher.addURI(PROVIDER_NAME, BASE_PATH, ASSIGNMENTS);
		aUriMatcher.addURI(PROVIDER_NAME, BASE_PATH + "/#", ASSIGNMENT_ID);
		aUriMatcher.addURI(PROVIDER_NAME, BASE_PATH2, COURSES);
		aUriMatcher.addURI(PROVIDER_NAME, BASE_PATH2 + "/#", COURSE_ID);
	}

	private MySQLiteHelper mySQLiteHelper;

	public AssignmentProvider() {
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Implement this to handle requests to delete one or more rows.
		int uriType = aUriMatcher.match(uri);
		SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
		int rowsDeleted;
		String id;

		switch (uriType)

		{
			case ASSIGNMENTS:
				rowsDeleted = db.delete(AssignmentTable.TABLE_ASSIGNMENTS, selection, selectionArgs);
				break;
			case ASSIGNMENT_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsDeleted = db.delete(AssignmentTable.TABLE_ASSIGNMENTS, AssignmentTable.COLUMN_ID + "="
							+ id, null);
				} else {
					rowsDeleted = db.delete(AssignmentTable.TABLE_ASSIGNMENTS, AssignmentTable.COLUMN_ID + "="
							+ id + " and " + selection, null);
				}
				break;
			case COURSES:
				rowsDeleted = db.delete(CourseTable.TABLE_COURSES, selection, selectionArgs);
				break;
			case COURSE_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection))
				{
					rowsDeleted = db.delete(CourseTable.TABLE_COURSES, CourseTable.COLUMN_ID + "="
							+ id, null);
				} else
				{
					rowsDeleted = db.delete(CourseTable.TABLE_COURSES, CourseTable.COLUMN_ID + "="
							+ id + " and " + selection, null);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public String getType(Uri uri) {
		// TODO: Implement this to handle requests for the MIME type of the data
		// at the given URI.
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = aUriMatcher.match(uri);
		SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
		long id;

		switch (uriType) {
			case ASSIGNMENTS:
				id = db.insert(AssignmentTable.TABLE_ASSIGNMENTS, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(BASE_PATH + "/" + id);
			case COURSES:
				id = db.insert(CourseTable.TABLE_COURSES, null, values);
				getContext().getContentResolver().notifyChange(uri, null);
				return Uri.parse(BASE_PATH2 + "/" + id);
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

	}

	@Override
	public boolean onCreate() {
		mySQLiteHelper = new MySQLiteHelper(getContext());
		db = mySQLiteHelper.getWritableDatabase();
		return (db != null);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
	                    String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qBuild = new SQLiteQueryBuilder();

		int uriType = aUriMatcher.match(uri);
		checkColumns(projection, uriType);




		switch (uriType) {
			case ASSIGNMENTS:
				qBuild.setTables(AssignmentTable.TABLE_ASSIGNMENTS);
				break;
			case ASSIGNMENT_ID:
				qBuild.setTables(AssignmentTable.TABLE_ASSIGNMENTS);
				qBuild.appendWhere(AssignmentTable.COLUMN_ID + "=" + uri.getLastPathSegment());
				break;
			case COURSES:
				qBuild.setTables(CourseTable.TABLE_COURSES);
				break;
			case COURSE_ID:
				qBuild.setTables(CourseTable.TABLE_COURSES);
				qBuild.appendWhere(CourseTable.COLUMN_ID + "=" + uri.getLastPathSegment());
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		/*if (sortOrder==null || sortOrder=="")
		{
			sortOrder=MySQLiteHelper.COLUMN_ID;
		}*/

		Cursor cursor = qBuild.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
	                  String[] selectionArgs) {
		int uriType = aUriMatcher.match(uri);
		SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
		int rowsUpdated;
		String id;

		switch (uriType) {
			case ASSIGNMENTS:
				rowsUpdated = db.update(AssignmentTable.TABLE_ASSIGNMENTS, values, selection, selectionArgs);
				break;
			case ASSIGNMENT_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection)) {
					rowsUpdated = db.update(AssignmentTable.TABLE_ASSIGNMENTS, values, AssignmentTable
							.COLUMN_ID + "="
							+ id, null);
				} else {
					rowsUpdated = db.update(AssignmentTable.TABLE_ASSIGNMENTS, values, AssignmentTable
							.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
				}
				break;
			case COURSES:
				rowsUpdated = db.update(CourseTable.TABLE_COURSES, values, selection, selectionArgs);
				break;
			case COURSE_ID:
				id = uri.getLastPathSegment();
				if (TextUtils.isEmpty(selection))
				{
					rowsUpdated = db.update(CourseTable.TABLE_COURSES
							, values, CourseTable
							.COLUMN_ID + "="
							+ id, null);
				} else
				{
					rowsUpdated = db.update(CourseTable.TABLE_COURSES, values, CourseTable
							.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private void checkColumns(String[] projection, int type)
	{
		switch (type)
		{
			case ASSIGNMENTS:
			case ASSIGNMENT_ID:
				String[] available = {AssignmentTable.COLUMN_ID, AssignmentTable.COLUMN_TITLE, AssignmentTable
						.COLUMN_DESCRIPTION, AssignmentTable.COLUMN_DEADLINE_DATE, AssignmentTable
						.COLUMN_DEADLINE_TIME, AssignmentTable.COLUMN_PRIORITY, AssignmentTable.COLUMN_REMINDER_SET};
				if (projection != null)
				{
					HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
					HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
					if (!availableColumns.containsAll(requestedColumns))
					{
						throw new IllegalArgumentException("Unknown columns in projection");
					}
				}
				break;
			case COURSES:
			case COURSE_ID:
				String[] available2 = {CourseTable.COLUMN_ID, CourseTable.COLUMN_COURSE_CODE,
						CourseTable.COLUMN_COURSE_TITLE, CourseTable.COLUMN_UNITS};
				if (projection != null)
				{
					HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
					HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available2));
					if (!availableColumns.containsAll(requestedColumns))
					{
						throw new IllegalArgumentException("Unknown columns in projection");
					}
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown this in projection");
		}

	}


}
