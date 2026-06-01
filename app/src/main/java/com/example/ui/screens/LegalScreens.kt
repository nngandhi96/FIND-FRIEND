package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.ui.MainViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Shield Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Privacy Directives",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "ETHICAL INTEGRITY & PROCEDURES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )

            Text(
                text = "Last updated: June 2026",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            PrivacySectionCard(
                title = "1. Public Domain Scope",
                text = "FindFriend is a dedicated directory assistant. We do not maintain private databases, nor do we perform scraping of password-locked or non-public personal repositories. All index lookup coordinates correspond to standard, publicly search-indexed social media platform channels."
            )

            PrivacySectionCard(
                title = "2. Ephemeral Image Retention",
                text = "Your uploaded photos are processed fully ephemerally in volatile memory solely for reverse multi-modal query encoding. We do not store, distribute, or retain faces or images on disk servers once search matching cycles are complete."
            )

            PrivacySectionCard(
                title = "3. Lawful Consent",
                text = "Users are strictly required to obtain lawful expression of consent before uploading portrait materials of third-party friends or family. Searching for non-consenting individuals is a violation of our Fair Use Policies."
            )

            PrivacySectionCard(
                title = "4. Data Erasure & Transparency",
                text = "Every search query generated in FindFriend is tied dynamically to your authenticated account for history lookup. You preserve absolute authority to delete entire query traces instantly from your history panel, removing them permanently from your local DB database storage."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Understood", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(innerPadding)
                .padding(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Gavel,
                contentDescription = "Gavel Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Fair Use Policies",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "USER TERMS & COMPLIANCE AGREEMENT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrivacySectionCard(
                title = "1. Strictly Ethical Application",
                text = "You unconditionally covenant to execute searches solely for transparent, fair-use, and legitimate reasons. FindFriend must never be deployed for stalking, cyber-surveillance, illegal identification tracking, coercion, intimidation, or harassment."
            )

            PrivacySectionCard(
                title = "2. No Claims of Guaranteed Precision",
                text = "FindFriend computes approximate estimations of potential public social profiles using multi-modal AI indices. No guarantees of exact matching or identity discovery are stated, nor should they be presumed by the user."
            )

            PrivacySectionCard(
                title = "3. Abuse Moderation & Reporting",
                text = "Instances of wrong matches, misclassifications, or privacy violations must be addressed promptly using our integrated report abuse channel. Reported profiles are cataloged to refine training vectors and rate limit anomalies."
            )

            PrivacySectionCard(
                title = "4. Misuse Safeguards & Rate Limits",
                text = "To preserve infrastructure and block bad actors, we enforce built-in rate-limiting logic on concurrent queries. Automated bots or scripting queries are strictly prohibited."
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Accept Policies", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportAbuseScreen(
    navController: NavController,
    viewModel: MainViewModel,
    profileName: String,
    platformName: String,
    profileLink: String
) {
    var reason by remember { mutableStateOf("Accuracy / Inaccurate Identity Match") }
    var comments by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }
    var showDropdown by remember { mutableStateOf(false) }

    val reasons = listOf(
        "Accuracy / Inaccurate Identity Match",
        "Impersonation or False Social Link",
        "Harassment / Targeted Cyberstalking Concern",
        "Underage / Privacy Infringement",
        "Other Operational Concerns"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Abuse", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Report Mismatch",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "ACCURACY & IDENTITY INFRINGEMENT REPORT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Help us maintain our ethical policies. Submitting a report log isolates the matching pair to block recurrence and refine search weights.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFE2E8F0)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "REPORTING TARGET PROFILE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Name: $profileName", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Platform: $platformName", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
                    Text("Link: $profileLink", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Reason Selector Dropdown trigger
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = reason,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Reason for Report") },
                    trailingIcon = {
                        IconButton(onClick = { showDropdown = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDropdown = true },
                    shape = RoundedCornerShape(16.dp)
                )

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    reasons.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item) },
                            onClick = {
                                reason = item
                                showDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = comments,
                onValueChange = { comments = it },
                label = { Text("Details & Comments") },
                placeholder = { Text("Provide any context or describe the mismatch...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    submitting = true
                    viewModel.submitAbuseReport(
                        profileName = profileName,
                        platformName = platformName,
                        profileLink = profileLink,
                        reason = reason,
                        comments = comments
                    ) { res ->
                        submitting = false
                        if (res) success = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                enabled = !submitting
            ) {
                if (submitting) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Submit Report", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }

    if (success) {
        Dialog(onDismissRequest = { success = false; navController.popBackStack() }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFE2E8F0)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(60.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Report Logged Successfully", fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thank you for supporting security of FindFriend database indexing. Our administrative dashboard will isolate this entry for review.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            success = false
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Finish")
                    }
                }
            }
        }
    }
}

@Composable
fun PrivacySectionCard(title: String, text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFE2E8F0)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(0.85f),
                lineHeight = 18.sp
            )
        }
    }
}
