package com.caneryildirim.sorunupaylasyks.util

import com.google.firebase.Timestamp
import java.io.Serializable

data class Soru(val downloadUrl:String,
                val email:String,
                val selectedAciklama:String,
                val selectedDers:String,
                val selectedKonu:String,
                val userUid:String,
                val userDisplayName:String,
                val userPhotoUrl: String,
                val docRef: String,
                val pId:String?,
                val dogruCevap:Boolean?,
                val dogruCevapString:String?,
                val dogruCevapImage:String?,
                val date: Timestamp
):Serializable {
}