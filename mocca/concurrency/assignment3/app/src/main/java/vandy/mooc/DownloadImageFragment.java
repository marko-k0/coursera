package vandy.mooc;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * Fragment used to download and grayscale filter an image.
 */
public class DownloadImageFragment extends Fragment {
    /**
     * Custom/user result code.
     */
    public static final int RESULT_DOWNLOAD_ERROR = Activity.RESULT_FIRST_USER + 1;

    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    private Activity mDownloadActivity;
    private Context mAppContext;
    private DownloadAsyncTask mDownloadAsyncTask;
    private GrayScaleAsyncTask mGrayScaleAsyncTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mDownloadActivity = activity;
        if (mAppContext == null) {
            mAppContext = mDownloadActivity.getApplicationContext();

            // Get the URL associated with the Intent data.
            final Uri url = mDownloadActivity.getIntent().getData();

            // Download the image in the background.
            mDownloadAsyncTask = new DownloadAsyncTask();
            mDownloadAsyncTask.execute(url);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDownloadActivity = null;
    }

    private class DownloadAsyncTask extends AsyncTask<Uri, Void, Uri> {
        @Override
        protected Uri doInBackground(Uri... urls) {
            Log.d(TAG, "Downloading image from " + urls[0].toString());
            return Utils.downloadImage(mAppContext, urls[0]);
        }

        @Override
        protected void onPostExecute(Uri filePath) {
            if (filePath == null) {
                Log.e(TAG, "Error occurred trying to download image");
                if (mDownloadActivity != null) {
                    mDownloadActivity.setResult(RESULT_DOWNLOAD_ERROR);
                    mDownloadActivity.finish();
                }
            } else {
                mGrayScaleAsyncTask = new GrayScaleAsyncTask();
                mGrayScaleAsyncTask.execute(filePath);
            }
        }
    }

    private class GrayScaleAsyncTask extends AsyncTask<Uri, Void, Uri> {
        @Override
        protected Uri doInBackground(Uri... urls) {
            Log.d(TAG, "Applying grayscale filter on image from " + urls[0].toString());
            return Utils.grayScaleFilter(mAppContext, urls[0]);
        }

        @Override
        protected void onPostExecute(Uri filePath) {
            if (filePath == null) {
                Log.e(TAG, "Error occurred trying to download image");
                mDownloadActivity.setResult(RESULT_DOWNLOAD_ERROR);
            } else {
                Intent data = new Intent();
                data.setData(filePath);

                mDownloadActivity.setResult(Activity.RESULT_OK, data);
            }

            mDownloadActivity.finish();
        }
    }
}
