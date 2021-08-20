package org.wit.myapplication.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import getVoiceAndTextNote
import kotlinx.android.synthetic.main.fragment_additional_comments.view.*
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.AdditionalCommentsModel
import java.util.*

@Suppress("DEPRECATION")
class AdditionalCommentsFragment: Fragment() {
    lateinit var app: MainApp
    lateinit var root: View

    private val REQUEST_CODE_SPEECH_INPUT_DELAY = 100
    private val REQUEST_CODE_SPEECH_INPUT_LINESMEN = 200
    private val REQUEST_CODE_SPEECH_INPUT_COMMENT = 300

    var additionalComments = AdditionalCommentsModel()
    var matchProgramme = true
    var jerseyNumbered = true
    var pitchMarked = true
    var grassCut = true
    var extraCommentsArray = arrayListOf<String>()
    var delayInStartArray = arrayListOf<String>()
    var linesmenAttireArray = arrayListOf<String>()
    var delayInStartTextNote = ""
    var linesmenAttireTextNote = ""
    var extraCommentsTextNote = ""
    var delayInStart = ""
    var linesmenAttire = ""
    var extraComments = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        app = activity?.application as MainApp
        root = inflater.inflate(R.layout.fragment_additional_comments, container, false)

        voiceNoteDelayListener(root)
        voiceNoteLinesmenListener(root)
        voiceNoteCommentLinstener(root)
        saveCardListener(root)
        return root

    }

    //    Listen for a click on the microphone to speak a note for a delay in start of game
    fun voiceNoteDelayListener(view: View) {
        view.delay_voice_note_btn.setOnClickListener {
            view.setBackgroundColor(Color.BLACK)
            speak(REQUEST_CODE_SPEECH_INPUT_DELAY)
        }
    }

    //    Listen for a click on the microphone to speak a note for linesmen attire
    fun voiceNoteLinesmenListener(view:View) {

        view.linesmen_voice_note_btn.setOnClickListener {
            view.setBackgroundColor(Color.BLACK)
            speak(REQUEST_CODE_SPEECH_INPUT_LINESMEN)
        }
    }

    //    Listen for a click on the microphone to speak a note for additional comments
    fun voiceNoteCommentLinstener(view:View){
        view.comment_voice_note_btn.setOnClickListener {
            view.setBackgroundColor(Color.BLACK)
            speak(REQUEST_CODE_SPEECH_INPUT_COMMENT)
        }
    }

    //    Voice Recognition records voice and converts it to text
    private fun speak(REQUEST_CODE_SPEECH_INPUT: Int) {
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to leave\na note")
        try {
            startActivityForResult(mIntent, REQUEST_CODE_SPEECH_INPUT)
        } catch (e: Exception) {
            Log.w(TAG, "Error saving voice note: $e")

        }
    }

    //    Result from the voice recording converted to text added to the array of notes
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === REQUEST_CODE_SPEECH_INPUT_DELAY && resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results!![0]
                }
            Log.i(TAG, "Spoken Text $spokenText")
            delayInStartArray.add(spokenText!!)
            Log.i(TAG, "Spoken Text Array ${delayInStartArray}")
        }
        else if (requestCode === REQUEST_CODE_SPEECH_INPUT_LINESMEN && resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results!![0]
                }
            Log.i(TAG, "Spoken Text $spokenText")
            linesmenAttireArray.add(spokenText!!)
            Log.i(TAG, "Spoken Text Array ${linesmenAttireArray}")
        }
        else if (requestCode === REQUEST_CODE_SPEECH_INPUT_COMMENT && resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results!![0]
                }
            Log.i(TAG, "Spoken Text $spokenText")
            extraCommentsArray.add(spokenText!!)
            Log.i(TAG, "Spoken Text Array ${extraCommentsArray}")
        }
    }

    //    Listener for a click on the save card button
    fun saveCardListener(view: View) {
        val db = FirebaseFirestore.getInstance()


        view.btnSaveAdditionalComments.setOnClickListener{
            try {
                val gameId = app.firebasestore.game.id;
                matchProgramme = view.programme_checkbox.isChecked
                jerseyNumbered = view.jersey_checkbox.isChecked
                grassCut = view.grass_checkbox.isChecked
                pitchMarked = view.pitch_checkbox.isChecked
                delayInStartTextNote = view.delay_note_input.text.toString()
                linesmenAttireTextNote = view.linesmen_note_input.text.toString()
                extraCommentsTextNote = view.comment_note_input.text.toString()

                if(delayInStartTextNote.isNotEmpty() || delayInStartArray.size>0){
                    delayInStart = getVoiceAndTextNote(delayInStartArray, delayInStartTextNote);
                }
                if(linesmenAttireTextNote.isNotEmpty() || linesmenAttireArray.size>0){
                    linesmenAttire = getVoiceAndTextNote(linesmenAttireArray, linesmenAttireTextNote);
                }
                if(extraCommentsTextNote.isNotEmpty() || extraCommentsArray.size>0){
                    extraComments = getVoiceAndTextNote(extraCommentsArray, extraCommentsTextNote);
                }

                additionalComments.delayInStart = delayInStart
                additionalComments.linesmenAttire = linesmenAttire
                additionalComments.extraComments = extraComments
                additionalComments.matchProgramme = matchProgramme
                additionalComments.jerseyNumbered = jerseyNumbered
                additionalComments.grassCut = grassCut
                additionalComments.pitchMarked = pitchMarked

                val saved = app.firebasestore.saveAdditionalComments(additionalComments)
                var savedMsg =""
                if(saved){
                    savedMsg = "Comments Saved"
                }
                else
                {
                    savedMsg = "Problem with Save"
                }

                Toast.makeText(
                    context, savedMsg,
                    Toast.LENGTH_LONG
                ).show()
            }
            catch (e: Exception) {
                Log.w(TAG, "Error saving Additional Comments Exception: $e")
                Toast.makeText(
                    context,
                    "Error Saving\nTry Again",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }
    companion object {
        private const val TAG = "Additional Comments Fragment"
    }
}