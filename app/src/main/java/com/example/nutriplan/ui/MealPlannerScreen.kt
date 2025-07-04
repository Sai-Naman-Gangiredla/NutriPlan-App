package com.example.nutriplan.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.BreakfastDining
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.zIndex

// Data classes

data class MealEntry(
    val foods: List<String>,
    val mealType: String,
    val sides: List<String>,
    val calories: Int,
    val date: String
)

// Expanded static food list and calorie mapping
val foodCalorieMap = mapOf(
    "Apple" to 95,
    "Egg" to 78,
    "Rice" to 206,
    "Chicken" to 335,
    "Oats" to 150,
    "Milk" to 122,
    "Banana" to 105,
    "Beef" to 250,
    "Broccoli" to 55,
    "Carrot" to 41,
    "Cheese" to 113,
    "Bread" to 80,
    "Pasta" to 131,
    "Fish" to 145,
    "Potato" to 163,
    "Orange" to 62,
    "Yogurt" to 59,
    "Soup" to 72,
    "Nuts" to 180,
    "Juice" to 110,
    "Salad" to 33,
    "Fruit" to 60,
    "Dessert" to 200,
    "Water" to 0
)
val foodList = foodCalorieMap.keys.toList()

val mealTypes = listOf("Breakfast", "Brunch", "Lunch", "Snacks", "Dinner")
val sideOptions = listOf("Salad", "Yogurt", "Juice", "Soup", "Bread", "Cheese", "Nuts", "Fruit", "Dessert", "Water")

// Define suitable calorie limits for each meal type
val mealTypeCalorieOptions = mapOf(
    "Breakfast" to listOf(200, 300, 400),
    "Brunch" to listOf(250, 350, 450),
    "Lunch" to listOf(400, 500, 600),
    "Snacks" to listOf(100, 200, 300),
    "Dinner" to listOf(350, 450, 550)
)

// Helper for meal type icons
fun mealTypeIcon(type: String) = when(type) {
    "Breakfast" -> Icons.Filled.BreakfastDining
    "Brunch" -> Icons.Filled.LocalCafe
    "Lunch" -> Icons.Filled.LunchDining
    "Snacks" -> Icons.Filled.EmojiFoodBeverage
    "Dinner" -> Icons.Filled.DinnerDining
    else -> Icons.Filled.Restaurant
}

// Categorized food and side lists
val foodCategories = mapOf(
    "Fruits" to listOf("Apple", "Banana", "Orange", "Fruit"),
    "Vegetables" to listOf("Broccoli", "Carrot", "Potato", "Salad"),
    "Grains" to listOf("Rice", "Oats", "Bread", "Pasta"),
    "Proteins" to listOf("Egg", "Chicken", "Beef", "Fish"),
    "Dairy" to listOf("Milk", "Cheese", "Yogurt"),
    "Snacks & Desserts" to listOf("Nuts", "Dessert"),
    "Drinks & Others" to listOf("Juice", "Soup", "Water")
)
val sideCategories = mapOf(
    "Salads & Veggies" to listOf("Salad", "Fruit"),
    "Dairy & Protein" to listOf("Yogurt", "Cheese", "Nuts"),
    "Breads & Soups" to listOf("Bread", "Soup"),
    "Drinks & Desserts" to listOf("Juice", "Dessert", "Water")
)

// Helper for category icons (use available Material icons or fallback)
@Composable
fun categoryIcon(category: String): Painter? = when (category) {
    "Fruits" -> painterResource(android.R.drawable.ic_menu_gallery)
    "Vegetables" -> painterResource(android.R.drawable.ic_menu_crop)
    "Grains" -> painterResource(android.R.drawable.ic_menu_sort_by_size)
    "Proteins" -> painterResource(android.R.drawable.ic_menu_manage)
    "Dairy" -> painterResource(android.R.drawable.ic_menu_myplaces)
    "Snacks & Desserts" -> painterResource(android.R.drawable.ic_menu_slideshow)
    "Drinks & Others" -> painterResource(android.R.drawable.ic_menu_compass)
    "Salads & Veggies" -> painterResource(android.R.drawable.ic_menu_crop)
    "Dairy & Protein" -> painterResource(android.R.drawable.ic_menu_myplaces)
    "Breads & Soups" -> painterResource(android.R.drawable.ic_menu_sort_by_size)
    "Drinks & Desserts" -> painterResource(android.R.drawable.ic_menu_compass)
    else -> null
}

