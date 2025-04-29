package com.example.al_quran

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.al_quran.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.tasks.Task

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        const val RC_SIGN_IN = 100
        const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Button click listener
        binding.btnLogin.setOnClickListener {
            signIn()
        }

        // Optional: Auto-login if already signed in
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            goToMain(account)
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.result
            goToMain(account)
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in failed: ${e.localizedMessage}")
            Toast.makeText(this, "Login gagal: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToMain(account: GoogleSignInAccount?) {
        val intent = Intent(this, SurahListActivity::class.java).apply {
            putExtra("name", account?.displayName)
            putExtra("email", account?.email)
        }
        startActivity(intent)
        finish()
    }
}
