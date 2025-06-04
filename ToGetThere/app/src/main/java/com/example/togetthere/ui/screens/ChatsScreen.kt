package com.example.togetthere.ui.screens

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.togetthere.R
import com.example.togetthere.model.Message
import com.example.togetthere.model.TripChat
import com.example.togetthere.ui.components.NotLoggedInComponent
import com.example.togetthere.ui.components.NothingToSeeHere
import com.example.togetthere.viewmodel.ChatViewModel
import com.example.togetthere.viewmodel.UserSessionViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    isLandscape: Boolean,
    navController: NavHostController,
    bottomPadding: Dp,
    userSessionViewModel: UserSessionViewModel,
    chatViewModel: ChatViewModel
) {
    val currentUser by userSessionViewModel.currentUser.collectAsState()

    if (currentUser == null) {
        NotLoggedInComponent(isLandscape, navController, bottomPadding)
        return
    }

    // Load chats when screen is first displayed
    LaunchedEffect(currentUser!!.userId) {
        chatViewModel.loadUserChats(currentUser!!.userId)
    }

    val chatItems by chatViewModel.chatItems.collectAsState()
    val currentChat by chatViewModel.currentChat.collectAsState()
    val messages by chatViewModel.messages.collectAsState()
    val loading by chatViewModel.loading.collectAsState()

    if (loading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (currentChat == null) "Group Chats" else currentChat!!.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    if (currentChat != null) {
                        IconButton(
                            onClick = {
                                chatViewModel.markChatAsRead(currentChat!!.id, currentUser!!.userId)
                                chatViewModel.selectChat(null) },
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to chats",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        },
        content = { innerPadding ->
            if (chatItems.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(innerPadding)
                ) {
                    NothingToSeeHere(title = "No chats yet", text = "Hurry up, make some new friends!")
                }
            } else {
                if (currentChat == null) {
                    ChatListScreen(
                        chats = chatItems,
                        onChatSelected = { chat -> chatViewModel.selectChat(chat) },
                        padding = innerPadding,
                        bottomPadding = bottomPadding,
                        chatViewModel = chatViewModel,
                        userSessionViewModel = userSessionViewModel,
                    )
                } else {
                    ChatDetailScreenContent(
                        chat = currentChat!!,
                        messages = messages,
                        currentUserId = currentUser!!.userId,
                        onSendMessage = { text -> chatViewModel.sendMessage(text) },
                        padding = innerPadding,
                        bottomPadding = bottomPadding,
                        vm = chatViewModel
                    )
                }
            }
        }
    )
}

// Rinominata e rimosso il Scaffold interno
@Composable
private fun ChatDetailScreenContent(
    chat: TripChat,
    messages: List<Message>,
    currentUserId: String,
    onSendMessage: (String) -> Unit,
    padding: PaddingValues,
    bottomPadding: Dp,
    vm: ChatViewModel
) {
    val configuration = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val adjustedPadding = if (configuration) {
        bottomPadding
    } else {
        bottomPadding - 40.dp
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(bottom = adjustedPadding)
    ) {
        MessagesList(
            messages = messages,
            currentUserId = currentUserId,
            modifier = Modifier.weight(1f),
            vm = vm
        )

        MessageInput(
            onSendMessage = onSendMessage
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatListScreen(
    chats: List<TripChat>,
    onChatSelected: (TripChat) -> Unit,
    padding: PaddingValues,
    bottomPadding: Dp,
    chatViewModel: ChatViewModel,
    userSessionViewModel: UserSessionViewModel,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(bottom = bottomPadding),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(chats) { chat ->
            ChatListItem(chat = chat, onChatSelected = onChatSelected, chatViewModel = chatViewModel, userSessionViewModel= userSessionViewModel,)
        }
    }
}

@Composable
private fun ChatListItem(chat: TripChat, onChatSelected: (TripChat) -> Unit, chatViewModel: ChatViewModel, userSessionViewModel: UserSessionViewModel,) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val now = System.currentTimeMillis()
    val chatTimeMillis = chat.timestamp.toDate().time
    val isToday = now - chatTimeMillis < 86400000 // 24 hours in milliseconds
    val currentUser by userSessionViewModel.currentUser.collectAsState()
    val unreadCount by chatViewModel.getUnreadCount(chat.id).collectAsState(0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onChatSelected(chat)},
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Chat image with badge for unread messages
            BadgedBox(
                badge = {
                    if (unreadCount > 0) {
                        Badge {
                            Text(unreadCount.toString())
                        }
                    }
                }
            ) {
                AsyncImage(
                    model = chat.image ?: R.drawable.hossegor3,
                    contentDescription = "Group Avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = chat.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = if (isToday) timeFormat.format(chat.timestamp.toDate())
                        else dateFormat.format(chat.timestamp.toDate()),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                val messageText = chat.lastMessage?.text?.takeIf { it.isNotBlank() } ?: "New Trip Chat"

                Text(
                    text = messageText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var messageText by rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = messageText,
            onValueChange = { messageText = it },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            placeholder = { Text("Type a message...") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(messageText)
                        messageText = ""
                    }
                    keyboardController?.hide()
                }
            ),
            singleLine = false,
            maxLines = 3
        )

        IconButton(
            onClick = {
                if (messageText.isNotBlank()) {
                    onSendMessage(messageText)
                    messageText = ""
                    keyboardController?.hide()
                }
            },
            enabled = messageText.isNotBlank()
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send message",
                tint = if (messageText.isNotBlank()) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun MessagesList(
    messages: List<Message>,
    currentUserId: String,
    modifier: Modifier = Modifier,
    vm: ChatViewModel
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        reverseLayout = true,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(messages.reversed()) { message ->
            MessageBubble(
                message = message,
                isCurrentUser = message.senderId == currentUserId,
                vm = vm
            )
        }
    }
}

@Composable
private fun MessageBubble(message: Message, isCurrentUser: Boolean, vm: ChatViewModel) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val userImageMap by vm.userImageMap.collectAsState()
    val senderImageUrl = userImageMap[message.senderId]

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        if (!isCurrentUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(senderImageUrl),
                    contentDescription = "Sender Avatar",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = if (isCurrentUser) 12.dp else 0.dp,
                        bottomEnd = if (isCurrentUser) 0.dp else 12.dp
                    )
                )
                .background(
                    if (isCurrentUser) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
                .padding(12.dp)
        ) {
            Text(
                text = message.text,
                color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val timestamp = message.timestamp
        val formattedTime = timestamp?.toDate()?.let { timeFormat.format(it) } ?: "—"

        Text(
            text = formattedTime,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp, start = if (isCurrentUser) 0.dp else 36.dp)
        )

    }
}