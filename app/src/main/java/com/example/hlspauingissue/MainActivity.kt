package com.example.hlspauingissue

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<HlSVideoView>(R.id.hlsVideoView).apply {
            setVideo("https://videos.sharethemeal.org/VID_1x1_GUINEA_HM_2021_12_Sec_20210303_stream.m3u8")// Pauses when repeating
            //setVideo("https://videos.sharethemeal.org/VID_4x3_1_TABLE_ONBOARDING_5_sec_20201125_stream.m3u8") // Seemless repeat
        }
    }

}
