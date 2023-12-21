package com.example.noteapp.ui.notif

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentNotifBinding
import com.example.noteapp.ui.dashboard.DashboardFragment
import com.example.noteapp.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotifFragment : Fragment(R.layout.fragment_notif) {

    private val binding by viewBinding(FragmentNotifBinding::bind)

    private lateinit var notifAdapter: AdapterNotif

    private val viewModel by viewModels<NotifViewModel>()

//    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notif, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeAdapter()
        setUpRv()
        collectNotes()
        // Initialize NavController
//        val navHostFragment = requireActivity().supportFragmentManager
//            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        navController = navHostFragment.navController

    }

    private fun initializeAdapter() {
        notifAdapter = AdapterNotif()
    }

    private fun collectNotes() = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.notif.collectLatest {
            notifAdapter.submitList(it)
        }
    }

    private fun setUpRv() = binding.apply {
            rvNotifUser.setHasFixedSize(true)
            rvNotifUser.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rvNotifUser.adapter = notifAdapter
    }
}