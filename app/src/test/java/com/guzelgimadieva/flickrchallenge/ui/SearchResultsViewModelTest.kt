package com.guzelgimadieva.flickrchallenge.ui

import app.cash.turbine.test
import com.guzelgimadieva.flickrchallenge.api.FlickrAPI
import com.guzelgimadieva.flickrchallenge.model.FlickrPhotos
import com.guzelgimadieva.flickrchallenge.model.Item
import com.guzelgimadieva.flickrchallenge.model.Media
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class SearchResultsViewModelTest {
    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private lateinit var viewModel: SearchResultsViewModel
    private val flickrApi = mockk<FlickrAPI>()
    private val flickrResponseItem = Item(
        "author",
        "id",
        "yesterday",
        "tag",
        "https://www.flickr.com/photos/192560872@N05/52644862710/",
        Media("https://www.flickr.com/photos/192560872@N05/52644862710/"),
        "yesterday",
        "tag",
        "tag"
    )

    @Before
    fun setup() {
        coEvery { flickrApi.getPhotos(any()) } returns FlickrPhotos(
            "Tag", "Tag",
            listOf(flickrResponseItem),
            "https://www.flickr.com/photos/192560872@N05/52644862710/", "yesterday", "tag"
        )
        viewModel = SearchResultsViewModel(flickrApi)
    }

    @Test
    fun `set selected photo saves item in ui state`() = runTest {
        val item = flickrResponseItem

        viewModel.searchUiState.test {
            assertNull(awaitItem().currentSelectedPhoto)
            viewModel.setSelectedPhoto(item)
            assertEquals(item, awaitItem().currentSelectedPhoto)
        }
    }

    @Test
    fun `update search saves list of photos in ui state`() = runTest {
        viewModel.searchUiState.test {
            assertEquals(listOf<Item>(), awaitItem().listOfItems)
            viewModel.updateSearch("tag")
            assertEquals(flickrApi.getPhotos("tag").items, awaitItem().listOfItems)
        }
    }

    @Test
    fun `update search saves search query in ui state`() = runBlocking {
        viewModel.updateSearch("5%")
        assertEquals("5%", viewModel.searchQueryState.value)
    }

    @ExperimentalCoroutinesApi
    class CoroutineTestRule(private val testDispatcher: TestDispatcher = StandardTestDispatcher()) : TestWatcher() {
        override fun starting(description: Description) {
            super.starting(description)
            Dispatchers.setMain(testDispatcher)
        }

        override fun finished(description: Description) {
            super.finished(description)
            Dispatchers.resetMain()
        }
    }
}