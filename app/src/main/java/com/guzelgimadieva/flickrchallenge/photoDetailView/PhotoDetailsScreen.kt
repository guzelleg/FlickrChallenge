package com.guzelgimadieva.flickrchallenge.photoDetailView

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.guzelgimadieva.flickrchallenge.model.Item
import com.guzelgimadieva.flickrchallenge.photosList.SearchResultsViewModel
import com.guzelgimadieva.flickrchallenge.photosList.SearchUiState
import com.guzelgimadieva.flickrchallenge.ui.theme.RobotoCondensed

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PhotoDetailsScreen(
    searchDetailUiState: SearchUiState
) {
    if(searchDetailUiState.currentSelectedPhoto != null) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        )
        {
            Image(
                painter = rememberImagePainter(data = searchDetailUiState
                    .currentSelectedPhoto.media.size_m_url),
                contentDescription = "",
                Modifier
                    .fillMaxWidth()
                    .size(200.dp),
                alignment = Alignment.Center,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Title:", fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Text(text = searchDetailUiState.currentSelectedPhoto.title)
            Text(text = "Author:")
            Text(text = searchDetailUiState.currentSelectedPhoto.author)
            Text(text = "Tags:")
            Text(text = searchDetailUiState.currentSelectedPhoto.tags)
        }
    }
}