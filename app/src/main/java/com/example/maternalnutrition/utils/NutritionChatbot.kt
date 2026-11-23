package com.example.maternalnutrition.utils

import com.example.maternalnutrition.database.Meal
import java.util.concurrent.TimeUnit

object NutritionChatbot {

    fun generateResponse(
        message: String,
        meals: List<Meal>
    ): String {

        val question = message.trim().lowercase()

        if (question.isEmpty()) {
            return "Please enter a question so I can assist you."
        }

        val recentMeals = getRecentMeals(meals)

        return when {

            containsAny(
                question,
                listOf("hello", "hi", "hey", "good morning",
                    "good afternoon", "good evening")
            ) -> {
                "Hello! Welcome to your Maternal Nutrition " +
                        "Assistant. I can help you explore food variety, " +
                        "review your recorded meals and learn about " +
                        "general healthy eating during pregnancy. " +
                        "What would you like to know?"
            }

            containsAny(
                question,
                listOf("my meals", "meal history", "what have i eaten",
                    "what did i eat", "my food records")
            ) -> getMealSummary(recentMeals)

            containsAny(
                question,
                listOf("repeat", "repeating", "same food",
                    "variety", "different food", "alternative",
                    "alternatives", "tired of eating")
            ) -> getVarietySuggestions(recentMeals)

            containsAny(
                question,
                listOf("food groups", "balanced diet",
                    "balanced meal", "nutrition groups")
            ) -> getFoodGroupInformation()

            containsAny(
                question,
                listOf("breakfast", "lunch", "supper", "dinner",
                    "what should i eat", "what can i eat",
                    "suggest a meal", "meal suggestion")
            ) -> getMealSuggestions(recentMeals)

            containsAny(
                question,
                listOf("fruit", "fruits", "vegetable",
                    "vegetables")
            ) -> {
                "Fruits and vegetables provide important vitamins, " +
                        "minerals and fibre. You can explore different " +
                        "options such as oranges, mangoes, pawpaw, " +
                        "bananas, sukuma wiki, nakati and spinach. " +
                        "Wash fresh produce properly before eating."
            }

            containsAny(
                question,
                listOf("protein", "proteins")
            ) -> {
                "Protein-containing foods help support growth " +
                        "and body maintenance. Examples include beans, " +
                        "peas, groundnuts, eggs, fish, milk and meat. " +
                        "You can vary these foods across your meals."
            }

            containsAny(
                question,
                listOf("water", "hydration", "drink")
            ) -> {
                "Drinking safe water regularly is important. " +
                        "Your hydration needs can vary depending on " +
                        "your health, activity and environment. Ask " +
                        "your healthcare provider if you have been " +
                        "given special fluid instructions."
            }

            containsAny(
                question,
                listOf("thank you", "thanks")
            ) -> {
                "You're welcome! Keep recording your meals " +
                        "to help you review your eating patterns."
            }

            containsAny(
                question,
                listOf("help", "what can you do", "features")
            ) -> {
                "I can help you with:\n\n" +
                        "1. Reviewing your recorded meals.\n" +
                        "2. Identifying foods you frequently record.\n" +
                        "3. Exploring different food options.\n" +
                        "4. Understanding food groups.\n" +
                        "5. Learning general nutrition information."
            }

            else -> {
                "I can help with general maternal nutrition " +
                        "information and food variety. You can ask me " +
                        "about your recorded meals, food groups, " +
                        "fruits, vegetables, protein or meal suggestions."
            }
        }
    }

    private fun getRecentMeals(
        meals: List<Meal>
    ): List<Meal> {

        val sevenDaysAgo = System.currentTimeMillis() -
                TimeUnit.DAYS.toMillis(7)

        return meals.filter {
            it.mealDate >= sevenDaysAgo
        }.sortedByDescending {
            it.mealDate
        }
    }

