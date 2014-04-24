(ns clojure-sandbox.clojure-from-the-ground-up)


(defn simple-palindromic?
  [s]
  (= s (apply str (reverse s))))

(defn recursive-palindromic?
  [s]
  (if (= s "")
    true
    (let [first (first s)
          last (last s)
          middle (apply str (rest (drop-last 1 s)))]
      (and
        (= first last)
        (recursive-palindromic? middle)))))

