package com.code.treeMarketplaceApp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TreeSpeciesAdapter(
    private var treeList: MutableList<TreeSpecies> // Change List to MutableList
) : RecyclerView.Adapter<TreeSpeciesAdapter.TreeViewHolder>() {

    inner class TreeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val treeImage: ImageView = view.findViewById(R.id.treeImage)
        val treeName: TextView = view.findViewById(R.id.treeName)
        val treeDescription: TextView = view.findViewById(R.id.treeDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tree_species, parent, false)
        return TreeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreeViewHolder, position: Int) {
        val tree = treeList[position]
        holder.treeName.text = tree.name
        holder.treeDescription.text = tree.description

        // Load the image using Glide library
        Glide.with(holder.itemView.context)
            .load(tree.imageUrl)
            .into(holder.treeImage)
    }

    override fun getItemCount(): Int = treeList.size

    // Add the updateData function
    fun updateData(newTreeList: List<TreeSpecies>) {
        treeList.clear()          // Clear the existing data
        treeList.addAll(newTreeList) // Add new data
        notifyDataSetChanged()      // Notify adapter of data change
    }
}
