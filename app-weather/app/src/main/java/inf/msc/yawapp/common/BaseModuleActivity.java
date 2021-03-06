/*
 * This source code is part of the research paper
 * "Applying Model-View-Presenter Architecture to the Android Framework with App Prototype"
 *
 * Software Architecture and Management,
 * Herman-Hollerith-Zentrum, Reutlingen University
 *
 * Authors:  Dennis G. Geisse,
 *           Iven John,
 *           Daniel Joos,
 *           Sebastian Kotstein,
 *           Michael Wurster
 *
 * Year:     2015
 */

package inf.msc.yawapp.common;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import inf.msc.yawapp.MainApplication;
import inf.msc.yawapp.R;
import inf.msc.yawapp.navigation.NavDrawerItem;
import inf.msc.yawapp.navigation.NavDrawerListViewAdapter;
import inf.msc.yawapp.navigation.NavigationPresenter;

public abstract class BaseModuleActivity extends ActionBarActivity {

    @Inject
    NavigationPresenter navigationPresenter;

    public interface GraphCreationStrategy {
        public ObjectGraph getModuleGraph(BaseModuleActivity activity);
    }

    private class DefaultGraphCreationStrategy implements GraphCreationStrategy {

        @Override
        public ObjectGraph getModuleGraph(BaseModuleActivity activity) {
            return ((MainApplication) getApplication()).createSubGraph(getModules().toArray());
        }
    }

    private ObjectGraph moduleGraph;
    private Toolbar actionBarToolbar;
    private GraphCreationStrategy graphCreationStrategy = new DefaultGraphCreationStrategy();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (graphCreationStrategy != null) {
            setModuleGraph(graphCreationStrategy.getModuleGraph(this));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        moduleGraph = null;
    }

    public void setModuleGraph(ObjectGraph moduleGraph) {
        this.moduleGraph = moduleGraph;
        this.moduleGraph.inject(this);
    }

    public void setGraphCreationStrategy(GraphCreationStrategy graphCreationStrategy) {
        this.graphCreationStrategy = graphCreationStrategy;
    }

    protected Toolbar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return actionBarToolbar;
    }

    private void setupNavDrawer() {
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout == null) {
            return;
        }

        final Toolbar toolbar = getActionBarToolbar();
        toolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
        createNavItems();
    }

    private void createNavItems() {
        final ListView navList = (ListView) findViewById(R.id.nav_drawer);
        if (navList == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        ArrayAdapter<NavDrawerItem> navListAdapter = (ArrayAdapter<NavDrawerItem>) navList.getAdapter();
        if (navListAdapter == null) {
            navListAdapter = new NavDrawerListViewAdapter(this);
            navList.setAdapter(navListAdapter);
        }
        navListAdapter.clear();
        for (NavDrawerItem item : NavDrawerItem.values()) {
            navListAdapter.add(item);
        }
        if (navList.getOnItemClickListener() == null) {
            final Context context = this;
            navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final NavDrawerItem item = (NavDrawerItem) parent.getItemAtPosition(position);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (navigationPresenter.navigate(item, context)) {
                                finish();
                            }
                        }
                    });
                }
            });
        }
    }

    protected abstract List<Object> getModules();
}
