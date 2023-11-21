package com.example.pomodorotimer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProfileFragment : Fragment() {
    private val sessionViewModel: SessionViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var sessionAdapter: SessionAdapter // You need to create this adapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewProfile) // Replace with your RecyclerView ID
        recyclerView.layoutManager = LinearLayoutManager(context)

        sessionAdapter = SessionAdapter() // Create an adapter for your sessions
        recyclerView.adapter = sessionAdapter

        sessionViewModel.sessionList.observe(viewLifecycleOwner) { sessions ->
            // Update the RecyclerView with the list of sessions
            sessionAdapter.submitList(sessions)
        }

        // Set up any onClick listeners related to sessions in ProfileFragment
        // ...
    }
}
