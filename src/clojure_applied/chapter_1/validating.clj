(ns clojure-applied.chapter-1.validating
  "Contains examples of using Prismatic Schema for validating entities."
  (:require [schema.core :as s]))

(s/defrecord Ingredient [name     :- s/Str
                         quantity :- s/Int
                         unit     :- s/Keyword])

(s/defrecord Recipe     [name        :- s/Str
                         description :- s/Str
                         ingredients :- [Ingredient]
                         steps       :- [s/Str]
                         servings    :- s/Int])

(s/defn add-ingredients :- Recipe
  "Adds `ingredients` to `recipe`"
  [recipe        :- Recipe
   & ingredients :- [Ingredient]]
  (update-in recipe [:ingredients] into ingredients))



(comment

  "We can get a description of the schema"
  (s/explain Recipe)

  "And check a valid instance against the schema"
  (s/check Recipe (map->Recipe
                    {:name          "Spaghetti Tacos"
                     :description   "It's spaghetti ... in a taco"
                     :ingredients   [(->Ingredient "Spaghetti" 1 :lb)
                                     (->Ingredient "Spaghetti Sauce" 16 :oz)
                                     (->Ingredient "Taco shell" 12 :shell)]
                     :steps         ["Cook spaghetti according to box."
                                     "Heat spaghetti sauce iuntil warm."
                                     "Mix spaghetti and sauce."
                                     "Put spaghetti into taco shells."]
                     :servings      4}))

  "Here's an example where an invalid Ingredient is checked"
  (s/check Ingredient (map->Ingredient {:name "Valid"
                                        :quantity "Twenty"}))

  "Type hints on s/defn's give nice docstrings"
  (clojure.repl/doc add-ingredients)

  "We can also use `s/with-fn-validation` to run a method body with
  argument validation."
  (s/with-fn-validation
    (add-ingredients (map->Recipe {:name "Empty Recipe"})
                      [(map->Ingredient {:name "Nothing"})]))
  )