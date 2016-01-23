package task.test.kataryna.dmytro.match.ui;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.testpackage.test_sdk.android.testlib.API;
import org.testpackage.test_sdk.android.testlib.interfaces.SuccessCallback;

import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;
import task.test.kataryna.dmytro.match.Constants;
import task.test.kataryna.dmytro.match.R;
import task.test.kataryna.dmytro.match.animation.AnimationCompleteListener;
import task.test.kataryna.dmytro.match.animation.AnimationListener;
import task.test.kataryna.dmytro.match.db.DataBaseHelper;
import task.test.kataryna.dmytro.match.db.PersonDAO;
import task.test.kataryna.dmytro.match.helpers.PreferencesUtils;

/**
 * Created by dmytroKataryna on 22.01.16.
 */
public class StartActivity extends AppCompatActivity {

    private DataBaseHelper databaseHelper;
    private TextView generateView;

    public static void open(Context context) {
        context.startActivity(new Intent(context, StartActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        generateView = (TextView) findViewById(R.id.generateView);
        redirectToMainActivity();
    }

    private void redirectToMainActivity() {
        PersonDAO dao = new PersonDAO(getHelper().getSimpleDataDao());
        if (!dao.getAllPersons().isEmpty())
            MainActivity.open(getApplicationContext());
    }

    public void generatePersons(View view) {
        animation(R.string.loading_text, R.drawable.button_yellow_background).onSuccessTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                return refreshPersons();
            }
        }).onSuccessTask(new Continuation<Void, Task<Void>>() {
            @Override
            public Task<Void> then(Task<Void> task) throws Exception {
                return animation(R.string.success_text, R.drawable.button_green_background);
            }
        }).onSuccess(new Continuation<Void, Void>() {
            @Override
            public Void then(Task<Void> task) throws Exception {
                PreferencesUtils.get(getApplicationContext()).setLoadedPage(Constants.ZERO);
                MainActivity.open(getApplicationContext());
                return null;
            }
        });
    }

    private Task<Void> refreshPersons() {
        final TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        API.INSTANCE.refreshPersons(new SuccessCallback() {
            @Override
            public void onSuccess() {
                tcs.setResult(null);
            }
        });
        return tcs.getTask();
    }

    private Task<Void> animation(final int titleResource, final int drawable) {
        final TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
        startFlipAnimation(generateView, true, new AnimationListener(new AnimationCompleteListener() {
            @Override
            public void onAnimationComplete() {
                generateView.setBackgroundResource(drawable);
                generateView.setText(titleResource);
                startFlipAnimation(generateView, false, new AnimationListener(new AnimationCompleteListener() {
                    @Override
                    public void onAnimationComplete() {
                        tcs.setResult(null);
                    }
                }));
            }
        }));
        return tcs.getTask();
    }

    private void startFlipAnimation(View view, boolean opening, AnimationListener listener) {
        ObjectAnimator animationRotation = ObjectAnimator.ofFloat(view, "rotationY", opening ? 0.0f : 271f, opening ? 89f : 360f);
        animationRotation.setDuration(700);
        animationRotation.setInterpolator(new AccelerateDecelerateInterpolator());
        animationRotation.start();
        animationRotation.addListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public DataBaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getApplicationContext(), DataBaseHelper.class);
        }
        return databaseHelper;
    }
}
