package com.guzelgimadieva.flickrchallenge.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.guzelgimadieva.flickrchallenge.photoDetailView.PhotoDetailsScreen
import com.guzelgimadieva.flickrchallenge.photosList.SearchResultsScreen
import com.guzelgimadieva.flickrchallenge.photosList.SearchResultsViewModel
import com.guzelgimadieva.flickrchallenge.photosList.SearchScreen

@Composable
fun FlickrSearchApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val viewModel : SearchResultsViewModel = viewModel()
    val searchUiState by viewModel.searchUiState.collectAsState()
    NavHost(
        navController,
        startDestination = SearchScreen.Start.name
    ) {
        composable(SearchScreen.Start.name) {
            SearchResultsScreen(
                onCardClicked = {
                    viewModel.setSelectedPhoto(it)
                    navController.navigate(SearchScreen.Details.name)
                }

            )
        }
        composable(
            SearchScreen.Details.name)
        {
                PhotoDetailsScreen(searchUiState)
            }
        }
    }