package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;


public class FilterImageActivity extends Activity {

    public static String ACTION_FILTER_IMAGE = "vandy.mooc.action.FILTER_IMAGE";
    private final String TAG = getClass().getSimpleName();
    private static String URI = "uri";
    private Uri grayscaleImagePath;
    private Uri mUrl;
    private FilterImageTask mFilterImageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "FilterImageActivity onCreate()");
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
            mUrl = getIntent().getData();
        }else{
            mUrl = Uri.parse(savedInstanceState.getString(URI));
        }
        mFilterImageTask = new FilterImageTask();
        mFilterImageTask.execute(mUrl);
    }

    @Override
    public void finish(){
        Log.d(TAG, "Result "+grayscaleImagePath.toString());
        setResult(RESULT_OK, new Intent().setData(grayscaleImagePath));
        super.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(URI, mUrl.toString());
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(mFilterImageTask != null){
            mFilterImageTask.cancel(true);
        }
    }

    private class FilterImageTask extends AsyncTask<Uri, Integer, Uri> {

        protected Uri doInBackground(Uri... urls) {
            Utils downloadUtils;
            downloadUtils = new Utils();
            grayscaleImagePath = downloadUtils.grayScaleFilter(FilterImageActivity.this, urls[0]);
            return grayscaleImagePath;
        }

        protected void onProgressUpdate(Integer... progress) {
            //setProgressPercent(progress[0]);
        }

        protected void onPostExecute(Uri result) {
            //showDialog("Downloaded " + result + " bytes");
            finish();
        }
    }

}
