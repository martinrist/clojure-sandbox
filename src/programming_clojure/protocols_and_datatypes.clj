(ns programming-clojure.protocols-and-datatypes
  (:import (java.lang StringBuilder)
           (java.io FileInputStream InputStreamReader BufferedReader FileOutputStream
                    OutputStreamWriter BufferedWriter)))

(defn gulp
  [src]
  (let [sb (StringBuilder.)]
    (with-open [reader (-> src
                           FileInputStream.
                           InputStreamReader.
                           BufferedReader.)]
      (loop [c (.read reader)]
        (if (neg? c)
          (str sb)
          (do
            (.append sb (char c))
            (recur (.read reader))))))))

(defn expectorate
  [dst content]
  (with-open [writer (-> dst
                         FileOutputStream.
                         OutputStreamWriter.
                         BufferedWriter.
                         )]
    (.write writer (str content))))

