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
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import convertJerseyNumToInt
import getVoiceAndTextNote
import kotlinx.android.synthetic.main.fragment_injury.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.*
import java.lang.Exception
import java.util.*


@Suppress("DEPRECATION")
class InjuryFragment : Fragment() {
    lateinit var app: MainApp
    lateinit var root: View
    private val model: LiveDataViewModel by activityViewModels()

    private val REQUEST_CODE_SPEECH_INPUT = 100

    var member = MemberModel()
    var injury = InjuryModel()
    var teamA: TeamModel? = null
    var teamB: TeamModel? = null
    var arrayOfNotes = arrayListOf<String>()
    var note: String =""
    lateinit var teamAbtn: ToggleButton
    lateinit var teamBbtn: ToggleButton

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        app = activity?.application as MainApp
        root = inflater.inflate(R.layout.fragment_injury, container, false)

        val teamAName = app.firebasestore.teamA.name
        val teamBName = app.firebasestore.teamB.name
        if(teamAName !=null && teamBName!=null) {
            root.injury_team1.text = teamAName
            root.injury_team2.text = teamBName
            root.injury_team1.textOn = teamAName
            root.injury_team1.textOff = teamAName
            root.injury_team2.textOn = teamBName
            root.injury_team2.textOff = teamBName
        }
        teamButtonListener(root)
        voiceNoteListener(root)
        saveInjuryListener(root)


        return root
    }


    //    Listen for a click on a team
    fun teamButtonListener(view: View) {
        teamAbtn = view.injury_team1
        teamBbtn = view.injury_team2
        // Toogle Button Listening for a Team to be selected
        teamAbtn.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                teamBbtn.isChecked = false
                teamA = model.teamA.value

            } else {
                teamA = null
            }
        }
        teamBbtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                teamAbtn.isChecked = false
                teamB = model.teamB.value

            } else {
                teamB = null
            }
        }
    }

    //    Listen for a click on the microphone to speak a note
    fun voiceNoteListener(view: View) {
        view.injury_voice_note_btn.setOnClickListener {
            view.setBackgroundColor(Color.BLACK)
            speak();
        }

    }

    //    Voice Recognition records voice and converts it to text
    private fun speak() {
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
        if (requestCode === REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {
            val spokenText: String? =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                    results!![0]
                }
            Log.i(TAG, "Spoken Text $spokenText")
            arrayOfNotes.add(spokenText!!)
            Log.i(TAG, "Spoken Text Array $arrayOfNotes")

        }
    }

    fun saveInjuryListener(view: View){
        val db = FirebaseFirestore.getInstance()
        var teamDocRef: DocumentReference? = null
        var memberDocRef: DocumentReference? = null
        var jerseyInput = 0
        var team =""

        view.saveInjuryBtn.setOnClickListener {
            try {
                val jerseynum = view.injury_player_number_input.text.toString()
                if(jerseynum.isNotEmpty())
                    jerseyInput = convertJerseyNumToInt(jerseynum)

                val textNote = view.injury_note_input.text.toString()

                // if user doesn't select a team prompt for selection
                if (teamA == null && teamB == null) {
                    Log.i(TAG, "Select A Team")
                    Toast.makeText(context, "Select A Team", Toast.LENGTH_LONG).show()
                } else if (jerseynum == "" || jerseyInput === 0) {
                    Log.i(TAG, "Jersey text: $jerseynum, num input: $jerseyInput")
                    Log.i(TAG, "Input a player number")
                    Toast.makeText(context, "Input Players\nJersey Number", Toast.LENGTH_LONG)
                        .show()
                } else {

                    // assign the team document to either teamA or teamB
                    if (teamA != null) {
                        team = "teamA"
                        teamDocRef =
                            db.collection("Team").document(app.firebasestore.game.teamA!!.id)

                    } else if (teamB != null) {
                        team = "teamB"
                        teamDocRef =
                            db.collection("Team").document(app.firebasestore.game.teamB!!.id)

                    }


                    if (jerseyInput > 0 && jerseynum != "" && team != null) {
                        memberDocRef = getMember(team!!, jerseyInput)
                        Log.i(TAG, "Member Doc Ref: $memberDocRef")
                        if (memberDocRef === null) {
                            Toast.makeText(
                                context,
                                "No player matching\nJersey Number $jerseyInput\non the teamsheet",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    if (textNote.isNotEmpty() || arrayOfNotes.size > 0)
                        note = getVoiceAndTextNote(arrayOfNotes, textNote)
                }

                if (memberDocRef != null && (teamA != null || teamB != null)) {
                    injury.game = db.collection("Game").document(app.firebasestore.game.id!!)
                    injury.member = memberDocRef
                    injury.team = teamDocRef
                    injury.note = note
                    injury.timestamp = Date()

                    doAsync {
                        try {
                            val injurySaved = app.firebasestore.saveInjury(injury)
                            uiThread {
                                if (injurySaved)
                                    Toast.makeText(context, "Injury Saved", Toast.LENGTH_LONG)
                                        .show()

                            }

                        } catch (e: Exception) {
                            Log.w(TAG, "Exception while saving Injury: $e")
                            resetInjuryToBlank(view)
                        }
                    }

                }

            }catch (e:Exception){
                Log.w(TAG, "Exception saving Injury: $e")
            }
        }
    }

    //        Return the member document reference  from teams heet A
    fun getMember(team: String, jerseyInput: Int): DocumentReference? {

        val db = FirebaseFirestore.getInstance()
        var memberDocRef: DocumentReference?
        member = app.firebasestore.findMemberByJerseyNum(team, jerseyInput)!!
        if(member.id !=null) {
            memberDocRef = db.collection("Member").document(member.id!!)
            return memberDocRef
        }
        Log.i(TAG,
            "GetMember: Number inputted: ${jerseyInput}Member: ${member.firstName} ${member.lastName} ${member.id}"
        )
       return null
    }

    fun resetInjuryToBlank(view: View){
        member = MemberModel()
        injury = InjuryModel()
        teamA = null
        teamB= null
        arrayOfNotes = arrayListOf<String>()
        note=""
        view?.injury_team1!!.isChecked = false
        view?.injury_team2!!.isChecked = false
        view?.injury_note_input!!.setText("")
    }


    companion object {
        private const val TAG = "Injury Fragment"
    }

}

