package nicolasfrossard.whattheloop

import android.content.Context

class LoopPlayer {

    private lateinit var player: PerfectLoopMediaPlayer

    fun play(context: Context, loopResourceRef: Int, beatResourceRef: Int) {
        player = PerfectLoopMediaPlayer.create(context, loopResourceRef, beatResourceRef)
    }

    fun pause() {
        if (::player.isInitialized) {
            player.reset()
        }
    }
}