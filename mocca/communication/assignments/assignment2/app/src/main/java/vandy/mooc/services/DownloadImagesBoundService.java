package vandy.mooc.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;

/**
 * A Bound Service that concurrently downloads an image requested via
 * a Message passed to a Request Messenger, stores the image in a file
 * on the local device, and returns the URI to the downloaded image
 * file back to the Activity via the Reply Messenger passed with the
 * original Message.
 */
public class DownloadImagesBoundService extends LifecycleLoggingService {
     /**
     * A RequestHandler that handles request Messages send from the
     * Activity.
     */
    private RequestHandler mRequestHandler = null;

    /**
     * A Messenger that encapsulates the RequestHandler used to handle
     * request Messages sent from the Activity.
     */
    private Messenger mRequestMessenger = null;

    /**
     * Factory method that returns an explicit Intent for downloading
     * an image.
     */
    public static Intent makeIntent(Context context) {
        // Create an intent that will download the image from the web.
        return new Intent(context, DownloadImagesBoundService.class);
    }

    /**
     * Hook method called when the Service is created.
     */
    @Override
    public void onCreate() {
        // Create a RequestHandler used to handle request Messages sent from an Activity.
        mRequestHandler = new RequestHandler(this);

        // Create a Messenger that encapsulates the RequestHandler.
        mRequestMessenger = new Messenger(mRequestHandler);
        Log.d(TAG, "DownloadImagesBoundService called");
    }

    /**
     * Factory method that returns the underlying IBinder associated
     * with the Request Messenger.
     */
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        // Return the iBinder associated with the Request Messenger.
        return mRequestMessenger.getBinder();
    }

    /**
     * Hook method called when the last client unbinds from the
     * Service.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Shutdown the RequestHandler.
        mRequestHandler.shutdown();
    }
}
