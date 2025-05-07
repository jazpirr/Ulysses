package cit.edu.ulysses.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cit.edu.ulysses.data.Note
import cit.edu.ulysses.databinding.DialogUpdateNoteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdateNoteDialogFragment(
    private val noteId: String,
    private val onNoteUpdated: () -> Unit
) : DialogFragment() {

    private var _binding: DialogUpdateNoteBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogUpdateNoteBinding.inflate(LayoutInflater.from(context))

        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString().trim()
            val newContent = binding.updateContentEditText.text.toString().trim()

            if (newTitle.isEmpty() || newContent.isEmpty()) {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedNote = mapOf(
                "title" to newTitle,
                "content" to newContent
            )
            userId?.let { uid ->
                db.collection("users")
                    .document(uid)
                    .collection("notes")
                    .document(noteId)
                    .update(updatedNote)
                    .addOnSuccessListener {
                        onNoteUpdated()
                        dismiss()
                    }
                    .addOnFailureListener { e ->
                        Log.e("UpdateNoteDialog", "Failed to update note", e)
                        Toast.makeText(requireContext(), "Failed to update note: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.btnOK.setOnClickListener {
            dismiss()
        }
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            (resources.displayMetrics.heightPixels * 0.85).toInt()
        )

        Log.d("UpdateNoteDialog", "Fetching note: $noteId for user: $userId")

        userId?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("notes")
                .document(noteId)
                .get()
                .addOnSuccessListener { doc ->
                    val note = doc.toObject(Note::class.java)
                    if (note != null) {
                        binding.updateTitleEditText.setText(note.title)
                        binding.updateContentEditText.setText(note.content)
                    } else {
                        Log.e("UpdateNoteDialog", "Note is null or not owned by user.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("UpdateNoteDialog", "Failed to fetch note", e)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}