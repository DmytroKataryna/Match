package task.test.kataryna.dmytro.match.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.squareup.otto.Subscribe;

import org.testpackage.test_sdk.android.testlib.API;
import org.testpackage.test_sdk.android.testlib.interfaces.PersonsExtendedCallback;

import java.util.ArrayList;
import java.util.List;

import task.test.kataryna.dmytro.match.Constants;
import task.test.kataryna.dmytro.match.R;
import task.test.kataryna.dmytro.match.adapters.EndlessRecyclerOnScrollListener;
import task.test.kataryna.dmytro.match.adapters.PersonsAdapter;
import task.test.kataryna.dmytro.match.db.DataBaseHelper;
import task.test.kataryna.dmytro.match.db.PersonDAO;
import task.test.kataryna.dmytro.match.helpers.PreferencesUtils;
import task.test.kataryna.dmytro.match.helpers.Utils;
import task.test.kataryna.dmytro.match.model.Person;
import task.test.kataryna.dmytro.match.otto.BusProvider;
import task.test.kataryna.dmytro.match.otto.PersonChangedEvent;
import task.test.kataryna.dmytro.match.otto.PersonRemovedEvent;
import task.test.kataryna.dmytro.match.otto.PersonsAddedEvent;

/**
 * Created by dmytroKataryna on 20.01.16.
 */
public class PersonsFragment extends Fragment {

    private ProgressBar progressBar;
    private PersonsAdapter mAdapter;
    private SuperRecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;

    private DataBaseHelper databaseHelper;
    private PersonDAO dao;

    public static PersonsFragment createInstance() {
        return new PersonsFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (dao == null)
            dao = new PersonDAO(getHelper(getActivity().getApplicationContext()).getSimpleDataDao());

        checkData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        mAdapter = new PersonsAdapter(new ArrayList<Person>(), getActivity().getApplicationContext());
        mRecyclerView = (SuperRecyclerView) view.findViewById(R.id.superListView);
        linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setOnScrollListener(getScrollListener(linearLayoutManager));
        mRecyclerView.setAdapter(mAdapter);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Subscribe
    public void personUpdated(PersonChangedEvent event) {
        int position = mAdapter.getPersonPosition(event.person);
        if (position != Constants.NOT_FOUND) {
            mAdapter.updatePersonAt(position, event.person);
            dao.savePerson(event.person);
        }
    }

    @Subscribe
    public void personRemoved(PersonRemovedEvent event) {
        dao.deletePerson(event.person);
    }

    private void loadData(final int page) {
        API.INSTANCE.getPersons(page, new PersonsExtendedCallback() {
            @Override
            public void onResult(String personsJSON) {
                List<Person> persons = Utils.getPersonsFromJSON(personsJSON);
                BusProvider.getInstance().post(new PersonsAddedEvent(persons));
                dao.savePersons(persons);
                addData(persons);
            }

            @Override
            public void onFail(String reason) {
                hideProgressBars();
            }
        });
    }

    private void checkData() {
        List<Person> list = dao.getAllPersons();
        if (list.isEmpty())
            loadData(Constants.ZERO);
        else
            addData(list);
    }

    private void addData(List<Person> persons) {
        mAdapter.addData(persons);
        hideProgressBars();
    }

    private void hideProgressBars() {
        progressBar.setVisibility(View.GONE);
        mRecyclerView.hideMoreProgress();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    private DataBaseHelper getHelper(Context context) {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(context, DataBaseHelper.class);
        }
        return databaseHelper;
    }


    private EndlessRecyclerOnScrollListener getScrollListener(LinearLayoutManager linearLayoutManager) {
        final PreferencesUtils utils = PreferencesUtils.get(getActivity().getApplicationContext());
        return new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (utils.getLoadedPage() > current_page) return;
                mRecyclerView.showMoreProgress();
                utils.setLoadedPage(current_page);
                loadData(current_page);
            }
        };
    }
}
