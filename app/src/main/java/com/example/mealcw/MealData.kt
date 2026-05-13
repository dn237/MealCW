package com.example.mealcw

object MealData {
    val hardcodedMeals = listOf(
        Meal(
            idMeal = "1",
            strMeal = "Sweet and Sour Pork",
            strCategory = "Pork",
            strArea = "Chinese",
            strInstructions = """
                Preparation
                1. Crack the egg into a bowl. Separate the egg white and yolk.
                
                Sweet and Sour Pork
                2. Slice the pork tenderloin into strips.
                3. Prepare the marinade using a pinch of salt, one teaspoon of starch, two teaspoons of light soy sauce, and an egg white.
                4. Marinade the pork strips for about 20 minutes.
                5. Put the remaining starch in a bowl. Add some water and vinegar to make a starchy sauce.
                
                Sweet and Sour Pork
                Cooking Instructions
                1. Pour the cooking oil into a wok and heat to 190°C (375°F). Add the marinated pork strips and fry them until they turn brown. Remove the cooked pork from the wok and place on a plate.
                2. Leave some oil in the wok. Put the tomato sauce and white sugar into the wok, and heat until the oil and sauce are fully combined.
                3. Add some water to the wok and thoroughly heat the sweet and sour sauce before adding the pork strips to it.
                4. Pour in the starchy sauce. Stir-fry all the ingredients until the pork and sauce are thoroughly mixed together.
                5. Serve on a plate and add some coriander for decoration.""".trimIndent(),
            strMealThumb = "https://www.themealdb.com/images/media/meals/1529442316.jpg",
            strTags = "Sweet",
            strYoutube = "https://www.youtube.com/watch?v=mdaBIhgEAMo",
            strIngredient1 = "Pork",
            strMeasure1 = "200g",
            strIngredient2 = "Egg",
            strMeasure2 = "1",
            strIngredient3 = "Water",
            strMeasure3 = "Dash",
            strIngredient4 = "Salt",
            strMeasure4 = "1/2 tsp",
            strIngredient5 = "Sugar",
            strMeasure5 = "1 tsp ",
            strIngredient6 = "Soy Sauce",
            strMeasure6 = "10g",
            strIngredient7 = "Starch",
            strMeasure7 = "10g",
            strIngredient8 = "Tomato Puree",
            strMeasure8 = "30g",
            strIngredient9 = "Vinegar",
            strMeasure9 = "10g",
            strIngredient10 = "Coriander",
            strMeasure10 = "Dash"
        ),

        Meal(
            idMeal = "2",
            strMeal = "Chicken Marengo",
            strCategory = "Chicken",
            strArea = "French",
            strInstructions = """
                Heat the oil in a large flameproof casserole dish and stir-fry the mushrooms until they start to soften. Add the chicken legs and cook briefly on each side to colour them a little.
                Pour in the passata, crumble in the stock cube and stir in the olives. Season with black pepper – you shouldn’t need salt. Cover and simmer for 40 mins until the chicken is tender. Sprinkle with parsley and serve with pasta and a salad, or mash and green veg, if you like.
                """.trimIndent(),
            strMealThumb = "https://www.themealdb.com/images/media/meals/qpxvuq1511798906.jpg",
            strYoutube = "https://www.youtube.com/watch?v=U33HYUr-0Fw",
            strIngredient1 = "Olive Oil",
            strMeasure1 = "1 tbsp",
            strIngredient2 = "Mushrooms",
            strMeasure2 = "300g",
            strIngredient3 = "Chicken Legs",
            strMeasure3 = "4",
            strIngredient4 = "Passata",
            strMeasure4 = "500g",
            strIngredient5 = "Chicken Stock Cube",
            strMeasure5 = "1",
            strIngredient6 = "Black Olives",
            strMeasure6 = "100g ",
            strIngredient7 = "Parsley",
            strMeasure7 = "Chopped",
            strSource = "https://www.bbcgoodfood.com/recipes/3146682/chicken-marengo"
        ),

        Meal(
            idMeal = "3",
            strMeal = "Beef Banh Mi Bowls with Sriracha Mayo, Carrot & Pickled Cucumber",
            strCategory = "Beef",
            strArea = "Vietnamese",
            strInstructions = """
                Add'l ingredients: mayonnaise, sriracha
                
                1. Place rice in a fine-mesh sieve and rinse until water runs clear. Add to a small pot with 1 cup water (2 cups for 4 servings) and a pinch of salt. Bring to a boil, then cover and reduce heat to low. Cook until rice is tender, 15 minutes. Keep covered off heat for at least 10 minutes or until ready to serve.
                2. Meanwhile, wash and dry all produce. Peel and finely chop garlic. Zest and quarter lime (for 4 servings, zest 1 lime and quarter both). Trim and halve cucumber lengthwise; thinly slice crosswise into half-moons. Halve, peel, and medium dice onion. Trim, peel, and grate carrot.
                3. In a medium bowl, combine cucumber, juice from half the lime, 1/4 tsp sugar (1/2 tsp for 4 servings), and a pinch of salt. In a small bowl, combine mayonnaise, a pinch of garlic, a squeeze of lime juice, and as much sriracha as you’d like. Season with salt and pepper.
                4. Heat a drizzle of oil in a large pan over medium-high heat. Add onion and cook, stirring, until softened, 4-5 minutes. Add beef, remaining garlic, and 2 tsp sugar (4 tsp for 4 servings). Cook, breaking up meat into pieces, until beef is browned and cooked through, 4-5 minutes. Stir in soy sauce. Turn off heat; taste and season with salt and pepper.
                5. Fluff rice with a fork; stir in lime zest and 1 TBSP butter. Divide rice between bowls. Arrange beef, grated carrot, and pickled cucumber on top. Top with a squeeze of lime juice. Drizzle with sriracha mayo.
                """.trimIndent(),
            strMealThumb = "https://www.themealdb.com/images/media/meals/z0ageb1583189517.jpg",
            strYoutube = "",
            strIngredient1 = "Rice",
            strMeasure1 = "White",
            strIngredient2 = "Onion",
            strMeasure2 = "1",
            strIngredient3 = "Lime",
            strMeasure3 = "1",
            strIngredient4 = "Garlic Clove",
            strMeasure4 = "3",
            strIngredient5 = "Cucumber",
            strMeasure5 = "1",
            strIngredient6 = "Carrots",
            strMeasure6 = "3 oz",
            strIngredient7 = "Ground Beef",
            strMeasure7 = "1 lb",
            strIngredient8 = "Soy Sauce",
            strMeasure8 = "2 oz"
        ),

        Meal(
            idMeal = "4",
            strMeal = "Leblebi Soup",
            strCategory = "Vegetarian",
            strArea = "Tunisian",
            strInstructions =  """
                Heat the oil in a large pot. Add the onion and cook until translucent.
                Drain the soaked chickpeas and add them to the pot together with the vegetable stock. Bring to the boil, then reduce the heat and cover. Simmer for 30 minutes.
                In the meantime toast the cumin in a small ungreased frying pan, then grind them in a mortar. Add the garlic and salt and pound to a fine paste.
                Add the paste and the harissa to the soup and simmer until the chickpeas are tender, about 30 minutes.
                Season to taste with salt, pepper and lemon juice and serve hot.
                """.trimIndent(),
            strMealThumb = "https://www.themealdb.com/images/media/meals/x2fw9e1560460636.jpg",
            strTags = "Soup",
            strYoutube = "https://www.youtube.com/watch?v=BgRifcCwinY",
            strIngredient1 = "Olive Oil",
            strMeasure1 = "2 tbs",
            strIngredient2 = "Onion",
            strMeasure2 = "1 medium finely diced",
            strIngredient3 = "Chickpeas",
            strMeasure3 = "250g",
            strIngredient4 = "Vegetable Stock",
            strMeasure4 = "1.5L",
            strIngredient5 = "Cumin",
            strMeasure5 = "1 tsp",
            strIngredient6 = "Garlic",
            strMeasure6 = "5 cloves",
            strIngredient7 = "Salt",
            strMeasure7 = "1/2 tsp",
            strIngredient8 = "Harissa Spice",
            strMeasure8 = "1 tsp",
            strIngredient9 = "Pepper",
            strMeasure9 = "Pinch",
            strIngredient10 = "Lime",
            strMeasure10 = "1/2",
            strSource = "http://allrecipes.co.uk/recipe/43419/leblebi--tunisian-chickpea-soup-.aspx"
        )
    )
}