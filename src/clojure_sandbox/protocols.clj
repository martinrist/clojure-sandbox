(ns clojure-sandbox.protocols
  (:refer-clojure :exclude [push pop peek]))

(defrecord TreeNode [val l r])

(defn xconj
  [t v]
  (cond
    (nil? t)        (TreeNode. v nil nil)
    (< v (:val t))  (TreeNode. (:val t) (xconj (:l t) v) (:r t))
    :else           (TreeNode. (:val t) (:l t) (xconj (:r t) v))))

(defn xseq
  [t]
  (when t
    (concat (xseq (:l t)) [(:val t)] (xseq (:r t)))))

(defprotocol FIXO
  (push [this value])
  (pop [this])
  (peek [this]))

(extend-type TreeNode
    FIXO
    (push [node value]
      (xconj node value))
    (peek [node]
      (if (:l node)
        (recur (:l node))
        (:val node)))
    (pop [node]
      (if (:l node)
        (TreeNode. (:val node) (pop (:l node)) (:r node))
        (:r node))))

(extend-type clojure.lang.IPersistentVector
  FIXO
  (push [vector value]
    (conj vector value))
  (peek [vector]
    (clojure.core/peek vector))
  (pop [vector]
    (clojure.core/pop vector)))