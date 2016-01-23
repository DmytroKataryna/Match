package task.test.kataryna.dmytro.match.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import task.test.kataryna.dmytro.match.R;

/**
 * Created by dmytroKataryna on 22.01.16.
 */
public class MatchActivity extends AppCompatActivity {

    public static final String PERSON_IMAGE = "personIMG";

    public static void open(Context context, String photoURL) {
        context.startActivity(new Intent(context, MatchActivity.class)
                .putExtra(PERSON_IMAGE, photoURL)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        String imageURL = getIntent().getStringExtra(PERSON_IMAGE);
        Picasso.with(getApplicationContext()).load(imageURL).fit().into((ImageView) findViewById(R.id.personImageView));
    }

    public void backButton(View view) {
        onBackPressed();
    }
}
