package com.example.noteapp.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentDashboardBinding
import com.example.noteapp.ui.createNote.CreateNoteFragment
import com.example.noteapp.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val binding by viewBinding(FragmentDashboardBinding::bind)

    private lateinit var noteAdapterNote: AdapterNotes

    private lateinit var navController: NavController

    private val viewModel by viewModels<DashboardViewModel>()

    companion object{
        @JvmStatic
        fun newInstance() =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRv()
        collectNotes()

            // Initialize NavController
            val navHostFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.onSearchQueryChanged(p0.toString())
                return true
            }
        })
    }

    private val onClicked = object : AdapterNotes.OnItemClickListener {
        override fun onClicked(notesId: Int) {

//            val fragment: Fragment
//            val bundle = Bundle()
//            fragment = CreateNoteFragment.newInstance()
//            fragment.arguments = bundle
            val args = Bundle().apply {
                putInt(getString(R.string.noteID), notesId)
            }
            navController.navigate(R.id.createNoteFragment, args)

        }
    }

    private fun collectNotes() = viewLifecycleOwner.lifecycleScope.launch {
        viewModel.notes.collectLatest {
            noteAdapterNote.submitList(it)
        }
    }

    private fun setUpRv() = binding.apply {
        noteAdapterNote = AdapterNotes().apply { setOnClickListener(onClicked) }
        rvUser.setHasFixedSize(true)
        rvUser.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rvUser.adapter = noteAdapterNote
    }
}