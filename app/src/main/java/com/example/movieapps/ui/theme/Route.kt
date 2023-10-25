package com.example.movieapps.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.movieapps.R
import com.example.movieapps.ui.theme.view.ListMovieView
import com.example.movieapps.ui.theme.view.LoadingView
import com.example.movieapps.ui.theme.view.MovieDetailView
import com.example.movieapps.ui.theme.view.ProfileView
import com.example.movieapps.ui.theme.view.SearchView
import com.example.movieapps.ui.theme.viewmodel.ListMovieUIState
import com.example.movieapps.ui.theme.viewmodel.ListMovieViewModel
import com.example.movieapps.ui.theme.viewmodel.MovieDetailUiState
import com.example.movieapps.ui.theme.viewmodel.MovieDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarMovieApps(
    scrollBehavior: TopAppBarScrollBehavior,
    contextToast: Context,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                text = "Movie Apps",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }else {
                IconButton(onClick = {
                    Toast.makeText(contextToast, "Menu Clicked", Toast.LENGTH_LONG).show()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Localized description"
                    )
                }
            }
        },
        actions = {
            if (!canNavigateBack) {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Logout"
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
fun BottomNavBarMovieApps(navController: NavController){

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Profile
    )

    NavigationBar{
        items.forEach{ item->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title)
                },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route){
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}


sealed class BottomNavItem(var title:String, var icon:Int, var route:String){
    object Home : BottomNavItem("Home", R.drawable.ic_home, ListScreen.ListMovie.name)
    object Search: BottomNavItem("Search", R.drawable.ic_search, ListScreen.Search.name)
    object Profile: BottomNavItem("Profile", R.drawable.ic_profile,ListScreen.Profile.name)
}

enum class ListScreen(){ // list viewnya
    Register,
    Login,
    Search,
    ListMovie,
    MovieDetail,
    Profile
}
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieAppsRoute() {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val navController = rememberNavController()
    val contextToast = LocalContext.current
    var canNavigateBack by remember { mutableStateOf(navController.previousBackStackEntry != null) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBarMovieApps(
                scrollBehavior = scrollBehavior,
                contextToast = contextToast,
                canNavigateBack = canNavigateBack,
                navigateUp = {navController.navigateUp()}
            ) },
        bottomBar = {
            if (!canNavigateBack) {
                BottomNavBarMovieApps(navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ListScreen.ListMovie.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            // List Routing
            composable(ListScreen.ListMovie.name) {
                val listMovieViewModel: ListMovieViewModel = viewModel()
                val status = listMovieViewModel.listMovieUIState
                when (status) {
                    is ListMovieUIState.Loading -> { LoadingView() }
                    is ListMovieUIState.Success -> {
                        ListMovieView(
                            movieList = status.data,
                            onFavClicked = { movie -> listMovieViewModel.onFavClicked(movie) },
                            onCardClick = {
                                navController.navigate(ListScreen.MovieDetail.name+"/"+it.id) // "/${it.id}" sama ajaa
                            }
                        )
                    }

                    is ListMovieUIState.Error -> {}
                }
            }

            composable(ListScreen.MovieDetail.name+"/{movieId}") {
                val movieDetailViewModel: MovieDetailViewModel = viewModel()

                movieDetailViewModel.getMovieById(
                    it.arguments?.getString("movieId")!!.toInt())

                val status = movieDetailViewModel.movieDetailUiState
                when (status) {
                    is MovieDetailUiState.Loading -> { LoadingView() }

                    is MovieDetailUiState.Success -> {
                        MovieDetailView(movie = status.data, onFavClicked = {})
                    }
                    is MovieDetailUiState.Error -> {}
                }
            }

            composable(ListScreen.Profile.name) {
                ProfileView()
            }

            composable(ListScreen.Search.name){
                SearchView()
            }
        }
    }
}