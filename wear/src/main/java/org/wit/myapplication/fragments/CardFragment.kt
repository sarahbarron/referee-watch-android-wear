package org.wit.myapplication.fragments

import android.app.Activity
import android.content.Intent
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
import kotlinx.android.synthetic.main.fragment_cards.view.*
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.*
import java.lang.Exception
import java.util.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


@Suppress("DEPRECATION")
class CardFragment : Fragment() {

    lateinit var app: MainApp
    lateinit var root: View
    private val model: LiveDataViewModel by activityViewModels()

    private val REQUEST_CODE_SPEECH_INPUT = 100

    var member = MemberModel()
    var card = CardModel()
    var teamA: TeamModel? = null
    var teamB: TeamModel? = null
    var yellowCard = false
    var redCard = false
    var blackCard = false
    var cardColor = ""
    var arrayOfNotes = arrayListOf<String>()
    var note: String = ""

    lateinit var teamAbtn: ToggleButton
    lateinit var teamBbtn: ToggleButton
    lateinit var yellowBtn: ToggleButton
    lateinit var redBtn: ToggleButton
    lateinit var blackBtn: ToggleButton

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        app = activity?.application as MainApp
        root = inflater.inflate(R.layout.fragment_cards, container, false)

        teamButtonListener(root)
        cardButtonListener(root)
        saveCardListener(root)
        voiceNoteListener(root)