val motivationalQuotes = listOf(
    "Eat good, feel good!",
    "A healthy outside starts from the inside.",
    "Let food be thy medicine.",
    "Small changes, big results!"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen() {
    // Food selection
    val selectedFoods = remember { mutableStateListOf<String>() }
    var mealType by remember { mutableStateOf(mealTypes[0]) }
    val selectedSides = remember { mutableStateListOf<String>() }
    val mealEntries = remember { mutableStateListOf<MealEntry>() }
    var showHistory by remember { mutableStateOf(false) }
    var lastRecordedCalories by remember { mutableStateOf<Int?>(null) }
    var alertMessage by remember { mutableStateOf<String?>(null) }
    var selectedMealCalorieLimit by remember { mutableStateOf<Int?>(null) }
    var mealTypeDropdownExpanded by remember { mutableStateOf(false) }
    var expandedFoodCategory by remember { mutableStateOf<String?>(null) }
    var expandedSideCategory by remember { mutableStateOf<String?>(null) }
    var quoteIndex by remember { mutableStateOf(0) }

    val totalCalories = mealEntries.sumOf { it.calories }
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val todayEntries = mealEntries.filter { it.date == today }
    val todayCalories = todayEntries.sumOf { it.calories }
    val maxMealsPerDay = 4
    val canRecord = selectedFoods.isNotEmpty() && mealType.isNotBlank() && todayEntries.size < maxMealsPerDay && selectedMealCalorieLimit != null

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(3500)
            quoteIndex = (quoteIndex + 1) % motivationalQuotes.size
        }
    }

    // Gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.background.copy(alpha = 1.0f)
                    ),
                    startY = 0f,
                    endY = 2000f,
                    tileMode = TileMode.Clamp
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(Modifier.width(8.dp))
                        Text("NutriPlan", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
                ),
                modifier = Modifier.zIndex(1f)
            )
            // Animated motivational quote
            AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                Text(
                    motivationalQuotes[quoteIndex],
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            }
            Divider()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp)
            ) {
                // Meal type dropdown with icons
                Text("Choose Meal Type:", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp))
                ExposedDropdownMenuBox(
                    expanded = mealTypeDropdownExpanded,
                    onExpandedChange = { mealTypeDropdownExpanded = !mealTypeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = mealType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Meal Type") },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mealTypeDropdownExpanded) },
                        leadingIcon = { Icon(mealTypeIcon(mealType), contentDescription = null) }
                    )
                    ExposedDropdownMenu(
                        expanded = mealTypeDropdownExpanded,
                        onDismissRequest = { mealTypeDropdownExpanded = false }
                    ) {
                        mealTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(mealTypeIcon(type), contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                                    Text(type)
                                } },
                                onClick = {
                                    mealType = type
                                    mealTypeDropdownExpanded = false
                                    selectedMealCalorieLimit = null // reset calorie limit when meal type changes
                                }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Calorie limit radio buttons for selected meal type
                val calorieOptions = mealTypeCalorieOptions[mealType] ?: emptyList()
                AnimatedVisibility(visible = calorieOptions.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                    Column {
                        Text("Choose Calorie Limit for $mealType:", style = MaterialTheme.typography.titleMedium)
                        calorieOptions.forEach { option ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedMealCalorieLimit == option,
                                    onClick = { selectedMealCalorieLimit = option },
                                    colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
                                )
                                Text("$option kcal", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                // Only show food/side selection if calorie limit is chosen
                AnimatedVisibility(visible = selectedMealCalorieLimit != null, enter = fadeIn(), exit = fadeOut()) {
                    Column {
                        // Food selection
                        Text("Select Foods:", style = MaterialTheme.typography.titleMedium)
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                foodCategories.forEach { (category, items) ->
                                    val sortedItems = items.sorted()
                                    val expanded = expandedFoodCategory == category
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                expandedFoodCategory = if (expanded) null else category
                                            }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(category, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Divider()
                                    AnimatedVisibility(visible = expanded) {
                                        Column {
                                            sortedItems.forEach { food ->
                                                val checked = selectedFoods.contains(food)
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .padding(end = 8.dp, bottom = 2.dp)
                                                        .fillMaxWidth()
                                                        .let { if (checked) it.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)) else it }
                                                ) {
                                                    Checkbox(
                                                        checked = checked,
                                                        onCheckedChange = { checked ->
                                                            if (checked) selectedFoods.add(food) else selectedFoods.remove(food)
                                                        },
                                                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                                                    )
                                                    Icon(Icons.Filled.Restaurant, contentDescription = null, tint = if (checked) MaterialTheme.colorScheme.primary else Color.Gray, modifier = Modifier.padding(end = 4.dp))
                                                    Text("$food (${foodCalorieMap[food]} kcal)", style = if (checked) MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary) else MaterialTheme.typography.bodyLarge)
                                                }
                                                Divider()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        // Side Options
                        Text("Side Options:", style = MaterialTheme.typography.titleMedium)
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(Modifier.padding(8.dp)) {
                                sideCategories.forEach { (category, items) ->
                                    val sortedItems = items.sorted()
                                    val expanded = expandedSideCategory == category
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                expandedSideCategory = if (expanded) null else category
                                            }
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Text(category, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
                                    }
                                    Divider()
                                    AnimatedVisibility(visible = expanded) {
                                        Column {
                                            sortedItems.forEach { side ->
                                                val checked = selectedSides.contains(side)
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier
                                                        .padding(end = 8.dp, bottom = 2.dp)
                                                        .fillMaxWidth()
                                                        .let { if (checked) it.background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)) else it }
                                                ) {
                                                    Checkbox(
                                                        checked = checked,
                                                        onCheckedChange = { checked ->
                                                            if (checked) selectedSides.add(side) else selectedSides.remove(side)
                                                        },
                                                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.secondary)
                                                    )
                                                    Icon(Icons.Filled.LocalCafe, contentDescription = null, tint = if (checked) MaterialTheme.colorScheme.secondary else Color.Gray, modifier = Modifier.padding(end = 4.dp))
                                                    Text("$side (${foodCalorieMap[side]} kcal)", style = if (checked) MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary) else MaterialTheme.typography.bodyLarge)
                                                }
                                                Divider()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        // Summary card before recording
                        val mealCalories = selectedFoods.sumOf { foodCalorieMap[it] ?: 0 } + selectedSides.sumOf { foodCalorieMap[it] ?: 0 }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Meal Summary", style = MaterialTheme.typography.titleMedium)
                                Text("Meal Type: $mealType", style = MaterialTheme.typography.bodyLarge)
                                Text("Calorie Limit: $selectedMealCalorieLimit kcal", style = MaterialTheme.typography.bodyLarge)
                                Text("Selected Foods: ${selectedFoods.joinToString()}" , style = MaterialTheme.typography.bodyMedium)
                                Text("Sides: ${selectedSides.joinToString()}", style = MaterialTheme.typography.bodyMedium)
                                Text("Total Calories: $mealCalories kcal", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        // Record Meal Button
                        Button(
                            onClick = {
                                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                if (canRecord) {
                                    val limit = selectedMealCalorieLimit
                                    if (limit != null && mealCalories > limit) {
                                        alertMessage = "This meal exceeds the calorie limit for $mealType!"
                                    } else {
                                        val entry = MealEntry(
                                            foods = selectedFoods.toList(),
                                            mealType = mealType,
                                            sides = selectedSides.toList(),
                                            calories = mealCalories,
                                            date = date
                                        )
                                        mealEntries.add(entry)
                                        lastRecordedCalories = mealCalories
                                        // Clear selection
                                        selectedFoods.clear()
                                        selectedSides.clear()
                                        mealType = mealTypes[0]
                                        selectedMealCalorieLimit = null
                                        alertMessage = null
                                    }
                                }
                            },
                            enabled = canRecord,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                            Text("Record Meal", style = MaterialTheme.typography.titleLarge)
                        }
                        if (lastRecordedCalories != null) {
                            Spacer(Modifier.height(12.dp))
                            Text("Calories for this meal: $lastRecordedCalories", style = MaterialTheme.typography.titleMedium)
                        }
                        if (alertMessage != null) {
                            Spacer(Modifier.height(8.dp))
                            Text(alertMessage!!, color = MaterialTheme.colorScheme.error)
                        }
                        if (todayEntries.size >= maxMealsPerDay) {
                            Spacer(Modifier.height(8.dp))
                            Text("You have reached the maximum number of meals for today.", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            // History button at the bottom (with gradient and rounded corners)
            Button(
                onClick = { showHistory = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(32.dp)),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Filled.Restaurant, contentDescription = null, modifier = Modifier.padding(end = 8.dp), tint = MaterialTheme.colorScheme.onPrimary)
                Text("History", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        if (showHistory) {
            // History view (overlay)
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showHistory = false },
                confirmButton = {
                    Button(onClick = { showHistory = false }) { Text("Close") }
                },
                title = { Text("Meal History") },
                text = {
                    if (mealEntries.isEmpty()) {
                        Text("No meals recorded yet.")
                    } else {
                        Column(Modifier.verticalScroll(rememberScrollState())) {
                            mealEntries.forEach { entry ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    elevation = CardDefaults.cardElevation(2.dp)
                                ) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text("${entry.foods.joinToString()} (${entry.mealType})", style = MaterialTheme.typography.titleSmall)
                                        if (entry.sides.isNotEmpty()) {
                                            Text("Sides: ${entry.sides.joinToString()}", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Text("Calories: ${entry.calories}", style = MaterialTheme.typography.bodySmall)
                                        Text("Date: ${entry.date}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Total Calories: $totalCalories", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            )
        }
    }
} 