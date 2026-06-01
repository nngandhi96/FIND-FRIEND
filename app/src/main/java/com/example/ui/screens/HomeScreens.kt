package com.example.ui.screens

import androidx.compose.ui.graphics.asImageBitmap
import com.example.auth.AuthManager

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.compose.foundation.isSystemInDarkTheme
import coil.compose.AsyncImage
import com.example.data.SocialProfile
import com.example.ui.MainViewModel
import com.example.ui.SearchState
import com.example.ui.components.AvatarOption
import com.example.ui.components.SampleAvatars
import kotlinx.coroutines.delay
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTabScreen(navController: NavController, viewModel: MainViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val user by viewModel.currentUser.collectAsStateWithLifecycle()

    if (user == null) {
        LaunchedEffect(Unit) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(if (selectedTab == 0) Icons.Filled.Search else Icons.Outlined.Search, "Search") },
                    label = { Text("Search") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(if (selectedTab == 1) Icons.Filled.History else Icons.Outlined.History, "History") },
                    label = { Text("History") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(if (selectedTab == 2) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder, "Saved") },
                    label = { Text("Saved") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(if (selectedTab == 3) Icons.Filled.Person else Icons.Outlined.Person, "Profile") },
                    label = { Text("Profile") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(if (selectedTab == 4) Icons.Filled.Settings else Icons.Outlined.Settings, "Settings") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> SearchTab(navController, viewModel)
                1 -> HistoryTab(viewModel)
                2 -> SavedTab(navController, viewModel)
                3 -> ProfileTab(navController, viewModel)
                4 -> SettingsTab(navController)
            }
        }
    }
}

