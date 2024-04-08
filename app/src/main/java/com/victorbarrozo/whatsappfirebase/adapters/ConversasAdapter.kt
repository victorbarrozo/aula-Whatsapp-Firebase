package com.victorbarrozo.whatsappfirebase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.victorbarrozo.whatsappfirebase.databinding.ItemMensagemDestinatarioBinding
import com.victorbarrozo.whatsappfirebase.databinding.ItemMensagemRemetenteBinding
import com.victorbarrozo.whatsappfirebase.model.Mensagem
import com.victorbarrozo.whatsappfirebase.utils.Constantes
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.newCoroutineContext

class ConversasAdapter: Adapter< ViewHolder >(){

    private var listaMensagens = emptyList< Mensagem >()
    fun adicionarLista ( lista: List< Mensagem > ) {
        listaMensagens = lista
        notifyDataSetChanged()
    }
    class MensagensRemententeViewHolder(
        private val binding: ItemMensagemRemetenteBinding
    ): ViewHolder( binding.root ){

        fun bind(mensagem: Mensagem){
            binding.textRemetente.text = mensagem.mensagem

        }
        companion object {
            fun inflarLayoutRemetente(parent: ViewGroup): MensagensRemententeViewHolder {
                val inflater = LayoutInflater.from( parent.context )
                val itemView = ItemMensagemRemetenteBinding.inflate(
                    inflater, parent, false
                )
                return MensagensRemententeViewHolder( itemView )
            }
        }

    }

    class MensagensDestinatarioViewHolder(
        private val binding: ItemMensagemDestinatarioBinding
    ): ViewHolder( binding.root ){
        fun bind(mensagem: Mensagem){
            binding.textDestintario.text = mensagem.mensagem
        }
        companion object {
            fun  inflarLayoutDestinatario( parent: ViewGroup ): MensagensDestinatarioViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = ItemMensagemDestinatarioBinding.inflate(
                    inflater, parent, false
                )
                return MensagensDestinatarioViewHolder( itemView )
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        val mensagem = listaMensagens[position]
        val idUsiarioLogado = FirebaseAuth.getInstance().currentUser?.uid.toString()
       return if ( idUsiarioLogado == mensagem.idUsuario ) {
            Constantes.TIPO_REMETENTE
        }else{
            Constantes.TIPO_DESTINATARIO
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //2 tipos de visualizacao esqueda branco e direita verde
        if ( viewType == Constantes.TIPO_REMETENTE ) {
            return MensagensRemententeViewHolder.inflarLayoutRemetente(parent)
        }
        return  MensagensDestinatarioViewHolder.inflarLayoutDestinatario( parent )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mensagem = listaMensagens[position]
        when ( holder ) {
            is MensagensRemententeViewHolder-> holder.bind( mensagem )
            is MensagensDestinatarioViewHolder-> holder.bind( mensagem )
        }



        /*val mensagensRemetenteViewHolder = holder as MensagensRemententeViewHolder
        mensagensRemetenteViewHolder.bind()*/

    }
    override fun getItemCount(): Int {
        return listaMensagens.size
    }
}