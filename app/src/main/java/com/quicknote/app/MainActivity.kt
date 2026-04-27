package com.quicknote.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var db: NoteDatabase
    private lateinit var adapter: NotesAdapter
    private lateinit var tvEmpty: View
    private lateinit var btnQuickCapture: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = NoteDatabase(this)

        recyclerView = findViewById(R.id.recyclerView)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnQuickCapture = findViewById(R.id.btnQuickCapture)

        recyclerView.layoutManager = LinearLayoutManager(this)

        btnQuickCapture.setOnClickListener {
            startActivityForResult(
                Intent(this, QuickCaptureActivity::class.java),
                REQUEST_CAPTURE
            )
        }

        loadNotes()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAPTURE) {
            loadNotes()
        }
    }

    override fun onResume() {
        super.onResume()
        loadNotes()
    }

    private fun loadNotes() {
        val notes = db.getAllNotes()
        if (notes.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvEmpty.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            tvEmpty.visibility = View.GONE
            adapter = NotesAdapter(notes) { note ->
                AlertDialog.Builder(this)
                    .setMessage("Apagar esta nota?")
                    .setPositiveButton("Apagar") { _, _ ->
                        db.deleteNote(note.id)
                        loadNotes()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            recyclerView.adapter = adapter
        }
    }

    companion object {
        private const val REQUEST_CAPTURE = 1001
    }
}

class NotesAdapter(
    private val notes: List<Note>,
    private val onLongClick: (Note) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_NOTE = 1
    }

    // Build list with day headers
    private val items: List<Any> = buildList {
        var lastDay = ""
        notes.forEach { note ->
            val day = note.dayHeader()
            if (day != lastDay) {
                add(day)
                lastDay = day
            }
            add(note)
        }
    }

    override fun getItemViewType(position: Int) =
        if (items[position] is String) TYPE_HEADER else TYPE_NOTE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_HEADER) {
            HeaderViewHolder(inflater.inflate(R.layout.item_header, parent, false))
        } else {
            NoteViewHolder(inflater.inflate(R.layout.item_note, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> holder.bind(items[position] as String)
            is NoteViewHolder -> holder.bind(items[position] as Note, onLongClick)
        }
    }

    override fun getItemCount() = items.size

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvHeader: TextView = view.findViewById(R.id.tvHeader)
        fun bind(header: String) { tvHeader.text = header }
    }

    class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvContent: TextView = view.findViewById(R.id.tvContent)
        private val tvTime: TextView = view.findViewById(R.id.tvTime)

        fun bind(note: Note, onLongClick: (Note) -> Unit) {
            tvContent.text = note.content
            tvTime.text = note.formattedDate()
            itemView.setOnLongClickListener {
                onLongClick(note)
                true
            }
        }
    }
}
