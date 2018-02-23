package com.emilieappandroid.flashchatnewfirebase;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by Emilie on 23/02/2018.
 */

public class ChatListAdapter extends BaseAdapter{

    private Activity mActivity;
    private DatabaseReference mDatabaseReference;
    private String mDisplayName;
    private ArrayList<DataSnapshot> mSnapShotList;

    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            mSnapShotList.add(dataSnapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    public ChatListAdapter(Activity mActivity, DatabaseReference mDatabaseReference, String mDisplayName) {
        this.mActivity = mActivity;
        this.mDatabaseReference = mDatabaseReference.child("messages");
        this.mDatabaseReference.addChildEventListener(mListener);
        this.mDisplayName = mDisplayName;
        this.mSnapShotList = new ArrayList<>();
    }

    static class ViewHolder{
        TextView authorNeme;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mSnapShotList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public InstantMessage getItem(int position) {
        DataSnapshot snapshot = mSnapShotList.get(position);
        return snapshot.getValue(InstantMessage.class);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_msg_row, parent, false);

            final ViewHolder holder = new ViewHolder();
            holder.authorNeme = (TextView) convertView.findViewById(R.id.author);
            holder.body = (TextView) convertView.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorNeme.getLayoutParams();
            convertView.setTag(holder);
        }

        final InstantMessage message = getItem(position);
        final ViewHolder holder = (ViewHolder) convertView.getTag();

        boolean isMe = message.getAuthor().equals(mDisplayName);
        setChatRowAppearence(isMe, holder);

        String msg = message.getMessage();
        holder.body.setText(msg);

        String author = message.getAuthor();
        holder.authorNeme.setText(author);

        return convertView;
    }

    private void setChatRowAppearence(boolean isItMe, ViewHolder holder){
        if(isItMe){
            holder.params.gravity = Gravity.END;
            holder.authorNeme.setTextColor(Color.GREEN);
            holder.body.setBackgroundResource(R.drawable.bubble2);
        }else{
            holder.params.gravity = Gravity.START;
            holder.authorNeme.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }

        holder.authorNeme.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);
    }

    public void cleanup(){
        mDatabaseReference.removeEventListener(mListener);
    }
}
