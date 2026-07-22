package com.example.microsprouts.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.microsprouts.ui.theme.SlateText
import com.example.microsprouts.util.GoogleDriveBackupManager
import com.example.microsprouts.data.database.MicroSproutsDatabase
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.launch
import android.widget.Toast
import android.accounts.Account
import java.security.MessageDigest
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onSeedData: () -> Unit,
    onClearData: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToLicenses: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var signedInEmail by remember { mutableStateOf<String?>(null) }
    
    // Hardcoded web client ID, for real app this should be in res/values/strings.xml
    // Note: this must match your OAuth 2.0 Web Client ID in Google Cloud Console
    val serverClientId = "957375127740-k8nhe385blmf0b5pu1i426gk43onejm4.apps.googleusercontent.com" 

    val credentialManager = remember { CredentialManager.create(context) }
    val backupManager = remember { GoogleDriveBackupManager(context, MicroSproutsDatabase.getDatabase(context)) }

    fun getGoogleCredential(): GoogleAccountCredential? {
        val email = signedInEmail ?: return null
        return GoogleAccountCredential.usingOAuth2(context, listOf(DriveScopes.DRIVE_APPDATA)).apply {
            selectedAccount = Account(email, "com.google")
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                title = {
                    Text(text = "Settings", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Developer & Test Controls",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Button(
                onClick = onSeedData,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(text = "Seed Sample Data")
            }

            OutlinedButton(
                onClick = onClearData,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                Text(text = "Clear All Tasks")
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Google Account Backup Section
            Text(
                text = "Google Account Backup",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            if (signedInEmail != null) {
                Text(
                    text = "Signed in as: $signedInEmail",
                    fontSize = 13.sp,
                    color = SlateText
                )

                Button(
                    onClick = { 
                        coroutineScope.launch {
                            val credential = getGoogleCredential()
                            if (credential != null) {
                                try {
                                    val backupFile = backupManager.createLocalBackupFile()
                                    val success = backupManager.uploadBackupToDrive(credential, backupFile)
                                    if (success) {
                                        Toast.makeText(context, "Backup successful!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Backup failed.", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "Backup failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Backup Now to Google Drive")
                }

                OutlinedButton(
                    onClick = { 
                        coroutineScope.launch {
                            val credential = getGoogleCredential()
                            if (credential != null) {
                                try {
                                    val backupFile = backupManager.downloadBackupFromDrive(credential)
                                    if (backupFile != null) {
                                        backupManager.restoreFromBackupFile(backupFile)
                                        Toast.makeText(context, "Restore successful!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Restore failed or no backup found.", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(context, "Restore failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Restore from Google Drive")
                }
                
                OutlinedButton(
                    onClick = { signedInEmail = null },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Sign Out")
                }
            } else {
                Button(
                    onClick = { 
                        coroutineScope.launch {
                            try {
                                val rawNonce = UUID.randomUUID().toString()
                                val bytes = rawNonce.toByteArray()
                                val md = MessageDigest.getInstance("SHA-256")
                                val digest = md.digest(bytes)
                                val hashedNonce = digest.joinToString("") { "%02x".format(it) }
                                
                                val googleIdOption = GetGoogleIdOption.Builder()
                                    .setFilterByAuthorizedAccounts(false)
                                    .setServerClientId(serverClientId)
                                    .setNonce(hashedNonce)
                                    .build()

                                val request = GetCredentialRequest.Builder()
                                    .addCredentialOption(googleIdOption)
                                    .build()

                                val result = credentialManager.getCredential(
                                    request = request,
                                    context = context,
                                )

                                val credential = result.credential
                                if (credential is androidx.credentials.CustomCredential &&
                                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                ) {
                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                    signedInEmail = googleIdTokenCredential.id
                                } else {
                                    Toast.makeText(context, "Unexpected credential type", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: GetCredentialException) {
                                e.printStackTrace()
                                Toast.makeText(context, "Sign-in failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Sign-in error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Sign in with Google")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // About Section
            Text(
                text = "About",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // App Version Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "App Version", fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
                Text(text = "v0.1.0", fontSize = 15.sp, color = SlateText, fontWeight = FontWeight.Medium)
            }

            // Terms of Service In-App Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToTerms() }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Terms of Service", fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = SlateText)
            }

            // License In-App Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToLicenses() }
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "License", fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = SlateText)
            }
        }
    }
}