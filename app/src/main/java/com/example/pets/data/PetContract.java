package com.example.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class PetContract {

    private PetContract(){}

    //we concatenate the CONTENT_AUTHORITY constant with the scheme “content://” we will create the BASE_CONTENT_URI which will be shared by every URI associated with PetContract
    public static final String CONTENT_AUTHORITY = "com.example.pets";

    //To make this a usable URI, we use the parse method which takes in a URI string and returns a Uri.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    //This constants stores the path for each of the tables which will be appended to the base content URI.
    public static final String PATH_PETS = "pets";







    public static abstract class PetEntry implements BaseColumns{

        // The content URI to access the pet data in the provider.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_PETS);

            /*we’re making use of the constants defined in the ContentResolver class: CURSOR_DIR_BASE_TYPE
    (which maps to the constant "vnd.android.cursor.dir") and CURSOR_ITEM_BASE_TYPE (which maps to the constant “vnd.android.cursor.item”).*/

        /**The MIME type of the {@link #CONTENT_URI} for a list of pets.*/
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        /**The MIME type of the {@link #CONTENT_URI} for a single pet.*/
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        public static final String TABLE_NAME = "pets";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PET_NAME = "name";
        public static final String COLUMN_PET_BREED = "breed";
        public static final String COLUMN_PET_GENDER = "gender";
        public static final String COLUMN_PET_WEIGHT = "weight";

        // Possible values for the gender of the pets
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        public static boolean isValidGender(int gender){
            if(gender == GENDER_FEMALE || gender == GENDER_MALE || gender == GENDER_UNKNOWN)return true;
            else return false;
        }

    }
}
