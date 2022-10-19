package com.caneryildirim.sorunupaylasyks.util

import com.google.firebase.Timestamp

data class Cevap(val downloadUrl:String,
                 val selectedAciklama:String,
                 val soruUid:String,
                 val userName:String,
                 val userUid:String,
                 val userUidSoru:String?,
                 val docUuid:String,
                 val dogruCevap:Boolean,
                 val pIdCevap:String?,
                 val userPhotoUrl:String?,
                 val date: Timestamp,
                 val yanlisCevap:Boolean?)
