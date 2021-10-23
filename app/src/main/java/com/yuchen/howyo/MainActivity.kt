package com.yuchen.howyo

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yuchen.howyo.databinding.ActivityMainBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.util.*
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    private lateinit var binding: ActivityMainBinding
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var locationPermissionGranted = false
    private lateinit var mContext: Context

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

//    private val onNavigationViewListener =
//        NavigationView.OnNavigationItemSelectedListener { item ->
//            when (item.itemId) {
//                R.
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mContext = this

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupToolbar()
        setupBottomNav()
        setupDrawer()
        setupNavController()
        getLocationPermission()
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
                R.id.planFragment -> CurrentFragmentType.PLAN
                R.id.groupMessageFragment -> CurrentFragmentType.GROUP_MESSAGE
                R.id.checkorShoppingListFragment -> CurrentFragmentType.CHECK_OR_SHOPPING_LIST
                R.id.paymentFragment -> CurrentFragmentType.PAYMENT
                R.id.paymentDetailFragment -> CurrentFragmentType.PAYMENT_DETAIL
//                R.id.findLocationFragment -> CurrentFragmentType.FIND_LOCATION
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
//                    binding.imageToolbarLogo.layoutParams = layoutParams
                    binding.textToolbarTitle.layoutParams = layoutParams
                }
            }
            Logger.i("====== ${Build.MODEL} ======")
        }
    }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {

            menuInflater.inflate(R.menu.home_toolbar_nav_view_menu, menu)
            return true
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

    override fun onBackPressed() {

        viewModel.resetSharedToolbarTitle()
        super.onBackPressed()
    }

    //These function about getting location should be in a dependent class
    fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Get Permission", Toast.LENGTH_LONG).show()
            locationPermissionGranted = true
            checkGPSState()
        } else {
            requestLocationPermission()
        }
    }

    fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setMessage("此應用程式，需要位置權限才能正常使用")
                .setPositiveButton("確定") { _, _ ->
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_PERMISSION
                    )
                }
                .setNegativeButton("取消") { _, _ -> requestLocationPermission() }
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        //已獲取到權限
                        locationPermissionGranted = true
                        checkGPSState()
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            //權限被永久拒絕
                            Toast.makeText(this, "位置權限已被關閉，功能將會無法正常使用", Toast.LENGTH_SHORT).show()

                            //Navigate to setting page
//                            AlertDialog.Builder(this)
//                                .setTitle("開啟位置權限")
//                                .setMessage("此應用程式，位置權限已被關閉，需開啟才能正常使用")
//                                .setPositiveButton("確定") { _, _ ->
//                                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                                    startActivityForResult(intent, REQUEST_LOCATION_PERMISSION)
//                                }
//                                .setNegativeButton("取消") { _, _ -> requestLocationPermission() }
//                                .show()
                        } else {
                            //權限被拒絕
                            Toast.makeText(this, "位置權限被拒絕，功能將會無法正常使用", Toast.LENGTH_SHORT).show()
//                            requestLocationPermission()
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                getLocationPermission()
            }
            REQUEST_ENABLE_GPS -> {
                checkGPSState()
            }
        }
    }

    private fun checkGPSState() {
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder(mContext)
                .setTitle("GPS 尚未開啟")
                .setMessage("使用此功能需要開啟 GSP 定位功能")
                .setPositiveButton("前往開啟",
                    DialogInterface.OnClickListener { _, _ ->
                        startActivityForResult(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_GPS
                        )
                    })
                .setNegativeButton("取消", null)
                .show()
        } else {
            //todo getDeviceLocation()
            Toast.makeText(this, "已獲取到位置權限且GPS已開啟，可以準備開始獲取經緯度", Toast.LENGTH_SHORT).show()
        }
    }
}