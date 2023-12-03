package com.guzelgimadieva.flickrchallenge.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.guzelgimadieva.flickrchallenge.R
import com.guzelgimadieva.flickrchallenge.model.Item
import com.guzelgimadieva.flickrchallenge.ui.theme.RobotoCondensed

enum class SearchScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Details(title = R.string.details_screen_title)
}

@Composable
private fun FlickrSearchAppBar(
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
fun FlickrSearchScreen(viewModel: SearchResultsViewModel, modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
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
    { innerPadding ->
        val searchUiState by viewModel.searchUiState.collectAsState()
        NavHost(
            navController,
            startDestination = SearchScreen.Start.name,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(SearchScreen.Start.name) {
                SearchResultsScreen(
                    viewModel = viewModel,
                    onCardClick = {
                        viewModel.setSelectedPhoto(it)
                        navController.navigate(SearchScreen.Details.name)
                    }
                )
            }
            composable(
                SearchScreen.Details.name
            ) {
                PhotoDetailsScreen(searchUiState)
            }
        }
    }
}

@Composable
fun SearchResultsScreen(
    onCardClick: (Item) -> Unit,
    viewModel: SearchResultsViewModel
) {
    val scrollState = rememberLazyGridState()
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CollapsibleSearchToolbar(
            viewModel,
            height =
            if (scrollState.firstVisibleItemIndex != 0) 0.dp
            else 150.dp
        )
        SearchResultGrid(
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel,
            onItemClick = {
                onCardClick(it)
            },
            scrollState = scrollState
        )
    }
}

@Composable
fun CollapsibleSearchToolbar(
    viewModel: SearchResultsViewModel,
    height: Dp
) {
    val searchQueryState by viewModel.searchQueryState.collectAsState()
    Column(
        Modifier.height(height)
    ) {
        Text(
            text = stringResource(id = R.string.search_image_title),
            fontSize = 36.sp, modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SearchBar(
            hint = stringResource(id = R.string.search_field_label),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            searchQuery = searchQueryState,
            onUserSearchChanged = {
                viewModel.updateSearch(it)
            })

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String,
    onUserSearchChanged: (String) -> Unit,
    searchQuery: String
) {

    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = searchQuery,
        onValueChange = onUserSearchChanged,
        maxLines = 1,
        singleLine = true,
        textStyle = TextStyle(color = Color.Black),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = { keyboardController?.hide() }),
        modifier = modifier
            .shadow(5.dp, CircleShape)
            .background(Color.White, CircleShape)
            .onFocusChanged {
                isHintDisplayed = !it.isFocused
            },
        decorationBox = { innerTextField ->
            Box(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                if (searchQuery.isEmpty()) {
                    Text(
                        text = hint,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.LightGray
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
private fun SearchResultGrid(
    modifier: Modifier,
    viewModel: SearchResultsViewModel,
    onItemClick: (Item) -> Unit,
    scrollState: LazyGridState
) {
    val searchScreenUiState by viewModel.searchUiState.collectAsState()
    val searchQueryState by viewModel.searchQueryState.collectAsState()
    val foundByTagItems = searchScreenUiState.listOfItems

    if (foundByTagItems.isNotEmpty()) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.search_result, ""),
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier.padding(8.dp),
            state = scrollState
        ) {
            items(foundByTagItems) { photo ->
                PhotoCard(photo, modifier) {
                    onItemClick(it)
                }
            }
        }
    } else {
        if (searchQueryState.isNotEmpty()) {
            NoResultFoundSection(modifier)
        } else {
            EmptySection(modifier)
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun PhotoCard(
    photoItem: Item,
    modifier: Modifier,
    onItemClick: (Item) -> Unit
) {
    val painter = rememberImagePainter(data = photoItem.media.mSizeUrl)

    Card(elevation = 4.dp,
        modifier = Modifier.clickable {
            onItemClick(photoItem)
        }) {
        Column {
            val painterState = painter.state
            Image(
                painter = painter,
                alignment = Alignment.TopCenter,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .height(dimensionResource(id = R.dimen.image_height))
            )
            if (painterState is ImagePainter.State.Loading) {
                CircularProgressIndicator()
            }
            Text(
                text = photoItem.title,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }

    }
}

@Composable
private fun NoResultFoundSection(modifier: Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            stringResource(id = R.string.no_results_message), color = Color.Red, fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun EmptySection(modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.empty_state_message, ""),
            fontFamily = RobotoCondensed,
            fontSize = 20.sp
        )
    }
}