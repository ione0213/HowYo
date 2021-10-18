package com.yuchen.howyo

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yuchen.howyo.databinding.ActivityMainBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.util.CurrentFragmentType
import com.yuchen.howyo.util.DrawerToggleType
import com.yuchen.howyo.util.Logger
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    private lateinit var binding: ActivityMainBinding
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navToHomeFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_discover -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navToDiscoverFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_plan_cover -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navToPlanCoverDialog())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_star -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navToFavoriteFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_profile -> {

                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navToProfileFragment())
                    return@OnNavigationItemSelectedListener true
//                    when (viewModel.isLoggedIn) {
//                        true -> {
//                            findNavController(R.id.myNavHostFragment).navigate(
//                                NavigationDirections.navigateToProfileFragment(
//                                    viewModel.user.value
//                                )
//                            )
//                        }
//                        false -> {
//                            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLoginDialog())
//                            return@OnNavigationItemSelectedListener false
//                        }
//                    }
//                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    // get the height of status bar from system
    private val statusBarHeight: Int
        get() {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return when {
                resourceId > 0 -> resources.getDimensionPixelSize(resourceId)
                else -> 0
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupToolbar()
        setupBottomNav()
        setupDrawer()
        setupNavController()
    }

    private fun setupBottomNav() {
        binding.bottomNavView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
    }

    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.homeFragment -> CurrentFragmentType.HOME
                R.id.discoverFragment -> CurrentFragmentType.DISCOVER
                R.id.favoriteFragment -> CurrentFragmentType.FAVORITE
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.notificationFragment -> CurrentFragmentType.NOTIFICATION
                R.id.groupMessageFragment -> CurrentFragmentType.GROUP_MESSAGE
                R.id.shoppingListFragment -> CurrentFragmentType.SHOPPING_LIST
                R.id.checkListFragment -> CurrentFragmentType.CHECK_LIST
                R.id.paymentFragment -> CurrentFragmentType.PAYMENT
                R.id.paymentDetailFragment -> CurrentFragmentType.PAYMENT_DETAIL
                R.id.findLocationFragment -> CurrentFragmentType.FIND_LOCATION
                R.id.friendsFragment -> CurrentFragmentType.FRIENDS
                R.id.settingFragment -> CurrentFragmentType.SETTING
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    private fun setupToolbar() {

        binding.toolbar.setPadding(0, statusBarHeight, 0, 0)

        launch {

            val dpi = resources.displayMetrics.densityDpi.toFloat()
            val dpiMultiple = dpi / DisplayMetrics.DENSITY_DEFAULT

            val cutoutHeight = getCutoutHeight()

            Logger.i("====== ${Build.MODEL} ======")
            Logger.i("$dpi dpi (${dpiMultiple}x)")
            Logger.i("statusBarHeight: ${statusBarHeight}px/${statusBarHeight / dpiMultiple}dp")

            when {
                cutoutHeight > 0 -> {
                    Logger.i("cutoutHeight: ${cutoutHeight}px/${cutoutHeight / dpiMultiple}dp")

                    val oriStatusBarHeight =
                        resources.getDimensionPixelSize(R.dimen.height_status_bar_origin)

                    binding.toolbar.setPadding(0, oriStatusBarHeight, 0, 0)
                    val layoutParams = Toolbar.LayoutParams(
                        Toolbar.LayoutParams.WRAP_CONTENT,
                        Toolbar.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.gravity = Gravity.CENTER

                    when (Build.MODEL) {
                        "Pixel 5" -> {
                            Logger.i("Build.MODEL is ${Build.MODEL}")
                        }
                        else -> {
                            layoutParams.topMargin = statusBarHeight - oriStatusBarHeight
                        }
                    }
                    binding.imageToolbarLogo.layoutParams = layoutParams
                    binding.textToolbarTitle.layoutParams = layoutParams
                }
            }
            Logger.i("====== ${Build.MODEL} ======")
        }
    }

    private fun setupDrawer() {

        // set up toolbar
        val navController = this.findNavController(R.id.myNavHostFragment)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = null

        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)

        binding.drawerLayout.fitsSystemWindows = true
        binding.drawerLayout.clipToPadding = false

        actionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
//            override fun onDrawerOpened(drawerView: View) {
//                super.onDrawerOpened(drawerView)
//
//                when (UserManager.isLoggedIn) { // check user login status when open drawer
//                    true -> {
//                        viewModel.checkUser()
//                    }
//                    else -> {
//                        findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLoginDialog())
//                        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                            binding.drawerLayout.closeDrawer(GravityCompat.START)
//                        }
//                    }
//                }
//            }
        }
//            .apply {
//            binding.drawerLayout.addDrawerListener(this)
//            syncState()
//        }

//        // Set up header of drawer ui using data binding
//        val bindingNavHeader = NavHeaderDrawerBinding.inflate(
//            LayoutInflater.from(this), binding.drawerNavView, false
//        )
//
//        bindingNavHeader.lifecycleOwner = this
//        bindingNavHeader.viewModel = viewModel
//        binding.drawerNavView.addHeaderView(bindingNavHeader.root)

        // Observe current drawer toggle to set the navigation icon and behavior
        viewModel.currentDrawerToggleType.observe(
            this,
            { type ->

                actionBarDrawerToggle?.isDrawerIndicatorEnabled = type.indicatorEnabled
                supportActionBar?.setDisplayHomeAsUpEnabled(!type.indicatorEnabled)

                when (type) {
                    DrawerToggleType.BACK -> binding.toolbar.setNavigationIcon(R.drawable.toolbar_back)
                    else -> {

                    }
                }

                actionBarDrawerToggle?.setToolbarNavigationClickListener {
                    when (type) {
                        DrawerToggleType.BACK -> onBackPressed()
                        else -> {
                        }
                    }
                }
            }
        )
    }
}