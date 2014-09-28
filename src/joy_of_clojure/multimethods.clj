(ns clojure-sandbox.joy-of-clojure.multimethods
  (:refer-clojure :exclude [get]))

(defn beget
  [this proto]
  (assoc this ::prototype proto))

(defn get
  [m k]
  (when m
    (if-let [[_ v] (find m k)]
      v
      (recur (::prototype m) k))))



(def unix {:os ::unix :c-compiler "cc" :home "/home" :dev "/dev"})
(def osx (beget {:os ::osx :llvm-compiler "clang" :home "/Users"} unix))

(defmulti compiler :os)
(defmethod compiler ::unix
           [m]
           (get m :c-compiler))
(defmethod compiler ::osx
           [m]
           (get m :llvm-compiler))

(defmulti home :os)
(defmethod home ::unix
           [m]
           (get m :home))
(defmethod home ::bsd
           [m]
  (str "bsd -> /home"))


(derive ::osx ::unix)
(derive ::osx ::bsd)