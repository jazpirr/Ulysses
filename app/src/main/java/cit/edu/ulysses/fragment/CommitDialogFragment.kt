package cit.edu.ulysses.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import cit.edu.ulysses.R

class CommitDialogFragment(
    private val onCommitConfirmed: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_commit_setting, null)

        val explanationText = dialogView.findViewById<TextView>(R.id.explanationText)
        explanationText.text = getString(R.string.commitWarning).trimIndent()

        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val dialog = builder.create()

        dialogView.findViewById<Button>(R.id.cancelCommitButton).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.commitButton).setOnClickListener {
            onCommitConfirmed()
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}
