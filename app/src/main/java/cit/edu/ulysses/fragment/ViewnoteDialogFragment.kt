package cit.edu.ulysses.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cit.edu.ulysses.databinding.DialogViewNoteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import cit.edu.ulysses.data.Note

class ViewnoteDialogFragment(
    private val noteId: String,
    private val onNoteUpdated: () -> Unit
) : DialogFragment() {

    private var _binding: DialogViewNoteBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogViewNoteBinding.inflate(LayoutInflater.from(context))

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

        Log.d("ViewNoteDialog", "Fetching note: $noteId for user: $userId")
        userId?.let { uid ->
            db.collection("users")
                .document(uid)
                .collection("notes")
                .document(noteId)
                .get()
                .addOnSuccessListener { doc ->
                    val note = doc.toObject(Note::class.java)
                    if (note != null) {
                        binding.viewDescription.setText(note.content)
                        binding.title.setText(note.title)
                    } else {
                        android.util.Log.e("ViewNoteDialog", "Note is null or not owned by user.")
                    }
                }
                .addOnFailureListener {
                    android.util.Log.e("ViewNoteDialog", "Failed to fetch note", it)
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
