package cit.edu.ulysses.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cit.edu.ulysses.data.Note

import cit.edu.ulysses.databinding.DialogAddNoteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddNoteFragment(private val onNoteAdded: () -> Unit) : DialogFragment() {

    private var _binding: DialogAddNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddNoteBinding.inflate(LayoutInflater.from(context))



        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            val content = binding.contentEditText.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            db.insertNote(Note(0, title, content))
//            onNoteAdded()
//            dismiss()

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId == null) {
                Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val note = hashMapOf(
                "title" to title,
                "content" to content,
                "timestamp" to System.currentTimeMillis()
            )

            FirebaseFirestore.getInstance()
                .collection("users").document(userId)
                .collection("notes")
                .add(note)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Note saved to Firestore", Toast.LENGTH_SHORT).show()
                    onNoteAdded()
                    dismiss()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to save note: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnOK.setOnClickListener{
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
