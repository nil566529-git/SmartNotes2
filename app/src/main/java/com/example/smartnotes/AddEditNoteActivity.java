package com.example.smartnotes;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddEditNoteActivity extends AppCompatActivity {

    private NoteDbHelper dbHelper;
    private EditText etTitle, etContent;
    private Button btnDelete;

    private int noteId = -1; // -1 means we are adding a new note
    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        dbHelper = new NoteDbHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        Button btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        if (getIntent().hasExtra(MainActivity.EXTRA_NOTE_ID)) {
            isEditing = true;
            noteId = getIntent().getIntExtra(MainActivity.EXTRA_NOTE_ID, -1);
            loadNote();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit Note");
            }
            btnDelete.setVisibility(android.view.View.VISIBLE);
        } else {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("New Note");
            }
            btnDelete.setVisibility(android.view.View.GONE);
        }

        btnSave.setOnClickListener(v -> saveNote());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadNote() {
        Note note = dbHelper.getNote(noteId);
        if (note != null) {
            etTitle.setText(note.getTitle());
            etContent.setText(note.getContent());
        }
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (TextUtils.isEmpty(title) && TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Note is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(title)) {
            title = "(No title)";
        }

        if (isEditing) {
            dbHelper.updateNote(noteId, title, content);
        } else {
            dbHelper.insertNote(title, content);
        }

        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deleteNote(noteId);
                    Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}