package task.test.kataryna.dmytro.match.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import task.test.kataryna.dmytro.match.Constants;
import task.test.kataryna.dmytro.match.R;
import task.test.kataryna.dmytro.match.helpers.CircleTransform;
import task.test.kataryna.dmytro.match.model.Person;
import task.test.kataryna.dmytro.match.otto.BusProvider;
import task.test.kataryna.dmytro.match.otto.PersonRemovedEvent;
import task.test.kataryna.dmytro.match.ui.MatchActivity;
import task.test.kataryna.dmytro.match.ui.PersonsFragment;
import task.test.kataryna.dmytro.match.ui.StartActivity;
import task.test.kataryna.dmytro.match.ui.service.NotificationService;

/**
 * Created by dmytroKataryna on 21.01.16.
 */
public class PersonsAdapter extends RecyclerView.Adapter<PersonsAdapter.PersonViewHolder> implements View.OnClickListener {

    private Context context;
    private List<Person> mData;

    public PersonsAdapter(List<Person> mData, Context context) {
        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public PersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_row, parent, false);
        return new PersonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PersonViewHolder holder, int position) {
        Person person = mData.get(position);
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            Picasso.with(context).load(person.getPhoto()).fit().transform(new CircleTransform()).into(holder.imageView);
        else
            Picasso.with(context).load(person.getPhoto()).fit().into(holder.imageView);

        holder.disLikeTextView.setTag(person);
        holder.likeTextView.setTag(person);

        holder.disLikeTextView.setTag(R.id.TAG_PERSON_POSITION, position);
        holder.likeTextView.setTag(R.id.TAG_PERSON_POSITION, position);

        checkPersonLikeStatus(holder.heartTextView, person);
    }

    private void checkPersonLikeStatus(TextView heartTextView, Person person) {
        if (person.getStatus().equals(Constants.Status.LIKE))
            heartTextView.setText(R.string.fontello_icon_heart);
        else
            heartTextView.setText(Constants.EMPTY);
    }

    public void addData(List<Person> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void updatePerson(Person person) {
        int position = getPersonPosition(person);
        updatePersonAt(position, person);
    }

    public void updatePersonAt(int position, Person person) {
        if (person.getStatus().equals(Constants.Status.REMOVED)) {
            NotificationService.start(context, person);
            removeAt(position, person);
        } else
            updateAt(position, person);
    }

    private void removeAt(int position, Person person) {
        if (position < Constants.ZERO) return;
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());

        BusProvider.getInstance().post(new PersonRemovedEvent(person));

        if (mData.isEmpty())
            StartActivity.open(context);
    }


    private void updateAt(int position, Person person) {
        if (position < Constants.ZERO) return;
        mData.set(position, person);
        notifyItemChanged(position);
    }

    public int getPersonPosition(Person person) {
        for (int i = 0; i < mData.size(); i++)
            if (mData.get(i).getId() == person.getId())
                return i;

        return Constants.NOT_FOUND;
    }

    @Override
    public void onClick(View v) {
        final Person person = (Person) v.getTag();
        final Integer position = (Integer) v.getTag(R.id.TAG_PERSON_POSITION);

        switch (v.getId()) {
            case R.id.likeTextView:
                if (person.getStatus().equals(Constants.Status.LIKE))
                    MatchActivity.open(context, person.getPhoto());
            case R.id.disLikeTextView:
                removeAt(position, person);
                break;
        }
    }

    public class PersonViewHolder extends RecyclerView.ViewHolder {

        public TextView disLikeTextView, likeTextView, heartTextView;
        public ImageView imageView;

        public PersonViewHolder(View itemView) {
            super(itemView);
            disLikeTextView = (TextView) itemView.findViewById(R.id.disLikeTextView);
            heartTextView = (TextView) itemView.findViewById(R.id.heartTextView);
            likeTextView = (TextView) itemView.findViewById(R.id.likeTextView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);

            disLikeTextView.setOnClickListener(PersonsAdapter.this);
            likeTextView.setOnClickListener(PersonsAdapter.this);

            disLikeTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fontello.ttf"));
            heartTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fontello.ttf"));
            likeTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fontello.ttf"));
        }
    }
}
