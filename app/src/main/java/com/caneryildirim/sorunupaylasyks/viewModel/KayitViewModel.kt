package com.caneryildirim.sorunupaylasyks.viewModel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.avatarfirst.avatargenlib.AvatarConstants
import com.avatarfirst.avatargenlib.AvatarGenerator
import com.caneryildirim.sorunupaylasyks.view.KayitFragmentDirections
import com.caneryildirim.sorunupaylasyks.view.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.onesignal.OneSignal
import io.grpc.internal.DnsNameResolver
import java.io.ByteArrayOutputStream

class KayitViewModel:ViewModel() {
    var auth= Firebase.auth
    var db= Firebase.firestore
    var storage=Firebase.storage
    val loginLoading= MutableLiveData<Boolean>(false)
    val userNames=MutableLiveData<List<String>>()
    val userNameList=ArrayList<String>()
    var userUidList=ArrayList<String>()
    var errorInternet=MutableLiveData<Boolean>(false)


    private lateinit var googleSignInClient:GoogleSignInClient
    val idGoogle ="249327647056-7opfpjgj3k5mt2v2uaqck7q3tvvhos8u.apps.googleusercontent.com"

    companion object {
        val TAG = "GoogleActivity"
        val RC_SIGN_IN = 9001

    }


    fun initGoogle(activity: Activity){
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(idGoogle)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(activity, gso)



    }


    fun kayitGoogle():Intent{
        val signInIntent = googleSignInClient.signInIntent
            return signInIntent
    }

    fun firebaseAuthWithGoogle(idToken: String,context: Context,activity: Activity) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                val user = auth.currentUser
                updateUI(user,context,activity)
            }.addOnFailureListener {
                updateUI(null,context,activity)
                println(it.localizedMessage)
            }


    }



    fun getDataUsername(){
        db.collection("Usernames").get().addOnSuccessListener {
            errorInternet.value=false
            if (it!=null){
                if (!it.isEmpty){
                    val documents=it.documents
                    userNameList.clear()
                    for (document in documents){
                        val username=document.get("username") as String
                        val userUid=document.get("userUid") as String
                        userUidList.add(userUid)
                        userNameList.add(username)
                    }
                    userNames.value=userNameList
                }
            }
        }.addOnFailureListener {
            println(it.localizedMessage)
        }
    }



    fun kayitOl(context:Context, view: View, email:String, password:String, userDisplayName:String){
        loginLoading.value=true

        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
            val user=auth.currentUser
            val bitmap=AvatarGenerator.avatarImage(
                context,
                200,
                AvatarConstants.RECTANGLE,
                userDisplayName
            ).bitmap
            val storageRef=storage.reference.child("usersPhoto/${auth.currentUser!!.uid}.jpg")
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            storageRef.putBytes(data).addOnSuccessListener {
                val downloadRef=storage.reference.child("usersPhoto/${auth.currentUser!!.uid}.jpg")
                downloadRef.downloadUrl.addOnSuccessListener {
                    val photoUrl=it.toString()

                    val profileUpdates = userProfileChangeRequest {
                        displayName = userDisplayName
                        photoUri= Uri.parse(photoUrl)
                    }
                    user!!.updateProfile(profileUpdates).addOnCompleteListener {
                        if (it.isSuccessful){
                            //updateUI(user)
                            user.sendEmailVerification().addOnSuccessListener {
                                val userNameData= hashMapOf<String,Any>()
                                userNameData.put("username",userDisplayName)
                                userNameData.put("userUid",auth.currentUser!!.uid)
                                userNameData.put("userEmail", auth.currentUser!!.email!!)
                                userNameData.put("userPhotoUrl",auth.currentUser!!.photoUrl!!)

                                db.collection("Usernames").add(userNameData).addOnSuccessListener {
                                    Toast.makeText(context,"Epostanıza aktivasyon kodu gönderildi.",Toast.LENGTH_SHORT).show()
                                    auth.signOut()
                                    val action=KayitFragmentDirections.actionKayitFragmentToGirisFragment()
                                    Navigation.findNavController(view).navigate(action)
                                    loginLoading.value=false
                                }.addOnFailureListener {
                                    Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                                    loginLoading.value=false
                                }

                            }
                        }
                    }

                }.addOnFailureListener {
                    Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                    loginLoading.value=false
                }
            }.addOnFailureListener {
                Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                loginLoading.value=false
            }



        }.addOnFailureListener {
            Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
            loginLoading.value=false
        }
    }

    private fun updateUI(user: FirebaseUser?,context: Context,activity: Activity) {
        if (user!=null){
            if (!userUidList.contains(user.uid)){
                val bitmap=AvatarGenerator.avatarImage(
                    context,
                    200,
                    AvatarConstants.RECTANGLE,
                    user.displayName.toString()
                ).bitmap
                val storageRef=storage.reference.child("usersPhoto/${auth.currentUser!!.uid}.jpg")
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                storageRef.putBytes(data).addOnSuccessListener {
                    val downloadRef=storage.reference.child("usersPhoto/${auth.currentUser!!.uid}.jpg")
                    downloadRef.downloadUrl.addOnSuccessListener {
                        val photoUrl=it.toString()
                        val profileUpdates = userProfileChangeRequest {
                            photoUri= Uri.parse(photoUrl)
                        }
                        user.updateProfile(profileUpdates).addOnSuccessListener {
                            val db= Firebase.firestore
                            val userNameData= hashMapOf<String,Any>()
                            userNameData.put("username",user.displayName!!)
                            userNameData.put("userUid",user.uid)
                            userNameData.put("userEmail",user.email!!)
                            userNameData.put("userPhotoUrl",user.photoUrl!!)
                            db.collection("Usernames").add(userNameData).addOnSuccessListener {
                                val intent=Intent(context, MainActivity::class.java)
                                activity.startActivity(intent)
                                activity.finish()

                            }.addOnFailureListener {
                                Toast.makeText(context,it.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                        }

                    }
                }.addOnFailureListener {
                    Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                }


            }else{
                val intent=Intent(context, MainActivity::class.java)
                activity.startActivity(intent)
                activity.finish()
            }
            }else{

            Toast.makeText(context,"Beklenmedik bir hata oluştu!",Toast.LENGTH_LONG).show()
            }


    }

}