package app.articles.vacabulary.editorial.gamefever.editorial;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import utils.Like;
import utils.ShortNotesManager;

/**
 * Created by gamef on 23-02-2017.
 */

public class DBHelperFirebase  {
    FirebaseDatabase database;

   

    public DBHelperFirebase() {
        database = FirebaseDatabase.getInstance();

        //database.setPersistenceEnabled(true);

    }




    public void getEditorialFullInfoByID(final EditorialGeneralInfo editorialGeneralInfo, final EditorialFeedActivity activity) {
        /*Return Full editorial object using editorial general info */

        DatabaseReference myRef = database.getReference("EditorialFullInfo/" + editorialGeneralInfo.getEditorialID());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EditorialExtraInfo editorialExtraInfo = dataSnapshot.getValue(EditorialExtraInfo.class);


                EditorialFullInfo editorialFullInfo = new EditorialFullInfo(editorialGeneralInfo, editorialExtraInfo);


                //onEditorialFullInfoById(editorialFullInfo);
                activity.onGetEditorialFullInfo(editorialFullInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    //editorial updte
    public void getEditorialExtraInfoByID(String editorialID, final OnEditorialListener onEditorialListener) {
        /*Return Full editorial object using editorial general info */

        DatabaseReference myRef = database.getReference("EditorialFullInfo/" + editorialID);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EditorialExtraInfo editorialExtraInfo = dataSnapshot.getValue(EditorialExtraInfo.class);

                //onEditorialFullInfoById(editorialFullInfo);
                onEditorialListener.onEditorialExtraInfo(editorialExtraInfo, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onEditorialListener.onEditorialExtraInfo(null, false);
            }
        });

    }


    public void getEditorialGeneralInfoByID(String editorialID, final OnEditorialListener onEditorialListener) {
        /*Return Full editorial object using editorial general info */

        DatabaseReference myRef = database.getReference("EditorialGeneralInfo/" + editorialID);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EditorialGeneralInfo editorialGeneralInfo = dataSnapshot.getValue(EditorialGeneralInfo.class);

                //onEditorialFullInfoById(editorialFullInfo);
                onEditorialListener.onEditorialGeneralInfo(editorialGeneralInfo, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onEditorialListener.onEditorialGeneralInfo(null, false);
            }
        });

    }


