package com.learning.b38firebasedemo.realtimedb.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.learning.b38firebasedemo.R
import com.learning.b38firebasedemo.databinding.ActivityNotesBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNotesBinding
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var ref: DatabaseReference
    private var items: MutableList<Notes> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        //initialise database ref
        ref = FirebaseDatabase.getInstance().getReference("NOTES")
        binding.btnAddNote.setOnClickListener {
            saveNote()
        }
        fetchNotes()
    }

    private fun saveNote() {
        val title = binding.edtTitle.text.toString()
        val desc = binding.edtDesc.text.toString()

        val sdf = SimpleDateFormat("dd/MMM/yyy")
        val date = sdf.format(Date())

        val note = Notes(title, desc, date)
        val noteId = ref.push().key.toString()
        ref.child(noteId).setValue(note).addOnCompleteListener {
            binding.edtTitle.text?.clear()
            binding.edtDesc.text?.clear()
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            fetchNotes()
        }
    }

    private fun fetchNotes() {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    items.clear()
                    for (h in snapshot.children) {
                        val notes = h.getValue(Notes::class.java)
                        items.add(notes!!)
                    }

                    notesAdapter = NotesAdapter((items as ArrayList<Notes>))
                    binding.recyclerNote.apply {
                        layoutManager = LinearLayoutManager(this@NotesActivity)
                        adapter = notesAdapter
                    }
                }
            }
        })
    }
}