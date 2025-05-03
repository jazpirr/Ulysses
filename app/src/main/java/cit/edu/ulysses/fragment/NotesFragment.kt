package cit.edu.ulysses.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cit.edu.ulysses.R
import cit.edu.ulysses.activities.SettingsActivity
import cit.edu.ulysses.adapters.NotesAdapter
import cit.edu.ulysses.data.Note
import cit.edu.ulysses.databinding.FragmentNotesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var notesAdapter: NotesAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var notesListener: ListenerRegistration? = null
    private val notes = mutableListOf<Note>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)

        binding.btnSetting.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesAdapter = NotesAdapter(notes, requireContext(), parentFragmentManager)
        binding.notesRecyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.notesRecyclerView.adapter = notesAdapter

        fetchNotesFromFirestore()

        binding.addButton.setOnClickListener {
            val dialog = AddNoteFragment {
                fetchNotesFromFirestore()
            }
            dialog.show(parentFragmentManager, "AddNoteDialog")
        }
    }

    private fun fetchNotesFromFirestore() {
        val uid = auth.currentUser?.uid ?: return

        notesListener?.remove()
        notesListener = firestore.collection("users")
            .document(uid)
            .collection("notes")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                notes.clear()
                for (doc in snapshot.documents) {
                    val note = doc.toObject(Note::class.java)
                    note?.let {
                        it.id = doc.id
                        notes.add(it)
                    }
                }
                notesAdapter.refreshData(notes)
            }
    }

    fun refreshNotes() {
        fetchNotesFromFirestore()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        notesListener?.remove()
        _binding = null
    }
}
