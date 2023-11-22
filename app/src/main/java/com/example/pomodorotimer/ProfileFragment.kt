package com.example.pomodorotimer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileFragment : Fragment() {
    private val sessionViewModel: SessionViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var sessionAdapter: SessionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewProfile)
        recyclerView.layoutManager = LinearLayoutManager(context)

        sessionAdapter = SessionAdapter()
        recyclerView.adapter = sessionAdapter

        context?.let { sessionViewModel.loadExistingSessions(it) }

        sessionViewModel.sessionList.observe(viewLifecycleOwner) { sessions ->
            Log.d("ProfileFragment", "Observed sessions: ${sessions.size}")
            if (sessions.isNotEmpty()) {
                sessionAdapter.submitList(sessions)
            }
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        Log.d("ProfileFragment", "onStart")
    }
    override fun onResume() {
        super.onResume()
        Log.d("ProfileFragment", "onResume")
    }


}
