(ns programming-clojure.protocols-and-datatypes
  (:import (java.lang StringBuilder)
           (java.io FileInputStream InputStreamReader BufferedReader FileOutputStream
                    OutputStreamWriter BufferedWriter)))

(defn make-reader
  [src]
  (-> src
      FileInputStream.
      InputStreamReader.
      BufferedReader.))

(defn make-writer
  [src]
  (-> src
      FileOutputStream.
      OutputStreamWriter.
      BufferedWriter.))

(defn gulp
  [src]
  (let [sb (StringBuilder.)]
    (with-open [reader (make-reader src)]
      (loop [c (.read reader)]
        (if (neg? c)
          (str sb)
          (do
            (.append sb (char c))
            (recur (.read reader))))))))

(defn expectorate
  [dst content]
  (with-open [writer (make-writer dst)]
    (.write writer (str content))))


