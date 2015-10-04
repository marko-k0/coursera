package vandy.mooc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;


/**
 * A main Activity that prompts the user for a URL to an image and
 * then uses Intents and other Activities to download the image and
 * view it.
 */
public class MainActivity extends LifecycleLoggingActivity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * A value that uniquely identifies the request to download an
     * image.
     */
    private static final int DOWNLOAD_IMAGE_REQUEST = 1;

    private static final int FILTER_IMAGE_REQUEST = 2;

    /**
     * EditText field for entering the desired URL to an image.
     */
    private EditText mUrlEditText;

    /**
     * URL for the image that's downloaded by default if the user
     * doesn't specify otherwise.
     */
    private Uri mDefaultUrl =
            Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");

    private boolean mProcessButtonClick = true;


    interface ResultCommand {
        void execute(Intent data);
        void printError(Intent data);
    };


    private SparseArray<ResultCommand> mResultArray = new SparseArray<ResultCommand>();

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);

        // Set the default layout.
        setContentView(R.layout.main_activity);

        // Cache the EditText that holds the urls entered by the user
        // (if any).
        mUrlEditText = (EditText) findViewById(R.id.url);

        mResultArray.put(DOWNLOAD_IMAGE_REQUEST, new ResultCommand() {
            @Override
            public void execute(Intent data) {
                mProcessButtonClick = true;
                // Call the makeGalleryIntent() factory method to
                // create an Intent that will launch the "Gallery" app
                // by passing in the path to the downloaded image
                // file.
                Uri pathToImageUri = data.getData();

                Intent filterImageIntent;
                filterImageIntent = makeFilterImageIntent(pathToImageUri);

                Log.d(TAG, "Calling FilterImageActivity");
                startActivityForResult(filterImageIntent, FILTER_IMAGE_REQUEST);

            }

            @Override
            public void printError(Intent data) {
                Utils.showToast(MainActivity.this, "The image could not be downloaded");
                mProcessButtonClick = true;
            }
        });

        mResultArray.put(FILTER_IMAGE_REQUEST, new ResultCommand() {
            @Override
            public void execute(Intent data) {
                // Call the makeGalleryIntent() factory method to
                // create an Intent that will launch the "Gallery" app
                // by passing in the path to the downloaded image
                // file.

                Uri pathToImageUri = data.getData();

                // Start the Gallery Activity.
                Log.d(TAG, "Opening gallery");
                startActivity(makeGalleryIntent(pathToImageUri.toString()));
            }

            @Override
            public void printError(Intent data) {
                Utils.showToast(MainActivity.this,
                        "The image could not be displayed");
            }
        });



    }

    /**
     * Called by the Android Activity framework when the user clicks
     * the "Download Image" button.
     *
     * @param view The view.
     */
    public void downloadImage(View view) {
        try {
            // Hide the keyboard.
            hideKeyboard(this,
                    mUrlEditText.getWindowToken());

            Log.d(TAG, "Download Image pressed.");

            startDownloadImageActivity(getUrl());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startDownloadImageActivity(Uri url){
        Log.d(TAG, "startDownloadImageActivity() "+ url.toString());
        if(url != null){
            if(mProcessButtonClick == false){
                Utils.showToast(this, "Already downloading an image.");
            }else if(!URLUtil.isValidUrl(url.toString())){
                Utils.showToast(this, "Invalid url.");
            }else{
                mProcessButtonClick = false;
                Intent intent = makeDownloadImageIntent(url);
                startActivityForResult(intent, DOWNLOAD_IMAGE_REQUEST);
            }
        }
    }

    /**
     * Hook method called back by the Android Activity framework when
     * an Activity that's been launched exits, giving the requestCode
     * it was started with, the resultCode it returned, and any
     * additional data from it.
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        // Check if the started Activity completed successfully.
        if (resultCode == RESULT_OK) {
            // Check if the request code is what we're expecting
            mResultArray.get(requestCode).execute(data);
        }
        // Check if the started Activity did not complete successfully
        // and inform the user a problem occurred when trying to
        // download contents at the given URL.
        else if (resultCode == RESULT_CANCELED) {
            mResultArray.get(requestCode).printError(data);
        }
    }

    /**
     * Factory method that returns an implicit Intent for viewing the
     * downloaded image in the Gallery app.
     */
    private Intent makeGalleryIntent(String pathToImageFile) {
        // Create an intent that will start the Gallery app to view
        // the image.

        return new Intent(Intent.ACTION_VIEW).
                setDataAndType(Uri.parse("file://" + pathToImageFile), "image/*");
    }

    /**
     * Factory method that returns an implicit Intent for downloading
     * an image.
     */
    private Intent makeDownloadImageIntent(Uri url) {
        return new Intent(DownloadImageActivity.ACTION_DOWNLOAD_IMAGE, url);
    }

    private Intent makeFilterImageIntent(Uri uri){
        return new Intent(this, FilterImageActivity.class).setData(uri);
    }

    /**
     * Get the URL to download based on user input.
     */
    protected Uri getUrl() {
        Uri url = null;

        // Get the text the user typed in the edit text (if anything).
        url = Uri.parse(mUrlEditText.getText().toString());

        // If the user didn't provide a URL then use the default.
        String uri = url.toString();
        if (uri == null || uri.equals(""))
            url = mDefaultUrl;

        return url;
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public void hideKeyboard(Activity activity,
                             IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                0);
    }
}