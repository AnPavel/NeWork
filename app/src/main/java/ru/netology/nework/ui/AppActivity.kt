package ru.netology.nework.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import ru.netology.nework.BuildConfig
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailabilityLight
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.ActivityAppBinding
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.UserViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    @Inject
    lateinit var appAuth: AppAuth

    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging

    @Inject
    lateinit var googleApiAvailability: GoogleApiAvailabilityLight

    private val authViewModel : AuthViewModel by viewModels()

    private val userViewModel : UserViewModel by viewModels()

    private lateinit var binding: ActivityAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nomVersionApp = System.getProperty("os.version")
        Log.d("MyAppLog", "AppActivity * OS: $nomVersionApp")

        MapKitFactory.setApiKey(BuildConfig.API_KEY)
        Log.d("MyAppLog", "AppActivity * MapKitFactory: ${BuildConfig.API_KEY}")

        binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navigation: BottomNavigationView = binding.navigation
        navigation.itemIconTintList = null

        val navController = findNavController(R.id.navFragmentActivityApp)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("MyAppLog", "AppActivity * navController.addOnDestinationChangedListener: $destination")
            when (destination.id) {
                R.id.navPostsFragment,
                R.id.navEventsFragment,
                R.id.navUsersFragment,
                R.id.navProfileFragment,
                -> {
                    navigation.visibility = View.VISIBLE
                }
                else -> {
                    navigation.visibility = View.GONE
                }
            }
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navPostsFragment,
                R.id.navEventsFragment,
                R.id.navUsersFragment,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navigation.setupWithNavController(navController)

        val itemIcon = navigation.menu.findItem(R.id.navProfileFragment)

        authViewModel.data.observe(this) { auth ->
            invalidateOptionsMenu()
            if (auth.id == 0L) {
                itemIcon.setIcon(R.drawable.ic_baseline_user_24)
            } else {
                userViewModel.getUserById(auth.id)
            }

            navigation.menu.findItem(R.id.navProfileFragment).setOnMenuItemClickListener {
                if (!authViewModel.authorized) {
                    findNavController(R.id.navFragmentActivityApp)
                        .navigate(R.id.navSignInFragment)
                    true
                } else {
                    userViewModel.getUserById(auth.id)
                    val bundle = Bundle().apply {
                        userViewModel.user.value?.id?.let { it ->
                            putLong("id", it)
                        }
                        putString("avatar", userViewModel.user.value?.avatar)
                        putString("name", userViewModel.user.value?.name)
                    }

                    findNavController(R.id.navFragmentActivityApp).popBackStack()

                    findNavController(R.id.navFragmentActivityApp)
                        .navigate(R.id.navProfileFragment, bundle)
                    true
                }
            }
        }

        userViewModel.user.observe(this) {
            Glide.with(this)
                .asBitmap()
                .load("${it.avatar}")
                .transform(CircleCrop())
                .into(object : CustomTarget<Bitmap>() {

                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?,
                    ) {
                        itemIcon.icon = BitmapDrawable(resources, resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                }
                )
        }
        //проверка на доступность Google сервисов
        checkGoogleApiAvailability()
        //разрешение на выполнение уведомлений
        requestNotificationsPermission()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.let {
            it.setGroupVisible(R.id.unauthentificated, !authViewModel.authorized)
            it.setGroupVisible(R.id.authentificated, authViewModel.authorized)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController(R.id.navFragmentActivityApp).navigateUp()
            }
            R.id.sign_in -> {
                findNavController(R.id.navFragmentActivityApp)
                    .navigate(R.id.navSignInFragment)
                true
            }
            R.id.sign_up -> {
                findNavController(R.id.navFragmentActivityApp)
                    .navigate(R.id.navSignUpFragment)
                true
            }
            R.id.sign_out -> {
                appAuth.removeAuth()
                findNavController(R.id.navFragmentActivityApp)
                    .navigate(R.id.navPostsFragment)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    private fun checkGoogleApiAvailability() {
        with(googleApiAvailability) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorString(code)
                return
            }
            Log.d("MyAppLog", "AppActivity * checkGoogleApiAvailability (!success) code: $code")
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        firebaseMessaging.token.addOnSuccessListener {
            println(it)
        }
    }

    private fun requestNotificationsPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Log.d(
                "MyAppLog",
                "AppActiviity * version_SDK_INT: ${Build.VERSION.SDK_INT} (notifications not allowed)"
            )
            return
        }

        val permission = Manifest.permission.POST_NOTIFICATIONS

        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            Log.d(
                "MyAppLog",
                "AppActiviity * requestNotificationsPermission: ${checkSelfPermission(permission)}"
            )
            return
        }

        requestPermissions(arrayOf(permission), 1)
    }

}
