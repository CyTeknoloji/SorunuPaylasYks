package com.caneryildirim.sorunupaylasyks.viewModel

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import com.caneryildirim.sorunupaylasyks.R
import com.caneryildirim.sorunupaylasyks.view.DersActivity
import com.caneryildirim.sorunupaylasyks.view.KayitFragmentDirections
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class KayitViewModel:ViewModel() {
    var auth= Firebase.auth
    var db= Firebase.firestore
    val loginLoading= MutableLiveData<Boolean>(false)
    val userNames=MutableLiveData<List<String>>()
    val userNameList=ArrayList<String>()


    private lateinit var pickGalleryLauncher: ActivityResultLauncher<Intent>

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
        val auth= Firebase.auth
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                val user = auth.currentUser
                updateUI(user,context,activity)
            }.addOnFailureListener {
                updateUI(null,context,activity)
                Toast.makeText(context,it.localizedMessage, Toast.LENGTH_LONG).show()
            }
    }



    fun getDataUsername(){
        db.collection("Usernames").get().addOnSuccessListener {
            if (it!=null){
                if (!it.isEmpty){
                    val documents=it.documents
                    userNameList.clear()
                    for (document in documents){
                        val username=document.get("username") as String
                        userNameList.add(username)
                    }
                    userNames.value=userNameList
                }
            }
        }
    }

    fun kayitOl(context:Context, view: View, email:String, password:String, userDisplayName:String){
        loginLoading.value=true
        auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
            val user=auth.currentUser
            val profileUpdates = userProfileChangeRequest {
                displayName = userDisplayName
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
                        }

                    }
                }
            }

        }.addOnFailureListener {
            Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
            loginLoading.value=false
        }
    }

    private fun updateUI(user: FirebaseUser?,context: Context,activity: Activity) {
        if (!userNameList.contains(user!!.displayName)){
            val db= Firebase.firestore
            val userNameData= hashMapOf<String,Any>()
            userNameData.put("username",user.displayName!!)
            userNameData.put("userUid",user.uid)
            userNameData.put("userEmail",user.email!!)
            userNameData.put("userPhotoUrl",user.photoUrl!!)
            db.collection("Usernames").add(userNameData).addOnSuccessListener {
                val intent=Intent(context, DersActivity::class.java)
                activity.startActivity(intent)
                activity.finish()


            }.addOnFailureListener {
                Toast.makeText(context,it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }else{
            val intent=Intent(context, DersActivity::class.java)
            activity.startActivity(intent)
            activity.finish()

        }
    }

}