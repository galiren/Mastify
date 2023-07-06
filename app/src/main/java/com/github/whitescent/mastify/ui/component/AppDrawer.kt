package com.github.whitescent.mastify.ui.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.github.whitescent.R
import com.github.whitescent.mastify.database.model.AccountEntity
import com.github.whitescent.mastify.ui.theme.AppTheme
import kotlinx.coroutines.launch

@Composable
fun AppDrawer(
  isSystemBarVisible: Boolean,
  drawerState: DrawerState,
  activeAccount: AccountEntity,
  accounts: MutableList<AccountEntity>,
  changeAccount: (Long) -> Unit,
  navigateToLogin: () -> Unit,
) {
  val scope = rememberCoroutineScope()
  ModalDrawerSheet(
    windowInsets = WindowInsets(0, 0, 0,0),
    drawerContainerColor = AppTheme.colors.background
  ) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotate by animateFloatAsState(targetValue = if (expanded) 180f else 0f)
    val transition = updateTransition(targetState = expanded)
    var accountListHeight by remember { mutableIntStateOf(0) }
    val animatedHeight by transition.animateInt {
      when (it) {
        true -> 0
        else -> -accountListHeight
      }
    }
    Box(
      Modifier.heightIn(max = 200.dp)
    ) {
      AsyncImage(
        model = activeAccount.header,
        contentDescription = null,
        modifier = Modifier
          .fillMaxSize()
          .alpha(0.6f),
        contentScale = ContentScale.Crop,
      )
      Column(
        modifier = Modifier
          .let {
            if (isSystemBarVisible) {
              // Ensure correct padding is maintained
              // when using this app in environments similar to WSA
              it
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp)
            } else {
              it.padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 8.dp)
            }
          },
      ) {
        CircleShapeAsyncImage(
          model = activeAccount.profilePictureUrl,
          modifier = Modifier.size(72.dp)
        )
        HeightSpacer(value = 6.dp)
        CenterRow {
          Column(
            modifier = Modifier.weight(1f)
          ) {
            Text(
              text = activeAccount.displayName,
              fontSize = 24.sp,
              color = AppTheme.colors.primaryContent
            )
            Text(
              text = activeAccount.fullName,
              fontSize = 16.sp,
              color = AppTheme.colors.primaryContent
            )
          }
          IconButton(
            onClick = { expanded = !expanded }
          ) {
            Icon(
              painter = painterResource(R.drawable.more_arrow),
              contentDescription = null,
              modifier = Modifier.rotate(rotate),
              tint = AppTheme.colors.primaryContent
            )
          }
        }
      }
    }

    Column(
      modifier = Modifier
        .clipToBounds()
        .offset { IntOffset(0, animatedHeight) }
    ) {
      Surface(
        modifier = Modifier
          .onGloballyPositioned {
            accountListHeight = it.size.height
          },
        color = AppTheme.colors.background
      ) {
        Column {
          accounts.forEach { account ->
            CenterRow(
              modifier = Modifier
                .clickable(
                  interactionSource = MutableInteractionSource(),
                  indication = rememberRipple(
                    bounded = true,
                    radius = 250.dp,
                  ),
                  onClick = {
                    if (account != activeAccount) changeAccount(account.id)
                    else {
                      scope.launch {
                        drawerState.close()
                      }
                    }
                  },
                )
                .padding(12.dp),
            ) {
              CircleShapeAsyncImage(
                model = account.profilePictureUrl,
                modifier = Modifier.size(40.dp)
              )
              WidthSpacer(value = 8.dp)
              Text(
                text = account.fullName,
                fontSize = 16.sp,
                color = AppTheme.colors.primaryContent,
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight(500),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
              )
              if (account == activeAccount) {
                Image(
                  painter = painterResource(id = R.drawable.check_circle_fill),
                  contentDescription = null,
                  modifier = Modifier
                    .size(32.dp)
                    .padding(start = 4.dp)
                )
              }
            }
          }
          CenterRow(
            modifier = Modifier
              .fillMaxWidth()
              .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                  bounded = true,
                  radius = 250.dp,
                ),
                onClick = navigateToLogin
              )
              .drawerListItemPadding()
          ) {
            Box(
              modifier = Modifier.size(40.dp),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = AppTheme.colors.primaryContent
              )
            }
            WidthSpacer(value = 8.dp)
            Text(
              text = stringResource(id = R.string.title_add_account),
              fontSize = 16.sp,
              fontWeight = FontWeight(500),
              color = AppTheme.colors.primaryContent,
            )
          }
          Divider(thickness = 0.5.dp)
        }
      }
      HeightSpacer(value = 8.dp)
      DrawerMenu()
    }

    LaunchedEffect(drawerState.isOpen) {
      if (!drawerState.isOpen) {
        expanded = false
      }
    }
  }

}

@Composable
fun DrawerMenu() {

  AppDrawerMenu.values().forEach {
    if (it.route == AppDrawerMenu.Settings.route) {
      Divider(thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
    }
    DrawerMenuItem(it.icon, it.redId) { }
  }

}

@Composable
fun DrawerMenuItem(icon: Int, name: Int, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(
          bounded = true,
          radius = 250.dp,
        ),
        onClick = onClick,
      )
  ) {
    CenterRow(
      modifier = Modifier.drawerListItemPadding()
    ) {
      Box(
        modifier = Modifier.size(40.dp),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          painter = painterResource(id = icon),
          contentDescription = null,
          modifier = Modifier.size(24.dp),
          tint = AppTheme.colors.primaryContent,
        )
      }
      WidthSpacer(value = 8.dp)
      Text(
        text = stringResource(id = name),
        fontSize = 18.sp,
        color = AppTheme.colors.primaryContent,
      )
    }
  }
}

enum class AppDrawerMenu(
  @DrawableRes val icon: Int,
  @StringRes val redId: Int,
  val route: String,
) {
  Profile(R.drawable.user, R.string.title_profile, "profile"),
  Bookmarks(R.drawable.bookmark_simple, R.string.title_bookmarks, "bookmarks"),
  Favorites(R.drawable.heart_2, R.string.title_favorites, "favorites"),
  Lists(R.drawable.list_bullets, R.string.title_lists, "lists"),
  Drafts(R.drawable.scroll, R.string.title_draft, "draft"),
  Settings(R.drawable.gear, R.string.title_settings, "settings"),
  About(R.drawable.info, R.string.title_about, "about"),
  Logout(R.drawable.sign_out, R.string.title_logout, "logout")
}

private fun Modifier.drawerListItemPadding(): Modifier =
  this.padding(horizontal = 16.dp, vertical = 6.dp)