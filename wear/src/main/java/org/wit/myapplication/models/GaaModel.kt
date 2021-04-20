package org.wit.myapplication.models;
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*


@Parcelize
data class ContactDetailsModel(
    @DocumentId
    var id: String? = null,
    var addressLine1: String? = null,
    var addressLine2: String? = null,
    var town: String? = null,
    var county: String? = null,
    var eircode: String? = null,
    var telephone: String? = null,
    var email: String? = null
):Parcelable

@Parcelize
data class ProvinceModel(
    @DocumentId
    var id: String? = null,
    var contactDetails: @RawValue DocumentReference? = null
):Parcelable

@Parcelize
data class CountyModel(
    @DocumentId
    var id: String? = null,
    var province: @RawValue DocumentReference? = null,
    var colors: ArrayList<String>? = null
):Parcelable

@Parcelize
data class ClubModel(
    @DocumentId
    var id: String? = null,
    var name: String? = null,
    var colors: ArrayList<String>? = null,
    var county: @RawValue DocumentReference? = null,
):Parcelable

@Parcelize
data class MembershipTypeModel(
    @DocumentId
    var id: String? = null,
    var description: String? = null
):Parcelable

@Parcelize
data class SportTypeModel(
    @DocumentId
    var id: String? = null
):Parcelable

@Parcelize
data class CompetitionModel(
    @DocumentId
    var id: String? = null,
    var county: @RawValue DocumentReference? = null,
    var grade: @RawValue DocumentReference? = null,
    var isNational: Boolean = false,
    var name: String? = null,
    var province: @RawValue DocumentReference? = null,
    var sportType: @RawValue DocumentReference? = null
):Parcelable

@Parcelize
data class GradeModel(
    @DocumentId
    var id: String? = null,
    var name: String? = null,
    var level: String? = null
):Parcelable

@Parcelize
data class MemberModel(
    @DocumentId
    var id: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    @ServerTimestamp
    var DOB: Date? = null,
    var clubPlayer: Boolean = false,
    var contactDetails: @RawValue DocumentReference? = null,
    var countyPlayer: Boolean = false,
    var firstClub: @RawValue DocumentReference? = null,
    var ownClub: @RawValue DocumentReference? = null,
    @ServerTimestamp
    var membershipDate: Date? = null,
    var membershipType: @RawValue DocumentReference? = null,
    var refereeOfClub: Boolean = false,
    var refereeOfCounty: Boolean = false,
    var registeredDate: Date? = null,
    var secretaryOfClub: Boolean = false,
    var secretaryOfCounty: Boolean = false,
    var secretaryOfProvince: Boolean = false,
    var secretaryOfCouncil: Boolean = false,
    var sportType: @RawValue DocumentReference? = null,
    var teamOfficial: Boolean = false
):Parcelable


@Parcelize
data class GameModel(
    @DocumentId
    var id: String? = null,
    var competition: @RawValue DocumentReference? = null,
    @ServerTimestamp
    var dateTime: Date? = null,
    var linesmen: ArrayList<String>? = null,
    var umpires: ArrayList<String>? = null,
    var referee: @RawValue DocumentReference? = null,
    var substituteReferee: @RawValue DocumentReference? = null,
    var teamA: @RawValue DocumentReference? = null,
    var teamB: @RawValue DocumentReference? = null,
    var teamAGoals: Int? = null,
    var teamBGoals: Int? = null,
    var teamAPoints: Int? = null,
    var teamBPoints: Int? = null,
    var venue: @RawValue DocumentReference? = null,
): Parcelable


@Parcelize
data class TeamModel(
    @DocumentId
    var id: String? = null,
    var club: @RawValue DocumentReference? = null,
    var county: @RawValue DocumentReference? = null,
    var name: String? = null,
    var players: ArrayList<@RawValue DocumentReference>? = null,
    var teamOfficials: ArrayList<@RawValue DocumentReference>? = null,
    var sportType: @RawValue DocumentReference? = null,
    var teamName: String? = null,
): Parcelable


@Parcelize
data class VenueModel(
    @DocumentId
    var id: String? = null,
    var club: @RawValue DocumentReference? = null,
    var contactDetails: @RawValue DocumentReference? = null,
    var county: @RawValue DocumentReference? = null,
    var lat: Double? = null,
    var lng: Double? = null,
    var name: String? = null
):Parcelable

@Parcelize
data class Score(
    @DocumentId
    var id:String?=null,
    var timestamp: Date?=null,
    var goal:Boolean?=null,
    var point: Boolean?=null,
    var member: @RawValue DocumentReference? = null,
    var game: @RawValue DocumentReference? = null
):Parcelable

@Parcelize
data class Card(
    @DocumentId
    var id:String?=null,
    var timestamp: Date?=null,
    var note: String? =null,
    var member: @RawValue DocumentReference? = null,
    var game: @RawValue DocumentReference? = null
):Parcelable


@Parcelize
data class Injury(
    @DocumentId
    var id:String?=null,
    var timestamp: Date?=null,
    var note: String? =null,
    var member: @RawValue DocumentReference? = null,
    var game: @RawValue DocumentReference? = null
):Parcelable

@Parcelize
data class Substitute(
    @DocumentId
    var id:String?=null,
    var timestamp: Date?=null,
    var memberOn:  @RawValue DocumentReference? = null,
    var memberOff: @RawValue DocumentReference? = null,
    var boodsub: Boolean = false,
    var game: @RawValue DocumentReference? = null
):Parcelable
