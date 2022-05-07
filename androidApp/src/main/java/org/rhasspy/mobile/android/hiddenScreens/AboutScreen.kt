package org.rhasspy.mobile.android.hiddenScreens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import androidx.navigation.NavController
import co.touchlab.kermit.Logger
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Brands
import compose.icons.fontawesomeicons.brands.Github
import org.rhasspy.mobile.MR
import org.rhasspy.mobile.android.bottomBarScreens.HiddenScreens
import org.rhasspy.mobile.android.utils.Icon
import org.rhasspy.mobile.android.utils.ListElement
import org.rhasspy.mobile.android.utils.Text
import org.rhasspy.mobile.android.utils.translate

private val logger = Logger.withTag("AboutScreen")

@Composable
fun AboutScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val context = LocalContext.current

        val manager = context.packageManager
        val info = manager.getPackageInfo(context.packageName, 0)
        val versionName = info.versionName
        val versionCode = PackageInfoCompat.getLongVersionCode(info).toInt()

        ListElement(
            modifier = Modifier.clickable {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Nailik/rhasspy_mobile")))
            },
            icon = { Icon(FontAwesomeIcons.Brands.Github, modifier = Modifier.size(24.dp), contentDescription = MR.strings.ok) },
            text = { Text("${translate(MR.strings.version)} $versionName - $versionCode") },
            secondaryText = { Text(MR.strings.aboutText) }
        )

        ListElement(
            modifier = Modifier.clickable {
                navController.navigate(HiddenScreens.LibrariesScreen.name)
            },
            text = { Text("Used Libraries") }
        )
    }
}
