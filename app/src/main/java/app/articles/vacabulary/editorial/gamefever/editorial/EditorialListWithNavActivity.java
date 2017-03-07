package app.articles.vacabulary.editorial.gamefever.editorial;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;

public class EditorialListWithNavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private List<EditorialGeneralInfo> editorialListArrayList = new ArrayList<>();
    private List<EditorialGeneralInfo> editorialListSortedArrayList = new ArrayList<>();

    private RecyclerView recyclerView;
    private EditorialGeneralInfoAdapter mAdapter;


    View addMoreButton;
    ProgressBar progressBar;
    private boolean isRefreshing = true;
    private boolean isSplashScreenVisible = true;
    public static boolean isShowingAd = false;
    public static String shareLink = "";
    public static int listLimit = 10;
    public String selectedSortWord = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initializeRemoteConfig();

        fetchEditorialGeneralList();

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }

        initializeSplashScreen();

    }

    public void initializeActivity() {

// the content to show and initialize navigation drawer

        setContentView(R.layout.activity_editorial_list_with_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle("Editorial");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


// the content of EditorialListActivity from here
        isSplashScreenVisible = false;


        recyclerView = (RecyclerView) findViewById(R.id.editoriallist_recyclerview);

        mAdapter = new EditorialGeneralInfoAdapter(editorialListSortedArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever

                        onRecyclerViewItemClick(position);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        addMoreButton = (View) findViewById(R.id.editoriallist_activity_add_button);
        addMoreButton.setVisibility(View.INVISIBLE);

        progressBar = (ProgressBar) findViewById(R.id.editoriallist_activity_progressbar);
        progressBar.setVisibility(View.VISIBLE);


        if (isShowingAd) {
            initializeAds();
        }


    }


    public void initializeSplashScreen() {

        setContentView(R.layout.splashlayout);

    }


    private void onRecyclerViewItemClick(int position) {
        EditorialGeneralInfo editorialgenralInfo = editorialListArrayList.get(position);
        Intent i = new Intent(this, EditorialFeedActivity.class);
        i.putExtra("editorialID", editorialgenralInfo.getEditorialID());
        i.putExtra("editorialDate", editorialgenralInfo.getEditorialDate());
        i.putExtra("editorialHeading", editorialgenralInfo.getEditorialHeading());
        i.putExtra("editorialSource", editorialgenralInfo.getEditorialSource());
        i.putExtra("editorialSubheading", editorialgenralInfo.getEditorialSubHeading());
        i.putExtra("editorialTag", editorialgenralInfo.getEditorialTag());

        startActivity(i);


    }

    public void fetchEditorialGeneralList() {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchEditorialList(EditorialListActivity.listLimit, "", this, true);

    }


    public void loadMoreClick(View view) {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchEditorialList(EditorialListActivity.listLimit, editorialListArrayList.get(editorialListArrayList.size() - 1).getEditorialID(), this, false);

        addMoreButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        sortEditorList(selectedSortWord);

    }

    public void loadMoreClick() {
        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchEditorialList(EditorialListActivity.listLimit, editorialListArrayList.get(editorialListArrayList.size() - 1).getEditorialID(), this, false);

        addMoreButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        sortEditorList(selectedSortWord);

    }

    public void onFetchEditorialGeneralInfo(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist, boolean isFirst) {

        if (isSplashScreenVisible) {
            initializeActivity();
        }

        int insertPosition = editorialListArrayList.size();
        if(!isFirst) {
            editorialGeneralInfoArraylist.remove(editorialGeneralInfoArraylist.size() - 1);
        }
        for (EditorialGeneralInfo editorialGeneralInfo : editorialGeneralInfoArraylist) {
            editorialListArrayList.add(insertPosition, editorialGeneralInfo);
        }
        mAdapter.notifyDataSetChanged();


        addMoreButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        isRefreshing = false;

        sortEditorList(selectedSortWord);

    }

    private void sortEditorList(String selectedSortWord) {



        if(!selectedSortWord.equals("")){


            editorialListSortedArrayList.clear();
            for(EditorialGeneralInfo editorialGeneralInfo : editorialListArrayList){

                if(editorialGeneralInfo.getEditorialSource().equals(selectedSortWord)){
                    editorialListSortedArrayList.add(editorialGeneralInfo);


                }
            }
        }else{
            editorialListSortedArrayList.clear();
            for(EditorialGeneralInfo editorialGeneralInfo : editorialListArrayList){
                    editorialListSortedArrayList.add(editorialGeneralInfo);
            }

        }
        mAdapter.notifyDataSetChanged();

        if(editorialListSortedArrayList.size()<5){
          // loadMoreClick();

        }

    }


    public void initializeAds() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8455191357100024~6634740792");
        AdView mAdView = (AdView) findViewById(R.id.editorialList_activity_adView);
        mAdView.setVisibility(View.VISIBLE);
        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

    }


    public void initializeRemoteConfig() {
        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();


        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);


        mFirebaseRemoteConfig.fetch()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(EditorialListActivity.this, "Fetch Succeeded", Toast.LENGTH_SHORT).show();

                            // Once the config is successfully fetched it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            // Toast.makeText(EditorialListActivity.this, "Fetch Failed", Toast.LENGTH_SHORT).show();
                        }
                        //displayWelcomeMessage();
                    }
                });


        EditorialListActivity.shareLink = mFirebaseRemoteConfig.getString("shareLink");

        try {
            FirebaseCrash.log("Value of isShowingad isWrong");
            EditorialListActivity.isShowingAd = Boolean.valueOf(mFirebaseRemoteConfig.getString("isShowingAd"));

        } catch (Exception e) {
            e.printStackTrace();
            EditorialListActivity.isShowingAd = false;
        }

        try {
            FirebaseCrash.log("Value of listLimit isWrong");
            EditorialListActivity.listLimit = Integer.valueOf(mFirebaseRemoteConfig.getString("listLimit"));

        } catch (Exception e) {
            e.printStackTrace();
            EditorialListActivity.listLimit = 20;
        }

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    public void onBackPressed() {
        if(!isSplashScreenVisible) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if ( drawer.isDrawerOpen(GravityCompat.START) ) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.editorial_list_with_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_all:
                onAllClick();
                break;

            case R.id.nav_the_hindu:
                onTheHinduClick();
                break;

            case R.id.nav_financial_express:
                onfinancialExpClick();
                break;

            case R.id.nav_economic_times:
                onEconomicTimesClick();
                break;


            case R.id.nav_vacabulary:
                onVacabularyClick();
                break;

            case R.id.nav_share:
                onShareClick();
                break;

            case R.id.nav_hindu_daily_note:
                onTheHinduNoteClick();
                break;

            case R.id.nav_about_us:
                onAboutClick();
                break;

            case R.id.nav_tutorial:
                onTutorialClick();
                break;


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void onVacabularyClick() {
        Intent i = new Intent(this, VacabularyActivity.class);
        startActivity(i);

    }

    private void onShareClick() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the app and Start reading");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, EditorialListActivity.shareLink);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void onSettingClick() {
    }

    private void onAboutClick() {

        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);

    }

    private void onRefreashClick() {

        editorialListArrayList.clear();
        mAdapter.notifyDataSetChanged();
        addMoreButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        if (!isRefreshing) {
            fetchEditorialGeneralList();
            isRefreshing = true;
        }

    }


    private void onEconomicTimesClick() {
        selectedSortWord="The Economic Times";
        sortEditorList(selectedSortWord);


    }

    private void onfinancialExpClick() {
        selectedSortWord="The Financial Express";
        sortEditorList(selectedSortWord);

    }

    private void onTheHinduClick() {
        selectedSortWord="The Hindu";
        sortEditorList(selectedSortWord);
    }

    private void onAllClick() {
        selectedSortWord="";
        sortEditorList(selectedSortWord);
    }

    private void onTheHinduNoteClick() {
    }

    private void onTutorialClick() {
    }


}