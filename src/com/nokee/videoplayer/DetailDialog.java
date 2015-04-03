package com.nokee.videoplayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import com.nokee.videoplayer.R;
import com.nokee.videoplayer.MovieListActivity.ViewHolder;

public class DetailDialog extends AlertDialog implements DialogInterface.OnClickListener {
    private static final String TAG = "DetailDialog";
    private static final boolean LOG = true;
    
    private static final int BTN_OK = DialogInterface.BUTTON_POSITIVE;
    private final Context mContext;
    
    private View mView;
    private TextView mTitleView;
    private TextView mTimeView;
    private TextView mPathView;
    private TextView mDurationView;
    private TextView mFileSizeView;
    
    private final ViewHolder mHolder;
    
    public DetailDialog(final Context context, final ViewHolder holder) {
        super(context);
        mContext = context;
        mHolder = holder;
        if (LOG) {
            LogUtils.v(TAG, "LimitDialog() holder=" + holder);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTitle(R.string.media_detail);
        mView = getLayoutInflater().inflate(R.layout.detail_dialog, null);
        if (mView != null) {
            setView(mView);
        }
        mTitleView = (TextView)mView.findViewById(R.id.title);
        mTimeView = (TextView)mView.findViewById(R.id.time);
        mDurationView = (TextView)mView.findViewById(R.id.duration);
        mPathView = (TextView)mView.findViewById(R.id.path);
        mFileSizeView = (TextView)mView.findViewById(R.id.filesize);

        mTitleView.setText(mContext.getString(R.string.detail_title, mHolder.mTitle));
        mPathView.setText(mContext.getString(R.string.detail_path, mHolder.mData));
        mDurationView.setText(mContext.getString(R.string.detail_duration, NokeeUtils.stringForTime(mHolder.mDuration)));
        mTimeView.setText(mContext.getString(R.string.detail_date_taken, NokeeUtils.localTime(mHolder.mDateTaken)));
        mFileSizeView.setText(mContext.getString(R.string.detail_filesize, Formatter.formatFileSize(
                                mContext, mHolder.mFileSize)));
        setButton(BTN_OK, mContext.getString(android.R.string.ok), this);
        super.onCreate(savedInstanceState);

    }
    
    public void onClick(final DialogInterface dialogInterface, final int button) {
        
    }
    
}