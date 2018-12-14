package nicolasfrossard.whattheloop;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

/**
 * Created by viksaaskool on 13-04-2017.
 */

public class PerfectLoopMediaPlayer {


    private static final String TAG = PerfectLoopMediaPlayer.class.getName();
    private Context mContext = null;
    private int mResId = 0;
    private int mBeatResId = 0;
    private String mPath = null;

    private MediaPlayer mCurrentBeatPlayer = null;
    private MediaPlayer mNextBeatPlayer = null;

    private MediaPlayer mCurrentPlayer = null;
    private MediaPlayer mNextPlayer = null;

    /**
     * Creating instance of the player with given context and raw resource
     *
     * @param context - context
     * @param resId   - raw resource
     * @return new instance
     */
    public static PerfectLoopMediaPlayer create(Context context, int resId, int beatResId) {
        return new PerfectLoopMediaPlayer(context, resId, beatResId);
    }


    private PerfectLoopMediaPlayer(Context context, int resId, int beatResId) {
        mContext = context;
        mResId = resId;
        mBeatResId = beatResId;
        try {
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(mResId);
            AssetFileDescriptor beatafd = context.getResources().openRawResourceFd(mBeatResId);

            mCurrentBeatPlayer = new MediaPlayer();
            mCurrentBeatPlayer.setDataSource(beatafd.getFileDescriptor(), beatafd.getStartOffset(), beatafd.getLength());
            mCurrentBeatPlayer.prepareAsync();

            mCurrentPlayer = new MediaPlayer();
            mCurrentPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mCurrentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mCurrentPlayer.start();
                    mCurrentBeatPlayer.start();
                }
            });
            mCurrentPlayer.prepareAsync();
            createNextMediaPlayerRaw();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNextMediaPlayerRaw() {
        AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(mResId);
        AssetFileDescriptor beatafd = mContext.getResources().openRawResourceFd(mBeatResId);

        mNextPlayer = new MediaPlayer();
        mNextBeatPlayer = new MediaPlayer();

        try {
            mNextBeatPlayer.setDataSource(beatafd.getFileDescriptor(), beatafd.getStartOffset(), beatafd.getLength());
            mNextBeatPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mNextBeatPlayer.seekTo(0);
                    mCurrentBeatPlayer.setNextMediaPlayer(mNextBeatPlayer);
                }
            });
            mNextBeatPlayer.prepareAsync();

            mNextPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mNextPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mNextPlayer.seekTo(0);
                    mCurrentPlayer.setNextMediaPlayer(mNextPlayer);
                    mCurrentPlayer.setOnCompletionListener(onCompletionListener);
                }
            });
            mNextPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final MediaPlayer.OnCompletionListener onCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mCurrentPlayer = mNextPlayer;
                    mCurrentBeatPlayer = mNextBeatPlayer;
                    createNextMediaPlayerRaw();
                    mediaPlayer.release();
                }
            };


    public void reset() {
        if (mCurrentPlayer != null) {
            Log.d(TAG, "reset()");
            mCurrentPlayer.reset();
        } else {
            Log.d(TAG, "reset() | mCurrentPlayer is NULL");
        }

        if (mCurrentBeatPlayer != null) {
            Log.d(TAG, "reset()");
            mCurrentBeatPlayer.reset();
        } else {
            Log.d(TAG, "reset() | mCurrentBeatPlayer is NULL");
        }

    }

}