@Composable
fun SearchTab(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    var nameHint by remember { mutableStateOf("") }
    var cityHint by remember { mutableStateOf("") }
    var schoolHint by remember { mutableStateOf("") }
    var consentChecked by remember { mutableStateOf(false) }

    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val activePhoto by viewModel.activePhoto.collectAsStateWithLifecycle()
    val activePhotoUrl by viewModel.activePhotoUrl.collectAsStateWithLifecycle()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val resolver = context.contentResolver
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(resolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    MediaStore.Images.Media.getBitmap(resolver, uri)
                }
                viewModel.selectPhoto(bitmap, null)
            } catch (e: Exception) {
                Toast.makeText(context, "Error decoding image block", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            viewModel.selectPhoto(it, null)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "FindFriend",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "PUBLIC SEARCH ENGINE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(0.12f))
                    .border(2.dp, if (isSystemInDarkTheme()) Color(0xFF1E293B) else Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val userInitial = user?.name?.take(2)?.uppercase() ?: "US"
                Text(
                    text = userInitial,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // DISCLAIMER BANNER
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, "Info", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Disclaimer: Results are estimated correlations based strictly on publicly available indexes. Face matches are approximate and not guaranteed.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    lineHeight = 15.sp,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // CUSTOM PHOTO CONTROLLER
        Text(
            text = "SELECT PHOTO OF PERSON",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.5.sp,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            shape = RoundedCornerShape(32.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(0.3f)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (activePhoto != null) {
                    Image(
                        bitmap = activePhoto!!.asImageBitmap(),
                        contentDescription = "Uploaded portrait picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(32.dp))
                    )
                    // Clear Image overlay button
                    IconButton(
                        onClick = { viewModel.selectPhoto(null, null) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(0.6f), CircleShape)
                    ) {
                        Icon(Icons.Default.Clear, "Clear Image", tint = Color.White)
                    }
                } else if (activePhotoUrl != null) {
                    AsyncImage(
                        model = activePhotoUrl,
                        contentDescription = "Selected Sample portrait url",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(32.dp))
                    )
                    IconButton(
                        onClick = { viewModel.selectPhoto(null, null) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(0.6f), CircleShape)
                    ) {
                        Icon(Icons.Default.Clear, "Clear Image", tint = Color.White)
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.PhotoCamera,
                                "Camera Icon",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.Center) {
                            Button(
                                onClick = { galleryLauncher.launch("image/*") },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Icon(Icons.Default.PhotoLibrary, "Library")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gallery", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = { cameraLauncher.launch() },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                modifier = Modifier.height(44.dp)
                            ) {
                                Icon(Icons.Default.PhotoCamera, "Camera")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Camera", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // EMULATOR ACCESSIBILITY CAROUSEL
        Text(
            text = "OR USE TEST PORTRAIT AVATARS (FOR EMULATORS)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 1.2.sp,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SampleAvatars.list) { avatar ->
                Card(
                    modifier = Modifier
                        .width(90.dp)
                        .clickable {
                            // Run a background thread to decode the Unsplash URL as a Bitmap
                            // so that the matching service has a concrete bitmap if required
                            viewModel.selectPhoto(null, avatar.imageUrl)
                            nameHint = avatar.name
                            cityHint = avatar.suggestedCity
                            schoolHint = avatar.suggestedSchool
                        },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        width = 2.dp,
                        color = if (activePhotoUrl == avatar.imageUrl) MaterialTheme.colorScheme.primary else Color.Transparent
                    ),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = avatar.imageUrl,
                            contentDescription = avatar.description,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(85.dp)
                        )
                        Text(
                            text = avatar.name.split(" ").firstOrNull() ?: "",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 4.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // METADATA HINTS SECTION
        Text(
            text = "ADD SEARCH CLUES / METADATA",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.5.sp,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = nameHint,
            onValueChange = { nameHint = it },
            label = { Text("Suspected Full Name / First Name") },
            leadingIcon = { Icon(Icons.Default.Person, "Name") },
            placeholder = { Text("e.g. David Chen") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = cityHint,
            onValueChange = { cityHint = it },
            label = { Text("City, State or Country Clues") },
            leadingIcon = { Icon(Icons.Default.Place, "Location") },
            placeholder = { Text("e.g. Seattle, WA") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = schoolHint,
            onValueChange = { schoolHint = it },
            label = { Text("College / Company Clues") },
            leadingIcon = { Icon(Icons.Default.Business, "Institution") },
            placeholder = { Text("e.g. UW Informatics") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ETHICAL CONSENT CHECKBOX
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .clickable { consentChecked = !consentChecked }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = consentChecked,
                    onCheckedChange = { consentChecked = it }
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "I state that I possess explicit authority or valid consensus to query public indices for this individual for transparent and lawful purposes.",
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.85f),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (activePhoto == null && activePhotoUrl == null) {
                    Toast.makeText(context, "Please select/upload a photo first.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (!consentChecked) {
                    Toast.makeText(context, "State consent to proceed responsibly.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                viewModel.setHints(nameHint, cityHint, schoolHint)
                navController.navigate("search_processing")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(Icons.Default.Search, "Spark")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Execute Query Trace", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SearchProcessingScreen(navController: NavController, viewModel: MainViewModel) {
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val activePhotoUrl by viewModel.activePhotoUrl.collectAsStateWithLifecycle()

    var statusMessage by remember { mutableStateOf("Encoding visual features...") }

    val infiniteTransition = rememberInfiniteTransition(label = "Radar Ring")
    val sizeProgress by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Radar Scale"
    )
    val opacityProgress by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Radar Opacity"
    )

    LaunchedEffect(Unit) {
        viewModel.performSearch()
        delay(800)
        statusMessage = "Analyzing metadata matching trees..."
        delay(1000)
        statusMessage = "Querying social database catalogs..."
        delay(800)
        statusMessage = "Correlating results..."
    }

    LaunchedEffect(searchState) {
        if (searchState is SearchState.Success) {
            navController.navigate("results") {
                popUpTo("search_processing") { inclusive = true }
            }
        } else if (searchState is SearchState.Error) {
            navController.navigate("results") {
                popUpTo("search_processing") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Multi-layered radar background rings
                Box(
                    modifier = Modifier
                        .size((160 * sizeProgress).dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(opacityProgress * 0.4f))
                )

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B)),
                    contentAlignment = Alignment.Center
                ) {
                    if (activePhotoUrl != null) {
                        AsyncImage(
                            model = activePhotoUrl,
                            contentDescription = "Radar photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Face,
                            contentDescription = "Scanning face",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "FindFriend Engine Active",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                statusMessage,
                fontSize = 14.sp,
                color = Color(0xFF9CA3AF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CircularProgressIndicator(
                modifier = Modifier.size(36.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(navController: NavController, viewModel: MainViewModel) {
    val searchState by viewModel.searchState.collectAsStateWithLifecycle()
    val activePhotoUrl by viewModel.activePhotoUrl.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Query Match Results", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { viewModel.clearSearchInput(); navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            when (searchState) {
                is SearchState.Processing -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is SearchState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Error, "Error", tint = Color.Red, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Search Error", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = (searchState as SearchState.Error).message,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Retry Search")
                        }
                    }
                }
                is SearchState.Success -> {
                    val results = (searchState as SearchState.Success).results
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(10.dp))
                            // CONSENT SUMMARY
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Shield, "Certified Safe", tint = Color(0xFF10B981), modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Ethical Consensus Assured", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("Query traces are saved locally inside your safe archive.", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "POSSIBLE PLATFORM INDEXES FOUND (${results.size}):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (results.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Cancel, "No indices found", tint = Color.Gray, modifier = Modifier.size(48.dp))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text("No public profile links matches.", fontWeight = FontWeight.Bold)
                                        Text("Modify search metadata hints to query more directories.", fontSize = 12.sp, textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        } else {
                            items(results) { profile ->
                                ProfileMatchResultCard(profile, viewModel, navController)
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }
                }
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Ready to Search.")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileMatchResultCard(profile: SocialProfile, viewModel: MainViewModel, navController: NavController) {
    val context = LocalContext.current
    var isSaved by remember { mutableStateOf(false) }

    LaunchedEffect(profile.id) {
        viewModel.isSaved(profile.id) { res ->
            isSaved = res
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFE2E8F0)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile Avatar frame
                AsyncImage(
                    model = profile.profilePhotoUrl,
                    contentDescription = profile.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(0.3f), CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = profile.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        // Platform Pill
                        Text(
                            text = profile.platformName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .background(getPlatformColor(profile.platformName), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = profile.username,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CONFIDENCE LEVEL INDICATOR BAR
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Similarity Correlation:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${profile.confidence}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = getConfidenceColor(profile.confidence)
                )
            }

            LinearProgressIndicator(
                progress = { profile.confidence / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = getConfidenceColor(profile.confidence)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ACTION CONTROLS BUTTONS ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Open Profile Icon/Button
                Button(
                    onClick = {
                        val webUrl = profile.profileLink
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot locate web browser", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f).height(44.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, "Launch Link", size = 18.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Visit Profile", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Copy profile link action
                IconButton(
                    onClick = {
                        val cb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cb.setPrimaryClip(ClipData.newPlainText("FindFriend Link", profile.profileLink))
                        Toast.makeText(context, "Saved profile link to Clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.ContentCopy, "Copy link", tint = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Save result toggle
                IconButton(
                    onClick = {
                        viewModel.toggleSaveProfile(profile)
                        isSaved = !isSaved
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (isSaved) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save profile toggler",
                        tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Report Abuse Icon launcher
                IconButton(
                    onClick = {
                        val rawName = URLEncoder.encode(profile.name, StandardCharsets.UTF_8.toString())
                        val rawPlatform = URLEncoder.encode(profile.platformName, StandardCharsets.UTF_8.toString())
                        val rawLink = URLEncoder.encode(profile.profileLink, StandardCharsets.UTF_8.toString())
                        navController.navigate("report_abuse/$rawName/$rawPlatform/$rawLink")
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.errorContainer.copy(0.4f), RoundedCornerShape(16.dp))
                ) {
                    Icon(Icons.Default.OutlinedFlag, "Report Abuse", tint = Color.Red)
                }
            }
        }
    }
}

// History panel list UI
@Composable
fun HistoryTab(viewModel: MainViewModel) {
    val histories by viewModel.searchHistory.collectAsStateWithLifecycle()
    var expandedHistoryId by remember { mutableStateOf<Int?>(null) }
    val sdf = SimpleDateFormat("MMM d, yyyy h:mm a", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Saved Queries History", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text("Locally archived queries", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)
            }
            if (histories.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearUserHistory() }) {
                    Icon(Icons.Default.DeleteSweep, "Clear all", tint = Color.Red)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (histories.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, "Empty history", tint = Color.Gray, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No past query traces located.", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Execute a search matches to construct archival logs.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(histories) { history ->
                    val isExpanded = expandedHistoryId == history.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedHistoryId = if (isExpanded) null else history.id },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = if (history.imagePath == "custom_uploaded_photo") Icons.Default.AccountBox else history.imagePath,
                                    contentDescription = "Search visual input preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (history.nameHint.isBlank()) "Query Ref: Person #${history.id}" else history.nameHint,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    val formattedDate = sdf.format(Date(history.timestamp))
                                    Text(formattedDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                }
                                Box(contentAlignment = Alignment.Center) {
                                    IconButton(onClick = { viewModel.deleteHistoryId(history.id) }) {
                                        Icon(Icons.Default.DeleteOutline, "Remove", tint = Color.Gray)
                                    }
                                }
                            }

                            if (isExpanded) {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("QUERY PARAMETERS:", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                if (history.cityHint.isNotBlank()) Text("Location Clue: ${history.cityHint}", fontSize = 12.sp)
                                if (history.schoolHint.isNotBlank()) Text("College/Company: ${history.schoolHint}", fontSize = 12.sp)

                                Spacer(modifier = Modifier.height(12.dp))
                                Text("INDICES DETECTED:", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                                val items = viewModel.parseResultJson(history.resultsJson)
                                if (items.isEmpty()) {
                                    Text("Zero correlations matched this entry.", fontSize = 12.sp, color = Color.Gray)
                                } else {
                                    items.forEach { profile ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${profile.platformName}: ${profile.name} (${profile.username})",
                                                fontSize = 12.sp,
                                                modifier = Modifier.weight(1f),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${profile.confidence}% match",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = getConfidenceColor(profile.confidence)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Saved profiles screen
@Composable
fun SavedTab(navController: NavController, viewModel: MainViewModel) {
    val items by viewModel.savedProfiles.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Text("Saved Social Coordinates", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("Quick-access high confidence indexes", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)

        Spacer(modifier = Modifier.height(16.dp))

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.BookmarkBorder, "Empty saved", tint = Color.Gray, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No saved bookmarks captured.", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Bookmark high-confidence result cards to archive them.", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items) { bookmark ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = bookmark.profilePhotoUrl,
                                    contentDescription = bookmark.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(bookmark.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                        Text(
                                            text = bookmark.platformName,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier
                                                .background(getPlatformColor(bookmark.platformName), RoundedCornerShape(6.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(bookmark.username, fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = {
                                        try {
                                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(bookmark.profileLink)))
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "No web browser located.", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(36.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.OpenInNew, "Go", size = 16.dp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Visit Profile", fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                OutlinedButton(
                                    onClick = { viewModel.removeSavedProfile(bookmark.profileId) },
                                    modifier = Modifier.height(36.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                                ) {
                                    Icon(Icons.Default.BookmarkRemove, "Remove", size = 16.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// User Profile tab
@Composable
fun ProfileTab(navController: NavController, viewModel: MainViewModel) {
    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    var showDeleteAlert by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar Profile",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = user?.name ?: "FindFriend Account",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = user?.email ?: "account@findfriend.com",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary
        )

        if (user?.isGoogleUser == true) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Verified, "Verified oauth", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Google OAuth Verified Access", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // QUICK METRIC STATISTICS
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Secure", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                    Text("Network Code", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                }
                Box(modifier = Modifier.size(1.dp, 36.dp).background(MaterialTheme.colorScheme.surfaceVariant))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Compliant", fontWeight = FontWeight.Bold, color = Color(0xFF10B981), fontSize = 18.sp)
                    Text("Index Scope", fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PREFERENCES CONTROLLER ACTION BUTTONS
        Button(
            onClick = { AuthManager.logout(); navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.ExitToApp, "Log out")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout Session", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { showDeleteAlert = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            border = BorderStroke(1.dp, Color.Red),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.DeleteForever, "Trash Forever")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete FindFriend Account", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showDeleteAlert) {
        AlertDialog(
            onDismissRequest = { showDeleteAlert = false },
            title = { Text("Permanently Terminate Account?") },
            text = { Text("This will destroy your matching profiles database locally, wipe saved bookmarks and close active session traces instantly. This action is irreversible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAlert = false
                        AuthManager.deleteAccount()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Delete Forever")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAlert = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// Settings Screen tab
@Composable
fun SettingsTab(navController: NavController) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        Text("Application Configuration", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("Safety, theme and license compliance", fontSize = 13.sp, color = MaterialTheme.colorScheme.secondary)

        Spacer(modifier = Modifier.height(20.dp))

        // THEME CONTROLLER MOCK
        SettingsSectionCard(title = "General Settings") {
            ListItem(
                headlineContent = { Text("Adaptive Theme Engine", fontWeight = FontWeight.SemiBold) },
                supportingContent = { Text("Automatically toggle light or dark canvas style depending on OS") },
                leadingContent = { Icon(Icons.Default.BrightnessMedium, "Theme") },
                trailingContent = { Switch(checked = true, onCheckedChange = {}) }
            )
            ListItem(
                headlineContent = { Text("Local Database Seed Logs", fontWeight = FontWeight.SemiBold) },
                supportingContent = { Text("Wipe isolated mismatch logs or reported abuse reports local copies") },
                leadingContent = { Icon(Icons.Default.SettingsBackupRestore, "Seed Logs") },
                trailingContent = { TextButton(onClick = {}) { Text("Wipe DB") } }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // LEGAL PORTAL CARDS
        SettingsSectionCard(title = "Safety & Legal Portals") {
            ListItem(
                headlineContent = { Text("Privacy Policy Objectives", fontWeight = FontWeight.SemiBold) },
                supportingContent = { Text("Learn about data retention and consent guarantees") },
                leadingContent = { Icon(Icons.Default.PrivacyTip, "Privacy") },
                modifier = Modifier.clickable { navController.navigate("privacy_policy") }
            )
            ListItem(
                headlineContent = { Text("Terms and Fair Use Conditions", fontWeight = FontWeight.SemiBold) },
                supportingContent = { Text("Understand lawful use requirements and prohibitions") },
                leadingContent = { Icon(Icons.Default.MenuBook, "Terms") },
                modifier = Modifier.clickable { navController.navigate("terms") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SYSTEM VERSION LOG
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.4f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "FindFriend v1.0.0 (Production Clean Architecture)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "Designed ethically to protect third-party identities with ephemeral search logic in compliance with Google Play Privacy guidelines.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    lineHeight = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SettingsSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF334155) else Color(0xFFE2E8F0)),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(content = content)
        }
    }
}

// Color and Platform UI resolvers
fun getPlatformColor(platformName: String): Color {
    return when (platformName.trim().lowercase()) {
        "instagram" -> Color(0xFFE1306C)
        "linkedin" -> Color(0xFF0077B5)
        "facebook" -> Color(0xFF1877F2)
        "x", "twitter" -> Color(0xFF1DA1F2)
        "snapchat" -> Color(0xFFFEE000)
        else -> Color(0xFF6B7280)
    }
}

fun getConfidenceColor(confidence: Int): Color {
    return when {
        confidence >= 85 -> Color(0xFF10B981) // Emerald Green
        confidence >= 65 -> Color(0xFF3B82F6) // Bright Blue
        confidence >= 45 -> Color(0xFFF59E0B) // Amber Yellow
        else -> Color(0xFFEF4444)             // Alert Red
    }
}

@Composable
private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String, size: androidx.compose.ui.unit.Dp) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = Modifier.size(size)
    )
}
