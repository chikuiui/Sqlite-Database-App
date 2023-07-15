/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.pets;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.pets.data.PetContract.PetEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

  //  private PetDbHelper mDbHelper;
    private static final int PET_LOADER = 0;
    PetCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

//        mDbHelper = new PetDbHelper(this);
        ListView petListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);

        // setup an adapter to create a list item for each row of pet data in the cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
         mCursorAdapter = new PetCursorAdapter(this,null);
         petListView.setAdapter(mCursorAdapter);

         petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                 Intent intent = new Intent(CatalogActivity.this,EditorActivity.class);
                 // form the content uri that represent the specific pet that was clicked on, by appending the "id" .
                 Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI,id);

                 // set the uri on the data field of the intent
                 intent.setData(currentPetUri);
                 startActivity(intent);
             }
         });

        // Kick off the loader
        getSupportLoaderManager().initLoader(PET_LOADER, null, this);

    }

//    @Override
//    protected void onStart(){
//        super.onStart();
//        displayDatabaseInfo();
//    }


//    private void displayDatabaseInfo() {
//
//        // Create and/or open a database to read from it like .open shelter.db the below statement is old.
////        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
////        // Perform this raw SQL query "SELECT * FROM pets"
////        // to get a Cursor that contains all rows from the pets table.
////        Cursor cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null);
//        String [] Projection = {PetEntry._ID,
//                                PetEntry.COLUMN_PET_NAME,
//                                PetEntry.COLUMN_PET_BREED,
//                                PetEntry.COLUMN_PET_GENDER,
//                                PetEntry.COLUMN_PET_WEIGHT
//        };
//
//       // currently we are directly using database which is wrong so thats why we create PetProvider which extends ContentProvider so we use that one.
////        Cursor cursor = db.query(PetEntry.TABLE_NAME,
////                                 Projection,
////                                 null,
////                                 null,
////                                 null,
////                                 null,
////                                 null);
//
//        // THe below code is using ContentProvider.
//        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI,Projection,null,null,null);
//
//        ListView petListView = (ListView) findViewById(R.id.list);
//
//        PetCursorAdapter adapter = new PetCursorAdapter(this,cursor);
//
//        // Display the number of rows in the Cursor (which reflects the number of rows in the
//        // pets table in the database).
////        TextView displayView = (TextView) findViewById(R.id.list_view_pets);
//
//   //     try {
//
////            displayView.setText("The pets table comtains: " + cursor.getCount() + " pets.\n\n");
////            displayView.append(PetEntry._ID + " - " + PetEntry.COLUMN_PET_NAME + " - "+
////                               PetEntry.COLUMN_PET_BREED + " - " + PetEntry.COLUMN_PET_GENDER + " - "+
////                               PetEntry.COLUMN_PET_WEIGHT + "\n");
//
//            //Figure out the index at each column
////            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
////            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
////            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
////            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
////            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
////
////            //  Iterate through all the returned rows in the cursor
////            while(cursor.moveToNext()){
////                // use that index to extract the String or Int value of the word at the current row the cursor is on.
////                int currentID = cursor.getInt(idColumnIndex);
////                String currentName = cursor.getString(nameColumnIndex);
////                String currentBreed = cursor.getString(breedColumnIndex);
////                int currentGender = cursor.getInt(genderColumnIndex);
////                int currentWeight = cursor.getInt(weightColumnIndex);
//////                displayView.append(("\n" + currentID + " - " + currentName   + " - " +
//////                                        currentBreed + " - " + currentGender + " - " +
//////                                        currentWeight));
////            }
//            petListView.setAdapter(adapter);
//   //     } finally {
//            // Always close the cursor when you're done reading from it. This releases all its
//            // resources and makes it invalid.
//  //          cursor.close();
//  //      }
//    }
    private void insertPet(){
      //  SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME,"Garfield");
        values.put(PetEntry.COLUMN_PET_BREED,"Tabby");
        values.put(PetEntry.COLUMN_PET_GENDER,PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT,7);

        // TODO : We have alredy done this work in PetProvider.java insert method so we skip the below code.
//        // insert into database but it will return an integer if that integer is -1 means its error.
//        long newRowId =  db.insert(PetEntry.TABLE_NAME,null,values);
//
//        if(newRowId == -1){
//            Toast.makeText(this,"Error while inserting the data into database. ",Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(this,"Data is successfully stored into the database. ",Toast.LENGTH_SHORT).show();
//        }
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI,values);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                // now what we do is when this option is clicked in app
                insertPet();
            //    displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

// *********************************************************************** LOADER METHODS BELOW WHICH ARE OVERRIDE *************************************************


    @NonNull
    @Override
    public androidx.loader.content.Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // define a projection that we cared about.
        String[] projection = {PetEntry._ID,
                               PetEntry.COLUMN_PET_NAME,
                               PetEntry.COLUMN_PET_BREED};
        // This loader will execute the contentProvider query method on a background thread.
        // NOTE :->    we are not creating new CursorLoader class because android provide us CursorLoader class for databases
        //             in quake report app we have to create our own loader class called EarthquakeLoader for background task
        //             to pass the value but here we don't need to.
        return new CursorLoader(this,PetEntry.CONTENT_URI,projection,null,null,null );
    }

    @Override
    public void onLoadFinished(@NonNull androidx.loader.content.Loader<Cursor> loader, Cursor data) {
        // update PetCursorAdapter with this new cursor containing updated pet data.
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull androidx.loader.content.Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }


}