    private fun getMealSummary(
        meals: List<Meal>
    ): String {

        if (meals.isEmpty()) {
            return "You have not recorded any meals in the " +
                    "last seven days. Start by selecting " +
                    "'Record Meal' on your dashboard. Once you " +
                    "record meals, I can help you review them."
        }

        val foodList = meals
            .take(10)
            .map { it.foodName }
            .distinct()

        val summary = foodList.joinToString("\n") {
            "• $it"
        }

        return "Here are some foods from your meal records " +
                "during the last seven days:\n\n" +
                "$summary\n\n" +
                "These records help you review your food choices. " +
                "They do not establish whether your diet meets " +
                "all your nutritional needs."
    }

    private fun getVarietySuggestions(
        meals: List<Meal>
    ): String {

        if (meals.isEmpty()) {
            return "I do not have recent meal records to " +
                    "compare yet. Record your meals first, " +
                    "then I can help identify foods you record " +
                    "frequently and suggest alternatives."
        }

        val frequency = meals
            .groupingBy {
                it.foodName.trim().lowercase()
            }
            .eachCount()

        val repeatedFoods = frequency
            .filter { it.value >= 2 }
            .entries
            .sortedByDescending { it.value }

        val repeatedText = if (repeatedFoods.isNotEmpty()) {

            repeatedFoods.take(5).joinToString("\n") {
                "• ${it.key.replaceFirstChar { char ->
                    char.uppercase()
                }} (${it.value} records)"
            }

        } else {
            "No repeated food names were identified in your " +
                    "recent records."
        }

        return "Let's explore food variety!\n\n" +
                "Foods appearing repeatedly in your recent " +
                "records:\n$repeatedText\n\n" +
                "You could explore other locally available " +
                "options, such as:\n" +
                "• Beans, peas, eggs or fish.\n" +
                "• Matooke, sweet potatoes or cassava.\n" +
                "• Sukuma wiki, nakati or spinach.\n" +
                "• Oranges, mangoes, pawpaw or bananas.\n\n" +
                "Try varying foods across different meals " +
                "according to your preferences, availability " +
                "and any advice from your healthcare provider."
    }

    private fun getMealSuggestions(
        meals: List<Meal>
    ): String {

        val repeated = meals
            .groupingBy {
                it.foodName.trim().lowercase()
            }
            .eachCount()
            .filter { it.value >= 2 }
            .keys

        val alternatives = listOf(
            "A meal combining beans, vegetables and a staple food.",
            "Sweet potatoes served with eggs and vegetables.",
            "Matooke accompanied by peas or beans.",
            "Fish served with vegetables and a staple food.",
            "Rice served with beans and a variety of vegetables."
        )

        val suggestions = alternatives.filter { suggestion ->
            repeated.none { food ->
                suggestion.contains(food, ignoreCase = true)
            }
        }

        val selectedSuggestions = if (suggestions.isEmpty()) {
            alternatives.take(3)
        } else {
            suggestions.take(3)
        }

        return "Here are some general meal ideas to explore:\n\n" +
                selectedSuggestions.joinToString("\n\n") {
                    "• $it"
                } +
                "\n\nThese are examples, not a prescribed " +
                "pregnancy diet. Choose foods suitable for " +
                "your needs and discuss any dietary restrictions " +
                "with your healthcare provider."
    }

    private fun getFoodGroupInformation(): String {

        return "A varied eating pattern can include different " +
                "food groups:\n\n" +
                "1. Energy-giving foods: rice, matooke, " +
                "sweet potatoes and maize.\n\n" +
                "2. Protein foods: beans, peas, eggs, fish " +
                "and meat.\n\n" +
                "3. Fruits: oranges, mangoes, pawpaw " +
                "and bananas.\n\n" +
                "4. Vegetables: spinach, nakati, carrots " +
                "and sukuma wiki.\n\n" +
                "5. Calcium-containing foods: milk, yoghurt " +
                "and other suitable dairy products.\n\n" +
                "Eating a variety of foods helps provide " +
                "different nutrients."
    }

    private fun containsAny(
        text: String,
        keywords: List<String>
    ): Boolean {

        return keywords.any {
            text.contains(it)
        }
    }
}
