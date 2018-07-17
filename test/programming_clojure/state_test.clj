(ns programming-clojure.state-test
  (:require [clojure.test :refer :all]))

(deftest ref-tests
  (testing "Cannot update refs outside a transaction"
    (let [r (ref nil)]
      (is (thrown? IllegalStateException (ref-set r 1)))
      (is (nil? @r))))

  (testing "Can update refs in a transaction"
    (let [r (ref nil)]
      (dosync
        (ref-set r 1))
      (is (= 1 @r))))

  (testing "Multiple ref updates in a transaction"
    (let [r (ref nil)]
      (dosync
        (ref-set r 1)
        (ref-set r 2))
      (is (= 2 @r))))

  (testing "Throwing exception in dosync prevents ref udpate"
    (let [r (ref nil)]
      (is (thrown? Exception (dosync
                               (ref-set r 1)
                               (throw (Exception.)))))
      (is (nil? @r))))

  (testing "Using alter to update ref based on existing value"
    (let [counter (ref 0)]
      (dosync
        (alter counter inc))
      (is (= 1 @counter))))

  (testing "Many incrementing alters in separate transactions don't corrupt state"
    (let [r (ref 0)
          iterations 1000]
      (->> (for [x (range iterations)]
             (future (dosync (alter r inc))))
           (map deref)
           doall)
      (is (= iterations @r))))

  (testing "Ref with validator function"
    (let [r (ref 0 :validator #(< % 100))]

      (testing "Update to < 100 works"
        (dosync (ref-set r 99))
        (is (= @r 99)))

      (testing "Update to >= 100 fails validation and doesn't change ref"
        (try
          (dosync (ref-set r 100))
          (catch IllegalStateException e ()))
        (is (= @r 99))))))

(deftest atom-tests
  (testing "Deref atom gets its initial state"
    (let [a (atom 0)]
      (is (= 0 @a)))
    )

  (testing "Deref atom after reset! gets new state"
    (let [a (atom nil)]
      (is (= 1 (reset! a 1)))
      (is (= 1 @a))))

  (testing "Update atom using swap!"
    (let [a (atom 0)]
      (is (= 1 (swap! a inc)))
      (is (= 1 @a)))))

(defn delay-fn
  "Takes a function `f` and a delay period in ms.  Returns a new function that sleeps for
  `delay-ms` then invokes `f`"
  [delay-ms f]
  (fn [& args]
    (Thread/sleep delay-ms)
    (apply f args)))

(deftest agent-tests
  (testing "Updating agent asynchronously updates value"
    (let [a (agent 0)]
      (send a inc)
      (Thread/sleep 100)
      (is (= 1 @a))))

  (testing "Updating agent with slow increment function eventually updates value"
    (let [a (agent 0)]
      (send a (delay-fn 100 inc))
      (is (= 0 @a))
      (Thread/sleep 200)
      (is (= 1 @a))))

  (testing "Updating agent with slow increment function and awaiting"
    (let [a (agent 0)]
      (send a (delay-fn 100 inc))
      (await a)
      (is (= 1 @a)))))


