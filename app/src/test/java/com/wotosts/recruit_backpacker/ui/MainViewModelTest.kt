package com.wotosts.recruit_backpacker.ui

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.wotosts.recruit_backpacker.R
import com.wotosts.recruit_backpacker.model.Local
import com.wotosts.recruit_backpacker.model.Weather
import com.wotosts.recruit_backpacker.model.WeatherDTO
import com.wotosts.recruit_backpacker.model.WeatherRow
import com.wotosts.recruit_backpacker.repository.WeatherRepository
import com.wotosts.recruit_backpacker.rule.CoroutinesTestRule
import com.wotosts.recruit_backpacker.utils.Resource
import getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.*

import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    val ERROR = "error"
    val SOME_ERROR = "some error"

    @get:Rule
    var coroutineTestRule = CoroutinesTestRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Captor
    private lateinit var argumentCaptor: ArgumentCaptor<List<WeatherRow>>

    @Mock
    private lateinit var observer: Observer<List<WeatherRow>?>

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var mockWeatherRepository: WeatherRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        //MockitoAnnotations.openMocks(this)
        viewModel = MainViewModel(
            application,
            mockWeatherRepository
        ).also {
            it.weatherListLiveData.observeForever(observer)
        }

        Mockito.`when`(application.getString(R.string.refresh_error)).thenReturn(ERROR)
        Mockito.`when`(application.getString(R.string.refresh_some_error)).thenReturn(SOME_ERROR)
    }

    @After
    fun tearDown() {
        viewModel.weatherListLiveData.removeObserver(observer)
    }

    @Test
    fun refreshWeather_failed_local_resource_error() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            val errorResource = Resource.Error<List<Local>>("Test Error", null)
            mockWeatherRepository.stub {
                onBlocking { getLocalList() }.thenReturn(errorResource)
            }

            viewModel.refreshWeather()

            verify(observer).onChanged(argumentCaptor.capture())
            verify(mockWeatherRepository, never()).getWeather(any())
            Assert.assertEquals(0, (argumentCaptor.value as List<*>).size)
            Assert.assertEquals(
                ERROR,
                viewModel.refreshEvent.getOrAwaitValue().peekContent()
            )
        }

    @Test
    fun refreshWeather_failed_all_weather_resource_error() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            val localList = getLocalList()
            val localResource = Resource.Success<List<Local>>(localList)
            val weatherResource = Resource.Error<WeatherDTO>("Test Error", null)
            mockWeatherRepository.stub {
                onBlocking { getLocalList() }.thenReturn(localResource)
                onBlocking { getWeather(any()) }.thenReturn(weatherResource)
            }
            viewModel.refreshWeather()

            verify(observer).onChanged(argumentCaptor.capture())
            verify(mockWeatherRepository, times(localList.size)).getWeather(any())
            Assert.assertEquals(0, argumentCaptor.value.size)
            Assert.assertEquals(
                ERROR,
                viewModel.refreshEvent.getOrAwaitValue().peekContent()
            )
        }

    @Test
    fun refreshWeather_success_some_weather_resource_error() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            val localList = getLocalList()
            val weatherDTOList = mutableListOf<Resource<WeatherDTO>>().apply {
                val weatherList = getWeatherList()
                add(Resource.Error("Test Error", null))
                add(Resource.Success(WeatherDTO(weatherList, localList[1].title)))
            }
            val localResource = Resource.Success<List<Local>>(localList)
            mockWeatherRepository.stub {
                onBlocking { getLocalList() }.thenReturn(localResource)
                onBlocking { getWeather(any()) }
                    .thenReturn(weatherDTOList[0])
                    .thenReturn(weatherDTOList[1])
            }
            viewModel.refreshWeather()

            verify(observer).onChanged(argumentCaptor.capture())
            verify(mockWeatherRepository, times(localList.size)).getWeather(any())
            Assert.assertEquals(1, (argumentCaptor.value as List<*>).size)
            Assert.assertEquals(
                SOME_ERROR,
                viewModel.refreshEvent.getOrAwaitValue().peekContent()
            )
        }

    @Test
    fun refreshWeather_success() =
        coroutineTestRule.testDispatcher.runBlockingTest {
            val localList = getLocalList()
            val weatherList = getWeatherList()
            val weatherDTOList = getWeatherDTOList(localList, weatherList)
            val localResource = Resource.Success<List<Local>>(localList)
            mockWeatherRepository.stub {
                onBlocking { getLocalList() }.thenReturn(localResource)
                onBlocking { getWeather(any()) }
                    .thenReturn(weatherDTOList[0])
                    .thenReturn(weatherDTOList[1])
            }
            viewModel.refreshWeather()

            verify(observer).onChanged(argumentCaptor.capture())
            verify(mockWeatherRepository, times(localList.size)).getWeather(any())

            val captorValue = argumentCaptor.value
            Assert.assertEquals(2, captorValue.size)
            Assert.assertEquals(weatherList[0], captorValue[0].today)
            Assert.assertEquals(weatherList[1], captorValue[0].tomorrow)
            Assert.assertEquals(localList[0].title, captorValue[0].local)
            Assert.assertEquals(weatherList[0], captorValue[1].today)
            Assert.assertEquals(weatherList[1], captorValue[1].tomorrow)
            Assert.assertEquals(localList[1].title, captorValue[1].local)
            Assert.assertEquals(
                "",
                viewModel.refreshEvent.getOrAwaitValue().peekContent()
            )
        }

    @Test
    fun refresh_again() = coroutineTestRule.testDispatcher.runBlockingTest {
        val localList = getLocalList()
        val weatherList = getWeatherList()
        val weatherDTOList = getWeatherDTOList(localList, weatherList)
        val localResource = Resource.Success<List<Local>>(localList)
        mockWeatherRepository.stub {
            onBlocking { getLocalList() }.thenReturn(localResource)
            onBlocking { getWeather(any()) }
                .thenReturn(weatherDTOList[0])
                .thenReturn(weatherDTOList[1])
        }

        viewModel.refreshWeather()
        Assert.assertEquals(2, viewModel.weatherListLiveData.getOrAwaitValue().size)

        viewModel.refreshWeather()

        verify(observer, times(3)).onChanged(argumentCaptor.capture())
        Assert.assertEquals(0, argumentCaptor.allValues[1].size)

        val captorValue = argumentCaptor.allValues[2] as List<WeatherRow>
        Assert.assertEquals(2, captorValue.size)
        Assert.assertEquals(
            "",
            viewModel.refreshEvent.getOrAwaitValue().peekContent()
        )
    }

    private fun getLocalList() = mutableListOf<Local>().apply {
        add(Local("test1", 1))
        add(Local("test2", 2))
    }

    private fun getWeatherList() = mutableListOf<Weather>().apply {
        add(Weather("1", "2", 3f, 4))
        add(Weather("5", "6", 7f, 8))
    }

    private fun getWeatherDTOList(
        localList: List<Local>,
        weatherList: List<Weather>
    ) = mutableListOf<Resource<WeatherDTO>>().apply {
        add(Resource.Success(WeatherDTO(weatherList, localList[0].title)))
        add(Resource.Success(WeatherDTO(weatherList, localList[1].title)))
    }
}