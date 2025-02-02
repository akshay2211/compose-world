package compose.world.composables.highlightable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import compose.world.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstagramHomePage() {

    var currentHighlightIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Instagram") }, actions = {
                ContentWithHighlightPopUp(
                    mainContent = {
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(Icons.Default.Favorite, contentDescription = "Liked")
                        }
                    },
                    popUpColor = Color.Blue,
                    description = "`Notifications` section! This is where you can keep track of your likes and other activies by your friends",
                    isPopUpVisible = currentHighlightIndex == 0,
                    onNext = {
                        currentHighlightIndex++
                    }
                )

                ContentWithHighlightPopUp(
                    mainContent = {
                        IconButton(
                            onClick = {}
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "DM")
                        }
                    },
                    popUpColor = Color.Black,
                    description = "`DM` section! Contact with anyone across the world! ",
                    isPopUpVisible = currentHighlightIndex == 1,
                    onNext = {
                        currentHighlightIndex++
                    }
                )
            })
        },
        bottomBar = {
            Row {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Home")
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Home")
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Home")
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues)
        ) {
            itemsIndexed(posts) { index, post ->
                PostItem(
                    post = post,
                    shouldHighlightPfp = index == 0 && currentHighlightIndex == 2,
                    shouldHighlightPost = index == 0 && currentHighlightIndex == 3,
                    onNext = { currentHighlightIndex++ }
                )
            }
        }
    }
}

@Composable
fun PostItem(
    post: Post,
    shouldHighlightPfp: Boolean,
    shouldHighlightPost: Boolean,
    onNext: () -> Unit
) {
    Column (
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContentWithHighlightPopUp(
                    mainContent = {
                        Image(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            painter = painterResource(id = post.userImage),
                            contentDescription = "User Image",
                            contentScale = ContentScale.Crop
                        )
                    },
                    popUpColor = Color.Red,
                    description = "You can view more on other posts by this owner, by clicking on the profile photo.",
                    onNext = onNext,
                    isPopUpVisible = shouldHighlightPfp
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = post.username,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            IconButton(onClick = { /* Handle action */ }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More")
            }
        }

        ContentWithHighlightPopUp(
            mainContent = {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(),
                    painter = painterResource(id = post.postImage),
                    contentDescription = "Post Image",
                    contentScale = ContentScale.FillWidth
                )
            },
            popUpColor = Color.Blue,
            description = "And this is the `post`. Our platform is all about sharing your memorable moments with people around you!",
            isPopUpVisible = shouldHighlightPost,
            highlightOptions = DefaultHighlightOptions
                .copy(baseScale = 1F),
            onNext = onNext
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${post.likes} likes",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(start = 8.dp)
        )
        Text(
            text = post.caption,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
        )
    }
}

data class Post(
    val username: String,
    val userImage: Int,
    val postImage: Int,
    val likes: Int,
    val caption: String
)

val posts = listOf(
    Post("Marvel studios", R.drawable.pfp_marvel, R.drawable.img_post_marvel, 120, "New marvel movie is coming soon!"),
    Post("CR7", R.drawable.pfp_ronaldo, R.drawable.img_post_ronaldo, 230, "900!")
)

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    InstagramHomePage()

}