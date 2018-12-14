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
            mCurrentPlayer = new MediaPlayer();
            mCurrentPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mCurrentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mCurrentPlayer.start();
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
        mNextPlayer = new MediaPlayer();
        try {
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

    /**
     * Creating instance of the player with given context
     * and internal memory/SD path resource
     *
     * @param context - context
     * @param path    - internal memory/SD path to sound resource
     * @return new instance
     */
    public static PerfectLoopMediaPlayer create(Context context, String path) {
        return new PerfectLoopMediaPlayer(context, path);
    }

    private PerfectLoopMediaPlayer(Context context, String path) {
        mContext = context;
        mPath = path;
        try {
            mCurrentPlayer.setDataSource(mPath);
            mCurrentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mCurrentPlayer.start();
                }
            });
            mCurrentPlayer.prepareAsync();
            createNextMediaPlayerPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNextMediaPlayerPath() {
        mNextPlayer = new MediaPlayer();
        try {
            mNextPlayer.setDataSource(mPath);
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
                    createNextMediaPlayerRaw();
                    mediaPlayer.release();
                }
            };


    public void start() throws IllegalStateException {
        if (mCurrentPlayer != null) {
            Log.d(TAG, "start()");
            mCurrentPlayer.start();
        } else {
            Log.d(TAG, "start() | mCurrentPlayer is NULL");
        }

    }

    public void reset() {
        if (mCurrentPlayer != null) {
            Log.d(TAG, "reset()");
            mCurrentPlayer.reset();
        } else {
            Log.d(TAG, "reset() | mCurrentPlayer is NULL");
        }

    }

}