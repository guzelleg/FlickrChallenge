package com.guzelgimadieva.flickrchallenge.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.guzelgimadieva.flickrchallenge.R
import com.guzelgimadieva.flickrchallenge.model.Item
import com.guzelgimadieva.flickrchallenge.ui.theme.RobotoCondensed
import java.util.*

enum class SearchScreen(@StringRes val title: Int) {
    Start (title = R.string.app_name),
    Details (title = R.string.details_screen_title)
}

@Composable
fun SearchResultsScreen(
    viewModel: SearchResultsViewModel = viewModel(),
    onCardClicked: (Item) -> Unit
) {
    val searchScreenUiState by viewModel.searchUiState.collectAsState()
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.search_image_title),
            fontSize = 36.sp, modifier = Modifier.align(CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        SearchBar(
            hint = stringResource(id = R.string.search_field_label),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            searchQuery = searchScreenUiState.searchQuery ?: "",
            onUserSearchChanged = {
                viewModel.updateUserSearch(it)
                viewModel.getPhotos(it)
            })

        Spacer(modifier = Modifier.height(16.dp))
        SearchResultGrid(
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel,
            onItemClicked = {
                onCardClicked(it)
            }
        )
    }
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onUserSearchChanged: (String) -> Unit,
    searchQuery: String
) {

    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = searchQuery,
            onValueChange = onUserSearchChanged,
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused
                }
        )
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun SearchResultGrid(
    modifier: Modifier,
    viewModel: SearchResultsViewModel,
    onItemClicked: (Item) -> Unit
) {
    val searchScreenUiState by viewModel.searchUiState.collectAsState()
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

    val foundByTagItems = searchScreenUiState.listOfItems
    if (searchScreenUiState.searchQuery != null) {
        if (foundByTagItems.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier.padding(8.dp)
            ) {
                items(foundByTagItems) { photo ->
                    PhotoCard(photo, modifier) {
                        onItemClicked(photo)
                    }
                }
            }
        } else {
            RetrySection(error = "No results found!")
            {
                viewModel.getPhotos(searchScreenUiState.searchQuery)
            }
        }
    }

}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PhotoCard(
    photoItem: Item,
    modifier: Modifier,
    onItemClick: () -> Unit
) {
    val painter = rememberImagePainter(data = photoItem.media.size_m_url)

    Card(elevation = 4.dp,
        modifier = Modifier.clickable {
            onItemClick()
        }) {
        Column {
            val painterState = painter.state

            Image(
                painter = painter,
                alignment = Alignment.TopCenter,
                contentDescription = null,
                modifier = modifier
                    .size(200.dp)
                    .align(CenterHorizontally)
            )
            if (painterState is ImagePainter.State.Loading) {
                CircularProgressIndicator()
            }
            Text(
                text = photoItem.title,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            error, color = Color.Red, fontSize = 18.sp,
            modifier = Modifier.align(CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}