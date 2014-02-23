(ns programming-clojure.exploring)

;;; Variadic functions

(defn greeting
  "Returns a greeting of various forms depending on number of parameters."
  ([] (str "Hello, world!"))
  ([username] (str "Hello, " username))
  ([username & friends]
   (let [friends-str (apply str (interpose ", " friends))]
     (str "Hello " username " and your friends " friends-str))))



;;; Anonymous functions

(defn indexable-word?
  "Determines whether a word is long enough to be indexed."
  [word]
  (> (count word) 3))

(defn split-words
  "Splits words on word boundary."
  [str]
  (clojure.string/split str #"\W+"))

(defn get-indexable-words-1
  "Returns indexable words from a string of text."
  [str]
  (filter indexable-word? (split-words str)))

(defn get-indexable-words-2
  "Variant of get-indexable-words that uses an anonymous function definition."
  [str]
  (filter (fn [w] (> (count w) 3)) (split-words str)))

(defn get-indexable-words-3
  "Variant of get-indexable-words that uses an even shorter anonymous function definition."
  [str]
  (filter #(> (count %) 3 ) (split-words str)))

(defn make-greeter
  "Returns a greeter function with the specified prefix"
  [greeter-prefix]
  #(str greeter-prefix ", " %))

(def greet-with-hello (make-greeter "Hello"))
(def greet-with-aloha (make-greeter "Aloha"))
