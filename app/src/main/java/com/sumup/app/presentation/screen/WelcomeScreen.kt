package com.sumup.app.presentation.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sumup.app.R
import com.sumup.app.presentation.theme.AppTheme
import com.sumup.app.presentation.theme.ButtonLabelStyle
import com.sumup.app.presentation.theme.LightSand
import com.sumup.app.presentation.theme.SparkBlue
import com.sumup.app.presentation.theme.SystemBarsAppearance
import com.sumup.app.presentation.theme.Violet
import com.sumup.app.presentation.theme.WelcomeTitleStyle

@Composable
internal fun WelcomeScreen(onStartClick: () -> Unit) {
    SystemBarsAppearance(darkIcons = false)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SparkBlue),
    ) {
        Image(
            painter = painterResource(R.drawable.bg_welcome_map),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.8f),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(SparkBlue, blendMode = BlendMode.Multiply),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.4f),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(R.drawable.ic_sumup_logo),
                contentDescription = stringResource(R.string.app_brand_name),
                modifier = Modifier
                    .width(127.dp)
                    .height(32.dp),
            )

            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(R.drawable.ic_location_pin),
                contentDescription = null,
                modifier = Modifier.size(47.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.welcome_title),
                style = WelcomeTitleStyle,
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onStartClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightSand,
                    contentColor = Violet,
                ),
            ) {
                Text(
                    text = stringResource(R.string.welcome_start_button),
                    style = ButtonLabelStyle,
                )
            }

            Spacer(modifier = Modifier.height(58.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WelcomeScreenPreview() {
    AppTheme {
        WelcomeScreen(onStartClick = {})
    }
}