        return root
    }


    fun teamButtonListener(view: View) {
        teamAbtn = view.card_team1
        teamBbtn = view.card_team2
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

    fun cardButtonListener(view: View) {
        yellowBtn = view.card_yellow
        redBtn = view.card_red
        blackBtn = view.card_black
        // Toogle Button Listening for a Card Color to be selected
        yellowBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                redBtn.isChecked = false
                blackBtn.isChecked = false
                yellowCard = true
                cardColor="yellow"
            } else {
                yellowCard = false
            }
        }

        redBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                yellowBtn.isChecked = false
                blackBtn.isChecked = false
                redCard = true
                cardColor="red"
            } else {
                redCard = false
            }
        }

        blackBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                redBtn.isChecked = false
                yellowBtn.isChecked = false
                blackCard = true
                cardColor="black"
            } else {
                blackCard = false
            }
        }
    }

    fun voiceNoteListener(view:View){
        view.card_voice_note_btn.setOnClickListener{
            speak();
        }

    }

    private fun speak() {
        val mIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        mIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        mIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to leave\na note")
        try{
            startActivityForResult(mIntent, REQUEST_CODE_SPEECH_INPUT)
        }catch (e:Exception){
            Log.w(TAG, "Error saving voice note: $e")

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode === REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK){
            val spokenText:String? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let{ results ->
                results!![0]
            }
            Log.i(TAG, "Spoken Text $spokenText")
            arrayOfNotes.add(spokenText!!)
            Log.i(TAG, "Spoken Text Array $arrayOfNotes")

        }
    }

    fun saveCardListener(view: View){
        val db = FirebaseFirestore.getInstance()
        var team = ""
        var teamDocRef: DocumentReference? = null
        var memberDocRef:DocumentReference? = null
        var jerseyInput = 0
        var onField = false

        view.saveCardBtn.setOnClickListener{
            try{
                // get the integer input of for the jersey number
                val jerseynum = root.card_player_number_input.text.toString()
                val textNote = root.card_note_input.text
                if (jerseynum != "") {
                    jerseyInput = convertJerseyNumToInt(jerseynum)
                }

                // if user doesn't select a team prompt for selection
                if (teamA == null && teamB == null) {
                    Log.i(TAG, "Select A Team")
                    Toast.makeText(context, "Select A Team", Toast.LENGTH_LONG).show()
                }

                // if user doesn't select a card prompt for selection
                else if (!yellowCard && !redCard && !blackCard)
                {
                    Log.i(TAG, "Select A Card Type")
                    Toast.makeText(context, "Select Yellow\nRed or Black", Toast.LENGTH_LONG).show()
                }
                else if(jerseynum === "" || jerseyInput === 0 ){
                    Log.i(TAG, "Select A Card Type")
                    Toast.makeText(context, "Input Players\nJersey Number", Toast.LENGTH_LONG).show()
                }
                // If a user enters a jersey number check if the player is on the field for the team
                else if (jerseyInput > 0) {
                    if (teamA != null) {
                        onField = app.firebasestore.isPlayerOnTheField("teamA", jerseyInput)

                        if (onField) {
                            member = app.firebasestore.findMemberByJerseyNum("teamA", jerseyInput)!!
                            memberDocRef = db.collection("Member").document(member.id!!)
                            Log.i(
                                "ScoreFragment",
                                "Number inputted: ${jerseyInput}Member: ${member.lastName} ${member.lastName} ${member.id}"
                            )
                        } else Toast.makeText(
                            context,
                            "Player $jerseyInput \nNot On The Field",
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (teamB != null) {
                        onField = app.firebasestore.isPlayerOnTheField("teamB", jerseyInput)
                        if (onField) {
                            member = app.firebasestore.findMemberByJerseyNum("teamB", jerseyInput)!!
                            memberDocRef = db.collection("Member").document(member.id!!)
                            Log.i(
                                "ScoreFragment",
                                "Number inputted: ${jerseyInput}: Member: ${member.firstName} ${member.lastName} ${member.id}"
                            )
                        }
                        else Toast.makeText(
                            context,
                            "Player $jerseyInput \nNot On The Field",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                // assign the team document to either teamA or teamB
                if (teamA != null) {
                    team = "teamA"
                    teamDocRef = db.collection("Team").document(model.teamA.value!!.id.toString())
                } else if (teamB != null) {
                    team = "teamB"
                    teamDocRef = db.collection("Team").document(model.teamB.value!!.id.toString())
                }

                // Add text notes to the card notes

                if(textNote.isNotEmpty()){
                    if(note.length === 0){
                        note = "$textNote"
                    }
                    else {
                        note = "$note. $textNote"
                    }
                }

                // Add voice notes to the card notes
                if(arrayOfNotes.size>0){
                    for(n in arrayOfNotes){
                        if(note.length===0)
                        {
                            Log.i(TAG, "This is the first voice Note")

                            note = n
                        }
                        else {
                            Log.i(TAG, "More voice Notes")
                            note = "$note. $n"
                        }
                    }
                }

                // if the team, card color and player have been inputted save the card
                if(onField && (teamA !=null || teamB != null) &&(yellowCard || redCard || blackCard)) {
                    card.game = db.collection("Game").document(app.firebasestore.game.id!!)
                    card.member = memberDocRef
                    card.team = teamDocRef
                    card.color = cardColor
                    card.timestamp = Date()
                    card.note = note


                    Log.i(TAG, "Card: $card")
                    doAsync {
                        val cardSaved = app.firebasestore.saveCard(card)
                        uiThread {
                            if (cardSaved) {
                                view.card_team1.isChecked = false
                                view.card_team2.isChecked = false
                                view.card_yellow.isChecked = false
                                view.card_red.isChecked = false
                                view.card_black.isChecked = false
                                view.card_note_input.setText("")
                                view.card_player_number_input.setText("")
                                arrayOfNotes.clear()
                                member = MemberModel()
                                card = CardModel()
                                teamA= null
                                teamB= null
                                yellowCard = false
                                redCard = false
                                blackCard = false
                                cardColor = ""
                                note= ""
                                team = ""
                                teamDocRef = null
                                memberDocRef = null
                                jerseyInput = 0
                                onField = false
                                Toast.makeText(context, "Card Saved", Toast.LENGTH_LONG).show()
                            }
                            else
                                Toast.makeText(
                                    context,
                                    "Error Saving\nTry Again",
                                    Toast.LENGTH_LONG
                                ).show()
                        }
                    }
                }
            }catch(e:Exception){
                Log.w(TAG, "Error saving Card: $e")
                Toast.makeText(
                    context,
                    "Error Saving\nTry Again",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    fun convertJerseyNumToInt(jerseyNum: String): Int{
        return Integer.parseInt(jerseyNum)
    }

    companion object {
        private const val TAG = "Firestore"
    }
}

