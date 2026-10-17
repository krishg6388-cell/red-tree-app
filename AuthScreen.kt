package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfileRepository
import com.example.ui.components.FloatingLeavesAtmosphere
import com.example.ui.components.GlowButton
import com.example.ui.components.RedTreeBrandingHeader
import com.example.ui.components.SegmentedNeonTabs
import com.example.ui.theme.BlackBase
import com.example.ui.theme.BlackSurface
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonOrange
import com.example.ui.theme.NeonRed
import com.example.ui.theme.NeonYellow
import com.example.ui.theme.RedOrangeGradient
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

object AuthSessionManager {
    private const val PREFS_NAME = "red_tree_auth_session_prefs"
    private const val KEY_IS_AUTHENTICATED = "is_authenticated_user"
    private const val KEY_AUTHENTICATED_EMAIL = "authenticated_user_email"

    fun isAuthenticated(context: Context): Boolean {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_AUTHENTICATED, false)
    }

    fun markAuthenticated(context: Context, email: String) {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_IS_AUTHENTICATED, true)
            .putString(KEY_AUTHENTICATED_EMAIL, email.trim())
            .commit()
    }

    fun clearSession(context: Context) {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_IS_AUTHENTICATED, false)
            .remove(KEY_AUTHENTICATED_EMAIL)
            .commit()
    }
}

@Composable
fun AuthScreen(
    onContinueToDashboard: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by rememberSaveable { mutableIntStateOf(0) } // 0 = Sign Up, 1 = Sign In
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var authError by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    val submitAuth = {
        focusManager.clearFocus()
        val cleanUsername = username.trim()
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        when {
            selectedTab == 0 && cleanUsername.isEmpty() -> {
                authError = "Please enter a username to create your account."
            }
            cleanEmail.isEmpty() || !cleanEmail.contains("@") -> {
                authError = "Please enter a valid email address."
            }
            cleanPassword.length < 4 -> {
                authError = "Please enter your password (at least 4 characters)."
            }
            else -> {
                authError = null
                if (selectedTab == 0 && cleanUsername.isNotEmpty()) {
                    val repo = UserProfileRepository.getInstance(context)
                    repo.updateProfile(
                        name = cleanUsername.replaceFirstChar { it.uppercase() },
                        username = cleanUsername.lowercase().replace(" ", "_")
                    )
                }
                AuthSessionManager.markAuthenticated(context, cleanEmail)
                onContinueToDashboard()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackBase)
            .testTag("auth_screen_root")
    ) {
        FloatingLeavesAtmosphere()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Branding Top Header
            RedTreeBrandingHeader(
                showSubtitle = true,
                subtitle = "Remove  •  Education  •  Distraction"
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Selector: [Sign Up | Sign In]
            SegmentedNeonTabs(
                items = listOf("Sign Up", "Sign In"),
                selectedIndex = selectedTab,
                onSelect = {
                    selectedTab = it
                    authError = null
                },
                activeGradient = RedOrangeGradient,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_segmented_tabs")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Input Fields
            if (selectedTab == 0) {
                // Username Input
                AuthFieldLabel("Username")
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        if (authError != null) authError = null
                    },
                    placeholder = { Text("Choose a username", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "User",
                            tint = NeonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = authTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_auth_username")
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Email Input
            AuthFieldLabel("Email")
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (authError != null) authError = null
                },
                placeholder = { Text("you@example.com", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Mail,
                        contentDescription = "Email",
                        tint = NeonYellow,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                shape = RoundedCornerShape(16.dp),
                colors = authTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_auth_email")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Password Input
            AuthFieldLabel("Password")
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (authError != null) authError = null
                },
                placeholder = { Text("At least 6 characters", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = "Password",
                        tint = NeonRed,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { submitAuth() }
                ),
                shape = RoundedCornerShape(16.dp),
                colors = authTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_auth_password")
            )

            if (authError != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = authError!!,
                    color = NeonRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_error_message")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button: Create Account / Sign In
            GlowButton(
                text = if (selectedTab == 0) "Create Account →" else "Sign In →",
                onClick = submitAuth,
                gradient = RedOrangeGradient,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_auth_submit")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Footer toggle with generous touch target so tapping anywhere on the row switches mode
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        focusManager.clearFocus()
                        selectedTab = if (selectedTab == 0) 1 else 0
                        authError = null
                    }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("btn_auth_toggle_mode"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (selectedTab == 0) "Already have an account? " else "Don't have an account? ",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Text(
                    text = if (selectedTab == 0) "Sign In" else "Sign Up",
                    color = NeonOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AuthFieldLabel(label: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp, start = 4.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            color = TextSecondary
        )
    }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = BlackSurface.copy(alpha = 0.85f),
    unfocusedContainerColor = BlackSurface.copy(alpha = 0.65f),
    focusedBorderColor = NeonGreen.copy(alpha = 0.6f),
    unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary
)
