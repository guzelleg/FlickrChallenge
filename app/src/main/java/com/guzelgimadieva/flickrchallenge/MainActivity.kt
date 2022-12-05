package com.guzelgimadieva.flickrchallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.guzelgimadieva.flickrchallenge.ui.theme.FlickrChallengeTheme
import com.guzelgimadieva.flickrchallenge.ui.FlickrSearchApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlickrChallengeTheme {
                FlickrSearchApp()
            }
        }
    }
}



