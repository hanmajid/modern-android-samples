package com.hanmajid.android.material.listcontainertransform

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hanmajid.android.material.listcontainertransform.databinding.ItemPokemonBinding

/**
 * [Pokemon] list adapter.
 */
class PokemonAdapter(
    private val onClickCard: (imageView: View, pokemon: Pokemon) -> Unit,
) : ListAdapter<Pokemon, PokemonAdapter.PokemonViewHolder>(PokemonComparator) {

    companion object {
        object PokemonComparator : DiffUtil.ItemCallback<Pokemon>() {
            override fun areItemsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Pokemon, newItem: Pokemon): Boolean {
                return oldItem == newItem
            }
        }

    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(getItem(position), onClickCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        return PokemonViewHolder.create(parent)
    }

    /**
     * [Pokemon] list ViewHolder class.
     */
    class PokemonViewHolder(private val binding: ItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            pokemon: Pokemon?,
            onClickCard: (imageView: View, pokemon: Pokemon) -> Unit,
        ) {
            pokemon?.apply {
                binding.pokemon = this
                binding.cardContainer.setOnClickListener {
                    onClickCard(binding.cardImage, this)
                }
                ViewCompat.setTransitionName(binding.cardImage, "image-$id")
            }
        }

        companion object {
            fun create(parent: ViewGroup): PokemonViewHolder {
                val binding = ItemPokemonBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false,
                )
                return PokemonViewHolder(binding)
            }
        }
    }
}