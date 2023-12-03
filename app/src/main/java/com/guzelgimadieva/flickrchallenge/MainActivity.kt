package com.guzelgimadieva.flickrchallenge

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.guzelgimadieva.flickrchallenge.ui.FlickrSearchScreen
import com.guzelgimadieva.flickrchallenge.ui.SearchResultsViewModel
import com.guzelgimadieva.flickrchallenge.ui.theme.FlickrChallengeTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.Forest.plant

@HiltAndroidApp
class FlickrSearchApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: SearchResultsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlickrChallengeTheme {
                FlickrSearchScreen(viewModel)
            }
        }
        if (BuildConfig.DEBUG) {
            plant(Timber.DebugTree())
        }
    }
}
