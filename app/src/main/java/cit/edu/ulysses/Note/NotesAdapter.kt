package cit.edu.ulysses.Note

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import cit.edu.ulysses.Fragment.UpdateNoteDialogFragment
import cit.edu.ulysses.Fragment.ViewnoteDialogFragment
import cit.edu.ulysses.R

class NotesAdapter(
    private var notes: List<Note>,
    private val context: Context,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val db: NotesHelper = NotesHelper(context)

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = notes[position]
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content

        holder.itemView.setOnClickListener{
            val dialog = ViewnoteDialogFragment(note.id){
                refreshData(db.getAllNotes())
            }
            dialog.show(fragmentManager, "ViewNoteDialog")
        }

        holder.updateButton.setOnClickListener {
            val dialog = UpdateNoteDialogFragment(note.id) {
                refreshData(db.getAllNotes())
            }
            dialog.show(fragmentManager, "UpdateNoteDialog")
        }

        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context).apply{
                setTitle("Delete Note")
                setMessage("Are you sure you want ot delete this note?")
                setPositiveButton("Yes"){_, _ ->
                    db.deleteNote(note.id)
                    refreshData(db.getAllNotes())
                    Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
                }
                setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
                show()
            }
        }
    }

    fun refreshData(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }
}