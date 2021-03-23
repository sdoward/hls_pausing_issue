package com.example.hlspauingissue

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import java.io.File

class HlSVideoView : FrameLayout {

    private var exoPlayer: SimpleExoPlayer? = null
    private var videoUrls: String? = null
    private var trackSelector: DefaultTrackSelector? = null
    private val videoCache: Cache = CacheProvider.getCache(context)

    private val bandwidthMeter: DefaultBandwidthMeter by lazy {
        DefaultBandwidthMeter.Builder(
            context
        ).build()
    }

    init {
        View.inflate(context, R.layout.stm_video_view, this)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun setVideo(videoUrl: String) {
        this.videoUrls = videoUrl
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachPlayer()
    }

    private fun attachPlayer() {
        trackSelector = DefaultTrackSelector(context, AdaptiveTrackSelection.Factory())
        trackSelector?.parameters = DefaultTrackSelector.Parameters.getDefaults(context)
        val exoPlayer = SimpleExoPlayer.Builder(context)
            .setBandwidthMeter(bandwidthMeter)
            .setTrackSelector(trackSelector!!)
            .build()
        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        val playerView = findViewById<PlayerView>(R.id.playerView)
        playerView.player = exoPlayer
        this.exoPlayer = exoPlayer
        videoUrls?.let { videoUrls ->
            val uri = Uri.parse(videoUrls)
            val dataSource =
                DefaultDataSourceFactory(context, Util.getUserAgent(context, "stm"), bandwidthMeter)
            val cacheDataSource = CacheDataSource.Factory()
                .apply {
                    setCache(videoCache)
                    setUpstreamDataSourceFactory(dataSource)
                }
            val mediaItem = MediaItem.fromUri(uri)
            val mediaSource = HlsMediaSource.Factory(cacheDataSource).createMediaSource(mediaItem)
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
            Log.d("qwer", "should be playing")
        }
    }

    override fun onDetachedFromWindow() {
        releasePlayer()
        super.onDetachedFromWindow()
    }

    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

}

private const val CACHE_SIZE: Long = 10 * 1024 * 1024 // 10MB

object CacheProvider {

    private var cache: Cache? = null

    fun getCache(context: Context): Cache {
        var cache = this.cache
        if (cache == null) {
            val cacheFolder = File(context.cacheDir, "videos")
            val cacheEvictor = LeastRecentlyUsedCacheEvictor(CACHE_SIZE)
            cache = SimpleCache(cacheFolder, cacheEvictor, ExoDatabaseProvider(context))
            this.cache = cache
        }
        return cache
    }
}

