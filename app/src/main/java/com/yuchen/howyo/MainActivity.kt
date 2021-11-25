package com.yuchen.howyo

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yuchen.howyo.databinding.ActivityMainBinding
import com.yuchen.howyo.ext.getVmFactory
import com.yuchen.howyo.service.UserLocateService
import com.yuchen.howyo.signin.UserManager
import com.yuchen.howyo.signin.UserManager.isLoggedIn
import com.yuchen.howyo.util.*
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    private lateinit var binding: ActivityMainBinding
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var locationPermissionGranted = false
    private lateinit var mContext: Context
    private var userLocateServiceBound = false
    private var userLocateService: UserLocateService? = null
    private lateinit var howYoBroadcastReceiver: HowYoBroadcastReceiver

    private val userLocateServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {

            val binder = service as UserLocateService.LocalBinder
            userLocateService = binder.service
            userLocateServiceBound = true
            viewModel.setUserLocateServiceStatus(userLocateServiceBound)
        }

        override fun onServiceDisconnected(name: ComponentName) {

            userLocateService = null
            userLocateServiceBound = false
            viewModel.setUserLocateServiceStatus(userLocateServiceBound)
        }
    }

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

                    findNavController(R.id.myNavHostFragment).navigate(
                        NavigationDirections.navToProfileFragment(UserManager.userId ?: "")
                    )
                    return@OnNavigationItemSelectedListener true
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

        setTheme(R.style.Theme_HowYo)

        super.onCreate(savedInstanceState)

        mContext = this

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        howYoBroadcastReceiver = HowYoBroadcastReceiver()

        viewModel.navigateToHomeByBottomNav.observe(this) {
            it?.let {
                binding.bottomNavView.selectedItemId = R.id.navigation_home
                viewModel.onHomeNavigated()
            }
        }

        viewModel.currentFragmentType.observe(this) {
            it?.let {
                when {
                    it != CurrentFragmentType.SIGN_IN && !isLoggedIn -> {
                        findNavController(R.id.myNavHostFragment)
                            .navigate(NavigationDirections.navToSignInFragment())
                    }
                }
            }
        }

        viewModel.isResetToolbar.observe(this) {
            it?.let {
                if (it) {
                    setupToolbar()
                    setupDrawer()
                    setupNavController()
                    viewModel.onResetToolbar()
                }
            }
        }

        viewModel.isUserLocateServiceReady.observe(this) {
            it?.let {
                if (it) {
                    userLocateService?.subscribeToLocationUpdates()
                    viewModel.resetUserLocateServiceStatus()
                }
            }
        }

        viewModel.isAccessAppFirstTime.observe(this) {
            it?.let {
                if (it && isLoggedIn) {
                    getLocationPermission()
                    viewModel.resetIsAccessAppFirstTime()
                }
            }
        }

        setupToolbar()
        setupBottomNav()
        setupDrawer()
        setupNavController()
        if (isLoggedIn) getLocationPermission()
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
                R.id.authorProfileFragment -> CurrentFragmentType.AUTHOR_PROFILE
                R.id.notificationFragment -> CurrentFragmentType.NOTIFICATION
                R.id.planFragment -> CurrentFragmentType.PLAN
                R.id.groupMessageFragment -> CurrentFragmentType.GROUP_MESSAGE
                R.id.checkOrShoppingListFragment -> CurrentFragmentType.CHECK_OR_SHOPPING_LIST
                R.id.locateFragment -> CurrentFragmentType.COMPANION_LOCATE
                R.id.paymentFragment -> CurrentFragmentType.PAYMENT
                R.id.paymentDetailFragment -> CurrentFragmentType.PAYMENT_DETAIL
                R.id.friendsFragment -> CurrentFragmentType.FRIENDS
                R.id.settingFragment -> CurrentFragmentType.SETTING
                R.id.signInFragment -> CurrentFragmentType.SIGN_IN
                R.id.commentFragment -> CurrentFragmentType.COMMENT
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

        }

        // Observe current drawer toggle to set the navigation icon and behavior
        viewModel.currentDrawerToggleType.observe(this) { type ->
            actionBarDrawerToggle?.isDrawerIndicatorEnabled = type.indicatorEnabled
            supportActionBar?.setDisplayHomeAsUpEnabled(!type.indicatorEnabled)

            when (type) {
                DrawerToggleType.BACK -> {
                    binding.toolbar.setNavigationIcon(R.drawable.toolbar_back)
                }
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

        viewModel.userLocation.observe(this) {
            it?.let {
                viewModel.updateUserLocation(it)
                viewModel.onUpdateUserLocation()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        bindService()
    }

    private fun bindService() {
        val serviceIntent = Intent(this, UserLocateService::class.java)
        if (isLoggedIn) bindService(serviceIntent, userLocateServiceConnection, BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isAccessAppFirstTime.value != true) {
            registerLocationReceiver()
            viewModel.resetBroadcastStatus()
        }
    }

    private fun registerLocationReceiver() {
        if (isLoggedIn && viewModel.isBroadcastRegistered.value != true) {
            LocalBroadcastManager.getInstance(this).registerReceiver(
                howYoBroadcastReceiver,
                IntentFilter(
                    UserLocateService.ACTION_HOW_YO_LOCATION_BROADCAST
                )
            )

            viewModel.setBroadcastRegistered()
        }
    }

    override fun onPause() {
        if (isLoggedIn) {
            LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(howYoBroadcastReceiver)
            viewModel.resetBroadcastStatus()
        }

        super.onPause()
    }

    override fun onStop() {
        if (userLocateServiceBound && isLoggedIn) {
            unbindService(userLocateServiceConnection)
            userLocateServiceBound = false
        }

        super.onStop()
    }

    override fun onBackPressed() {
        viewModel.resetSharedToolbarTitle()
        viewModel.resetToolbar()

        super.onBackPressed()
    }

    // These function about getting location should be in a dependent class
    private fun getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true

            checkGPSState()
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setMessage(getString(R.string.request_location_permission_message))
                .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_PERMISSION
                    )
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> requestLocationPermission() }
                .show()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        locationPermissionGranted = true
                        checkGPSState()
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
                .setTitle(getString(R.string.check_gps_title))
                .setMessage(getString(R.string.check_gps_message))
                .setPositiveButton(
                    getString(R.string.navigate_to_open_setting)
                ) { _, _ ->
                    startActivityForResult(
                        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_GPS
                    )
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        } else {
            bindService()
            registerLocationReceiver()
        }
    }

    private inner class HowYoBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                UserLocateService.EXTRA_LOCATION
            )

            if (location != null) {
                viewModel.setUserLocation(location)
            }
        }
    }
}
