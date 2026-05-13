package com.example.mealcw.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = FoodieAccent,
    onPrimary = Color.White,

    background = Color(0xFF1C1B17),
    onBackground = Color(0xFFE6E1D9),

    surface = Color(0xFF2B2922),
    onSurface = Color(0xFFE6E1D9),

    secondary = FoodieAccent.copy(alpha = 0.8f),
    surfaceVariant = Color(0xFF3E3C33)
)

private val LightColorScheme = lightColorScheme(
    primary = FoodieAccent,
    onPrimary = Color.White,
    background = FoodieBackground,
    onBackground = FoodieText,
    surface = FoodieCard,
    onSurface = FoodieText,

    secondary = FoodieAccent.copy(alpha = 0.7f),
    surfaceVariant = Color(0xFFF0EAE2)
)
@Composable


fun MealCWTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Cleans the raw JSON-like strings
 */
@Composable
fun formatMealResultsAnnotated(rawText: String): AnnotatedString {
    val cleanText = rawText
        .replace("{", "")
        .replace("}", "")
        .replace("\"", "")

    return buildAnnotatedString {
        cleanText.split(",").forEach { line ->
            val parts = line.split(":", limit = 2)
            if (parts.size == 2) {
                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                ) {
                    append(parts[0].trim() + ": ")
                }
                append(parts[1].trim() + "\n\n")
            } else {
                append(line.trim() + "\n")
            }
        }
    }
}

/**
 * A reusable custom TextField
 */
@Composable
fun NewTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector = Icons.Default.Search
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        // Apply the theme's rounded shapes
        shape = RoundedCornerShape(16.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        colors = TextFieldDefaults.colors(
            // Set colors to contrast beautifully with the Cream background
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            // Hide the standard Material underline to keep the design clean and modern
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )
    )
}
