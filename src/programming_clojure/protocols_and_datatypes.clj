(ns programming-clojure.protocols-and-datatypes
  (:import (java.lang StringBuilder)
           (java.io FileInputStream InputStreamReader BufferedReader FileOutputStream
                    OutputStreamWriter BufferedWriter)
           (java.net Socket URL)))

(defn make-reader
  [src]
  (-> (condp = (type src)
        java.io.InputStream src
        java.lang.String (FileInputStream. src)
        java.io.File (FileInputStream. src)
        java.net.Socket (.getInputStream src)
        java.net.URL (if (= "file" (.getProtocol src))
                       (-> src .getPath FileInputStream.)
                       (.openStream src)))
      InputStreamReader.
      BufferedReader.))

(defn make-writer
  [dst]
  (-> (condp = (type dst)
        java.io.OutputStream dst
        java.lang.String (FileOutputStream. dst)
        java.io.File (FileOutputStream. dst)
        java.net.Socket (.getOutputStream dst)
        java.net.URL (if (= "file" (.getProtocol dst))
                       (-> dst .getPath FileOutputStream.)
                       (throw (IllegalArgumentException.
                                "Can't write to non-file URL"))))
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


