package vandy.mooc;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    private static final String TAG_DOWNLOAD_FRAGMENT = "download_fragment";

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

        FragmentManager fm = getFragmentManager();
        DownloadImageFragment downloadFragment =
                (DownloadImageFragment)fm.findFragmentByTag(TAG_DOWNLOAD_FRAGMENT);

        // If the Fragment is non-null, then it is currently being
        // retained across a configuration change.
        if (downloadFragment == null) {
            downloadFragment = new DownloadImageFragment();
            fm.beginTransaction().add(downloadFragment, TAG_DOWNLOAD_FRAGMENT).commit();
        }
    }
}
