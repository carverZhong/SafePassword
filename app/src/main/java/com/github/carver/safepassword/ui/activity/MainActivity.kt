package com.github.carver.safepassword.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.carver.safepassword.R
import com.github.carver.safepassword.data.source.local.PasswordEntity
import com.github.carver.safepassword.ui.theme.SafePasswordTheme
import com.github.carver.safepassword.viewmodel.MainViewModel
import kotlin.collections.set

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<MainViewModel>()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainContent(viewModel, {
                PasswordEditActivity.start(this, it)
            }, {
                startActivity(Intent(this, SettingsActivity::class.java))
            })
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPasswords()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    mainViewModel: MainViewModel = viewModel(),
    onItemClick: (PasswordEntity?) -> Unit,
    onSettingClick: () -> Unit
) {
    SafePasswordTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "密码本") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.surface,
                    ),
                    actions = {
                        IconButton(
                            modifier = Modifier.size(26.dp),
                            onClick = {
                                mainViewModel.switchView()
                            }
                        ) {
                            Icon(
                                painter = painterResource(if (mainViewModel.isShowCategory.value) {
                                    R.drawable.ic_category
                                } else {
                                    R.drawable.ic_list
                                }),
                                contentDescription = "category"
                            )
                        }
                        Spacer(Modifier
                            .fillMaxHeight()
                            .width(8.dp))
                        IconButton(
                            modifier = Modifier.size(26.dp),
                            onClick = onSettingClick
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_settings),
                                contentDescription = "settings"
                            )
                        }
                        Spacer(Modifier
                            .fillMaxHeight()
                            .width(8.dp))
                    }
                )
            },
            modifier = Modifier.fillMaxSize(),
            floatingActionButton = {
                FloatingActionView {
                    onItemClick(null)
                }
            }
        ) { innerPadding ->
            if (mainViewModel.isShowCategory.value) {
                PasswordExpandableListView(innerPadding, mainViewModel, onItemClick)
            } else {
                PasswordListView(innerPadding, mainViewModel, onItemClick)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingActionView(onAction: () -> Unit) {
    FloatingActionButton(
        onClick = onAction
    ) {
        Icon(Icons.Default.Add, contentDescription = "Add")
    }
}

@Composable
fun PasswordExpandableListView(
    innerPadding: PaddingValues,
    mainViewModel: MainViewModel,
    onItemClick: (PasswordEntity) -> Unit
) {
    val data by mainViewModel.passwordMap.collectAsState(initial = emptyMap())
    val expandedCategories = remember { mutableStateMapOf<String, Boolean>() }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.White),
    ) {
        data.entries.forEachIndexed { index, entry ->
            item(key = entry.key) {
                Column {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .clickable {
                                expandedCategories[entry.key] =
                                    !(expandedCategories[entry.key] ?: false)
                            },
                        shape = RectangleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp, 10.dp, 12.dp, 4.dp),
                            text = entry.key,
                            fontSize = 20.sp,
                            color = Color.Black,
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp, 4.dp, 12.dp, 4.dp),
                            text = "共${entry.value.size}个账号",
                            fontSize = 15.sp
                        )
                    }
                    HorizontalDivider(
                        color = Color(0xFFEEEEEE),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                    if (expandedCategories[entry.key] == true) {
                        entry.value.forEachIndexed { entityIndex, entity ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable {
                                        onItemClick(entity)
                                    },
                                contentAlignment = Alignment.CenterStart,
                            ) {
                                Text(
                                    modifier = Modifier.padding(20.dp, 0.dp, 0.dp, 0.dp),
                                    text = entity.account,
                                    fontSize = 18.sp,
                                )
                            }
                            HorizontalDivider(
                                color = Color(0xFFEEEEEE),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                            )
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun PasswordListView(innerPadding: PaddingValues,
                     mainViewModel: MainViewModel,
                     onItemClick: (PasswordEntity) -> Unit) {
    val data by mainViewModel.passwordList.collectAsState(initial = emptyList())
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.White),
    ) {
        items(data.size) { index ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 4.dp, 0.dp, 0.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(75.dp)
                        .clickable { onItemClick(data[index]) },
                    shape = RectangleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(14.dp, 10.dp, 12.dp, 4.dp),
                        text = data[index].account,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(14.dp, 4.dp, 12.dp, 4.dp),
                        text = data[index].category,
                        fontSize = 15.sp
                    )
                }
                HorizontalDivider(
                    color = Color(0xFFEEEEEE),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                )
            }
        }
    }
}

