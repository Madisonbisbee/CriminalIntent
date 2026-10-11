package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView
    private lateinit var addCrimeButton: Button
    private lateinit var crimeAdapter: CrimeAdapter

    private var crimes: List<Crime> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_crime_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crimeRecyclerView = view.findViewById(R.id.crimeRecyclerView)
        addCrimeButton = view.findViewById(R.id.addCrimeButton)

        crimeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        crimeAdapter = CrimeAdapter(crimes) { crime ->
            openCrimeDetail(crime)
        }

        crimeRecyclerView.adapter = crimeAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            CrimeRepository.get().getCrimes().collect { crimeList ->
                crimes = crimeList
                crimeAdapter = CrimeAdapter(crimes) { crime ->
                    openCrimeDetail(crime)
                }
                crimeRecyclerView.adapter = crimeAdapter
            }
        }

        addCrimeButton.setOnClickListener {
            val crime = Crime()

            viewLifecycleOwner.lifecycleScope.launch {
                CrimeRepository.get().addCrime(crime)
                openCrimeDetail(crime)
            }
        }
    }

    private fun openCrimeDetail(crime: Crime) {
        val fragment = CrimeDetailFragment.newInstance(crime.id.toString())

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}