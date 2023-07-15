package com.example.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.pets.data.PetContract.PetEntry;

public class PetProvider extends ContentProvider {

    // Database helper object
    private PetDbHelper mDbHelper;
    /** Tag for the log messages */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    private static final int PETS = 100;
    private static final int PET_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS,PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PET_ID);
    }


/* ************************************************************************ON-CREATE METHOD**************************************************************************************************/
    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbHelper = new PetDbHelper(getContext());

        return true;
    }
/* ************************************************************************ON-CREATE METHOD  END**************************************************************************************************/

/* ************************************************************************QUERY METHOD**************************************************************************************************/
    // returns the cursor containing the rows of interest.
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                // TODO: Perform database query on pets table
                cursor = db.query(PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PET_ID:
                selection = PetEntry._ID + "=?";
                // ContentUris.parseId(uri) is used when we have to fetch the last digit of URI example -> content://com.example.android.pets/pets/5  so we get "5" in selectionArgs
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(PetEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // set notifaction URI on the cursor so we know what content uri the cursor was created for. if the data at this URI changes then
        // we know we need to update the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }
/* ************************************************************************QUERY METHOD END**************************************************************************************************/

/* ************************************************************************GET TYPE METHOD**************************************************************************************************/

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
/* ************************************************************************GET TYPE METHOD END**************************************************************************************************/

/* ************************************************************************INSERT METHOD**************************************************************************************************/
    // returns content uri for the specific pet that was just inserted.
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PETS:
                return insertPet(uri,contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
                // part of insert method.
     private Uri insertPet(Uri uri,ContentValues values){
                    // TODO: To check data is valid or not
                    String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
                    Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
                    Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
                    // we dont have to check for breed bcoz than can be null.

                    if(name.equals(""))throw new IllegalArgumentException("Pet requires a name");
                    if(gender == null || !PetEntry.isValidGender(gender))throw new IllegalArgumentException("Pet requires valid gender");
                    if(weight != null && weight < 0)throw new IllegalArgumentException("Pet requires valid weight");





                    // TODO: Insert a new pet into database table with the given Content Values.
                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                    long id = db.insert(PetEntry.TABLE_NAME,null,values);
                    if(id == -1){
                        Log.e(LOG_TAG,"Failed to insert row for "+ uri);
                        return null;
                    }
                    // Notify all listeners that the data has changed for the pet content URI
                    getContext().getContentResolver().notifyChange(uri,null);
                    return ContentUris.withAppendedId(uri,id);
     }
/* ************************************************************************INSERT METHOD END **************************************************************************************************/

/* ************************************************************************DELETE METHOD**************************************************************************************************/

    // returns the no. of rows that were deleted.
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        // track the number of rows that were deleted.
        int rowsDeleted;

        switch (match){
            case PETS:
                rowsDeleted = db.delete(PetEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case PET_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(PetEntry.TABLE_NAME,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }
/* ************************************************************************DELETE METHOD END**************************************************************************************************/

/* ************************************************************************UPDATE METHOD**************************************************************************************************/

    // returns the no. of rows that were updated.
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
       final int match = sUriMatcher.match(uri);
       switch (match){
           case PETS:
               return updatePet(uri,contentValues,selection,selectionArgs);
           case PET_ID:
               // for the pet_id extract out the id from the uri so we know which row to update . selection will be "_id=?" and selection args
               // will be String array containing the actual ID.
               selection = PetEntry._ID + "=?";
               selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
               return updatePet(uri,contentValues,selection,selectionArgs);
           default:
               throw new IllegalArgumentException("Update is not supported for " + uri);
       }
    }
    private int updatePet(Uri uri,ContentValues values,String selection,String[] selectionArgs){


        // TODO: To check data is valid or not
          if(values.containsKey(PetEntry.COLUMN_PET_NAME)){
              String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
              if(name.equals(""))throw new IllegalArgumentException("Pet requires a name");
          }
          if(values.containsKey(PetEntry.COLUMN_PET_GENDER)){
              Integer gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
              if(gender == null || !PetEntry.isValidGender(gender))throw new IllegalArgumentException("Pet requires valid gender");
          }
          if(values.containsKey(PetEntry.COLUMN_PET_WEIGHT)){
              Integer weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
              if(weight != null && weight < 0)throw new IllegalArgumentException("Pet requires valid weight");
          }

        // if there is no values to update,then don't try to update the database.
        if(values.size() == 0)return 0;

        // TODO: Update the selected pets in the pets database table with the given ContentValues
         SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // TODO: Return the number of rows that were affected
        int rowsUpdated = db.update(PetContract.PetEntry.TABLE_NAME, values, selection, selectionArgs);


        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
/* ************************************************************************UPDATE METHOD END**************************************************************************************************/

}
