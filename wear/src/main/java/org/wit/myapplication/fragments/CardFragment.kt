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
import kotlinx.android.synthetic.main.fragment_cards.view.*
import kotlinx.android.synthetic.main.fragment_injury.view.*
import kotlinx.android.synthetic.main.fragment_score.view.*
import kotlinx.android.synthetic.main.fragment_stopwatch.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.wit.myapplication.R
import org.wit.myapplication.main.MainApp
import org.wit.myapplication.models.*
import java.lang.Exception
import java.util.*
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

        val teamAName = app.firebasestore.teamA.name
        val teamBName = app.firebasestore.teamB.name
        if(teamAName !=null && teamBName!=null) {
            root.card_team1.text = teamAName
            root.card_team2.text = teamBName
            root.card_team1.textOn = teamAName
            root.card_team1.textOff = teamAName
            root.card_team2.textOn = teamBName
            root.card_team2.textOff = teamBName
        }
        teamButtonListener(root)
        cardButtonListener(root)
        saveCardListener(root)
        voiceNoteListener(root)

        return root
    }


    //    Listen for a click on a team
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

    //    Listen for a click on a card color
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
                cardColor = "yellow"
            } else {
                yellowCard = false
            }
        }

        redBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                yellowBtn.isChecked = false
                blackBtn.isChecked = false
                redCard = true
                cardColor = "red"
            } else {
                redCard = false
            }
        }

        blackBtn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                redBtn.isChecked = false
                yellowBtn.isChecked = false
                blackCard = true
                cardColor = "black"
            } else {
                blackCard = false
            }
        }
    }

    //    Listen for a click on the microphone to speak a note
    fun voiceNoteListener(view: View) {
        view.card_voice_note_btn.setOnClickListener {
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

    //    Listener for a click on the save card button
    fun saveCardListener(view: View) {

        val db = FirebaseFirestore.getInstance()
        var teamDocRef: DocumentReference? = null
        var memberDocRef: DocumentReference? = null
        var jerseyInput = 0
        var onField = false
        var team = ""

        view.saveCardBtn.setOnClickListener {

            try {
                // get the integer input of for the jersey number
                val jerseynum = root.card_player_number_input.text.toString()
                val textNote = root.card_note_input.text

                if (jerseynum != "") {
                    jerseyInput = convertJerseyNumToInt(jerseynum)
                    Log.i(TAG, "Jersey Num: $jerseynum")
                }
                // assign the team document to either teamA or teamB
                if (teamA != null) {
                    team = "teamA"
                    teamDocRef = db.collection("Team").document(app.firebasestore.game.teamA!!.id)
                    Log.i(TAG, "Team: $team, Team Doc: $teamDocRef")

                } else if (teamB != null) {
                    team = "teamB"
                    teamDocRef = db.collection("Team").document(app.firebasestore.game.teamB!!.id)
                    Log.i(TAG, "Team: $team, Team Doc: $teamDocRef")

                }

                // Add text notes to the card notes
                if (textNote.isNotEmpty()) {
                    getTextNotes(textNote.toString())
                    Log.i(TAG, "Text Note: $note")

                }

                // Add voice notes to the card notes
                if (arrayOfNotes.size > 0) {
                    getVoiceNotes()
                    Log.i(TAG, "Voice Note: $note")

                }

                // if user doesn't select a team prompt for selection
                if (teamA == null && teamB == null) {
                    Log.i(TAG, "Select A Team")
                    Toast.makeText(context, "Select A Team", Toast.LENGTH_LONG).show()
                }
                // if user doesn't select a card prompt for selection
                else if (!yellowCard && !redCard && !blackCard) {
                    Log.i(TAG, "Select A Card Type")
                    Toast.makeText(context, "Select Yellow\nRed or Black", Toast.LENGTH_LONG).show()
                } else if (jerseynum == "" || jerseyInput === 0) {
                    Log.i(TAG, "Input a player number")
                    Toast.makeText(context, "Input Players\nJersey Number", Toast.LENGTH_LONG)
                        .show()
                }
                // If a user enters a jersey number check if the player is on the field for the team
                else if (jerseyInput > 0 && jerseynum != "" && team != null) {
                    memberDocRef = getMember(team!!, jerseyInput)
                    onField = app.firebasestore.isPlayerOnTheField(team, jerseyInput)
                    if (!onField) {
                        Toast.makeText(
                            context,
                            "Player $jerseyInput\nIs NOT on the Field",
                            Toast.LENGTH_LONG
                        )
                            .show()
                    }
                    Log.i(TAG, "Member Doc Ref: $memberDocRef : onField: $onField")
                }


                // if the team, card color and player have been inputted save the card
                if (onField && memberDocRef != null && (teamA != null || teamB != null) && (yellowCard || redCard || blackCard)) {
                    card.game = db.collection("Game").document(app.firebasestore.game.id!!)
                    card.member = memberDocRef
                    card.team = teamDocRef
                    card.color = cardColor
                    card.timestamp = Date()
                    card.note = note


                    Log.i(TAG, "Card: $card")
                    doAsync {
                        try {
                            val cardSaved = app.firebasestore.saveCard(card)

                            uiThread {
                                if (cardSaved) {
                                    Toast.makeText(context, "Card Saved", Toast.LENGTH_LONG).show()

                                    // update the number of cards issued to a team
                                    if (card.color == "red") {
                                        Toast.makeText(
                                            context,
                                            "CARD SAVED\n\nSend Player Off",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        app.firebasestore.setPlayerOffField(team, jerseyInput )
                                    }
                                    /* Check if a player is already on a yellow or black card
                                        if the player is the player must be sent off.
                                        alert the referee
                                    * */
                                    else if (app.firebasestore.checkIfPlayerIsOnASecondCard(
                                            memberDocRef!!)) {
                                        view.setBackgroundColor(Color.RED)
                                        Toast.makeText(
                                            context,
                                            "CARD SAVED\n\n 2nd Card = RED\n\nSend Player Off",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        setBackgroundBlack(view)
                                        app.firebasestore.setPlayerOffField(team, jerseyInput )
                                    }
                                    /* If it is a black card check if the player can be
                                    substituted or not and alert the referee
                                    * */
                                    else if (card.color === "black") {
                                        val allowedASub =
                                            app.firebasestore.isTeamAllowedFootballBlackCardSubs(
                                                team!!
                                            )
                                        if (allowedASub)
                                            Toast.makeText(
                                                context,
                                                "BLACK CARD SAVED\n\nReplacement ALLOWED",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        else
                                            Toast.makeText(
                                                context,
                                                "CARD SAVED\n\nMax Subs Used\n\nReplacement NOT Allowed",
                                                Toast.LENGTH_LONG
                                            )
                                                .show()
                                        app.firebasestore.setPlayerOffField(team, jerseyInput )

                                    }
                                    resetCardToBlank()
                                } else
                                    Toast.makeText(
                                        context,
                                        "Error Saving\nTry Again",
                                        Toast.LENGTH_LONG
                                    ).show()
                            }
                        } catch (e: Exception) {
                            Log.w(TAG, "Exception saving card : $e")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error saving Card Exception: $e")
                Toast.makeText(
                    context,
                    "Error Saving\nTry Again",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    //  Concat all voice notes from the array to a String
    fun getVoiceNotes() {
        for (n in arrayOfNotes) {
            if (note.length === 0) {
                note = n
            } else {
                note = "$note. $n"
            }
        }
    }

    //    Contcat text note to the String note
    fun getTextNotes(textNote: String) {
        if (note.length === 0) {
            note = "$textNote"
        } else {
            note = "$note. $textNote"
        }
    }


    //        Return the member document reference  from teams heet A
    fun getMember(team: String, jerseyInput: Int): DocumentReference? {

        val db = FirebaseFirestore.getInstance()
        var memberDocRef: DocumentReference?
        member = app.firebasestore.findMemberByJerseyNum(team, jerseyInput)!!
        memberDocRef = db.collection("Member").document(member.id!!)
        Log.i(
            TAG,
            "GetMember: Number inputted: ${jerseyInput}Member: ${member.firstName} ${member.lastName} ${member.id}"
        )
        return memberDocRef
    }


    // Reset the values back to their original states
    fun resetCardToBlank() {
        view?.card_team1!!.isChecked = false
        view?.card_team2!!.isChecked = false
        view?.card_yellow!!.isChecked = false
        view?.card_red!!.isChecked = false
        view?.card_black!!.isChecked = false
        view?.card_note_input!!.setText("")
        view?.card_player_number_input!!.setText("")
        arrayOfNotes.clear()
        member = MemberModel()
        card = CardModel()
        teamA = null
        teamB = null
        yellowCard = false
        redCard = false
        blackCard = false
        cardColor = ""
        note = ""
    }

    // Convert the Jersey Number input String value to an integer
    fun convertJerseyNumToInt(jerseyNum: String): Int {
        return Integer.parseInt(jerseyNum)
    }

    /* After a Red card has been given and the background has been set to red
        use a coroutine to delay for 5 seconds before changing the color back to black
    * */
    fun setBackgroundBlack(view: View) {
        GlobalScope.launch {
            delay(5000)
            view.setBackgroundColor(Color.BLACK)
        }
    }

    companion object {
        private const val TAG = "Card Fragment"
    }
}

