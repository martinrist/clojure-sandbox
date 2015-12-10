(ns clojure-applied.chapter-1.recipe
  "Contains model definitions and functions to work with a representation
  of recipes.

  These illustrate two options for how to relate recipes and the people who
  created them.")

; Basic record definitions for Recipe and Person.
; We want the `author` of a recipe to be a `Person`.
(defrecord Recipe [name author description ingredients steps servings])
(defrecord Person [fname lname])

(comment

  "If we want to focus mainly on the Recipe, nest the Person inside it"
  (def toast
    (->Recipe "Toast"
              (->Person "Martin" "Rist")
              "Cripsy bread"
              ["Slice of bread"]
              ["Toast bread in toaster"]))

  "Alternatively, use identifiers for people and recipes"
  (def people
    {"p1" (->Person "Martin" "Rist")})

  (def recipes
    {"r1" (->Recipe "Toast"
                    "p1"
                    "Crispy bread"
                    ["Slice of bread"]
                    ["Toast bread in toaster"])})
  )