(ns programming-clojure.exploring
  (:require [clojure.string :as str]))

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
  [input]
  (str/split input #"\W+"))

(defn get-indexable-words-1
  "Returns indexable words from a string of text."
  [input]
  (filter indexable-word? (split-words input)))

(defn get-indexable-words-2
  "Variant of get-indexable-words that uses an anonymous function definition."
  [input]
  (filter (fn [w] (> (count w) 3)) (split-words input)))

(defn get-indexable-words-3
  "Variant of get-indexable-words that uses an even shorter anonymous function definition."
  [input]
  (filter #(> (count %) 3 ) (split-words input)))

(defn make-greeter
  "Returns a greeter function with the specified prefix"
  [greeter-prefix]
  #(str greeter-prefix ", " %))

(def greet-with-hello (make-greeter "Hello"))
(def greet-with-aloha (make-greeter "Aloha"))



;;; Destructuring
(defrecord Author [first-name last-name])

(defn greet-author-1
  "Greets an author by their first name, without destructuring"
  [author]
  (str "Hello, " (:first-name author)))

(defn greet-author-2
  "Greets an author by their first name, using destructuring to pick out :first-name"
  [{fname :first-name}]
  (str "Hello, " fname))

(defn greet-author-3
  "Greets an author by their first name, but also show full details, using destructuring to also bind the whole map"
  [{fname :first-name :as author}]
  (str "Hello, " fname " (" author ")"))

(defn ellipsize
  "Takes a string and returns the first three words, followed by ..."
  [words]
  (let [[first second third] (split-words words)]
    (str/join " " [first second third "..."])))



;;; Loop / Recur
(defn countdown
  "Returns a sequence counting down from the specified number"
  [num]
  (loop [result []
         x num]
    (if (zero? x)
      result
      (recur (conj result x) (dec x)))))