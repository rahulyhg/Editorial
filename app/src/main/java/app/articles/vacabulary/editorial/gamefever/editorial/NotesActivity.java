package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.ArrayList;

import utils.AdsSubscriptionManager;
import utils.AuthenticationManager;
import utils.ShortNotesAdapter;
import utils.ShortNotesManager;


public class NotesActivity extends AppCompatActivity {
    private ArrayList<Object> shortNotesArrayList = new ArrayList<>();

    private RecyclerView recyclerView;
    private ShortNotesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.notes_list_recyclerView);
        mAdapter = new ShortNotesAdapter(shortNotesArrayList, this);
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
                        onRecyclerViewItemLongClick(position);
                    }
                })
        );


        DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
        dbHelperFirebase.fetchShortNotesList(AuthenticationManager.getUserUID(this), 20, new DBHelperFirebase.OnShortNoteListListener() {
            @Override
            public void onShortNoteList(ArrayList<ShortNotesManager> shortNotesManagerArrayList, boolean isSuccessful) {

                if (isSuccessful) {
                    //initialize list
                    for (Object shortNotes :shortNotesManagerArrayList){
                        shortNotesArrayList.add(shortNotes);
                    }

                    addNativeExpressAds();
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onShortNoteUpload(boolean isSuccessful) {

            }
        });

    }

    private void onRecyclerViewItemLongClick(final int position) {
        if (position % 8 == 0) {
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this Notes")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteNotes(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();

    }

    private void deleteNotes(final int position) {
        final ProgressDialog pd = ProgressDialog.show(this,"Deleting","Please wait");

        DBHelperFirebase dbHelperFirebase =new DBHelperFirebase();
        dbHelperFirebase.deleteShortNotes(AuthenticationManager.getUserUID(this),((ShortNotesManager)shortNotesArrayList.get(position)).getNoteArticleID(), new DBHelperFirebase.OnShortNoteListListener() {
            @Override
            public void onShortNoteList(ArrayList<ShortNotesManager> shortNotesManagerArrayList, boolean isSuccessful) {

            }

            @Override
            public void onShortNoteUpload(boolean isSuccessful) {
                shortNotesArrayList.remove(position);
                mAdapter.notifyDataSetChanged();
                pd.dismiss();

                Toast.makeText(NotesActivity.this, "Notes Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void onRecyclerViewItemClick(int position) {
        if (position % 8 == 0) {
            return;
        }



        Intent intent =new Intent(NotesActivity.this ,NotesFeedActivity.class);
        intent.putExtra("shortNotes",((ShortNotesManager)shortNotesArrayList.get(position)));


        startActivity(intent);

    }

    private void initializeRecyclerView() {

    }

    private void addNativeExpressAds() {

        boolean checkShowAds = AdsSubscriptionManager.checkShowAds(this);

        //main function where ads is merged in editorial list as an object

        for (int i = 0; i < (shortNotesArrayList.size()); i += 8) {
            if (shortNotesArrayList.get(i).getClass() != NativeExpressAdView.class) {
                final NativeExpressAdView adView = new NativeExpressAdView(this);

                adView.setAdUnitId("ca-app-pub-8455191357100024/8254824112");
                adView.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

                adView.setAdSize(new AdSize(320, 132));
                if (checkShowAds) {
                    adView.loadAd(new AdRequest.Builder().build());
                }
                shortNotesArrayList.add(i, adView);

            }
        }






    }


}
