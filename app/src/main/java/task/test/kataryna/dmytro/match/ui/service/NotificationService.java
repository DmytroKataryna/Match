package task.test.kataryna.dmytro.match.ui.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import task.test.kataryna.dmytro.match.Constants;
import task.test.kataryna.dmytro.match.R;
import task.test.kataryna.dmytro.match.helpers.CircleTransform;
import task.test.kataryna.dmytro.match.model.Person;

/**
 * Created by dmytroKataryna on 22.01.16.
 */
public class NotificationService extends IntentService {

    private static final String PERSON_ID = "personID";
    private static final String PERSON_IMAGE = "personIMG";
    private static final String PERSON_STATUS = "personStatus";

    public static void start(Context context, Person person) {
        context.startService(new Intent(context, NotificationService.class)
                .putExtra(PERSON_ID, person.getId())
                .putExtra(PERSON_IMAGE, person.getPhoto())
                .putExtra(PERSON_STATUS, person.getStatus()));
    }

    public NotificationService() {
        super(NotificationService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int mId = intent.getIntExtra(PERSON_ID, Constants.NOT_FOUND);
        String mImageURL = intent.getStringExtra(PERSON_IMAGE);
        String mStatus = intent.getStringExtra(PERSON_STATUS);

        try {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.notification_icon)
                    .setLargeIcon(Picasso.with(getApplicationContext()).load(mImageURL).transform(new CircleTransform()).resize(80, 80).get())
                    .setContentTitle("Match notification")
                    .setContentText("Person id: " + mId + " was " + mStatus)
                    .setSound(mStatus.equals(Constants.Status.REMOVED) ? null : RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setVibrate(mStatus.equals(Constants.Status.REMOVED) ? new long[]{0l} : new long[]{500l, 500l, 500l})
                    .setAutoCancel(true);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(mId, mBuilder.build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
