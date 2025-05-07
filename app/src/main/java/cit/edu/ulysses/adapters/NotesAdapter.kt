package cit.edu.ulysses.adapters

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
import cit.edu.ulysses.data.Note
import cit.edu.ulysses.R
import cit.edu.ulysses.fragment.UpdateNoteDialogFragment
import cit.edu.ulysses.fragment.ViewnoteDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NotesAdapter(
    private var notes: List<Note>,
    private val context: Context,
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.gifImage)
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

        holder.itemView.setOnClickListener {
            val dialog = ViewnoteDialogFragment(note.id!!) {

            }
            dialog.show(fragmentManager, "ViewNoteDialog")
        }

        holder.updateButton.setOnClickListener {
            val dialog = UpdateNoteDialogFragment(note.id!!) {

            }
            dialog.show(fragmentManager, "UpdateNoteDialog")
        }

        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(context).apply {
                setTitle("Delete Note")
                setMessage("Are you sure you want to delete this note?")
                setPositiveButton("Yes") { _, _ ->
                    firestore.collection("users")
                        .document(auth.currentUser?.uid ?: return@setPositiveButton)
                        .collection("notes")
                        .document(note.id!!)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show()
                            val updatedNotes = notes.filter { it.id != note.id }
                            refreshData(updatedNotes)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to delete note: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                setNegativeButton("No") { dialog, _ ->
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
