package cit.edu.ulysses.Fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.DialogFragment
import cit.edu.ulysses.Note.NotesHelper
import cit.edu.ulysses.R
import cit.edu.ulysses.databinding.DialogViewNoteBinding

class ViewnoteDialogFragment(
    private val noteId: Int,
    private val onNoteUpdated: () -> Unit
) : DialogFragment() {

    private var _binding: DialogViewNoteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogViewNoteBinding.inflate(LayoutInflater.from(context))
        val db = NotesHelper(requireContext())

        val note = db.getNoteByID(noteId)
        binding.viewDescription.setText(note.content)
        binding.title.setText(note.title)

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