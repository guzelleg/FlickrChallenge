package com.guzelgimadieva.flickrchallenge.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.guzelgimadieva.flickrchallenge.R

@Composable
fun FlickrSearchAppBar(
    modifier: Modifier = Modifier,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    currentScreen: SearchScreen
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun FlickrSearchApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val viewModel: SearchResultsViewModel = viewModel()
    val currentScreen = SearchScreen.valueOf(
        backStackEntry?.destination?.route ?: SearchScreen.Start.name
    )

    Scaffold(
        topBar = {
            FlickrSearchAppBar(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                currentScreen = currentScreen,
            )
        }
    )
    { innerPadding ->  val searchUiState by viewModel.searchUiState.collectAsState()
        NavHost(
            navController,
            startDestination = SearchScreen.Start.name,
            modifier = modifier.padding(innerPadding)
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
                SearchScreen.Details.name
            )
            {
                PhotoDetailsScreen(searchUiState)
            }
        }
    }
}