package com.victorbarrozo.whatsappfirebase.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.squareup.picasso.Picasso
import com.victorbarrozo.whatsappfirebase.databinding.ItemContatosBinding
import com.victorbarrozo.whatsappfirebase.model.Usuario
import org.checkerframework.checker.units.qual.C


class ContatosAdapter(
    private val onClick: ( Usuario ) -> Unit
): Adapter<ContatosAdapter.ContatosViewHolder>() {
    private var listaContatos = emptyList<Usuario>()
    fun adcionarLista(lista: List<Usuario>) {
        listaContatos = lista
        notifyDataSetChanged()
    }

    inner class ContatosViewHolder(
        private val binding: ItemContatosBinding
    ): ViewHolder( binding.root ){

        fun bind( usuario: Usuario ){
            binding.textPerfilContatos.text = usuario.nome
            Log.i("picasso", "${usuario.foto}")
            Picasso.get().load(usuario.foto).into(binding.imgPerfilContatos)

            binding.clContato.setOnClickListener {
                onClick( usuario )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatosViewHolder {
        val inflater = LayoutInflater.from( parent.context )
        val itemView = ItemContatosBinding.inflate( inflater, parent, false )
        return ContatosViewHolder( itemView )
    }
    override fun onBindViewHolder(holder: ContatosViewHolder, position: Int) {
        val usuario = listaContatos[position]
        holder.bind( usuario )
    }
    override fun getItemCount(): Int {
        return listaContatos.size
    }

}