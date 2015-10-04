package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    public static String ACTION_DOWNLOAD_IMAGE = "vandy.mooc.action.DOWNLOAD_IMAGE";
    private static String URI = "uri";
    private final String TAG = getClass().getSimpleName();
    private Uri pathToImageUri;
    private Uri mUrl;
    private DownloadImageTask mDownloadImageTask;


    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            mUrl = getIntent().getData();
        }else{
            mUrl = Uri.parse(savedInstanceState.getString(URI));
        }

        // Get the URL associated with the Intent data.

        Log.d(TAG, "Download Image Activity on create: "+ mUrl.toString());

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.
        mDownloadImageTask = new DownloadImageTask();
        mDownloadImageTask.execute(mUrl);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(URI, mUrl.toString());
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mDownloadImageTask != null){
            mDownloadImageTask.cancel(true);
        }
    }

    @Override
    public void finish(){
        if(pathToImageUri == null){
            setResult(RESULT_CANCELED, new Intent());
        }else{
            setResult(RESULT_OK, new Intent().setData(pathToImageUri));
        }
        super.finish();
    }


    private class DownloadImageTask extends AsyncTask<Uri, Integer, Uri> {

        protected Uri doInBackground(Uri... urls) {
            Utils downloadUtils = new Utils();
            return downloadUtils.downloadImage(DownloadImageActivity.this, urls[0]);
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(Uri result) {
            pathToImageUri = result;
            finish();
        }
    }




}