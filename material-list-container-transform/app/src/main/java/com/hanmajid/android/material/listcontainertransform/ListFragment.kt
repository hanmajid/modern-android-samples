package com.hanmajid.android.material.listcontainertransform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hanmajid.android.material.listcontainertransform.databinding.FragmentListBinding

class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pokemonAdapter = PokemonAdapter { transitioningView, pokemon ->
            findNavController().navigate(
                R.id.action_listFragment_to_detailFragment,
                Bundle().apply {
                    putInt("pokemonId", pokemon.id)
                    putInt("pokemonImage", pokemon.image)
                },
                null,
                FragmentNavigatorExtras(transitioningView to transitioningView.transitionName)
            )
        }
        pokemonAdapter.submitList(pokemonData)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = pokemonAdapter
        }

        /**
         * The below code is required to animate correctly when the user returns from [DetailFragment].
         */
        postponeEnterTransition()
        (requireView().parent as ViewGroup).viewTreeObserver
            .addOnPreDrawListener {
                startPostponedEnterTransition()
                true
            }
    }

    private val pokemonData = listOf(
        Pokemon(
            1,
            "Bulbasaur",
            R.drawable.bulbasaur,
        ),
        Pokemon(
            2,
            "Charmander",
            R.drawable.charmander,
        ),
        Pokemon(
            3,
            "Squirtle",
            R.drawable.squirtle,
        ),
    )
}