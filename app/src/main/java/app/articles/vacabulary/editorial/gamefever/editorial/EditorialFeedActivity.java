package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.InviteEvent;
import com.crashlytics.android.answers.RatingEvent;
import com.crashlytics.android.answers.ShareEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.messaging.FirebaseMessaging;


import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;

import utils.AdsSubscriptionManager;
import utils.AppRater;
import utils.AuthenticationManager;
import utils.Like;
import utils.ShortNotesManager;
import utils.UrlShortner;

public class EditorialFeedActivity extends AppCompatActivity implements
        TextToSpeech.OnInitListener {


    EditorialFullInfo currentEditorialFullInfo = new EditorialFullInfo(new EditorialGeneralInfo(), new EditorialExtraInfo());

    private TextToSpeech tts;

    TextView translateText;
    private BottomSheetBehavior mBottomSheetBehavior;
    public String selectedWord = "null";

    CommentsListViewAdapter mCommentAdapter;
    ArrayList<Comment> commentList = new ArrayList<>();

    boolean isPushNotification = false;
    boolean isDynamicLink = false;
    private boolean notesMode = false;
    private InterstitialAd mSubscriptionInterstitialAd;


    private ShortNotesManager shortNotesManager = new ShortNotesManager(new HashMap<String, String>());
    private boolean saveShortNotes;

    boolean muteVoice=false;
    /*
    private ActionMode mActionMode;
    private android.view.ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add(2, 2, 2, "Add this point").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.add(1, 1, 1, "Done").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.add(0, 0, 0, "Cancel").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            saveShortNotes = false;

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            String selectedString = "";
            if (item.getItemId() == 2) {
                TextView definitionView = (TextView) findViewById(R.id.editorialfeed_notesText_textview);
                if (definitionView.hasSelection()) {
                    selectedString = currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText().substring(definitionView.getSelectionStart(), definitionView.getSelectionEnd());

                }


                Toast.makeText(EditorialFeedActivity.this, "Text selected is " + selectedString, Toast.LENGTH_SHORT).show();

                shortNotesManager.getShortNotePointList().put(definitionView.getSelectionStart() + "-" + definitionView.getSelectionEnd(), selectedString);

                definitionView.clearFocus();
                saveShortNotes = true;

            } else if (item.getItemId() == 1) {

                if (mActionMode != null) {
                    mActionMode.finish();
                }
            } else if (item.getItemId() == 0) {
                saveShortNotes = false;
                if (mActionMode != null) {
                    mActionMode.finish();
                }
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            //storyTextView.setTextIsSelectable(false);

            if (saveShortNotes) {
                final ProgressDialog pd = ProgressDialog.show(EditorialFeedActivity.this, "Saving Notes", "Please wait");
                new DBHelperFirebase().uploadShortNote(AuthenticationManager.getUserUID(EditorialFeedActivity.this), shortNotesManager, new DBHelperFirebase.OnShortNoteListListener() {
                    @Override
                    public void onShortNoteList(ArrayList<ShortNotesManager> shortNotesManagerArrayList, boolean isSuccessful) {

                    }

                    @Override
                    public void onShortNoteUpload(boolean isSuccessful) {

                        try {
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }

                        } catch (Exception e) {

                        }

                        if (isSuccessful) {
                            Toast.makeText(EditorialFeedActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }

        }
    };
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.FeedActivityThemeDark);
        }
        setContentView(R.layout.activity_editorial_feed);

        try {
            Toolbar toolbar = (Toolbar) findViewById(R.id.editorialfeed_activity_toolbar);
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setSubtitle("Feeds");
            // getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        } catch (Exception e) {

        }

        tts = new TextToSpeech(this, this);
        Intent i = getIntent();
        intialiseViewAndFetch(i);

        translateText = (TextView) findViewById(R.id.editorial_feed_cardview_textview);


        View bottomSheet = findViewById(R.id.editorial_activity_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setHideable(false);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {


                   /* openBottomSheet(true);

                    Dictionary dictionary = new Dictionary(selectedWord);
                    dictionary.fetchWordMeaning(selectedWord, EditorialFeedActivity.this);

                    TextView tv = (TextView) findViewById(R.id.editorial_bottomsheet_heading_textview);
                    tv.setText(translateText.getText());

*/

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        if (AdsSubscriptionManager.checkShowAds(this)) {
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-8455191357100024~6634740792");

            initializeTopNativeAds();
            //initializeAds();
            initializeNativeAds();
            initializeBottomSheetAd();
            initializeSubscriptionAds();
            CardView cardView = (CardView) findViewById(R.id.editorialfeed_removeAd_cardView);
            cardView.setVisibility(View.VISIBLE);
        }


        //setThemeinactivity();

        //checkRateUsOption();


    }


    private void checkRateUsOption() {
        SharedPreferences prefs = getSharedPreferences("RateUsNum", MODE_PRIVATE);
        int ratenum = prefs.getInt("ratenum", 0);

        if (ratenum < 5) {
            ratenum++;

            SharedPreferences.Editor edit = prefs.edit();
            edit.putInt("ratenum", ratenum);
            edit.apply();


        } else {

            boolean rateeus = prefs.getBoolean("rateus", false);
            if (rateeus) {


                return;
            } else {


                //show rate Pop Up


                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditorialFeedActivity.this);


                // Setting Dialog Title
                alertDialog.setTitle("Rate us");

                // Setting Dialog Message
                alertDialog.setMessage("Do you like this app");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.ic_menu_share);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Toast.makeText(EditorialFeedActivity.this, "ThankYou", Toast.LENGTH_SHORT).show();

                        // Write your code here to invoke YES event

                        dialog.cancel();
                    }
                });


                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Not much", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event

                        sendSuggestionEmail();

                        dialog.cancel();
                    }
                });

                // Showing Alert Message
                alertDialog.show();

                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("rateus", true);
                edit.commit();

            }
        }


    }

    private void sendSuggestionEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"acraftystudio@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion for Editorial App");
        intent.putExtra(Intent.EXTRA_TEXT, "Your suggestion here \n");

        intent.setType("message/rfc822");

        startActivity(Intent.createChooser(intent, "Select Email Sending App"));
    }

    private void intialiseViewAndFetch(Intent i) {
        EditorialGeneralInfo editorialGeneralInfo = new EditorialGeneralInfo();
        try {
            editorialGeneralInfo = (EditorialGeneralInfo) i.getSerializableExtra("editorial");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (editorialGeneralInfo == null) {
            editorialGeneralInfo = new EditorialGeneralInfo();
        }
        editorialGeneralInfo.setEditorialID(i.getExtras().getString("editorialID"));
        editorialGeneralInfo.setEditorialDate(i.getExtras().getString("editorialDate"));
        editorialGeneralInfo.setEditorialHeading(i.getExtras().getString("editorialHeading"));
        editorialGeneralInfo.setEditorialSource(i.getExtras().getString("editorialSource"));
        editorialGeneralInfo.setEditorialSubHeading(i.getExtras().getString("editorialSubheading"));
        editorialGeneralInfo.setEditorialTag(i.getExtras().getString("editorialTag"));


        boolean isBookMarked = i.getBooleanExtra("isBookMarked", false);
        isPushNotification = i.getBooleanExtra("isPushNotification", false);
        isDynamicLink = i.getBooleanExtra("isDynamicLink", false);


        if (isBookMarked) {
            DatabaseHandlerBookMark databasehandlerBookmark = new DatabaseHandlerBookMark(this);
            EditorialExtraInfo editorialExtraInfo = databasehandlerBookmark.getBookMarkEditorial(editorialGeneralInfo.getEditorialID());
            currentEditorialFullInfo.setEditorialExtraInfo(editorialExtraInfo);
            init(editorialExtraInfo.getEditorialText());
            findViewById(R.id.editorialfeed_activity_progressbar).setVisibility(View.GONE);


        } else {
            DBHelperFirebase dbHelper = new DBHelperFirebase();
            dbHelper.getEditorialFullInfoByID(editorialGeneralInfo, this);
        }


        if (isPushNotification) {

            new DBHelperFirebase().getEditorialGeneralInfoByID(editorialGeneralInfo.getEditorialID(), new DBHelperFirebase.OnEditorialListener() {
                @Override
                public void onEditorialGeneralInfo(EditorialGeneralInfo editorialGeneralInfo, boolean isSuccessful) {

                    if (isSuccessful) {

                        currentEditorialFullInfo.setEditorialGeneralInfo(editorialGeneralInfo);

                        TextView tv = (TextView) findViewById(R.id.editorial_heading_textview);
                        tv.setText(editorialGeneralInfo.getEditorialHeading());
                        tv = (TextView) findViewById(R.id.editorial_source_textview);
                        tv.setText(editorialGeneralInfo.getEditorialSource());
                        tv = (TextView) findViewById(R.id.editorial_date_textview);
                        tv.setText(editorialGeneralInfo.getEditorialDate());
                        tv = (TextView) findViewById(R.id.editorial_tag_textview);
                        tv.setText(editorialGeneralInfo.getEditorialTag());

                        try {
                            Answers.getInstance().logContentView(new ContentViewEvent()
                                    .putContentId(editorialGeneralInfo.getEditorialID())
                                    .putContentName(editorialGeneralInfo.getEditorialHeading())
                                    .putContentType("By push notification"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(EditorialFeedActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onEditorialExtraInfo(EditorialExtraInfo editorialExtraInfo, boolean isSuccessful) {

                }
            });

            try {
                Answers.getInstance().logInvite(new InviteEvent()
                        .putMethod("push notification")
                        .putCustomAttribute("editorialID", editorialGeneralInfo.getEditorialID())
                );
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


        //Fetching comments
        new DBHelperFirebase().fetchComment(editorialGeneralInfo.getEditorialID(), 50, new DBHelperFirebase.OnCommentListener() {
            @Override
            public void onCommentInserted(Comment comment) {

            }

            @Override
            public void onCommentFetched(ArrayList<Comment> commentArrayList) {
                EditorialFeedActivity.this.commentList = commentArrayList;
                initializeCommentList();

            }
        });
        //end fetching comment


        currentEditorialFullInfo.setEditorialGeneralInfo(editorialGeneralInfo);

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();

        }

        TextView tv = (TextView) findViewById(R.id.editorial_heading_textview);
        tv.setText(editorialGeneralInfo.getEditorialHeading());
        tv = (TextView) findViewById(R.id.editorial_source_textview);
        tv.setText(editorialGeneralInfo.getEditorialSource());
        tv = (TextView) findViewById(R.id.editorial_date_textview);
        tv.setText(editorialGeneralInfo.getEditorialDate());
        tv = (TextView) findViewById(R.id.editorial_tag_textview);
        tv.setText(editorialGeneralInfo.getEditorialTag());

        try {
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentId(editorialGeneralInfo.getEditorialID())
                    .putContentName(editorialGeneralInfo.getEditorialHeading()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void init(String textToShow) {

        try {

            String definition = textToShow;
            TextView definitionView = (TextView) findViewById(R.id.editorial_text_textview);
            definitionView.setTextIsSelectable(false);
            definitionView.setMovementMethod(LinkMovementMethod.getInstance());
            definitionView.setText(definition, TextView.BufferType.SPANNABLE);

            Spannable spans = (Spannable) definitionView.getText();
            BreakIterator iterator = BreakIterator.getWordInstance(Locale.US);
            iterator.setText(definition);
            int start = iterator.first();
            for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator
                    .next()) {
                String possibleWord = definition.substring(start, end);
                if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
                    ClickableSpan clickSpan = getClickableSpan(possibleWord);
                    spans.setSpan(clickSpan, start, end,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }

        } catch (Exception e) {
            TextView definitionView = (TextView) findViewById(R.id.editorial_text_textview);
            definitionView.setText(textToShow);

        }

    }


    private ClickableSpan getClickableSpan(final String word) {
        return new ClickableSpan() {
            final String mWord;

            {
                mWord = word;
            }

            @Override
            public void onClick(View widget) {
                Log.d("tapped on:", mWord);


                onWordTap(mWord);

            }


            public void updateDrawState(TextPaint ds) {
                //ds.setUnderlineText(false);

               /* if (mWord.contentEquals(selectedWord)){
                    ds.setColor(ds.linkColor);
                }*/
                //ds.setColor(ds.linkColor);
                //super.updateDrawState(ds);
            }
        };
    }

    private void onWordTap(String mWord) {

        speakOutWord(mWord);
        Translation translation = new Translation(mWord);
        translation.fetchTranslation(this);

        translateText.setText(mWord);
        selectedWord = mWord;


        fetchWordMeaning();

    }

    private void fetchWordMeaning() {
        openBottomSheet(true);

        Dictionary dictionary = new Dictionary(selectedWord);
        dictionary.fetchWordMeaning(selectedWord, EditorialFeedActivity.this);

    }

    public void updateTranslateText(Translation translation) {
        if (translation.word.equalsIgnoreCase(translateText.getText().toString().trim())) {
            translateText.setText(translation.word + " = " + translation.wordTranslation);
        }
    }

    public void updateDictionaryText(final Dictionary dictionary) {
        if (selectedWord.equalsIgnoreCase(dictionary.getWord())) {
            TextView tv;
            tv = (TextView) findViewById(R.id.editorial_bottomsheet_meaning_textview);
            tv.setText(dictionary.getWordMeaning());
            tv = (TextView) findViewById(R.id.editorial_bottomsheet_partspeech_textview);
            tv.setText(dictionary.getWordPartOfSpeech());
            tv = (TextView) findViewById(R.id.editorial_bottomsheet_synonyms_textview);
            String synonymstring = "";
            for (int i = 0; i < dictionary.getWordsynonym().length; i++) {
                synonymstring = synonymstring + dictionary.getWordsynonym()[i] + " , ";
            }
            tv.setText(synonymstring);

            Button bt = (Button) findViewById(R.id.editorial_bottomSheet_add_button);
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHandler databaseHandler = new DatabaseHandler(EditorialFeedActivity.this);
                    dictionary.setWord(translateText.getText().toString());
                    databaseHandler.addToDictionary(dictionary);
                    Toast.makeText(EditorialFeedActivity.this, "Word Added To Dictionary", Toast.LENGTH_SHORT).show();


                }
            });
        }
    }

    public void onDictionaryClick(View v) {
        //Intent i =new Intent(this ,);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        /*openBottomSheet(true);

        Dictionary dictionary = new Dictionary(selectedWord);
        dictionary.fetchWordMeaning(selectedWord, EditorialFeedActivity.this);
*/

        /*openBottomSheet(true);
        Dictionary dictionary = new Dictionary(selectedWord);
        dictionary.fetchWordMeaning(selectedWord, EditorialFeedActivity.this);
*/

    }

    private void openBottomSheet(boolean b) {

        TextView tv;
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_meaning_textview);
        tv.setText("Loading...");
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_partspeech_textview);
        tv.setText("Loading...");
        tv = (TextView) findViewById(R.id.editorial_bottomsheet_synonyms_textview);

        tv.setText("Loading...");
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        super.onDestroy();
    }

    @Override
    public  void onStop(){
        super.onStop();
        if (tts != null) {
            speakOutWord(".");
            tts.stop();
            tts.shutdown();
        }
    }

    @Override
    public void onBackPressed() {


        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {

            if (saveShortNotes) {
                onNotesSaveClick();
            }

            if (isPushNotification || isDynamicLink) {
                Intent intent = new Intent(EditorialFeedActivity.this, EditorialListWithNavActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {

                super.onBackPressed();

            }


        }

    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);
            tts.setPitch(0.8f);


            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // btnSpeak.setEnabled(true);
                speakOutWord("");
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }

    }

    private void speakOutWord(String speakWord) {

        try {
            if (!muteVoice) {
                tts.speak(speakWord, TextToSpeech.QUEUE_FLUSH, null);
            }
        } catch (Exception e) {

        }
    }

    public void onGetEditorialFullInfo(EditorialFullInfo editorialFullInfo) {

        try {

            init(editorialFullInfo.getEditorialExtraInfo().getEditorialText());
            currentEditorialFullInfo.setEditorialExtraInfo(editorialFullInfo.getEditorialExtraInfo());
            findViewById(R.id.editorialfeed_activity_progressbar).setVisibility(View.GONE);
            initializeSourceLink();
            initializeCommentList();

            intializeShareAndLike();

            //calling rate now dialog

            AppRater.app_launched(EditorialFeedActivity.this);

        } catch (NullPointerException nl) {
            editorialFullInfo.getEditorialExtraInfo().setEditorialText("No editorial found");
            init(editorialFullInfo.getEditorialExtraInfo().getEditorialText());
            currentEditorialFullInfo = editorialFullInfo;
            findViewById(R.id.editorialfeed_activity_progressbar).setVisibility(View.GONE);
            initializeCommentList();
            nl.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, "Something Went wrong", Toast.LENGTH_SHORT).show();
        }


    }

    private void intializeShareAndLike() {
        TextView likeTextView = (TextView) findViewById(R.id.editorialfeed_like_textView);
        likeTextView.setText(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialLike() + " Likes");

        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.editorialfeed_like_linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLikeClick(v);
                linearLayout.setEnabled(false);
            }
        });
        final Button button = (Button) findViewById(R.id.editorialFeed_share_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareClick();

            }
        });


    }

    private void initializeSourceLink() {
        TextView textView = (TextView) findViewById(R.id.editorialfeed_sourceLink_textView);
        textView.setText("Read Editorial from - " + currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialSourceLink());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (notesMode) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_notes_mode_action, menu);
        } else {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_editorial_list_actions, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.action_toggle:

                switchTheme();
                return true;
            case R.id.action_share:
                // help action
                onShareClick();
                return true;
            case R.id.action_bookmark:
                // refresh
                onBookmark();
                return true;

            case R.id.action_tts_reader:
                onTtsReaderClick(item);
                return true;

            case R.id.action_notes:
                onTakeNotesClick();
                return true;

            case R.id.notes_mode_action_add_point:
                onAddPointClick();
                return true;


            case R.id.notes_mode_action_save:
                onNotesSaveClick();
                return true;

            case R.id.notes_mode_action_cancel:
                onNotesCancelClick();
                return true;

            case R.id.action_open_notes:
                onOpenNotesActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onTtsReaderClick(MenuItem item) {

        if (tts.isSpeaking()) {
            speakOutWord("");
            item.setTitle("Read Editorial (Voice)");

        } else {
            item.setTitle("Stop Reader");
            speakOutWord(currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText());
        }

    }

    private void onOpenNotesActivity() {
        Intent intent =new Intent(EditorialFeedActivity.this ,NotesActivity.class);
        startActivity(intent);
    }

    private void onNotesCancelClick() {
        Toast.makeText(this, "Cancel notes", Toast.LENGTH_SHORT).show();
        saveShortNotes = false;
        onTakeNotesClick();
    }

    private void onNotesSaveClick() {
        //Toast.makeText(this, "save notes", Toast.LENGTH_SHORT).show();

        String userUID =AuthenticationManager.getUserUID(EditorialFeedActivity.this);
        if (userUID ==null){
            return ;
        }

        if (saveShortNotes) {
            saveShortNotes=false;


            final ProgressDialog pd = ProgressDialog.show(EditorialFeedActivity.this, "Saving Notes", "Please wait");
            new DBHelperFirebase().uploadShortNote(userUID, shortNotesManager, new DBHelperFirebase.OnShortNoteListListener() {
                @Override
                public void onShortNoteList(ArrayList<ShortNotesManager> shortNotesManagerArrayList, boolean isSuccessful) {

                }

                @Override
                public void onShortNoteUpload(boolean isSuccessful) {

                    try {
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (isSuccessful) {
                        Toast.makeText(EditorialFeedActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

        onTakeNotesClick();

    }

    private void onAddPointClick() {
        Toast.makeText(this, "add point notes", Toast.LENGTH_SHORT).show();

        TextView definitionView = (TextView) findViewById(R.id.editorialfeed_notesText_textview);
        String selectedString = "";
        if (definitionView.hasSelection()) {
            selectedString = currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText().substring(definitionView.getSelectionStart(), definitionView.getSelectionEnd());

        }


        Toast.makeText(EditorialFeedActivity.this, "Text selected is " + selectedString, Toast.LENGTH_SHORT).show();

        shortNotesManager.getShortNotePointList().put(definitionView.getSelectionStart() + "-" + definitionView.getSelectionEnd(), selectedString);

        definitionView.clearFocus();
        saveShortNotes = true;

    }

    private void onTakeNotesClick() {

        if (notesMode) {
            TextView notesTextview = (TextView) findViewById(R.id.editorialfeed_notesText_textview);
            notesTextview.setText(null);

            init(currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText());
            notesMode = false;
            //item.setTitle("Take Notes");
            try {
                getSupportActionBar().setSubtitle("Feeds");
            } catch (Exception e) {
                e.printStackTrace();
            }
            ActivityCompat.invalidateOptionsMenu(this);

        } else {
            TextView editorialText = (TextView) findViewById(R.id.editorial_text_textview);
            editorialText.setText(null);

            final TextView definitionView = (TextView) findViewById(R.id.editorialfeed_notesText_textview);
            definitionView.setText(currentEditorialFullInfo.getEditorialExtraInfo().getEditorialText());
            definitionView.setTextIsSelectable(true);
            notesMode = true;
           // item.setTitle("Exit notes mode");
            Toast.makeText(this, "Entered notes mode", Toast.LENGTH_SHORT).show();

            try {
                getSupportActionBar().setSubtitle("Notes");
            } catch (Exception e) {
                e.printStackTrace();
            }

            shortNotesManager.setNoteArticleID(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialID());
            shortNotesManager.setShortNoteHeading(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading());
            shortNotesManager.setNoteArticleSource(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialSource());
            shortNotesManager.setShortNoteEditTimeInMillis(currentEditorialFullInfo.getEditorialGeneralInfo().getTimeInMillis());

            ActivityCompat.invalidateOptionsMenu(this);


        }

    }

    private void switchTheme() {
        if (AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        recreate();
    }

    private void onBookmark() {
        DatabaseHandlerBookMark databaseHandlerBookMark = new DatabaseHandlerBookMark(this);
        databaseHandlerBookMark.addToBookMark(currentEditorialFullInfo.getEditorialGeneralInfo(), currentEditorialFullInfo.getEditorialExtraInfo());
        Toast.makeText(this, "Editorial Bookmarked", Toast.LENGTH_SHORT).show();

        try {
            Answers.getInstance().logCustom(new CustomEvent("Bookmark").putCustomAttribute("Editorial title", currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading()));

        } catch (Exception e) {

        }

    }


    private void onShareClick() {

        String appCode = getString(R.string.app_code);
        String appName = getString(R.string.app_name);
        String packageName = this.getPackageName();
        String imageUrl = "https://firebasestorage.googleapis.com/v0/b/editorial-8cbf6.appspot.com/o/editorial%20logo%20png.png?alt=media&token=632a8d65-b5cb-4f68-94a0-e65b20890405";


        String utmSource = getString(R.string.utm_source);
        String utmCampaign = getString(R.string.utm_campaign);
        String utmMedium = getString(R.string.utm_medium);

        final ProgressDialog pd = new ProgressDialog(EditorialFeedActivity.this);
        pd.setMessage("Creating link ...");
        pd.show();

        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://goo.gl/Ae4Mhw?editorialID=" + currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialID()))
                .setDynamicLinkDomain(appCode)
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(packageName)
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading())
                                .setDescription(appName)
                                .setImageUrl(Uri.parse("https://firebasestorage.googleapis.com/v0/b/editorial-8cbf6.appspot.com/o/logo.png?alt=media&token=e2c451aa-e7ef-4f57-8e77-2c783dcc290e"))
                                .build())
                .setGoogleAnalyticsParameters(
                        new DynamicLink.GoogleAnalyticsParameters.Builder()
                                .setSource(utmSource)
                                .setMedium(utmMedium)
                                .setCampaign(utmCampaign)
                                .build())
                .buildShortDynamicLink()
                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            if (pd.isShowing()) {
                                pd.dismiss();
                            }
                            openShareDialog(shortLink);

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditorialFeedActivity.this, "Connection Failed! Try again later", Toast.LENGTH_SHORT).show();
                       try {
                           pd.dismiss();
                       }catch (Exception exception){
                           e.printStackTrace();
                       }
                    }
                });

    }

    private void openShareDialog(Uri shortLink) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        //sharingIntent.putExtra(Intent.EXTRA_STREAM, newsMetaInfo.getNewsImageLocalPath());

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the app and Start reading");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shortLink
                + "\n" + currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading()
                + "\n\nRead full editorial at Daily editorial app");
        startActivity(Intent.createChooser(sharingIntent, "Share Editorial via"));

    }


    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void initializeAds() {
        AdView mAdView = (AdView) findViewById(R.id.editorialfeed_activity_adView);

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "feed top banner"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void initializeNativeAds() {
        NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.editorialfeed_native_adView);
        adView.setVisibility(View.VISIBLE);

        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);

        adView.setAdListener(new AdListener() {

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "Feed native bottom").putCustomAttribute("errorType", i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



        });


    }


    public void initializeBottomSheetAd() {
        NativeExpressAdView mAdView = (NativeExpressAdView) findViewById(R.id.editorialFeed_bottomSheet_native_adView);
        mAdView.setVisibility(View.VISIBLE);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "Bottom sheet").putCustomAttribute("errorType", i));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeTopNativeAds() {
        NativeExpressAdView adView = (NativeExpressAdView) findViewById(R.id.editorialFeed_top_nativeAds);
        adView.setVisibility(View.VISIBLE);

        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "top Native small").putCustomAttribute("errorType", i));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void readFullArticle(View view) {
       if (muteVoice){
          muteVoice =false ;
           Toast.makeText(this, "Voice enabled", Toast.LENGTH_SHORT).show();
       }else{
           Toast.makeText(this, "Voice disabled", Toast.LENGTH_SHORT).show();
           muteVoice=true;
       }
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();


    }

    public void initializeCommentList() {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.editorialFeed_commentsystem_linearLayout);
        linearLayout.setVisibility(View.VISIBLE);

        ListView commentListView = (ListView) findViewById(R.id.editorialFeed_comments_listView);


        TextView textView = (TextView) findViewById(R.id.editorialfeed_comment_textView);
        textView.setText(commentList.size() + " Discussion");

        if (commentList.size() == 0) {
            //commentList = new ArrayList<>();

            if (currentEditorialFullInfo.getEditorialExtraInfo().getComments() == null) {
                Comment comment = new Comment();
                comment.setCommentText("No Comment");
                comment.seteMailID("");
                commentList.add(comment);


            } else {
                for (Comment comment : currentEditorialFullInfo.getEditorialExtraInfo().getComments().values()) {
                    commentList.add(comment);
                }
            }
        }

        mCommentAdapter = new CommentsListViewAdapter(this, commentList);
        commentListView.setAdapter(mCommentAdapter);



        //resizeCommentListView();


    }

    private void resizeCommentListView() {
        ListView commentListView = (ListView) findViewById(R.id.editorialFeed_comments_listView);

        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());

        switch (mCommentAdapter.getCount()) {

            case 1:
                height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100,
                        getResources()
                                .getDisplayMetrics());
                break;

            case 2:
                height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150,
                        getResources()
                                .getDisplayMetrics());
                break;
            case 3:
                height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200,
                        getResources()
                                .getDisplayMetrics());
                break;


        }

        ViewGroup.LayoutParams layoutParams = commentListView.getLayoutParams();

        layoutParams.height = height;

        commentListView.setLayoutParams(layoutParams);

    }


    public void insertCommentBtnClick(View view) {

        EditText editText = (EditText) findViewById(R.id.editorialFeed_commentemail_edittext);
        String emailString = editText.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.editorialFeed_commenttext_edittext);
        String commentString = editText2.getText().toString();

        if (emailString.length() > 5 && commentString.length() > 1) {
            DBHelperFirebase dbHelperFirebase = new DBHelperFirebase();
            Comment commentToPost = new Comment();
            commentToPost.setCommentText(commentString);
            commentToPost.seteMailID(emailString);

            commentToPost.setCommentDate(SimpleDateFormat.getDateInstance().format(Calendar.getInstance().getTime()));


            dbHelperFirebase.insertComment(currentEditorialFullInfo.getEditorialGeneralInfo()
                    .getEditorialID(), commentToPost);

            editText.setText("");
            editText2.setText("");
            Toast.makeText(this, "Posting", Toast.LENGTH_SHORT).show();

            mCommentAdapter.add(commentToPost);
            mCommentAdapter.notifyDataSetChanged();
            resizeCommentListView();


        } else {
            Toast.makeText(this, "Comment Size is small", Toast.LENGTH_SHORT).show();
        }


    }

    public void setThemeinactivity() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = sharedPref.getString("theme_list", "Day");

        TextView mainText = (TextView) findViewById(R.id.editorial_text_textview);

        if (theme.contentEquals("Night")) {

            ScrollView scrollView = (ScrollView) findViewById(R.id.editorialFeed_scrollView);
            try {
                scrollView.setBackgroundColor(getResources().getColor(R.color.nightThemeBackGroundColor));

                mainText.setTextColor(getResources().getColor(R.color.nightThemeTextColor));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        mainText.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.valueOf(sharedPref.getString("font_size_list", "16")));

    }


    public void hideBottomsheet(View view) {
        if (mBottomSheetBehavior != null) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        }
    }


    public void onLikeClick(View view) {
        Like like = new Like();
        like.setEditorialID(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialID());
        like.setEditorialTitle(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading());

        new DBHelperFirebase().uploadLike(like, new DBHelperFirebase.OnLikeListener() {
            @Override
            public void onLikeUpload(boolean isSuccessful) {
                Toast.makeText(EditorialFeedActivity.this, "Thank you for liking the editorial ", Toast.LENGTH_SHORT).show();
            }
        });

        currentEditorialFullInfo.getEditorialGeneralInfo().setEditorialLike(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialLike() + 1);
        TextView likeTextView = (TextView) findViewById(R.id.editorialfeed_like_textView);
        likeTextView.setText((currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialLike()) + " Likes");

        try {
            Answers.getInstance().logRating(new RatingEvent()
                    .putContentName("Like")
                    .putContentId(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialID())
                    .putContentType(currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading())
                    .putRating(1)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onShareButtonClick(View view) {
        onShareClick();
    }

    public void initializeSubscriptionAds() {
        mSubscriptionInterstitialAd = new InterstitialAd(this);
        mSubscriptionInterstitialAd.setAdUnitId("ca-app-pub-8455191357100024/6262441391");
        //test ad unit
        //mSubscriptionInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mSubscriptionInterstitialAd.loadAd(new AdRequest.Builder().build());

        mSubscriptionInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mSubscriptionInterstitialAd.loadAd(new AdRequest.Builder().build());


                Button button = (Button) EditorialFeedActivity.this.findViewById(R.id.editorialfeed_removeAd_button);
                if (AdsSubscriptionManager.checkShowAds(EditorialFeedActivity.this)) {
                    Toast.makeText(EditorialFeedActivity.this, "You need to click on the ad to get Pro features (with no ads) \n Try again", Toast.LENGTH_LONG).show();
                    button.setText("Try again? Click on the ads");
                } else {
                    Toast.makeText(EditorialFeedActivity.this, "Thank you for subscribing. \nAll the ads will be removed from next session.", Toast.LENGTH_LONG).show();
                    button.setText("Thank you for subscription");
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);

                try {
                    Answers.getInstance().logCustom(new CustomEvent("Ad failed to load")
                            .putCustomAttribute("Placement", "Subscription ad feed").putCustomAttribute("Error code", i));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();

                Toast.makeText(EditorialFeedActivity.this, "Now Click on the ads to get pro features", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                //Toast.makeText(EditorialFeedActivity.this, "Ad clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();

                AdsSubscriptionManager.setSubscriptionTime(EditorialFeedActivity.this);

                try {
                    Answers.getInstance().logCustom(new CustomEvent("Subscription").putCustomAttribute("user subscribed from feed", currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialHeading()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public void onRemoveAdClick(View view) {
       /* AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Ads for Free");

        builder.setMessage("Remove all the ads from app for free in just one click for 3 days\n" +
                "Press Remove Ads --> Ad will be displayed --> Click on the Ad shown --> Done. All the ads from app will be removed")
                .setPositiveButton("Remove Ads", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (mSubscriptionInterstitialAd.isLoaded()) {
                            mSubscriptionInterstitialAd.show();
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                            Toast.makeText(EditorialFeedActivity.this, "Ads didn't loaded yet ,Try again later", Toast.LENGTH_SHORT).show();
                        }

                        Answers.getInstance().logCustom(new CustomEvent("Subscription").putCustomAttribute("user show dialogue", "Clicked yes"));

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        Answers.getInstance().logCustom(new CustomEvent("Subscription").putCustomAttribute("user show dialogue", "Clicked No"));

                    }
                });

        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
*/

        if (mSubscriptionInterstitialAd != null) {
            if (mSubscriptionInterstitialAd.isLoaded()) {
                mSubscriptionInterstitialAd.show();

                Answers.getInstance().logCustom(new CustomEvent("Subscription").putCustomAttribute("user shown ad", "successful"));

            } else if (mSubscriptionInterstitialAd.isLoading()) {
                Toast.makeText(EditorialFeedActivity.this, "Ads didn't loaded yet ,Try in few minutes", Toast.LENGTH_SHORT).show();

            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.");
                Toast.makeText(EditorialFeedActivity.this, "Ads didn't loaded yet ,Try again later", Toast.LENGTH_SHORT).show();
                Answers.getInstance().logCustom(new CustomEvent("Subscription").putCustomAttribute("user shown ad", "not loaded"));
                mSubscriptionInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        }


    }

    public void onSourceTextClick(View view) {
        try {
            Intent intent = new Intent(EditorialFeedActivity.this, EditorialListWithNavActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("sourceIndex", currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialSourceIndex());
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onCategoryTextClick(View view) {
        try {
            Intent intent = new Intent(EditorialFeedActivity.this, EditorialListWithNavActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("categoryIndex", currentEditorialFullInfo.getEditorialGeneralInfo().getEditorialCategoryIndex());
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
