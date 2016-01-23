package task.test.kataryna.dmytro.match.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import task.test.kataryna.dmytro.match.animation.LatLngInterpolator;
import task.test.kataryna.dmytro.match.animation.MarkerAnimation;
import task.test.kataryna.dmytro.match.db.DataBaseHelper;
import task.test.kataryna.dmytro.match.db.PersonDAO;
import task.test.kataryna.dmytro.match.helpers.CircleTransform;
import task.test.kataryna.dmytro.match.helpers.Utils;
import task.test.kataryna.dmytro.match.model.Person;
import task.test.kataryna.dmytro.match.otto.BusProvider;
import task.test.kataryna.dmytro.match.otto.PersonChangedEvent;
import task.test.kataryna.dmytro.match.otto.PersonRemovedEvent;
import task.test.kataryna.dmytro.match.otto.PersonsAddedEvent;

/**
 * Created by dmytroKataryna on 20.01.16.
 */
public class MapFragment extends SupportMapFragment implements OnMapReadyCallback {

    final Set<Target> protectedFromGarbageCollectorTargets = new HashSet<>();
    private HashMap<Integer, PersonMarker> collection;
    private GoogleMap mMap;

    private DataBaseHelper databaseHelper;
    private PersonDAO dao;

    public static MapFragment createInstance() {
        return new MapFragment();
    }

    public MapFragment() {
        super();
        getMapAsync(this);
        collection = new HashMap<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (dao == null)
            dao = new PersonDAO(getHelper(getActivity().getApplicationContext()).getSimpleDataDao());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void personUpdated(PersonChangedEvent event) {
        if (collection.containsKey(event.person.getId()))
            updateMarker(event.person);
    }

    @Subscribe
    public void personsAdded(PersonsAddedEvent event) {
        for (Person person : event.persons)
            createMarker(person);
    }

    @Subscribe
    public void personRemoved(PersonRemovedEvent event) {
        try {
            collection.remove(event.person.getId()).getMarker().remove();
        } catch (Exception ignored) {
        }
    }

    private void updateMarker(Person person) {
        Marker marker = collection.get(person.getId()).getMarker();
        LatLng destination = Utils.getLatLng(person.getLocation());
        MarkerAnimation.animateMarker(marker, destination, new LatLngInterpolator.Linear());
        marker.setPosition(destination);
        collection.put(person.getId(), new PersonMarker(person, marker));
    }

    private void createMarker(final Person person) {
        Target bitmapTarget = new BitmapTarget(person);
        protectedFromGarbageCollectorTargets.add(bitmapTarget);
        Picasso.with(getContext()).load(person.getPhoto()).resize(100, 100).transform(new CircleTransform()).into(bitmapTarget);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkData();
    }

    private void checkData() {
        List<Person> list = dao.getAllPersons();

        if (!list.isEmpty())
            personsAdded(new PersonsAddedEvent(list));
    }

    private DataBaseHelper getHelper(Context context) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        }
        return databaseHelper;
    }

    private class PersonMarker {

        private Person person;
        private Marker marker;

        public PersonMarker(Person person, Marker marker) {
            this.person = person;
            this.marker = marker;
        }

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        public Marker getMarker() {
            return marker;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }
    }

    /**
     * Picasso does not hold a strong reference to the Target object, thus it's being garbage collected and onBitmapLoaded is not called.
     * <p/>
     * The solution is quiet simple, just make a strong reference to the Target.
     */

    private class BitmapTarget implements Target {

        private Person person;

        public BitmapTarget(Person person) {
            this.person = person;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            Integer id = person.getId();
            Marker marker = mMap.addMarker(new MarkerOptions().position(Utils.getLatLng(person.getLocation())).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
            collection.put(id, new PersonMarker(person, marker));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 12));
            protectedFromGarbageCollectorTargets.remove(this);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            protectedFromGarbageCollectorTargets.remove(this);
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {

        }
    }

}
