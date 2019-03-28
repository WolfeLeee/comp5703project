package comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {

    private static final String TAG = "RecyclerItemClickListen";

    interface OnRecyclerClickListener {
        void onItemClick(View view, int position);
    }

    private final OnRecyclerClickListener mListener;
    private final GestureDetectorCompat mgestureDetector;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnRecyclerClickListener listener) {
        this.mListener = listener;
        this.mgestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: starts");
                View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView != null && mListener != null){
                    mListener.onItemClick(childView,recyclerView.getChildAdapterPosition(childView));
                }

                return true;
            }
        });
        Log.d(TAG, "RecyclerItemClickListener: Successfully constructs");
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: starts");
        if(mgestureDetector != null){
            boolean result =mgestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent: returned "+result);
            return result;
        }
        else {
            Log.d(TAG, "onInterceptTouchEvent: returned false");
            return false;
        }
    }
}
