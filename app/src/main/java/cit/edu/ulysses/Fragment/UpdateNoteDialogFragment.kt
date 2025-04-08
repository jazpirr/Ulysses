package cit.edu.ulysses.Fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cit.edu.ulysses.Note.Note
import cit.edu.ulysses.Note.NotesHelper
import cit.edu.ulysses.R
import cit.edu.ulysses.databinding.DialogUpdateNoteBinding

class UpdateNoteDialogFragment(
    private val noteId: Int,
    private val onNoteUpdated: () -> Unit
) : DialogFragment() {

    private var _binding: DialogUpdateNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogUpdateNoteBinding.inflate(LayoutInflater.from(context))
        val db = NotesHelper(requireContext())

        val note = db.getNoteByID(noteId)
        binding.updateTitleEditText.setText(note.title)
        binding.updateContentEditText.setText(note.content)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setCancelable(true)
            .create()


        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString().trim()
            val newContent = binding.updateContentEditText.text.toString().trim()

            if (newTitle.isEmpty() || newContent.isEmpty()) {
                Toast.makeText(requireContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.updateNote(Note(noteId, newTitle, newContent))
            onNoteUpdated()
            dismiss()
        }

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .create()
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            (resources.displayMetrics.heightPixels * 0.8).toInt()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
