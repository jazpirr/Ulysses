package cit.edu.ulysses.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cit.edu.ulysses.Note.Note
import cit.edu.ulysses.Note.NotesHelper
import cit.edu.ulysses.databinding.DialogAddNoteBinding

class AddNoteFragment(private val onNoteAdded: () -> Unit) : DialogFragment() {

    private var _binding: DialogAddNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddNoteBinding.inflate(LayoutInflater.from(context))

        val db = NotesHelper(requireContext())

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

            db.insertNote(Note(0, title, content))
            onNoteAdded()
            dismiss()
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
