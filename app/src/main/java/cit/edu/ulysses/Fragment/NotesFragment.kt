package cit.edu.ulysses.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cit.edu.ulysses.adapters.NotesAdapter
import cit.edu.ulysses.helpers.NotesHelper
import cit.edu.ulysses.R
import cit.edu.ulysses.activities.SettingsActivity
import cit.edu.ulysses.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: NotesHelper
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotesBinding.inflate(inflater, container, false)

        binding.btnSetting.setOnClickListener{
            startActivity(
                Intent(requireContext(), SettingsActivity:: class.java)
            )
        }
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = NotesHelper(requireContext())
        notesAdapter = NotesAdapter(db.getAllNotes(), requireContext(), parentFragmentManager)
        binding.notesRecyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.notesRecyclerView.adapter = notesAdapter

//        binding.addButton.setOnClickListener {
//            val dialog = AddNoteFragment {
//                notesAdapter.refreshData(db.getAllNotes())
//            }
//            dialog.show(parentFragmentManager, "AddNoteDialog")
//        }
    }
    fun refreshNotes() {
        notesAdapter.refreshData(db.getAllNotes())
    }
    override fun onResume() {
        super.onResume()
        notesAdapter.refreshData(db.getAllNotes())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}