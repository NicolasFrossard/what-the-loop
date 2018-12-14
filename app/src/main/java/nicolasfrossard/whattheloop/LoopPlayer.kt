package nicolasfrossard.whattheloop

import android.content.Context

class LoopPlayer {

    private lateinit var player: PerfectLoopMediaPlayer

    fun play(context: Context, resourceRef: Int) {
        player = PerfectLoopMediaPlayer.create(context, resourceRef)
        player.start()
    }

    fun pause() {
        if (::player.isInitialized) {
            player.reset()
        }
    }
}