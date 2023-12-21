package com.example.noteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation
import com.example.noteapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

//    private var db by lazy { RealtimeDatabase.instance()}

    companion object {
        // you can put any unique id here, but because I am using Navigation Component I prefer to put it as
        // the fragment id.
         val DASHBOARD_ITEM = R.id.dashboardFragment
         val NOTIF_ITEM = R.id.notifFragment
         val CREATENOTE_ITEM = R.id.createNoteFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(binding.root)
            initNavHost()
            setUpBottomNavigation()

        }
    }

    private fun ActivityMainBinding.setUpBottomNavigation() {
        val bottomNavigationItems = mutableListOf(
            CurvedBottomNavigation.Model(DASHBOARD_ITEM, getString(R.string.dashBoard), R.drawable.ic_outline_dashboard),
            CurvedBottomNavigation.Model(NOTIF_ITEM, getString(R.string.notif), R.drawable.ic_outline_notifications),
            CurvedBottomNavigation.Model(CREATENOTE_ITEM, getString(R.string.createNote), R.drawable.ic_outline_add_box),
        )
        bottomNavigation.apply {
            bottomNavigationItems.forEach { add(it) }
            setOnClickMenuListener {
                if (it.id == CREATENOTE_ITEM) {
//                    val createNoteFragment = CreateNoteFragment.newInstance()
                    val args = Bundle().apply {
                    }
                    navController.navigate(it.id, args)
                } else {
                    navController.navigate(it.id)
                }
            }
            show(DASHBOARD_ITEM)
            // optional
            setupNavController(navController)
        }
    }

    // if you need your backstack of your items always back to home please override this method
    private fun handleBackPressed() {
        if (navController.currentDestination?.id == DASHBOARD_ITEM) {
            super.onBackPressed()
        } else {
            when (navController.currentDestination?.id) {
                NOTIF_ITEM, CREATENOTE_ITEM -> {
                    navController.popBackStack(R.id.dashboardFragment, false)
                }
                else -> {
                    navController.navigateUp()
                }
            }
        }
    }

    private fun initNavHost() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }
}