//after editorial update


    public void getEditorialExtraInfoByID(final String editorialId, final EditorialListWithNavActivity activity) {
        /*Return Full editorial object using editorial general info */

        DatabaseReference myRef = database.getReference("EditorialGeneralInfo/" + editorialId);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EditorialGeneralInfo editorialGeneralInfo = dataSnapshot.getValue(EditorialGeneralInfo.class);


                //onEditorialFullInfoById(editorialFullInfo);
                activity.getEditorialExtraInfoByIDListner(editorialGeneralInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


    public void insertEditorial(EditorialFullInfo editorialFullInfo) {
        /*insert editorials detail to two diffirent node
        * editorial gn info
        * editorial full info*/

        DatabaseReference myRef = database.getReference("EditorialGeneralInfo");
        String pushkey = myRef.push().getKey();

        editorialFullInfo.getEditorialExtraInfo().setEditorialId(pushkey);
        editorialFullInfo.getEditorialGeneralInfo().setEditorialID(pushkey);


        myRef = database.getReference("EditorialFullInfo/" + pushkey);
        myRef.setValue(editorialFullInfo.getEditorialExtraInfo());

        DatabaseReference myRef2 = database.getReference("EditorialGeneralInfo/" + pushkey);

        myRef2.setValue(editorialFullInfo.getEditorialGeneralInfo());


    }



    public void fetchEditorialList(int limit, String end, final EditorialListActivity activity, boolean isFirst) {
        /*return list of editorial of size limit which end at end*/

        DatabaseReference myRef2 = database.getReference("EditorialGeneralInfo");
        Query query;
        if (isFirst) {
            query = myRef2.limitToLast(limit);
        } else {
            query = myRef2.orderByChild("editorialID").limitToLast(limit).endAt(end);


        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist = new ArrayList<EditorialGeneralInfo>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    EditorialGeneralInfo editorialGeneralInfo = ds.getValue(EditorialGeneralInfo.class);


                    editorialGeneralInfoArraylist.add(editorialGeneralInfo);
                }


                // activity.onFetchEditorialGeneralInfo(editorialGeneralInfoArraylist);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


    }


    public void fetchEditorialList(int limit, String end, final EditorialListWithNavActivity activity, final boolean isFirst) {
        /*return list of editorial of size limit which end at end*/

        DatabaseReference myRef2 = database.getReference("EditorialGeneralInfo");
        Query query;
        if (isFirst) {
            query = myRef2.limitToLast(limit);
        } else {
            query = myRef2.orderByChild("editorialID").limitToLast(limit).endAt(end);


        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist = new ArrayList<EditorialGeneralInfo>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    EditorialGeneralInfo editorialGeneralInfo = ds.getValue(EditorialGeneralInfo.class);


                    editorialGeneralInfoArraylist.add(editorialGeneralInfo);
                }


                activity.onFetchEditorialGeneralInfo(editorialGeneralInfoArraylist, isFirst);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });


    }


    public void fetchEditorialList(int limit, String end, final OnEditorialListListener onEditorialListListener) {
        /*return list of editorial of size limit which end at end*/

        DatabaseReference myRef2 = database.getReference("EditorialGeneralInfo");
        Query query;

        query = myRef2.orderByChild("editorialID").limitToLast(limit).endAt(end);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist = new ArrayList<EditorialGeneralInfo>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    EditorialGeneralInfo editorialGeneralInfo = ds.getValue(EditorialGeneralInfo.class);


                    editorialGeneralInfoArraylist.add(editorialGeneralInfo);
                }


                onEditorialListListener.onMoreEditorialList(editorialGeneralInfoArraylist, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onEditorialListListener.onMoreEditorialList(null, false);

            }
        });


    }

    public void fetchEditorialList(int limit, final OnEditorialListListener onEditorialListListener) {
        /*return list of editorial of size limit which end at end*/

        DatabaseReference myRef2 = database.getReference("EditorialGeneralInfo");
        Query query;

        query = myRef2.limitToLast(limit);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist = new ArrayList<EditorialGeneralInfo>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    EditorialGeneralInfo editorialGeneralInfo = ds.getValue(EditorialGeneralInfo.class);


                    editorialGeneralInfoArraylist.add(editorialGeneralInfo);
                }


                onEditorialListListener.onEditorialList(editorialGeneralInfoArraylist, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onEditorialListListener.onEditorialList(null, false);

            }
        });


    }

    public void fetchSourceSortEditorialList(int limit,int sourceIndex, final OnEditorialListListener onEditorialListListener) {
        /*return list of editorial of size limit which end at end*/

        DatabaseReference myRef2 = database.getReference("EditorialGeneralInfo");
        Query query;

        query = myRef2.orderByChild("editorialSourceIndex").equalTo(sourceIndex).limitToLast(limit);



        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist = new ArrayList<EditorialGeneralInfo>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    EditorialGeneralInfo editorialGeneralInfo = ds.getValue(EditorialGeneralInfo.class);


                    editorialGeneralInfoArraylist.add(editorialGeneralInfo);
                }


                onEditorialListListener.onEditorialList(editorialGeneralInfoArraylist, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onEditorialListListener.onEditorialList(null, false);

            }
        });


    }

    public void fetchSourceSortEditorialList(int limit ,String end,int sourceIndex, final OnEditorialListListener onEditorialListListener) {
        /*return list of editorial of size limit which end at end*/

        DatabaseReference myRef2 = database.getReference("EditorialGeneralInfo");
        Query query;

        query = myRef2.orderByChild("editorialSourceIndex").endAt(sourceIndex ,end).limitToLast(limit);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist = new ArrayList<EditorialGeneralInfo>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    EditorialGeneralInfo editorialGeneralInfo = ds.getValue(EditorialGeneralInfo.class);


                    editorialGeneralInfoArraylist.add(editorialGeneralInfo);
                }


                onEditorialListListener.onMoreEditorialList(editorialGeneralInfoArraylist, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onEditorialListListener.onEditorialList(null, false);

            }
        });


    }

    public void fetchCategorySortEditorialList(int limit,int categoryIndex, final OnEditorialListListener onEditorialListListener) {
        /*return list of editorial of size limit which end at end*/

        DatabaseReference myRef2 = database.getReference("EditorialGeneralInfo");
        Query query;

        query = myRef2.orderByChild("editorialCategoryIndex").equalTo(categoryIndex).limitToLast(limit);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist = new ArrayList<EditorialGeneralInfo>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    EditorialGeneralInfo editorialGeneralInfo = ds.getValue(EditorialGeneralInfo.class);


                    editorialGeneralInfoArraylist.add(editorialGeneralInfo);
                }


                onEditorialListListener.onEditorialList(editorialGeneralInfoArraylist, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                onEditorialListListener.onEditorialList(null, false);

            }
        });


    }

    public void fetchCategorySortEditorialList(int limit ,String end,int categoryIndex, final OnEditorialListListener onEditorialListListener) {
        /*return list of editorial of size limit which end at end*/

        DatabaseReference myRef2 = database.getReference("EditorialGeneralInfo");
        Query query;

        query = myRef2.orderByChild("editorialCategoryIndex").endAt(categoryIndex ,end).limitToLast(limit);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist = new ArrayList<EditorialGeneralInfo>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    EditorialGeneralInfo editorialGeneralInfo = ds.getValue(EditorialGeneralInfo.class);


                    editorialGeneralInfoArraylist.add(editorialGeneralInfo);
                }


                onEditorialListListener.onMoreEditorialList(editorialGeneralInfoArraylist, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onEditorialListListener.onEditorialList(null, false);

            }
        });


    }

    public void fetchDateSortEditorialList(long fromTimeInMillis , long toTimeInMillis, final OnEditorialListListener onEditorialListListener){

        DatabaseReference myRef = database.getReference("EditorialGeneralInfo");
        Query myref2 = myRef.orderByChild("timeInMillis").startAt(fromTimeInMillis).endAt(toTimeInMillis);

        myref2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<EditorialGeneralInfo> editorialGeneralInfoArraylist = new ArrayList<EditorialGeneralInfo>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    EditorialGeneralInfo editorialGeneralInfo = ds.getValue(EditorialGeneralInfo.class);


                    editorialGeneralInfoArraylist.add(editorialGeneralInfo);
                }


                Collections.reverse(editorialGeneralInfoArraylist);

                onEditorialListListener.onEditorialList(editorialGeneralInfoArraylist, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onEditorialListListener.onEditorialList(null, false);

            }
        });

    }

    public void insertComment(String editorialID, Comment userComment) {

        DatabaseReference myRef = database.getReference("Comments/" + editorialID);
        myRef.push().setValue(userComment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    public void fetchComment(String editorialID, int limitTo, final OnCommentListener onCommentListener) {


        DatabaseReference myRef = database.getReference("Comments/" + editorialID);
        Query query = myRef.orderByKey().limitToLast(limitTo);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Comment> commentArrayList = new ArrayList<Comment>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    commentArrayList.add(snapshot.getValue(Comment.class));


                }
                onCommentListener.onCommentFetched(commentArrayList);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    public void uploadLike(Like like, final OnLikeListener onLikeListener) {
        DatabaseReference myRef = database.getReference().child("likes/");
        myRef.push().setValue(like).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onLikeListener.onLikeUpload(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                onLikeListener.onLikeUpload(true);
            }
        });


    }



    public void fetchShortNotesList(String userUID, int limitTo , final OnShortNoteListListener onShortNoteListListener){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("shortNote/"+userUID);

        database.orderByKey().limitToLast(limitTo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<ShortNotesManager> shortNotesManagerArrayList =new ArrayList<ShortNotesManager>();

                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    ShortNotesManager shortNotesManager =snapshot.getValue(ShortNotesManager.class);

                    shortNotesManagerArrayList.add(shortNotesManager);
                }

                Collections.reverse(shortNotesManagerArrayList);

                onShortNoteListListener.onShortNoteList(shortNotesManagerArrayList ,true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onShortNoteListListener.onShortNoteList(null ,false);
            }
        });


    }

    public void deleteShortNotes(String userUID , String pushKey, final OnShortNoteListListener onShortNoteListListener){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("shortNote/"+userUID+"/"+pushKey);

        database.removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onShortNoteListListener.onShortNoteUpload(false);
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onShortNoteListListener.onShortNoteUpload(true);

            }
        });

    }

    public void uploadShortNote(String userUID , ShortNotesManager shortNotesManager , final OnShortNoteListListener onShortNoteListListener){
        if (userUID == null){
            onShortNoteListListener.onShortNoteUpload(false);
        }

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("shortNote/"+userUID+"/"+shortNotesManager.getNoteArticleID());
        database.setValue(shortNotesManager).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("uploaded", "onSuccess: ");
                onShortNoteListListener.onShortNoteUpload(true);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                onShortNoteListListener.onShortNoteUpload(false);
            }
        });

    }

    public void fetchShortNotesList(String userUID, int limitTo ,String lastID, final OnShortNoteListListener onShortNoteListListener){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("shortNote/"+userUID);

        Query query = database.orderByKey().limitToLast(limitTo).endAt(lastID);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<ShortNotesManager> shortNotesManagerArrayList =new ArrayList<ShortNotesManager>();

                for (DataSnapshot snapshot :dataSnapshot.getChildren()){
                    ShortNotesManager shortNotesManager =snapshot.getValue(ShortNotesManager.class);

                    shortNotesManagerArrayList.add(shortNotesManager);
                }

                shortNotesManagerArrayList.remove(shortNotesManagerArrayList.size()-1);

                Collections.reverse(shortNotesManagerArrayList);

                onShortNoteListListener.onShortNoteList(shortNotesManagerArrayList ,true);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                onShortNoteListListener.onShortNoteList(null ,false);
            }
        });


    }


    public interface OnCommentListener {
        public void onCommentInserted(Comment comment);

        public void onCommentFetched(ArrayList<Comment> commentArrayList);
    }

    public interface OnEditorialListener {
        public void onEditorialGeneralInfo(EditorialGeneralInfo editorialGeneralInfo, boolean isSuccessful);

        public void onEditorialExtraInfo(EditorialExtraInfo editorialExtraInfo, boolean isSuccessful);
    }

    public interface OnLikeListener {
        void onLikeUpload(boolean isSuccessful);
    }

    public interface OnEditorialListListener {
        public void onEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful);

        public void onMoreEditorialList(ArrayList<EditorialGeneralInfo> editorialGeneralInfoArrayList, boolean isSuccessful);

    }

    public interface OnShortNoteListListener{
        public void onShortNoteList(ArrayList<ShortNotesManager> shortNotesManagerArrayList ,boolean isSuccessful);
        public void onShortNoteUpload(boolean isSuccessful);
    }

}
